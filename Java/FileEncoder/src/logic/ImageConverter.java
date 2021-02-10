package logic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * Separate class for converting files from / to images
 */
public class ImageConverter {

	private static final Charset CHARSET = Charset.forName("US-ASCII");
	private static final String IMAGE_FORMAT = "png";
	private static final int BYTES_PER_PIXEL = 4;
	private static final int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;

	private static final String FILENAME = "filename";
	private static final String LENGTH = "length";
	private static final String DELIMITER = ":";
	private static final String HEADER_END = "</head>";

	public static void convert(File file, ProgressListener progressListener) {
		try {
			String fileName = file.getName();
			if (fileName.endsWith("." + IMAGE_FORMAT)) {
				convertToBytes(file, progressListener);
			} else {
				convertToImage(file, progressListener);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void convertToImage(File bytesFile, ProgressListener progressListener) throws IOException {
		byte[] content = readBytes(bytesFile);

		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append(FILENAME);
		headerBuilder.append(DELIMITER);
		headerBuilder.append(bytesFile.getName());
		headerBuilder.append(DELIMITER);
		headerBuilder.append(LENGTH);
		headerBuilder.append(DELIMITER);
		headerBuilder.append(content.length);
		headerBuilder.append(HEADER_END);

		byte[] header = headerBuilder.toString().getBytes(CHARSET);

        final byte[] total = new byte[header.length + content.length];
        System.arraycopy(header, 0, total, 0, header.length);
        System.arraycopy(content, 0, total, header.length, content.length);
		int pixelCount = (int) Math.ceil(total.length / (double) BYTES_PER_PIXEL);
		int width = (int) Math.sqrt(pixelCount);
		int height = (int) Math.ceil(((double) pixelCount) / width);
		BufferedImage image = new BufferedImage(width, height, IMAGE_TYPE);

		System.out.println(width + "x" + height + " (using " + pixelCount + " pixels) for " + total.length + " bytes");
		progressListener.onProgress(0, total.length);

		int byteIndex = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (byteIndex < total.length) {
					int a = (total[byteIndex] & 255);
					byteIndex++;
					int r = (byteIndex < total.length) ? (total[byteIndex] & 255) : 0;
					byteIndex++;
					int g = (byteIndex < total.length) ? (total[byteIndex] & 255) : 0;
					byteIndex++;
					int b = (byteIndex < total.length) ? (total[byteIndex] & 255) : 0;
					byteIndex++;

					int colorValue = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
					image.setRGB(x, y, colorValue);

					progressListener.onProgress(Math.min(byteIndex + 1, total.length), total.length);
				} else {
					break;
				}
			}
		}

		File destination = new File(bytesFile.getAbsolutePath() + "." + IMAGE_FORMAT);
		ImageIO.write(image, IMAGE_FORMAT, destination);
	}

	@SuppressWarnings("resource")
	private static void convertToBytes(File imageFile, ProgressListener progressListener) throws IOException {
		BufferedImage in = ImageIO.read(imageFile);

		System.out.println("loaded image, starting to parse header...");

		ImageByteInputStream imageInputStream = new ImageByteInputStream(in);
		BufferedReader reader = new BufferedReader(new InputStreamReader(imageInputStream, CHARSET));

		StringBuilder headerBuilder = new StringBuilder();
		boolean headerEnded = false;
		char lastHeaderChar = HEADER_END.charAt(HEADER_END.length() - 1);
		while (!headerEnded) {
			int nextInt = reader.read();
			if (nextInt >= 0) {
				char nextChar = (char) nextInt;
				headerBuilder.append(nextChar);
				if (nextChar == lastHeaderChar && headerBuilder.toString().endsWith(HEADER_END)) {
					headerEnded = true;
				}
			} else {
				throw new IllegalArgumentException("Given file does not contain a valid header.");
			}
		}

		reader.close();

		System.out.println("parsed header [size=" + headerBuilder.toString().getBytes(CHARSET).length + "], starting to parse content...");

		String header = headerBuilder.toString();
		String filename = "";
		int length = 0;
		String[] splitHeader = header.replace(HEADER_END, "").split(DELIMITER);
		for (int i = 0; i + 1 < splitHeader.length; i += 2) {
			if (splitHeader[i].equals(FILENAME)) {
				filename = splitHeader[i + 1];
			} else if (splitHeader[i].equals(LENGTH)) {
				length = Integer.parseInt(splitHeader[i + 1]);
			}
		}

		progressListener.onProgress(0, length);

		byte[] data = new byte[length];
		ImageByteInputStream inputStream = new ImageByteInputStream(in);
		inputStream.skip(header.getBytes(CHARSET).length);
		for (int i = 0; i < length; i++) {
			int next = inputStream.read();
			if (next >= 0) {
				data[i] = (byte) next;
			} else {
				throw new IllegalArgumentException("Given file has shorter content than defined by the header.");
			}
			progressListener.onProgress(i + 1, length);
		}

		File destination = new File(imageFile.getParentFile().getAbsolutePath() + "/" + filename);
		writeBytes(destination, data);
	}

	public static void main(String[] args) {
		JPanel dropPanel = new JPanel(new BorderLayout());
		final JLabel stateLabel = new JLabel("drop files");
		final JProgressBar porgress = new JProgressBar();
		porgress.setVisible(false);
		stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dropPanel.add(stateLabel);
		dropPanel.add(porgress, BorderLayout.SOUTH);
		new FileDrop(dropPanel, new FileDrop.Listener() {

			boolean processing = false;

			@Override
			public void filesDropped(File[] files) {
				if (processing)
					return;
				processing = true;
				porgress.setVisible(true);
				porgress.setValue(0);
				porgress.setMaximum(1);
				new Thread() {

					@Override
					public void run() {
						for (File file : files) {
							stateLabel.setText("processing " + file.getName());
							convert(file, new ProgressListener() {

								@Override
								public void onProgress(int current, int max) {
									porgress.setMaximum(max);
									porgress.setValue(current);
								}
							});
						}
						porgress.setVisible(false);
						stateLabel.setText("drop files");
						processing = false;
					}
				}.start();
			}
		});

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setPreferredSize(new Dimension(500, 500));
		frame.add(dropPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static class ImageByteInputStream extends InputStream {

		private final BufferedImage image;
		private final int width;
		private final int height;
		private int currentX = 0;
		private int currentY = 0;
		private int currentColorIndex = 0;

		public ImageByteInputStream(BufferedImage image) {
			this.image = image;
			width = image.getWidth(null);
			height = image.getHeight(null);
		}

		@Override
		public long skip(long byteCount) {
			return skip((int) byteCount);
		}

		public int skip(int byteCount) {
			int remaining = byteCount;
			while (remaining > 0) {
				if (currentColorIndex + 1 < BYTES_PER_PIXEL) {
					currentColorIndex++;
				} else {
					if (currentX + 1 < width) {
						currentColorIndex = 0;
						currentX++;
					} else {
						if (currentY + 1 < height) {
							currentColorIndex = 0;
							currentX = 0;
							currentY++;
						} else {
							return remaining;
						}
					}
				}
				remaining--;
			}
			return 0;
		}

		@SuppressWarnings("unused")
		public int getCurrentByteIndex() {
			return currentColorIndex + currentX * BYTES_PER_PIXEL + currentY * width * BYTES_PER_PIXEL;
		}

		@Override
		public int read() throws IOException {
			if (currentY < height) {
				int color = image.getRGB(currentX, currentY);
				int index = currentColorIndex;

				skip(1);

				switch (index) {
				case 0:
					// read alpha
					return (color >> 24) & 0xff;
				case 1:
					// read red
					return (color >> 16) & 0xFF;
				case 2:
					// read green
					return (color >> 8) & 0xFF;
				case 3:
					// read blue
					return (color >> 0) & 0xFF;
				default:
					// should not happen
					throw new IllegalStateException();
				}
			} else {
				return -1;
			}
		}
	}
	
	
	private static byte[] readBytes(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		byte[] result = new byte[(int) file.length()];
		in.read(result);
		in.close();
		return result;
	}
	
	private static void writeBytes(File file, byte[] bytes) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(bytes);
		out.close();
	}
	

	// *******************************************************************
	// *******************************************************************
	// ************* Copied classes to make this independent *************
	// *******************************************************************
	// *******************************************************************

	public static interface ProgressListener {
		public void onProgress(int current, int max);
	}

	public static class FileDrop {
		private transient javax.swing.border.Border normalBorder;
		private transient java.awt.dnd.DropTargetListener dropListener;

		private static Boolean supportsDnD;

		// Default border color
		private static java.awt.Color defaultBorderColor = new java.awt.Color(0f, 0f, 1f, 0.25f);

		public FileDrop(final java.awt.Component c, final Listener listener) {
			this(null, // Logging stream
					c, // Drop target
					javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag
																									// border
					true, // Recursive
					listener);
		} // end constructor

		public FileDrop(final java.awt.Component c, final boolean recursive, final Listener listener) {
			this(null, // Logging stream
					c, // Drop target
					javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag
																									// border
					recursive, // Recursive
					listener);
		} // end constructor

		public FileDrop(final java.io.PrintStream out, final java.awt.Component c, final Listener listener) {
			this(out, // Logging stream
					c, // Drop target
					javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), false, // Recursive
					listener);
		} // end constructor

		public FileDrop(final java.io.PrintStream out, final java.awt.Component c, final boolean recursive, final Listener listener) {
			this(out, // Logging stream
					c, // Drop target
					javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag
																									// border
					recursive, // Recursive
					listener);
		} // end constructor

		public FileDrop(final java.awt.Component c, final javax.swing.border.Border dragBorder, final Listener listener) {
			this(null, // Logging stream
					c, // Drop target
					dragBorder, // Drag border
					false, // Recursive
					listener);
		} // end constructor

		public FileDrop(final java.awt.Component c, final javax.swing.border.Border dragBorder, final boolean recursive, final Listener listener) {
			this(null, c, dragBorder, recursive, listener);
		} // end constructor

		public FileDrop(final java.io.PrintStream out, final java.awt.Component c, final javax.swing.border.Border dragBorder, final Listener listener) {
			this(out, // Logging stream
					c, // Drop target
					dragBorder, // Drag border
					false, // Recursive
					listener);
		} // end constructor

		public FileDrop(final java.io.PrintStream out, final java.awt.Component c, final javax.swing.border.Border dragBorder, final boolean recursive, final Listener listener) {

			if (supportsDnD()) { // Make a drop listener
				dropListener = new java.awt.dnd.DropTargetListener() {
					public void dragEnter(java.awt.dnd.DropTargetDragEvent evt) {
						log(out, "FileDrop: dragEnter event.");

						// Is this an acceptable drag event?
						if (isDragOk(out, evt)) {
							// If it's a Swing component, set its border
							if (c instanceof javax.swing.JComponent) {
								javax.swing.JComponent jc = (javax.swing.JComponent) c;
								normalBorder = jc.getBorder();
								log(out, "FileDrop: normal border saved.");
								jc.setBorder(dragBorder);
								log(out, "FileDrop: drag border set.");
							} // end if: JComponent

							// Acknowledge that it's okay to enter
							// evt.acceptDrag(
							// java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
							evt.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
							log(out, "FileDrop: event accepted.");
						} // end if: drag ok
						else { // Reject the drag event
							evt.rejectDrag();
							log(out, "FileDrop: event rejected.");
						} // end else: drag not ok
					} // end dragEnter

					public void dragOver(java.awt.dnd.DropTargetDragEvent evt) { // This
																					// is
																					// called
																					// continually
																					// as
																					// long
																					// as
																					// the
																					// mouse
																					// is
																					// over
																					// the
																					// drag
																					// target.
					} // end dragOver

					@SuppressWarnings("unchecked")
					public void drop(java.awt.dnd.DropTargetDropEvent evt) {
						log(out, "FileDrop: drop event.");
						try { // Get whatever was dropped
							java.awt.datatransfer.Transferable tr = evt.getTransferable();

							// Is it a file list?
							if (tr.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
								// Say we'll take it.
								// evt.acceptDrop (
								// java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE
								// );
								evt.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
								log(out, "FileDrop: file list accepted.");

								// Get a useful list
								java.util.List<File> fileList = (java.util.List<File>) tr.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
								fileList.iterator();

								// Convert list to array
								java.io.File[] filesTemp = new java.io.File[fileList.size()];
								fileList.toArray(filesTemp);
								final java.io.File[] files = filesTemp;

								// Alert listener to drop.
								if (listener != null)
									listener.filesDropped(files);

								// Mark that drop is completed.
								evt.getDropTargetContext().dropComplete(true);
								log(out, "FileDrop: drop complete.");
							} // end if: file list
							else // this section will check for a reader flavor.
							{
								// Thanks, Nathan!
								// BEGIN 2007-09-12 Nathan Blomquist -- Linux
								// (KDE/Gnome) support added.
								DataFlavor[] flavors = tr.getTransferDataFlavors();
								boolean handled = false;
								for (int zz = 0; zz < flavors.length; zz++) {
									if (flavors[zz].isRepresentationClassReader()) {
										// Say we'll take it.
										// evt.acceptDrop (
										// java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE
										// );
										evt.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
										log(out, "FileDrop: reader accepted.");

										Reader reader = flavors[zz].getReaderForText(tr);

										BufferedReader br = new BufferedReader(reader);

										if (listener != null)
											listener.filesDropped(createFileArray(br, out));

										// Mark that drop is completed.
										evt.getDropTargetContext().dropComplete(true);
										log(out, "FileDrop: drop complete.");
										handled = true;
										break;
									}
								}
								if (!handled) {
									log(out, "FileDrop: not a file list or reader - abort.");
									evt.rejectDrop();
								}
								// END 2007-09-12 Nathan Blomquist -- Linux
								// (KDE/Gnome) support added.
							} // end else: not a file list
						} // end try
						catch (java.io.IOException io) {
							log(out, "FileDrop: IOException - abort:");
							io.printStackTrace(out);
							evt.rejectDrop();
						} // end catch IOException
						catch (java.awt.datatransfer.UnsupportedFlavorException ufe) {
							log(out, "FileDrop: UnsupportedFlavorException - abort:");
							ufe.printStackTrace(out);
							evt.rejectDrop();
						} // end catch: UnsupportedFlavorException
						finally {
							// If it's a Swing component, reset its border
							if (c instanceof javax.swing.JComponent) {
								javax.swing.JComponent jc = (javax.swing.JComponent) c;
								jc.setBorder(normalBorder);
								log(out, "FileDrop: normal border restored.");
							} // end if: JComponent
						} // end finally
					} // end drop

					public void dragExit(java.awt.dnd.DropTargetEvent evt) {
						log(out, "FileDrop: dragExit event.");
						// If it's a Swing component, reset its border
						if (c instanceof javax.swing.JComponent) {
							javax.swing.JComponent jc = (javax.swing.JComponent) c;
							jc.setBorder(normalBorder);
							log(out, "FileDrop: normal border restored.");
						} // end if: JComponent
					} // end dragExit

					public void dropActionChanged(java.awt.dnd.DropTargetDragEvent evt) {
						log(out, "FileDrop: dropActionChanged event.");
						// Is this an acceptable drag event?
						if (isDragOk(out, evt)) { // evt.acceptDrag(
													// java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE
													// );
							evt.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
							log(out, "FileDrop: event accepted.");
						} // end if: drag ok
						else {
							evt.rejectDrag();
							log(out, "FileDrop: event rejected.");
						} // end else: drag not ok
					} // end dropActionChanged
				}; // end DropTargetListener

				// Make the component (and possibly children) drop targets
				makeDropTarget(out, c, recursive);
			} // end if: supports dnd
			else {
				log(out, "FileDrop: Drag and drop is not supported with this JVM");
			} // end else: does not support DnD
		} // end constructor

		private static boolean supportsDnD() { // Static Boolean
			if (supportsDnD == null) {
				boolean support = false;
				try {
					Class.forName("java.awt.dnd.DnDConstants");
					support = true;
				} // end try
				catch (Exception e) {
					support = false;
				} // end catch
				supportsDnD = new Boolean(support);
			} // end if: first time through
			return supportsDnD.booleanValue();
		} // end supportsDnD

		// BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
		private static String ZERO_CHAR_STRING = "" + (char) 0;

		private static File[] createFileArray(BufferedReader bReader, PrintStream out) {
			try {
				java.util.List<File> list = new java.util.ArrayList<File>();
				java.lang.String line = null;
				while ((line = bReader.readLine()) != null) {
					try {
						// kde seems to append a 0 char to the end of the reader
						if (ZERO_CHAR_STRING.equals(line))
							continue;

						java.io.File file = new java.io.File(new java.net.URI(line));
						list.add(file);
					} catch (Exception ex) {
						log(out, "Error with " + line + ": " + ex.getMessage());
					}
				}

				return (java.io.File[]) list.toArray(new File[list.size()]);
			} catch (IOException ex) {
				log(out, "FileDrop: IOException");
			}
			return new File[0];
		}
		// END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.

		private void makeDropTarget(final java.io.PrintStream out, final java.awt.Component c, boolean recursive) {
			// Make drop target
			final java.awt.dnd.DropTarget dt = new java.awt.dnd.DropTarget();
			try {
				dt.addDropTargetListener(dropListener);
			} // end try
			catch (java.util.TooManyListenersException e) {
				e.printStackTrace();
				log(out, "FileDrop: Drop will not work due to previous error. Do you have another listener attached?");
			} // end catch

			// Listen for hierarchy changes and remove the drop target when the
			// parent gets cleared out.
			c.addHierarchyListener(new java.awt.event.HierarchyListener() {
				public void hierarchyChanged(java.awt.event.HierarchyEvent evt) {
					log(out, "FileDrop: Hierarchy changed.");
					java.awt.Component parent = c.getParent();
					if (parent == null) {
						c.setDropTarget(null);
						log(out, "FileDrop: Drop target cleared from component.");
					} // end if: null parent
					else {
						new java.awt.dnd.DropTarget(c, dropListener);
						log(out, "FileDrop: Drop target added to component.");
					} // end else: parent not null
				} // end hierarchyChanged
			}); // end hierarchy listener
			if (c.getParent() != null)
				new java.awt.dnd.DropTarget(c, dropListener);

			if (recursive && (c instanceof java.awt.Container)) {
				// Get the container
				java.awt.Container cont = (java.awt.Container) c;

				// Get it's components
				java.awt.Component[] comps = cont.getComponents();

				// Set it's components as listeners also
				for (int i = 0; i < comps.length; i++)
					makeDropTarget(out, comps[i], recursive);
			} // end if: recursively set components as listener
		} // end dropListener

		/** Determine if the dragged data is a file list. */
		private boolean isDragOk(final java.io.PrintStream out, final java.awt.dnd.DropTargetDragEvent evt) {
			boolean ok = false;

			// Get data flavors being dragged
			java.awt.datatransfer.DataFlavor[] flavors = evt.getCurrentDataFlavors();

			// See if any of the flavors are a file list
			int i = 0;
			while (!ok && i < flavors.length) {
				// BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome)
				// support added.
				// Is the flavor a file list?
				final DataFlavor curFlavor = flavors[i];
				if (curFlavor.equals(java.awt.datatransfer.DataFlavor.javaFileListFlavor) || curFlavor.isRepresentationClassReader()) {
					ok = true;
				}
				// END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support
				// added.
				i++;
			} // end while: through flavors

			// If logging is enabled, show data flavors
			if (out != null) {
				if (flavors.length == 0)
					log(out, "FileDrop: no data flavors.");
				for (i = 0; i < flavors.length; i++)
					log(out, flavors[i].toString());
			} // end if: logging enabled

			return ok;
		} // end isDragOk

		private static void log(java.io.PrintStream out, String message) { // Log
																			// message
																			// if
																			// requested
			if (out != null)
				out.println(message);
		} // end log

		public static boolean remove(java.awt.Component c) {
			return remove(null, c, true);
		} // end remove

		public static boolean remove(java.io.PrintStream out, java.awt.Component c, boolean recursive) { // Make
																											// sure
																											// we
																											// support
																											// dnd.
			if (supportsDnD()) {
				log(out, "FileDrop: Removing drag-and-drop hooks.");
				c.setDropTarget(null);
				if (recursive && (c instanceof java.awt.Container)) {
					java.awt.Component[] comps = ((java.awt.Container) c).getComponents();
					for (int i = 0; i < comps.length; i++)
						remove(out, comps[i], recursive);
					return true;
				} // end if: recursive
				else
					return false;
			} // end if: supports DnD
			else
				return false;
		} // end remove

		/* ******** I N N E R I N T E R F A C E L I S T E N E R ******** */

		public static interface Listener {

			public abstract void filesDropped(java.io.File[] files);

		} // end inner-interface Listener

		/* ******** I N N E R C L A S S ******** */

		public static class Event extends java.util.EventObject {
			private static final long serialVersionUID = 1L;

			private java.io.File[] files;

			public Event(java.io.File[] files, Object source) {
				super(source);
				this.files = files;
			} // end constructor

			public java.io.File[] getFiles() {
				return files;
			} // end getFiles

		} // end inner class Event

		/* ******** I N N E R C L A S S ******** */

		public static class TransferableObject implements java.awt.datatransfer.Transferable {

			public final static String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";

			public final static java.awt.datatransfer.DataFlavor DATA_FLAVOR = new java.awt.datatransfer.DataFlavor(FileDrop.TransferableObject.class, MIME_TYPE);

			private Fetcher fetcher;
			private Object data;

			private java.awt.datatransfer.DataFlavor customFlavor;

			public TransferableObject(Object data) {
				this.data = data;
				this.customFlavor = new java.awt.datatransfer.DataFlavor(data.getClass(), MIME_TYPE);
			} // end constructor

			public TransferableObject(Fetcher fetcher) {
				this.fetcher = fetcher;
			} // end constructor

			public TransferableObject(Class<?> dataClass, Fetcher fetcher) {
				this.fetcher = fetcher;
				this.customFlavor = new java.awt.datatransfer.DataFlavor(dataClass, MIME_TYPE);
			} // end constructor

			public java.awt.datatransfer.DataFlavor getCustomDataFlavor() {
				return customFlavor;
			} // end getCustomDataFlavor

			/* ******** T R A N S F E R A B L E M E T H O D S ******** */

			public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
				if (customFlavor != null)
					return new java.awt.datatransfer.DataFlavor[] { customFlavor, DATA_FLAVOR, java.awt.datatransfer.DataFlavor.stringFlavor }; // end
																																				// flavors
																																				// array
				else
					return new java.awt.datatransfer.DataFlavor[] { DATA_FLAVOR, java.awt.datatransfer.DataFlavor.stringFlavor }; // end
																																	// flavors
																																	// array
			} // end getTransferDataFlavors

			public Object getTransferData(java.awt.datatransfer.DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
				// Native object
				if (flavor.equals(DATA_FLAVOR))
					return fetcher == null ? data : fetcher.getObject();

				// String
				if (flavor.equals(java.awt.datatransfer.DataFlavor.stringFlavor))
					return fetcher == null ? data.toString() : fetcher.getObject().toString();

				// We can't do anything else
				throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
			} // end getTransferData

			public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor flavor) {
				// Native object
				if (flavor.equals(DATA_FLAVOR))
					return true;

				// String
				if (flavor.equals(java.awt.datatransfer.DataFlavor.stringFlavor))
					return true;

				// We can't do anything else
				return false;
			} // end isDataFlavorSupported

			/* ******** I N N E R I N T E R F A C E F E T C H E R ******** */

			public static interface Fetcher {

				public abstract Object getObject();
			} // end inner interface Fetcher

		} // end class TransferableObject

	} // end class FileDrop

}

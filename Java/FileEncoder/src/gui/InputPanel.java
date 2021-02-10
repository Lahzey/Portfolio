package gui;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import graphics.swing.FileDrop;
import graphics.swing.FileDrop.Listener;
import graphics.swing.JAnimationPanel;
import graphics.swing.JImage;
import graphics.swing.NestedCheckBox;
import graphics.swing.TextProgressBar;
import util.ColorUtil;
import util.StringFormatter;
import util.StringUtil;

import logic.Engine;
import logic.Engine.Options;
import logic.Engine.ProgressCallback;
import net.miginfocom.swing.MigLayout;


public class InputPanel extends JPanel implements Listener {
	
	private List<File> files = null;
	
	private JImage settingsButton;
	private JPanel dropPanel;
	private JButton encodeButton;
	private TextProgressBar progressBar;

	private JAnimationPanel settingsPanel;
	private NestedCheckBox<JTextField> maxFileSizeCheckbox;
	private JTextField maxFileSizeInput;

	
	public InputPanel(){
		setLayout(new OverlayLayout(this));
		setBackground(ColorUtil.INFO_BACKGROUND_COLOR);
		
		JPanel content = new JPanel(new MigLayout("wrap 1, hidemode 3", "[grow, fill]", "[grow, fill][][]"));
		content.setOpaque(false);
		JPanel settingsOverlay = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", "[][grow, fill]"));
		settingsOverlay.setOpaque(false);
		
		add(settingsOverlay);
		add(content);
		
		// content
		dropPanel = new JPanel(new MigLayout("fill, center center, insets 0", "[center]", ""));
		dropPanel.setOpaque(false);
		new FileDrop(dropPanel, this);
		content.add(dropPanel);
		
		encodeButton = new JButton("Encode / Decode");
		encodeButton.setOpaque(false);
		encodeButton.addActionListener(e -> submit());
		content.add(encodeButton);
		
		progressBar = new TextProgressBar("Waiting for input...");
		progressBar.setOpaque(false);
		progressBar.setVisible(false);
		content.add(progressBar);
		

		
		// settings overlay
		JPanel container = new JPanel(new MigLayout("wrap 1, hidemode 2", "[grow]", "[][][grow, fill]"));
		container.setBackground(ColorUtil.mix(ColorUtil.INFO_BACKGROUND_COLOR, UIManager.getColor("Panel.background")));
		container.setOpaque(false);
		settingsOverlay.add(container, "right");
		JPanel filler = new JPanel();
		filler.setOpaque(false);
		settingsOverlay.add(filler);
		
		settingsButton = new JImage(FontAwesomeSolid.COG, Color.DARK_GRAY);
		container.add(settingsButton, "right");
		settingsButton.generateStateImages();
		
		settingsPanel = new JAnimationPanel(new MigLayout("wrap 1, insets 0", "[grow, fill]"));
		maxFileSizeInput = new JTextField("9 MB");
		maxFileSizeCheckbox = new NestedCheckBox<>("Max File Size", true, maxFileSizeInput);
		maxFileSizeCheckbox.setOpaque(false);
		maxFileSizeCheckbox.setToolTipText("<html>Splits the input into files which do not exceed the given size (if necessary).<br/>"
											+ "The size may be declared in <b>B</b> (Byte), <b>KB</b> (Kilobyte), <b>MB</b> (Megabyte) or <b>GB</b> (Gigabyte).<br/>"
											+ "<i>Examples: 10 MB | 5.5 GB | 10B | 70.5MB</i></html>");
		settingsPanel.add(maxFileSizeCheckbox);
		settingsPanel.setOpaque(false);
		settingsPanel.setVisible(false);
		container.add(settingsPanel, "grow");
		
		

		settingsButton.addActionListener(e -> {
			boolean visible = !settingsPanel.isVisible();
			if(visible){
				settingsPanel.open(200, JAnimationPanel.HORIZONTAL + JAnimationPanel.VERTICAL);
				
				container.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, ColorUtil.mix(ColorUtil.INFO_BACKGROUND_COLOR, Color.DARK_GRAY)));
				container.setOpaque(true);
				settingsButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				settingsButton.setIcon(FontAwesomeSolid.COG, ColorUtil.INFO_FOREGROUND_COLOR);
				settingsButton.generateStateImages();
			}else{
				settingsPanel.close(200, JAnimationPanel.HORIZONTAL + JAnimationPanel.VERTICAL).then(() -> {
					container.setBorder(null);
					container.setOpaque(false);
					settingsButton.setBorder(null);
					settingsButton.setIcon(FontAwesomeSolid.COG, Color.DARK_GRAY);
					settingsButton.generateStateImages();
				});
			}
		});
		
		
		// initialize for 0 files
		setFiles(null);
	}
	
	private void setFiles(List<File> files){
		dropPanel.removeAll();
		if(files == null || files.isEmpty()){
			JLabel dropLabel = new JLabel("Drop Files here");
			dropLabel.setForeground(ColorUtil.INFO_FOREGROUND_COLOR);
			dropPanel.add(dropLabel);
			encodeButton.setEnabled(false);
			this.files = null;
		}else{
			boolean encode = false;
			boolean decode = false;
			if(files.size() > 1){
				JLabel title = new JLabel(files.size() + " Files dropped:");
				title.setFont(title.getFont().deriveFont(Font.BOLD));
				title.setForeground(ColorUtil.INFO_FOREGROUND_COLOR);
				JPanel filesPanel = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", ""));
				filesPanel.setOpaque(false);
				filesPanel.add(title);
				for(File file : files){
					if(Engine.isEncoded(file)) decode = true;
					else encode = true;
					
					JLabel fileLabel = new JLabel(file.getName());
					filesPanel.add(fileLabel);
				}
				JScrollPane scroll = new JScrollPane(filesPanel);
				scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scroll.setOpaque(false);
				scroll.getViewport().setOpaque(false);
				scroll.setBorder(null);
				scroll.setViewportBorder(null);
				dropPanel.add(scroll);
				
				if(encode && decode){
					encodeButton.setText("Encode / Decode");
				}else{
					encodeButton.setText(decode ? "Decode" : "Encode");
				}
			}else{
				dropPanel.add(new JLabel(files.get(0).getName()));
				
				encodeButton.setText(Engine.isEncoded(files.get(0)) ? "Decode" : "Encode");
			}
			encodeButton.setEnabled(true);
			this.files = files;
		}
		dropPanel.revalidate();
		dropPanel.repaint();
	}


	@Override
	public void filesDropped(File[] files) {
		List<File> fileList = new ArrayList<>();
		for(File file : files){
			if(file.isFile()){
				fileList.add(file);
			}
		}
		setFiles(fileList);
	}
	
	private void submit(){
		if(files == null){
			return;
		}
		
		Options options = new Options();
		if(maxFileSizeCheckbox.isSelected()){
			options.maxFileSize = StringUtil.toBytes(maxFileSizeInput.getText().replace(" ", ""));
			if(options.maxFileSize <= 0){
				maxFileSizeInput.setForeground(ColorUtil.ERROR_FOREGROUND_COLOR);
				return;
			}
		}
		maxFileSizeInput.setForeground(Color.BLACK);
		
		progressBar.setText("Initializing...");
		progressBar.setValue(0);
		progressBar.setMaximum(0);
		progressBar.setColor(null);
		progressBar.setVisible(true);
		encodeButton.setEnabled(false);
		revalidate();
		repaint();
		new Thread() {
			
			@Override
			public void run() {
				Engine.process(files, options, new ProgressCallback() {
					
					@Override
					public void onProgress(long currentByteCount, long totalByteCount) {
						SwingUtilities.invokeLater(() -> {
							progressBar.setValue(currentByteCount);
							progressBar.setMaximum(totalByteCount);
							if(currentByteCount == 0){
								progressBar.setText("Calculating...   (" + StringFormatter.formatByteCount(totalByteCount) + ")");
							}else{
								progressBar.setText(StringFormatter.formatByteCount(currentByteCount) + " / " + StringFormatter.formatByteCount(totalByteCount));
							}
						});
					}
					
					@Override
					public void onError(File file, Exception error) {
						progressBar.setColor(ColorUtil.WARNING_FOREGROUND_COLOR);
					}
					
					@Override
					public void onCancel() {
						progressBar.setColor(ColorUtil.ERROR_FOREGROUND_COLOR);
						progressBar.setText("Action Cancelled");
						progressBar.setMaximum(1);
						progressBar.setValue(1);
					}
				});

				SwingUtilities.invokeLater(() -> {
					encodeButton.setEnabled(true);
					repaint();
				});
			}
		}.start();
	}
}

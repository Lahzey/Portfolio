package graphics.swing.components;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class FileTextField extends JTextField{
	
	private static final long serialVersionUID = 1L;

	private FileFilter fileFilter;
	
	private File[] possibilites = File.listRoots();
	private File parent = null;
	private String lastInput = "";

	public FileTextField() {
		super();
		init();
	}

	public FileTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		init();
	}

	public FileTextField(int columns) {
		super(columns);
		init();
	}

	public FileTextField(String text, int columns) {
		super(text, columns);
		init();
	}

	public FileTextField(String text) {
		super(text);
		init();
	}
	
	public void setFileFilter(FileFilter filter){
		if(filter == null) filter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return true;
			}
		};
		
		fileFilter = filter;
	}
	
	public FileFilter getFileFilter(){
		return fileFilter;
	}
	
	public File getFile(){
		return new File(getText());
	}
	
	public void setFile(File file){
		setText(file != null ? file.getAbsolutePath() : "");
	}
	
	private void init(){
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
			
			private void update(){
				Runnable doUpdate = new Runnable(){
					public void run(){
						String input = getText();
						input = input.replace('/', File.separatorChar);
						input = input.replace('\\', File.separatorChar);
						if(input.equals(lastInput)) return;
						File inputFile = new File(getText());
						File inputParent;
						if(input.endsWith(File.separator)){
							inputParent = inputFile;
						}else{
							inputParent = inputFile.getParentFile();
						}
						String absoluteInput = inputFile.getAbsolutePath();
						if(!inputFile.isAbsolute()){
							inputParent = null;
							absoluteInput = input;
						}
						
						if(inputParent != parent){
							parent = inputParent;
							if(parent == null){
								possibilites = File.listRoots();
							}else{
								possibilites = parent.listFiles();
							}
						}
						
						if(possibilites == null) return;
						for(File possibility : possibilites){
							if(possibility.getAbsolutePath().startsWith(absoluteInput) && (fileFilter == null || fileFilter.accept(possibility))){
								String addition = possibility.getAbsolutePath().substring(absoluteInput.length());
								if(possibility.isDirectory() && !addition.endsWith(File.separator)) addition += File.separator;
								String totalText = input + addition;
								setText(totalText);
								lastInput = totalText;
								select(input.length(), totalText.length());
								break;
							}
						}
					}
				};
				SwingUtilities.invokeLater(doUpdate);
			}
		});

        
        setFocusTraversalKeysEnabled(false); //So when pressing tab, it deselects text instead of tabbing out
		
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					moveCaretPosition(getSelectionEnd());
					setSelectionStart(getSelectionEnd());
				}else if(e.getKeyCode() == KeyEvent.VK_TAB){
					int selectionEnd = getSelectionEnd();
					if(selectionEnd - getSelectionStart() > 0){
						setCaretPosition(selectionEnd);
					}else{
						tabForward();
					}
				}
			}
			
			private void tabForward() {
	            final KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
	            manager.focusNextComponent();

	            SwingUtilities.invokeLater(new Runnable()
	            {
	                public void run()
	                {
	                    if (manager.getFocusOwner() instanceof JScrollBar)
	                        manager.focusNextComponent();
	                }
	            });
	        }
		});
	}
}

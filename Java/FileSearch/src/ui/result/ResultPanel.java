package ui.result;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.components.JImage;
import graphics.swing.components.ScaledMenuItem;
import util.ColorUtil;
import util.Selectable;
import util.zip.ZipEntryFile;

import net.miginfocom.swing.MigLayout;
import search.data.SearchResult.Match;
import ui.Images;	

class ResultPanel extends JPanel implements Selectable{
	private static final long serialVersionUID = 1L;
	private static final String NOTEPAD_PLUS_PLUS_PATH = "C:\\Apps\\NOTEPAD++\\notepad++.exe";
	
	public static Color NORMAL_COLOR = ColorUtil.changeBrightness(UIManager.getColor("Panel.background"), 0.9f);
	public static Color HOVER_COLOR = new Color(181, 209, 255);
	public static Color SELECTED_COLOR = new Color(132, 178, 255);
	
	ResultList resultList;
	private boolean contentResults;
	
	private File file;
	private String filename;
	private String filepath;
	
	private JImage expandButton = new JImage(FontIcon.of(FontAwesomeSolid.CARET_RIGHT).toImage());
	private JLabel nameLabel = new JLabel();
	private JLabel pathLabel = new JLabel();
	private JPanel matches = new JPanel(new MigLayout("gapy 2, wrap 1", "[grow, fill]", ""));
	private List<MatchPanel> matchPanels = Collections.synchronizedList(new ArrayList<>());
	
	private boolean expanded = false;
	private boolean selected = false;
	private boolean hovered = false;
	
	ResultPanel(File file, ResultList resultList){
		super(new MigLayout("hidemode 3, gapy 2", "[]10px[]10px[grow, fill]", ""));
		this.resultList = resultList;
		contentResults = !resultList.getResults().search.containingText.isEmpty();
		
		this.file = file;
		filename = file.getName();
		filepath = file.getAbsolutePath();
		
		expandButton.setVisible(contentResults);
		nameLabel.setText(filename);
		nameLabel.setToolTipText(file.getAbsolutePath());
		pathLabel.setText(filepath);
		pathLabel.setForeground(Color.DARK_GRAY);
		
		setComponentPopupMenu(createPopupMenu(0, false));
		
		add(expandButton);
		add(nameLabel);
		add(pathLabel, "gapleft 10px, growx, wrap");

		matches.setOpaque(false);
		matches.setVisible(false);
		add(matches, "growx, span 3");
		
		expandButton.addActionListener(e -> setExpanded(!expanded));
		
		MouseListener ml = new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent e) {
				hovered = false;
				setColors();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				hovered = true;
				setColors();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				resultList.select(ResultPanel.this);
				if(e.getClickCount() == 2){
					setExpanded(!expanded);
				}
			}
		};
		addMouseListener(ml);
		expandButton.addMouseListener(ml);
		nameLabel.addMouseListener(ml);
		pathLabel.addMouseListener(ml);
		
		setColors();
	}
	
	public void addMatch(int matchIndex){
		if(contentResults){
			Match match = resultList.getResults().matches.get(file).get(matchIndex);
			MatchPanel matchPanel = new MatchPanel(match, this);
			matches.add(matchPanel);
			matchPanels.add(matchPanel);
		}
	}
	
	public boolean filter(String filter){
		if(filepath.contains(filter)){
			for(MatchPanel matchPanel : matchPanels){
				matchPanel.filter("");
				if(matchPanel.getParent() == null) matches.add(matchPanel);
			}
			return true;
		}else{
			boolean hasResults = false;
			for(MatchPanel matchPanel : matchPanels){
				if(matchPanel.filter(filter)){
					hasResults = true;
				}else{
					matches.remove(matchPanel);
				}
			}
			if(hasResults) setExpanded(true);
			return hasResults;
		}
	}
	
	JPopupMenu createPopupMenu(int lineNr, boolean openIsFat){
		JPopupMenu menu = new JPopupMenu();
		
		boolean isZipEntry = file instanceof ZipEntryFile;
		ZipEntryFile zipEntryFile = isZipEntry ? (ZipEntryFile) file : null;

		if(hasNotepadPlusPlus()){
			String text = openIsFat ? "<html><p><b>open with Notepad++</b>...</p></html>" : "open with Notepad++...";
			ScaledMenuItem openItem = new ScaledMenuItem(text, Images.NOTEPAD_PLUS_PLUS_IMAGE);
			openItem.addActionListener(e -> openNppFile(isZipEntry ? zipEntryFile.getTempFileSilent(true) : file, lineNr));
			menu.add(openItem);
		}else{
			String text = openIsFat ? "<html><p><b>open</b>...</p></html>" : "open...";
			ScaledMenuItem openItem = new ScaledMenuItem(text, Images.FILE_IMAGE);
			openItem.addActionListener(e -> openFile(isZipEntry ? zipEntryFile.getTempFileSilent(true) : file));
			menu.add(openItem);
		}
		
		ScaledMenuItem showItem = new ScaledMenuItem(isZipEntry ? "show containing zip..." : "show...", Images.EXPLORER_IMAGE);
		showItem.addActionListener(e -> showFile(isZipEntry ? zipEntryFile.getRoot() : file));
		menu.add(showItem);
		
		return menu;
	}
	
	public static void openNppFile(File file, int lineNr){
		try{
			Runtime.getRuntime().exec(new String[]{NOTEPAD_PLUS_PLUS_PATH, file.getAbsolutePath(), "-n" + lineNr});
		}catch(IOException e){
			System.err.println("Failed to open " + file.getName() + " with notepad++, trying default editor:");
			e.printStackTrace();
			openFile(file);
		}
	}
	
	public static void openFile(File file){
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean hasNotepadPlusPlus(){
		return new File(NOTEPAD_PLUS_PLUS_PATH).exists();
	}
	
	private void showFile(File file){
		try {
			Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setExpanded(boolean expanded){
		this.expanded = expanded;
		matches.setVisible(expanded);
		expandButton.setImage(expanded ? FontIcon.of(FontAwesomeSolid.CARET_DOWN).toImage() : FontIcon.of(FontAwesomeSolid.CARET_RIGHT).toImage());
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
		setColors();
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}
	
	private void setColors(){
		if(selected){
			setBackground(hovered ? HOVER_COLOR : NORMAL_COLOR);
			setBorderColor(SELECTED_COLOR);
		}else if(hovered){
			setBackground(HOVER_COLOR);
			setBorderColor(HOVER_COLOR);
		}else{
			setBackground(NORMAL_COLOR);
			setBorderColor(NORMAL_COLOR);
		}
	}
	
	private void setBorderColor(Color color){
		setBorder(BorderFactory.createMatteBorder(2, 4, 2, 2, ColorUtil.changeBrightness(color, 0.9f)));
	}
	
	@Override
	public void setBackground(Color color){
		super.setBackground(color);
	}

	@Override
	public Transferable getTransferableContent() {
		return new StringSelection(filepath);
	}
	
}

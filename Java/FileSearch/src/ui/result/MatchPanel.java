package ui.result;

import java.awt.Color;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.ColorUtil;
import util.Selectable;
import util.zip.ZipEntryFile;

import net.miginfocom.swing.MigLayout;
import search.data.SearchResult.Match;

class MatchPanel extends JPanel implements Selectable{
	private static final long serialVersionUID = 1L;

	private Match match;
	
	private JLabel lineNrLabel;
	private JLabel matchLabel;
	
	private boolean selected;
	private boolean hovered;
	
	MatchPanel(Match match, ResultPanel resultPanel){
		super(new MigLayout("insets 10 0 10 0", "[][grow, fill]", ""));
		this.match = match;
		lineNrLabel = new JLabel(match.getLineNumber() + ":");
		lineNrLabel.setForeground(Color.GRAY);
		add(lineNrLabel);
		matchLabel = new JLabel(match.getFormattedMatchingLine());
		matchLabel.setComponentPopupMenu(resultPanel.createPopupMenu(match.getLineNumber(), true));
		add(matchLabel);
		
		boolean isZipEntry = match.file instanceof ZipEntryFile;
		ZipEntryFile zipEntryFile = isZipEntry ? (ZipEntryFile) match.file : null;
		
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
				resultPanel.resultList.select(MatchPanel.this);
				if(e.getClickCount() == 2){
					if(ResultPanel.hasNotepadPlusPlus()){
						ResultPanel.openNppFile(isZipEntry ? zipEntryFile.getTempFileSilent(true) : match.file, match.getLineNumber());
					}else{
						ResultPanel.openFile(isZipEntry ? zipEntryFile.getTempFileSilent(true) : match.file);
					}
				}
			}
		};
		addMouseListener(ml);
		lineNrLabel.addMouseListener(ml);
		matchLabel.addMouseListener(ml);
		
		setColors();
	}
	
	public Match getMatch(){
		return match;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		setColors();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	
	public boolean filter(String filter){
		if(match.getMatchingLine().contains(filter)){
			matchLabel.setText(match.getFilteredFormattedMatchingLine(filter));
			return true;
		}else return false;
	}
	
	private void setColors(){
		if(selected){
			setBackground(ResultPanel.SELECTED_COLOR);
			setBorderColor(ResultPanel.SELECTED_COLOR);
		}else if(hovered){
			setBackground(ResultPanel.HOVER_COLOR);
			setBorderColor(ResultPanel.HOVER_COLOR);
		}else{
			setBackground(null);
			setBorderColor(ResultPanel.NORMAL_COLOR);
		}
	}

	private void setBorderColor(Color color){
		setBorder(BorderFactory.createDashedBorder(ColorUtil.changeBrightness(color, 0.9f), 1, 3, 2, true));
	}
	
	@Override
	public void setBackground(Color bg){
		super.setBackground(bg);
		setOpaque(bg != null);
	}

	@Override
	public Transferable getTransferableContent() {
		return new StringSelection(match.getMatchingLine());
	}
}

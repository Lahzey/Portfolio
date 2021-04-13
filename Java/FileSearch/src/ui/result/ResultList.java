package ui.result;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.components.JImage;
import graphics.swing.components.SearchField;
import util.Selectable;

import net.miginfocom.swing.MigLayout;
import search.data.SearchResult;

public class ResultList extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private JImage expandAll = new JImage(FontIcon.of(FontAwesomeRegular.PLUS_SQUARE).toImage());
	private boolean allExpanded = false;
	private SearchField searchField = new SearchField();
	
	private SearchResult results;
	private final Map<File, ResultPanel> resultPanels = Collections.synchronizedMap(new HashMap<>());
	private JPanel resultContainer = new JPanel(new MigLayout("hidemode 3", "[grow, fill]", ""));
	private Selectable selection = null;
	
	public ResultList(SearchResult results){
		super(new MigLayout("", "[]10px[grow, fill]", ""));
		this.results = results;
		
		add(expandAll);
		add(searchField, "wrap");
		
		JScrollPane scroll = new JScrollPane(resultContainer);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		
		add(scroll, "span 2, wrap, grow, pushy");
		
		expandAll.addActionListener(e -> setAllExpanded(!allExpanded));
		
		searchField.addActionListener(e -> filter(searchField.getText()));
		searchField.addChangeListener(e -> filter(searchField.getText()));
		
		
		InputMap inputMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK ), "copy");
		getActionMap().put("copy", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(selection != null){
					Transferable transferable = selection.getTransferableContent();
					if(transferable != null) Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
				}
			}
		});
		
	}
	
	public void add(File file, int matchIndex){
		if(!resultPanels.containsKey(file)){
			ResultPanel panel = new ResultPanel(file, ResultList.this);
			resultPanels.put(file, panel);
			resultContainer.add(panel, "span 2, wrap, grow");
		}
		resultPanels.get(file).addMatch(matchIndex);
	}
	
	
	public void setAllExpanded(boolean expanded){
		allExpanded = expanded;
		expandAll.setImage(expanded ? FontIcon.of(FontAwesomeRegular.MINUS_SQUARE).toImage() : FontIcon.of(FontAwesomeRegular.PLUS_SQUARE).toImage());
		for(ResultPanel resultPanel : resultPanels.values()){
			resultPanel.setExpanded(expanded);
		}
	}
	
	public void select(Selectable selection){
		if(selection != this.selection){
			if(this.selection != null) this.selection.setSelected(false);
			if(selection != null) selection.setSelected(true);
			this.selection = selection;
			if(selection instanceof Component){
				((Component) selection).requestFocus();
			}
		}
	}
	
	public SearchResult getResults(){
		return results;
	}
	
	private void filter(String filter){
		for(ResultPanel resultPanel : resultPanels.values()){
			if(!resultPanel.filter(filter)) resultPanel.setVisible(false);
			else resultPanel.setVisible(true);
		}
	}

}

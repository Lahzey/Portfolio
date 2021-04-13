package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import graphics.swing.components.FileTextField;
import main.SearchConfig;
import net.miginfocom.swing.MigLayout;
import search.Index;
import search.SearchEngine;
import search.SearchEngine.SearchCallback;
import search.data.Search;
import ui.result.SearchResultDialog;

public class SearchInputPanel extends JPanel{

	//Statics
	private static final long serialVersionUID = 1L;
	
	private static final int MIN_WIDTH = 1000;
	
	private static final String NO_SCOPE = "Please select a scope";
	private static final String INVALID_SCOPE = "The given scope does no exist";
	private static final String NO_INDEX_AT_SCOPE = "The given scope is not indexed";
	private static final String INDEX_AT_SCOPE = "The given scope was indexed at ";
	
	private static final Color VALID_COLOR = new Color(22, 124, 0);
	private static final Color INVALID_COLOR = new Color(153, 0, 0);
	private static final Color NEUTRAL_COLOR = Color.DARK_GRAY;
	
	
	//UI elements
	private final JLabel containingLabel = new JLabel("Containing Text:");
	private final JLabel containingInfoLabel = new JLabel("(* = any string, ? = any character, \\ = escape for literals: * ? \\)");
	private final JTextField containingField = new JTextField();
	private final JCheckBox containingCase = new JCheckBox("Case sensitive");
	private final JCheckBox containingRegex = new JCheckBox("Regular expression");

	private final JLabel namePatternLabel = new JLabel("File name patterns:");
	private final JLabel namePatternInfoLabel = new JLabel("Patterns are separated by a comma (* = any string, ? = any character)");
	private final JTextField namePatternField = new JTextField();
	private final JCheckBox namePatternCase = new JCheckBox("Case sensitive");
	private final JCheckBox namePatternRegex = new JCheckBox("Regular expression");

	private final JLabel scopeLabel = new JLabel("Scope:");
	private final JLabel scopeInfoLabel = new JLabel(NO_SCOPE);
	private final FileTextField scopeField = new FileTextField();
	private final JButton scopeButton = new JButton("Choose...");
	private final JCheckBox storeIndex = new JCheckBox("Store index");
	private final JCheckBox searchArchives = new JCheckBox("Search archives");
	
	private final JButton searchButton = new JButton("Search");
	
	
	//Attributes
	private Search search;
	
	
	public SearchInputPanel(){
		this(null);
	}
	
	public SearchInputPanel(Search search){
		super(new MigLayout("wrap 2", "[grow, fill][]", ""));
		
		add(containingLabel, "span 2");
		add(containingField);
		add(containingCase);
		add(containingInfoLabel);
		add(containingRegex);
		
		add(new JSeparator(), "span 2");
		
		add(namePatternLabel, "span 2");
		add(namePatternField);
		add(namePatternCase);
		add(namePatternInfoLabel);
		add(namePatternRegex);
		
		add(new JSeparator(), "span 2");
		
		add(scopeLabel, "span 2");
		add(scopeField);
		add(scopeButton);
		add(scopeInfoLabel);
		add(storeIndex);
		add(searchArchives, "skip");
		
		add(new JSeparator(), "span 2, gapy 15px 0px");
		
		add(searchButton, "span 2");
		
		scopeField.setFileFilter(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		scopeField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				changed();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				changed();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) 	{
				changed();
			}
			
			private void changed(){
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						File location = new File(scopeField.getText());
						if(location.isDirectory()){
							Index index = new Index(location);
							long createdAt = index.getCreatedAt();
							if(createdAt > 0){
								scopeInfoLabel.setForeground(VALID_COLOR);
								scopeInfoLabel.setText(INDEX_AT_SCOPE + new Date(createdAt));
							}else{
								scopeInfoLabel.setForeground(NEUTRAL_COLOR);
								scopeInfoLabel.setText(NO_INDEX_AT_SCOPE);
							}
						}else{
							scopeInfoLabel.setForeground(INVALID_COLOR);
							scopeInfoLabel.setText(scopeField.getText().isEmpty() ? NO_SCOPE : INVALID_SCOPE);
						}
					}
				});
			}
		});
		
		scopeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(scopeField.getFile());
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int option = chooser.showOpenDialog(SearchInputPanel.this);
				if(option == JFileChooser.APPROVE_OPTION){
					scopeField.setFile(chooser.getSelectedFile());
				}
			}
		});
		
		searchButton.addActionListener(e -> search());
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
		    @Override
		    public void run(){
		        submitFields();
		    }
		});
		
		setSearch(search);
	}
	
	public void setSearch(Search search){
		if(search == null) search = new Search();
		this.search = search;
		
		containingField.setText(search.containingText);
		containingCase.setSelected(search.containingCase);
		containingRegex.setSelected(search.containingRegex);
		
		namePatternField.setText(search.namePattern);
		namePatternCase.setSelected(search.nameCase);
		namePatternRegex.setSelected(search.nameRegex);
		
		scopeField.setFile(search.scope);
		storeIndex.setSelected(search.storeIndex);
		searchArchives.setSelected(search.searchArchives);
	}
	
	private void submitFields(){
		search.containingText = containingField.getText();
		search.containingCase = containingCase.isSelected();
		search.containingRegex = containingRegex.isSelected();
		
		search.namePattern = namePatternField.getText();
		search.nameCase = namePatternCase.isSelected();
		search.nameRegex = namePatternRegex.isSelected();
		
		search.scope = scopeField.getFile();
		search.storeIndex = storeIndex.isSelected();
		search.searchArchives = searchArchives.isSelected();
		
		SearchConfig.setSearch(search);
		SearchConfig.store();
	}
	
	
	private void search(){
		submitFields();
		SearchResultDialog resultDialog = new SearchResultDialog(search.result, this);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				resultDialog.setVisible(true);
				resultDialog.requestFocus();
			}
		});

		new Thread(){
			public void run(){
				Search currentSearch = search;
				search = new Search(search);
				SearchEngine.performSearch(currentSearch, new SearchCallback() {
					
					@Override
					public boolean prompt(String question) {
						int answer = JOptionPane.showConfirmDialog(resultDialog, question, "Search Prompt", JOptionPane.YES_NO_OPTION);
						return answer == JOptionPane.YES_OPTION;
					}

					@Override
					public void onFind(File file, int matchIndex) {
						resultDialog.add(file, matchIndex);
					}
					
					@Override
					public void onStatusChange(Status status) {
						resultDialog.setStatus(status);
					}

					@Override
					public void onProgressChange(int progress) {
						resultDialog.setProgress(progress);
					}

					@Override
					public void onMaxProgressChange(int maxProgress) {
						resultDialog.setMaxProgress(maxProgress);
					}
				});
			}
		}.start();
	}
	
	public Dimension getPreferredSize(){
		Dimension superSize = super.getPreferredSize();
		return new Dimension(Math.max(superSize.width, MIN_WIDTH), superSize.height);
	}
}

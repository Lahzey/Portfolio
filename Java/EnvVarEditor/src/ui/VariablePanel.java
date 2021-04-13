package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.colors.Backgrounds;
import graphics.swing.components.JImage;
import util.ColorUtil;

import logic.Registry;
import logic.RegistryException;
import logic.Variable;
import logic.Variable.VariableScope;
import net.miginfocom.swing.MigLayout;

public class VariablePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private Variable variable;
	private VariableScope scope;
	
	private final JLabel nameLabel = new JLabel("Name");
	private final JTextField nameField = new JTextField();
	
	private final JPanel valuesArea = new JPanel(new MigLayout("fill, hidemode 3", "[grow, fill]", "[][grow, fill]"));
	private final JPanel valuesContainer = new JPanel(new MigLayout("insets 0, wrap 2, hidemode 3", "[]5px[grow, fill]", ""));
	private final JScrollPane valuesScroll = new JScrollPane(valuesContainer);
	private final List<JTextField> valueFields = new ArrayList<>();
	private final Map<JTextField, JImage> deleteButtons = new HashMap<>();
	
	private String valueFilter = "";
	private final FilterDialog filterDialog = new FilterDialog(this);
	private final JLabel filterLabel = new JLabel();
	
	public VariablePanel(Variable var){
		super(new MigLayout("insets 10px 10px 0px 10px", "[][grow, fill]", "[][grow, fill]"));
		if(var == null) throw new IllegalArgumentException("Variable may not be null.");
		if(var.getScope() == null) throw new IllegalArgumentException("Scope of the given variable may not be null.");
		else this.scope = var.getScope();
		
		add(nameLabel);
		add(nameField, "wrap");
		
		valuesArea.setBorder(BorderFactory.createTitledBorder("Values"));
		add(valuesArea, "grow, span 2, wrap");
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "find");
		getActionMap().put("find", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				filterDialog.setVisible(true);
				filterDialog.setTitle("Filter values of " + getVariable().getName());
				filterDialog.toFront();
			}
		});
		
		setVariable(var);
	}
	
	public VariablePanel(VariableScope scope){
		this(new Variable("", scope));
	}
	
	public void setValueFilter(String valueFilter){
		if(valueFilter == null) valueFilter = "";
		for(JTextField valueField : valueFields){
			if(valueField.getText().toUpperCase().contains(valueFilter.toUpperCase())){
				valueField.setVisible(true);
				deleteButtons.get(valueField).setVisible(true);
			}else{
				valueField.setVisible(false);
				deleteButtons.get(valueField).setVisible(false);
			}
		}
		filterLabel.setText("Filtered by '" + valueFilter + "'");
		filterLabel.setVisible(!valueFilter.isEmpty());
		this.valueFilter = valueFilter;
	}
	
	public String getValueFilter(){
		return valueFilter;
	}
	
	public boolean showInNewWindow(Component relativeTo){
		final Boolean[] success = new Boolean[]{null};
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JFrame frame = new JFrame(getVariable().getName());
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				frame.add(VariablePanel.this, BorderLayout.CENTER);
				
				JPanel buttonPanel = new JPanel(new MigLayout("", "[][][grow]"));
				JButton cancelButton = new JButton("Cancel");
				buttonPanel.add(cancelButton, "alignx left");
				JButton resetButton = new JButton("Reset");
				buttonPanel.add(resetButton, "alignx left");
				JButton applyButton = new JButton("Apply");
				buttonPanel.add(applyButton, "alignx right");
				frame.add(buttonPanel, BorderLayout.SOUTH);
				
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(JOptionPane.showConfirmDialog(VariablePanel.this, "Do you really want to cancel?\nAll changes will be lost.", "Confirm Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
							reset();
							frame.dispose();
							success[0] = false;
						}
					}
				});
				resetButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(JOptionPane.showConfirmDialog(VariablePanel.this, "Do you really want to reset?\nAll changes will be lost.", "Confirm Reset", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
							reset();
						}
					}
				});
				applyButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(validateInput() && JOptionPane.showConfirmDialog(VariablePanel.this, "Do you really want to apply your changes?", "Confirm Apply", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
							try {
								persist();
								frame.dispose();
								success[0] = true;
							} catch (RegistryException ex) {
								String message = "An error occured while saving " + scope.name().toLowerCase() + " variable '" + nameField.getText() + "':\n" + ex.getMessage();
								JOptionPane.showMessageDialog(VariablePanel.this, message, "Unable to save", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
				frame.addWindowListener(new WindowAdapter() {
					
					@Override
					public void windowClosing(WindowEvent e) {
						cancelButton.doClick();
					}
				});
				
				frame.setPreferredSize(new Dimension(500, 300));
				frame.pack();
				frame.setLocationRelativeTo(relativeTo);
				frame.setVisible(true);
			}
		});
		while(success[0] == null){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// That is fine, no need for handling
			}
		}
		return success[0];
	}
	
	public Variable getVariable(){
		return variable;
	}

	public void setVariable(Variable variable){
		if(variable == null) throw new IllegalArgumentException("Variable may not be null, create an empty variable instead.");
		this.variable = variable;
		
		// Clear values
		valuesArea.removeAll();
		valuesContainer.removeAll();
		valueFields.clear();
		deleteButtons.clear();
		
		// Name
		nameField.setText(variable.getName());
		
		// Add value button
		JButton addButton = new JButton("+");
		Color background = UIManager.getColor("Panel.background");
		Backgrounds.set(addButton, background, ColorUtil.changeBrightness(background, 0.9f), ColorUtil.changeBrightness(background, 0.8f));
		addButton.setBorder(BorderFactory.createDashedBorder(ColorUtil.changeBrightness(background, 0.9f), 5, 5));
		addButton.setContentAreaFilled(false);
		addButton.setOpaque(true);
		addButton.setFont(addButton.getFont().deriveFont(Font.BOLD, addButton.getFont().getSize() * 1.5f));
		addButton.addActionListener(e -> addValue("", true));
		valuesArea.add(addButton, "wrap, gapy 1px 1px");
		filterLabel.setVisible(false);
		valuesArea.add(filterLabel, "wrap, gapy 1px 1px");
		
		// Values
		for(String value : variable.getValues()){
			addValue(value, false);
		}
		valuesScroll.setBorder(null);
		valuesScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		valuesScroll.getHorizontalScrollBar().setUnitIncrement(16);
		valuesArea.add(valuesScroll);
	}
	
	private void addValue(String value, boolean focus){
		JImage deleteButton = new JImage(FontIcon.of(FontAwesomeSolid.TRASH_ALT).toImage());
		valuesContainer.add(deleteButton);
		
		JTextField valueField = new JTextField(value);
		valuesContainer.add(valueField);
		valueFields.add(valueField);
		deleteButtons.put(valueField, deleteButton);
		
		deleteButton.addActionListener(e -> removeValue(valueField));

		revalidate();
		repaint();
		
		if(focus){
			valueField.requestFocus();
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					valuesScroll.getVerticalScrollBar().setValue(valueField.getLocation().y);
				}
			});
		}
	}
	
	private void removeValue(JTextField valueField){
		valueFields.remove(valueField);
		valuesContainer.remove(valueField);
		valuesContainer.remove(deleteButtons.get(valueField));
		deleteButtons.remove(valueField);
		revalidate();
		repaint();
	}
	
	public boolean validateInput(){
		if(nameField.getText().isEmpty()){
			nameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED), UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border")));
			nameField.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					onUpdate();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					onUpdate();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					onUpdate();
				}
				
				private void onUpdate(){
					nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
					nameField.getDocument().removeDocumentListener(this);
				}
			});
			return false;
		}else{
			nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
			return true;
		}
	}
	
	public void reset(){
		setVariable(variable);
	}
	
	public void persist() throws RegistryException{
		String oldName = variable.getName();
		String[] oldValues = variable.getValues();
		try{
			String name = nameField.getText();
			if(name.isEmpty()){
				
			}
			if(variable == null){
				variable = new Variable(name, scope);
			}else{
				variable.setName(name);
			}
			String[] values = new String[valueFields.size()];
			for(int i = 0; i < values.length; i++) values[i] = valueFields.get(i).getText();
			variable.setValues(values);
			Registry.persist(variable);
		}catch(RegistryException e){
			variable.setName(oldName);
			variable.setValues(oldValues);
			throw e;
		}
	}
	
}
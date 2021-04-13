package ui;

import java.awt.Cursor;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.components.ScaledImageButton;
import logic.Registry;
import logic.RegistryException;
import logic.Variable.VariableScope;
import net.miginfocom.swing.MigLayout;

public class EnvVarEditorPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	private JComboBox<VariableScope> scopeDropdown = new JComboBox<>(VariableScope.values());
	private JComboBox<String> variableDropdown = new JComboBox<>();

	private ScaledImageButton addButton = new ScaledImageButton("Add", FontIcon.of(FontAwesomeSolid.PLUS).toImage());
	private ScaledImageButton editButton = new ScaledImageButton("Edit", FontIcon.of(FontAwesomeSolid.PEN).toImage());
	private ScaledImageButton deleteButton = new ScaledImageButton("Delete", FontIcon.of(FontAwesomeSolid.TRASH_ALT).toImage());
	
	public EnvVarEditorPanel(){
		super(new MigLayout("fillx", "[][][]", "[]20px[]10px[]"));
		
		add(scopeDropdown, "growx, span 3, wrap");
		add(variableDropdown, "growx, span 3, wrap");
		
		add(addButton, "alignx left");
		add(editButton, "alignx center");
		add(deleteButton, "alignx right, wrap");
		
		addButton.addActionListener(e -> add());
		editButton.addActionListener(e -> edit((VariableScope) scopeDropdown.getSelectedItem(), variableDropdown.getSelectedItem().toString()));
		deleteButton.addActionListener(e -> delete((VariableScope) scopeDropdown.getSelectedItem(), variableDropdown.getSelectedItem().toString()));
		
		scopeDropdown.addActionListener(e -> loadVariables((VariableScope) scopeDropdown.getSelectedItem()));
		loadVariables((VariableScope) scopeDropdown.getSelectedItem());
		
	}
	
	private void add(){
		new Thread() {
			
			@Override
			public void run() {
				if(new VariablePanel((VariableScope) scopeDropdown.getSelectedItem()).showInNewWindow(EnvVarEditorPanel.this)){
					refresh();
				}
			}
		}.start();
	}
	
	private void edit(VariableScope scope, String name){
		new Thread() {
			
			@Override
			public void run() {
				if(new VariablePanel(Registry.get(scope, name)).showInNewWindow(EnvVarEditorPanel.this)){
					refresh();
				}
			}
		}.start();
	}
	
	private void delete(VariableScope scope, String name){
		if(JOptionPane.showConfirmDialog(this, "Do you really want to delete the " + scope.name().toLowerCase() + " variable '" + name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
			try {
				Registry.remove(scope, name);
				refresh();
			} catch (RegistryException ex) {
				String message = "An error occured while deleting " + scope.name().toLowerCase() + " variable '" + name + "':\n" + ex.getMessage();
				JOptionPane.showMessageDialog(this, message, "Unable to delete", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void refresh(){
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Registry.refresh();
		loadVariables((VariableScope) scopeDropdown.getSelectedItem());
		setCursor(Cursor.getDefaultCursor());
	}
	
	private void loadVariables(VariableScope scope){
		variableDropdown.removeAllItems();
		for(String varName : Registry.getSortedVarNames(scope)){
			variableDropdown.addItem(varName);
		}
	}

}

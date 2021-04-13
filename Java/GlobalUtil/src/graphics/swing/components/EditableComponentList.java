package graphics.swing.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;


public class EditableComponentList<T extends Component> extends Container{
	
	private List<T> components = new ArrayList<T>();
	private T selection;
	
	private List<SelectionListener<T>> selectionListeners = new ArrayList<SelectionListener<T>>();
	
	public EditableComponentList(T... components){
		setLayout(new MigLayout("wrap 1, insets 0", "[grow, fill]", "[]0px[]"));
		addItem(components);
	}
	
	private void addRow(final T component){
		JPanel row = new JPanel(new BorderLayout());
		row.setBackground(Color.WHITE);
		row.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		row.add(component);
		MouseListener mouseListener = new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelection(component);
			}
		};
		row.addMouseListener(mouseListener);
		super.add(row);
	}
	
	public void setSelection(T component){
		if(!components.contains(component)) throw new IllegalArgumentException("Component " + component + " cannot be selected: not contained in component list");
		if(selection != null){
			selection.getParent().setBackground(Color.WHITE);
			selection = null;
		}
		
		selection = component;
		if(selection != null) selection.getParent().setBackground(javax.swing.UIManager.getDefaults().getColor("Table.selectionBackground"));
		for(SelectionListener<T> listener : selectionListeners) listener.selectionChanged(selection);
	}
	
	public T getSelection(){
		return selection;
	}
	
	private void removeRow(T component){
		Container row = component.getParent();
		row.remove(component);
		super.remove(row);
	}
	
	public void addItem(T... components){
		addItem(this.components.size(), components);
	}
	
	public void addItem(int index, T... components){
		for(T component : components){
			this.components.add(index, component);
			index++;
			addRow(component);
		}
	}
	
	public void removeItem(T... components){
		for(T component : components){
			this.components.remove(component);
			if(selection == component) selection = null;
			removeRow(component);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void removeItem(int... indexes){
		for(int index : indexes){
			removeItem(components.get(index));
		}
	}
	
	
	public void addSelectionListener(SelectionListener<T> listener){
		selectionListeners.add(listener);
	}
	
	public void removeSelectionListener(SelectionListener<T> listener){
		selectionListeners.remove(listener);
	}
	
	public static interface SelectionListener<T>{
		public void selectionChanged(T newSelection);
	}

}

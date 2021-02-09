package com.creditsuisse.graphics.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("unchecked")
public class NestedCheckBox<T extends Component> extends JPanel {
	
	private static final int DEFAULT_INSET = 10;

	private JCheckBox topCheckBox;
	private Map<T, Boolean> enabledConditions = new HashMap<T, Boolean>();
	

	// delegate constructors for topCheckBox
	public NestedCheckBox() {
		topCheckBox = new JCheckBox();
		init();
	}
	public NestedCheckBox(Action a) {
		topCheckBox = new JCheckBox(a);
		init();
	}
	public NestedCheckBox(Icon icon, boolean selected) {
		topCheckBox = new JCheckBox(icon, selected);
		init();
	}
	public NestedCheckBox(Icon icon) {
		topCheckBox = new JCheckBox(icon);
		init();
	}
	public NestedCheckBox(String text, boolean selected) {
		topCheckBox = new JCheckBox(text, selected);
		init();
	}
	public NestedCheckBox(String text, Icon icon, boolean selected) {
		topCheckBox = new JCheckBox(text, icon, selected);
		init();
	}
	public NestedCheckBox(String text, Icon icon) {
		topCheckBox = new JCheckBox(text, icon);
		init();
	}
	public NestedCheckBox(String text) {
		topCheckBox = new JCheckBox(text);
		init();
	}
	public NestedCheckBox(String text, T... nestedComponents) {
		this(text, DEFAULT_INSET, null, nestedComponents);
	}
	public NestedCheckBox(String text, Boolean enabledCondition, T... nestedComponents) {
		this(text, DEFAULT_INSET, enabledCondition, nestedComponents);
	}
	public NestedCheckBox(String text, int inset, T... nestedComponents) {
		this(text, inset, null, nestedComponents);
	}
	public NestedCheckBox(String text, int inset, Boolean enabledCondition, T... nestedComponents) {
		topCheckBox = new JCheckBox(text);
		init();
		for(T comp : nestedComponents){
			add(inset, enabledCondition, comp);
		}
	}
	
	private void init(){
		setLayout(new MigLayout("wrap 1, insets 0, gap 0", "[grow, fill]", ""));
		super.addImpl(topCheckBox, null, -1);
		
		topCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for(Component comp : getComponents()){
					if(enabledConditions.get(comp) != null){
						comp.setEnabled(enabledConditions.get(comp) == topCheckBox.isSelected());
					}
				}
			}
		});
	}
	
	public JCheckBox getTopCheckBox(){
		return topCheckBox;
	}
	
	public boolean isSelected(){
		return topCheckBox.isSelected();
	}
	
	public void setSelected(boolean selected){
		topCheckBox.setSelected(selected);
	}
	
	public void setOpaque(boolean isOpaque){
		super.setOpaque(isOpaque);
		Component[] comps = getComponents();
		for(int i = 0; i < comps.length; i++){
			if(comps[i] instanceof JComponent){
				JComponent jComp = (JComponent) comps[i];
				jComp.setOpaque(isOpaque);
			}
		}
	}
	
	public void setToolTipText(String text){
		super.setToolTipText(text);
		Component[] comps = getComponents();
		for(int i = 0; i < comps.length; i++){
			if(comps[i] instanceof JComponent){
				JComponent jComp = (JComponent) comps[i];
				if(jComp.getToolTipText() == null || jComp.getToolTipText().isEmpty()){
					jComp.setToolTipText(text);
				}
			}
		}
	}
	
	public List<T> getNestedComponents(){
		List<T> nestedComps = new ArrayList<T>();
		Component[] comps = getComponents();
		for(int i = 1; i < comps.length; i++){
			nestedComps.add((T) comps[i]);
		}
		return nestedComps;
	}
	
	protected void addImpl(int inset, Boolean enabledCondition, Component comp, Object constraints, int index) {
		try{
			T tComponent = (T) comp;
			if(constraints == null) super.addImpl(comp, "gapleft " + inset, index);
			else super.addImpl(comp, "gapleft " + inset + ", " + constraints.toString(), index);
			
			if(enabledCondition != null){
				comp.setEnabled(enabledCondition == topCheckBox.isSelected());
				enabledConditions.put(tComponent, enabledCondition);
			}
		}catch (Throwable e){
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot add component: illegal type " + comp.getClass());
		}
	}
	
	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		addImpl(DEFAULT_INSET, null, comp, constraints, index);
	}
	
	public T add(Boolean enabledCondition, T comp){
		addImpl(DEFAULT_INSET, enabledCondition, comp, null, -1);
		return comp;
	}
	public T add(int inset, Boolean enabledCondition, T comp){
		addImpl(inset, enabledCondition, comp, null, -1);
		return comp;
	}
	public T add(int inset, Boolean enabledCondition, String name, T comp) {
		addImpl(inset, enabledCondition, comp, name, -1);
		return comp;
	}
	public T add(int inset, Boolean enabledCondition, T comp, int index) {
		addImpl(inset, enabledCondition, comp, null, -1);
		return comp;
	}
	public void add(int inset, Boolean enabledCondition, T comp, Object constraints) {
		addImpl(inset, enabledCondition, comp, constraints, -1);
	}
	public void add(int inset, Boolean enabledCondition, T comp, Object constraints, int index) {
		addImpl(inset, enabledCondition, comp, constraints, index);
	}
	
	
	
	
}

package com.creditsuisse.graphics.swing;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class ExtendedJOptionPane extends JOptionPane {
	private static final long serialVersionUID = 1L;
	
	private static boolean remeberDecision = false;
	private static Integer decision = null;
	
	public static void reset(){
		remeberDecision = false;
		decision = null;
	}
	
	public static int showConfirmDialog(Component parentComponent, Object message) {
		return showConfirmDialog(parentComponent, message, UIManager.getString("OptionPane.titleText"), YES_NO_CANCEL_OPTION);
	}
	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) {
		return showConfirmDialog(parentComponent, message, title, optionType, QUESTION_MESSAGE);
	}
	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType) {
		return showConfirmDialog(parentComponent, message, title, optionType, messageType, null);
	}
	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon) {
		return showOptionDialog(parentComponent, message, title, optionType, messageType, icon, null, null);
	}

	public static int showOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue) {
		if(remeberDecision && decision != null) return decision;
		else{
			JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);
			JCheckBox remeberDecisionBox = new JCheckBox("Remeber my decision");
			pane.add(remeberDecisionBox);
			JDialog dialog = pane.createDialog(parentComponent, title);
			dialog.setVisible(true);
			Object value = pane.getValue();
			while(value.equals(JOptionPane.UNINITIALIZED_VALUE)){
				value = pane.getValue();
			}
			remeberDecision = remeberDecisionBox.isSelected();
			int decision = (Integer)value;
			if(remeberDecision) ExtendedJOptionPane.decision = decision;
			return decision;
		}
	}
}

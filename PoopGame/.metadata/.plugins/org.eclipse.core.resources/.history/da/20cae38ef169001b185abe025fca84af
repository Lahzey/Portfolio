package com.creditsuisse.graphics.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.creditsuisse.util.Predicate;

public class ErrorPanel extends JComponent{
	
	public String errorText;
	private Predicate<Component> rules;
	private Component view;
	
	private JLabel errorLabel = new JLabel(){
		public String getText(){
			return errorText;
		}
	};
	
	public ErrorPanel(Component view, Predicate<Component> rules, String errorText){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.view = view;
		this.rules = rules;
		this.errorText = errorText;
		errorLabel.setVisible(false);
		errorLabel.setForeground(Color.RED);
		add(errorLabel);
		add(view);
	}
	
	
	public boolean validateView(){
		if(rules.test(view)){
			return true;
		}else{
			setErrorVisible(true);
			return false;
		}
	}
	
	public void setErrorVisible(boolean visible){
		errorLabel.setVisible(visible);
	}

}

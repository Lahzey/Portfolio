package com.creditsuisse.graphics.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class HorizontalSpinner<T> extends JComponent{
	
	private T[] values;

	private JLabel title, value;
	private JButton prevButton, nextButton;
	private int currentIndex = 0;
	
	public HorizontalSpinner(T... values){
		this.values = values;
		if(values.length < 1) throw new IllegalArgumentException("Minimum 1 value required for HorizontalSpinner");
		setLayout(new MigLayout("fill, insets 0", "[][grow, fill][]", ""));
		
		//Key Listener for left right arrow controll
		InputMap inputMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "previous");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
		ActionMap actionMap = getActionMap();
		actionMap.put("previous", new AbstractAction() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        previous();
		    }
		});
		actionMap.put("next", new AbstractAction() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        next();
		    }
		});
		
		prevButton = new JButton("<");
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				previous();
				HorizontalSpinner.this.requestFocus();
			}
		});
		nextButton = new JButton(">");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				next();
				HorizontalSpinner.this.requestFocus();
			}
		});
		
		value = new JLabel();
		value.setHorizontalAlignment(SwingConstants.CENTER);
		
		title = new JLabel();
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setVisible(false);
		
		add(prevButton);
		add(value);
		add(nextButton);
		add(title, "north");
		
		update();
	}
	
	private void update(){
		updateValue();
		updateButtons();
	}
	
	private void updateValue(){
		value.setText(values[currentIndex].toString());
	}
	
	private void updateButtons(){
		if(currentIndex <= 0) prevButton.setEnabled(false);
		else prevButton.setEnabled(true);
		if(currentIndex + 1 >= values.length) nextButton.setEnabled(false);
		else nextButton.setEnabled(true);
	}
	
	private void previous(){
		if(currentIndex > 0) currentIndex--;
		update();
	}
	
	private void next(){
		if(values.length > currentIndex + 1) currentIndex++;
		update();
		
	}
	
	public T getValue(){
		return values[currentIndex];
	}
	
	public void setTitle(String title){
		if(title == null) this.title.setVisible(false);
		else{
			this.title.setText(title);
			this.title.setVisible(true);
		}
	}

}

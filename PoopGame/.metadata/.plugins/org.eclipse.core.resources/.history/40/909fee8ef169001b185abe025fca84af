package com.creditsuisse.graphics.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A Dialog that asks the user to choose a key. Only keys that are characters may be chosen (so F1 wouldn't work).
 * @author A469627
 *
 */
public class KeyChooser extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private char selectedKey;
	private char defaultKey;
	private JLabel keyChooserLabel;
	private JButton cancelButton = new JButton("Cancel");
	private JButton okButton = new JButton("Ok");

	/**
	 * Constructs a new KeyChooser with the given key selected from start.
	 * @param realtiveTo the component this dialog should be be placed in the middle of (if null, it will be places in the middle of the screen).
	 * @param defaultKey the key to be selected by default
	 */
	public KeyChooser(Component relativeTo, char defaultKey) {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
        		cancelButton.doClick();
			}
		});
		this.defaultKey = defaultKey;
		selectedKey = defaultKey;
		setLayout(new BorderLayout());
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		configComponents();
		pack();
		setLocationRelativeTo(relativeTo);
	}
	
	/**
	 * Configures the components and adds them at the right places.
	 */
	private void configComponents() {
		keyChooserLabel = new JLabel();
		refreshLabelText();
		keyChooserLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(keyChooserLabel, BorderLayout.CENTER);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(okButton);
		add(buttonsPanel, BorderLayout.SOUTH);
		
		//Listener for the key selection
		keyChooserLabel.addMouseListener(new MouseAdapter() {
			private KeyEventDispatcher keyListener = new KeyEventDispatcher() {
				@Override
				public boolean dispatchKeyEvent(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						cancel();
					} else if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
						submit(e.getKeyChar());
					}
					return false;
				}
			};
			
			@Override
			public void mouseClicked(MouseEvent e) {
				keyChooserLabel.setForeground(Color.RED);
				keyChooserLabel.removeMouseListener(this);
				keyChooserLabel.setText("Press a key");
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyListener);
			}
			private void cancel(){
				onEnd();
			}
			private void submit(char newKey){
				selectedKey = newKey;
				onEnd();
			}
			private void onEnd(){
				refreshLabelText();
				keyChooserLabel.setForeground(Color.BLACK);
				KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyListener);
				keyChooserLabel.addMouseListener(this);
			}
		});
		
		
		//Listener for ok and cancel button
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedKey = defaultKey;
				setVisible(false);
			}
		});
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}
	
	/**
	 * Returns the key selected by the user. If the user clicked cancel, the default key given in the constructor will be returned.
	 * @return the selected key
	 */
	public char getSelectedKey() {
		return selectedKey;
	}
	
	/**
	 * Sets the text of the label to "Press to select a new key (current: " + selectedKey + ")".
	 */
	private void refreshLabelText(){
		keyChooserLabel.setText("Press to select a new key (current: " + selectedKey + ")");
		pack();
	}

	/**
	 * Creates a new dialog asking the user to enter a new key.
	 * Once the user clicks 'Ok', the selected key will be returned.
	 * If he clicks 'Cancel' or closes the window, the default key will be returned.
	 * The cursor will stay in this method until the user clicks 'Ok' or 'Cancel' or closes the window.
	 * @param realtiveTo the component this dialog should be be placed in the middle of (if null, it will be places in the middle of the screen).
	 * @param defaultKey the key to be selected from the start
	 * @return the selected key when pressing 'Ok' or the default key when pressing 'Cancel'
	 */
	public static char showDialog(Component relativeTo, char defaultKey){
		KeyChooser chooser = new KeyChooser(relativeTo, defaultKey);
		chooser.setVisible(true);
		while(chooser.isVisible()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return chooser.getSelectedKey();
	}
}

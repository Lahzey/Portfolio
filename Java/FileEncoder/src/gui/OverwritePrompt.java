package gui;

import java.awt.Component;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;

public class OverwritePrompt extends JDialog {
	
	public static final int CANCEL = -1;
	public static final int NO = 0;
	public static final int YES = 1;
	public static final int NO_ALL = 2;
	public static final int YES_ALL = 3;

	private JCheckBox repeatCheckbox;
	
	private Integer result = null;
	
	private OverwritePrompt(File file, int remainingPromptCount, Component parent){
		setTitle("Confirm Overwrite");
		setLayout(new MigLayout("wrap 3", "[grow][][]", "[]"));
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		add(new JLabel(file.getAbsolutePath() + " already exists."), "span 3, grow");
		add(new JLabel("Do you want to overwrite?"), "span 3, grow");
		
		int nextCount = remainingPromptCount - 1;
		repeatCheckbox = new JCheckBox("Repeat for the next " + nextCount + (nextCount > 1 ? " files" : "file"));
		if(nextCount > 0){
			add(repeatCheckbox, "span 3, grow");
		}
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> setResult(CANCEL));
		add(cancelButton, "left");
		
		JButton yesButton = new JButton("Yes");
		yesButton.addActionListener(e -> setResult(repeatCheckbox.isSelected() ? YES_ALL : YES));
		add(yesButton);
		
		JButton noButton = new JButton("No");
		noButton.addActionListener(e -> setResult(repeatCheckbox.isSelected() ? NO_ALL : NO));
		add(noButton);
		
		pack();
		
		setLocationRelativeTo(parent);
	}
	
	private void setResult(int result){
		if(result == CANCEL){
			if(JOptionPane.showConfirmDialog(this, "Are you sure you want to abort the whole process?", "Confirm Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				this.result = result;
				dispose();
			}
		}else if(result == NO_ALL || result == YES_ALL){
			if(JOptionPane.showConfirmDialog(this, "Are you sure you want to repeat your selection for all remaining conflicts?", "Confirm Repeat", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				this.result = result;
				dispose();
			}
		}else{
			this.result = result;
			dispose();
		}
	}
	
	
	public static int show(File file, int remainingPromptCount, Component parent){
		OverwritePrompt prompt = new OverwritePrompt(file, remainingPromptCount, parent);
		prompt.setVisible(true);
		while(prompt.result == null && prompt.isVisible()){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// does not matter
			}
		}
		return prompt.result == null ? NO : prompt.result;
	}
}

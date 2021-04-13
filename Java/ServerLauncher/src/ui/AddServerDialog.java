package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import graphics.swing.components.TextFieldFileChooser;
import util.ColorUtil;

import logic.FileLaunchable;
import net.miginfocom.swing.MigLayout;

public class AddServerDialog extends JDialog{
	private static final long serialVersionUID = 1L;

	private JLabel formErrors = new JLabel("");
	private TextFieldFileChooser serverInput = new TextFieldFileChooser(true);
	private JButton addButton = new JButton("Add");
	private JButton cancelButton = new JButton("Cancel");
	
	private FileLaunchable.Type serverType;
	private FileLaunchable result;
	
	public AddServerDialog(File server, Component relativeTo){
		setLocationRelativeTo(relativeTo);
		setMinimumSize(new Dimension(400, 0));
		setLayout(new MigLayout("hidemode 3, wrap 2, fill", "[50%][50%]", ""));

		formErrors.setForeground(ColorUtil.ERROR_FOREGROUND_COLOR);
		formErrors.setVisible(false);
		add(formErrors, "span 2");

		add(new JLabel("Select a server:"), "span 2");
		serverInput.setText(server != null ? server.getAbsolutePath() : "");
		add(serverInput, "span 2, grow");
		
		add(new JSeparator(), "span 2, grow");

		add(new JLabel("Choose a type:"), "span 2");
		ButtonGroup radioGroup = new ButtonGroup();
		for(FileLaunchable.Type type : FileLaunchable.Type.values()){
			JRadioButton typeRadio = new JRadioButton(type.getDisplayName());
			typeRadio.setActionCommand(type.name());
			typeRadio.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					serverType = FileLaunchable.Type.valueOf(e.getActionCommand());
				}
			});
			add(typeRadio);
			radioGroup.add(typeRadio);
		}
		
		if(Type.values().length % 2 != 0){
			add(new JPanel()); //add placeholder if there is an uneven amount of radio buttons
		}

		add(new JSeparator(), "span 2, grow");
		
		add(cancelButton);
		add(addButton, "right");
		
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				result = null; // just to make sure
				setVisible(false);
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String serverPath = serverInput.getText();
				File server = new File(serverPath);
				String errorText = "";
				boolean inputsValid = true;
				if(serverPath == null || serverPath.isEmpty()){
					inputsValid = false;
					if(!errorText.isEmpty()) errorText += "<br/>";
					errorText += "You must select a server.";
				}else if (!server.exists()){
					inputsValid = false;
					if(!errorText.isEmpty()) errorText += "<br/>";
					errorText += "The selected server must exist.";
				}
				if(serverType == null){
					inputsValid = false;
					if(!errorText.isEmpty()) errorText += "<br/>";
					errorText += "You must choose a type.";
				}
				if(inputsValid){
					formErrors.setVisible(false);
					result = new FileLaunchable(server, serverType);
					setVisible(false);
				}else{
					formErrors.setText("<html>" + errorText + "</html>");
					formErrors.setVisible(true);
					pack();
				}
			}
		});
	}
	
	public FileLaunchable getResult(){
		return result;
	}
	
	public static FileLaunchable showDialog(File server, Component relativeTo){
		final AddServerDialog dialog = new AddServerDialog(server, relativeTo);;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				dialog.pack();
				dialog.setVisible(true);
			}
		});
		boolean wasVisible = false;
		while(!wasVisible || dialog.isVisible()){
			if(dialog.isVisible()) wasVisible = true;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// thats fine
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				dialog.setVisible(false);
			}
		});
		return dialog.getResult();
	}
}

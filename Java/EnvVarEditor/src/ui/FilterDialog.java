package ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class FilterDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	
	private VariablePanel filterFor;
	
	private JLabel filterLabel = new JLabel("Filter");
	private JTextField filterField = new JTextField();
	
	private JButton cancelButton = new JButton("Cancel");
	private JButton removeButton = new JButton("Remove Filter");
	private JButton filterButton = new JButton("Filter");

	public FilterDialog(VariablePanel filterFor){
		this.filterFor = filterFor;
		JPanel contentPane = new JPanel(new MigLayout("debug", "[][grow, fill]"));
		setContentPane(contentPane);
		add(filterLabel, "");
		add(filterField, "wrap");
		
		JPanel buttonPanel = new JPanel(new MigLayout("fill, insets 0", "[][][grow]", ""));
		buttonPanel.add(cancelButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(filterButton, "alignx right");
		add(buttonPanel, "span 2, grow");
		
		setMinimumSize(new Dimension(300, 0));
		pack();
		setLocationRelativeTo(filterFor);
		
		contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		contentPane.getActionMap().put("cancel", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				cancelButton.doClick();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				cancelButton.doClick();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				filterField.setText(filterFor.getValueFilter());
				setVisible(false);
				SwingUtilities.getWindowAncestor(filterFor).toFront();
			}
		});
		removeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				filterFor.setValueFilter("");
				filterField.setText("");
				setVisible(false);
				SwingUtilities.getWindowAncestor(filterFor).toFront();
			}
		});
		filterButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				filterFor.setValueFilter(filterField.getText());
				setVisible(false);
				SwingUtilities.getWindowAncestor(filterFor).toFront();
			}
		});
		
		filterField.addActionListener(e -> filterButton.doClick());
	}
	
}

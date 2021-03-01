package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import graphics.swing.AutoScrollPane;
import graphics.swing.HighlightWrapper;
import graphics.swing.JAnimationPanel;
import graphics.swing.JImage;
import graphics.swing.NestedCheckBox;
import graphics.swing.TextLineNumber;
import graphics.swing.colors.Backgrounds;
import util.ColorUtil;
import util.ExtendedThread;

import net.miginfocom.swing.MigLayout;

public class LogPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int SCROLL_INCREMENT = 16;
	
	public static final int INFO = ColorUtil.INFO;
	public static final int SUCCESS = ColorUtil.SUCCESS;
	public static final int WARNING = ColorUtil.WARNING;
	public static final int ERROR = ColorUtil.ERROR;
	
	private static final Color DOCK_COLOR = ColorUtil.INFO_BACKGROUND_COLOR;
	private static final Color TRANSPARENT_DOCK_COLOR = new Color(DOCK_COLOR.getRed(), DOCK_COLOR.getGreen(), DOCK_COLOR.getBlue(), 0);
	private static final int DOCK_HEIGHT = 10;
	
	private JSplitPane splitter;

	// Filter logic
	private String currentFilter;
	private int currentMatchIndex;
	private List<HighlightWrapper> highlights = new ArrayList<HighlightWrapper>();
	private ExtendedThread filterThread;
	
	// Log components
	private JTextArea logArea;
	private TextLineNumber textLineNumber;
	private AutoScrollPane scroll;
	
	// Filter components
	private JTextField filterField;
	private JLabel filterCountLabel;
		// -> Filter settings
	private JImage filterSettingsButton;
	private JAnimationPanel filterSettingsPanel;
	private JCheckBox caseSensitiveCheckBox;
	private NestedCheckBox<JCheckBox> regexCheckBox;
	private JCheckBox dotAllCheckBox;
	
	// Message components
	private JPanel messageArea;
	private JLabel noMessageLabel;
	private Map<JPanel, Integer> messagePanels = new HashMap<JPanel, Integer>();
	private Object messageHighlight;
	private Selection currentSelection = null;
	
	public LogPanel(final ServerPanel serverPanel){
		super(new BorderLayout());
		setPreferredSize(new Dimension(1500, 750));
		
		
		// Left part (log and filter)
		JPanel left = new JPanel(new MigLayout("", "[grow, fill]", "[grow, fill]"));
		
		logArea = new JTextArea(){
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g){
				super.paintComponent(g);
				if(scroll.isScrollToBottom()){
		            Graphics2D g2d = (Graphics2D) g;
					int width = logArea.getWidth();
					int height = logArea.getHeight();
					GradientPaint gradientPaint = new GradientPaint(0, height - DOCK_HEIGHT, TRANSPARENT_DOCK_COLOR, 0, height, DOCK_COLOR);
					g2d.setPaint(gradientPaint);
					g2d.fillRect(0, height - DOCK_HEIGHT, width, DOCK_HEIGHT);
				}
			}
		};
		logArea.setEditable(false);
		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		
		textLineNumber = new TextLineNumber(logArea);
		
		scroll = new AutoScrollPane(logArea);
		scroll.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		scroll.setRowHeaderView(textLineNumber);
		
		left.add(scroll);
		
		// filter area
		final JPanel filterArea = new JPanel(new MigLayout("insets 0, hidemode 3", "[grow, fill]5px[]10px[]", "[]0px[]"));
		filterField = new JTextField();
		filterField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filter();
			}
		});
		filterArea.add(filterField);
		filterCountLabel = new JLabel("enter to filter");
		filterCountLabel.setForeground(Color.GRAY);
		filterArea.add(filterCountLabel);
		left.add(filterArea, "north");
		
		// filter settings
		filterSettingsButton = new JImage(FontAwesomeSolid.COG);
		Backgrounds.set(filterSettingsButton, null, Color.LIGHT_GRAY, Color.GRAY);
		filterSettingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean toggled = !filterSettingsPanel.isVisible();
				filterSettingsButton.setIcon(FontAwesomeSolid.COG, toggled ? ColorUtil.INFO_FOREGROUND_COLOR : Color.BLACK);
				filterSettingsButton.setBorder(toggled ? BorderFactory.createEmptyBorder(1, 1, 1, 1) : null);
				filterArea.setBackground(toggled ? ColorUtil.INFO_BACKGROUND_COLOR : UIManager.getColor("Panel.background"));
				filterSettingsPanel.setBackground(toggled ? ColorUtil.INFO_BACKGROUND_COLOR : UIManager.getColor("Panel.background"));
				if(toggled) filterSettingsPanel.open(250, JAnimationPanel.VERTICAL);
				else filterSettingsPanel.close(250, JAnimationPanel.VERTICAL);
			}
		});
		filterArea.add(filterSettingsButton, "wrap, gapright 5px");
		
		filterSettingsPanel = new JAnimationPanel(new MigLayout("insets 0", "5%[]5%[]5%[]5%", "[top, grow]"));
		filterSettingsPanel.setVisible(false);
		caseSensitiveCheckBox = new JCheckBox("Case Sensitive");
		caseSensitiveCheckBox.setSelected(false);
		caseSensitiveCheckBox.setOpaque(false);
		filterSettingsPanel.add(caseSensitiveCheckBox);
		filterSettingsPanel.add(new JSeparator(SwingConstants.VERTICAL), "grow");
		regexCheckBox = new NestedCheckBox<JCheckBox>("Regex");
		regexCheckBox.setSelected(false);
		regexCheckBox.setOpaque(false);
		regexCheckBox.getTopCheckBox().setOpaque(false);
		filterSettingsPanel.add(regexCheckBox);
		dotAllCheckBox = new JCheckBox("Dot matches all characters (including line breaks)");
		dotAllCheckBox.setSelected(false);
		dotAllCheckBox.setOpaque(false);
		regexCheckBox.add(true, dotAllCheckBox);
		filterArea.add(filterSettingsPanel, "span 3");
		

		// Right part
		JPanel right = new JPanel(new BorderLayout());
		
		// Control
		JPanel controlPanel = new JPanel(new MigLayout("", "[grow, fill][][]", "")){
			@Override
			public Color getBackground(){
				return serverPanel.status == null ? UIManager.getColor("Panel.background") : ColorUtil.mix(serverPanel.status.getColor(), Color.WHITE);
			}
		};
		JLabel controlTitle = new JLabel("Server Control");
		controlTitle.setFont(controlTitle.getFont().deriveFont(Font.BOLD));
		controlPanel.add(controlTitle);
		JImage stopButton = serverPanel.stopButton.createDelegate();
		controlPanel.add(stopButton);
		JImage startButton = serverPanel.startButton.createDelegate();
		controlPanel.add(startButton);
		right.add(controlPanel, BorderLayout.NORTH);
		
		// Messages
		messageArea = new JPanel(new MigLayout("wrap 1", "[grow, fill]", ""));
		JLabel messagesTitle = new JLabel("Messages");
		messagesTitle.setFont(messagesTitle.getFont().deriveFont(Font.BOLD));
		messagesTitle.setHorizontalAlignment(SwingConstants.CENTER);
		messageArea.add(messagesTitle);
		messageArea.add(new JSeparator());
		noMessageLabel = new JLabel("no messages found");
		noMessageLabel.setForeground(Color.GRAY);
		noMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		messageArea.add(noMessageLabel);
		AutoScrollPane messageScroll = new AutoScrollPane(messageArea);
		messageScroll.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		right.add(messageScroll);
		
		
		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		splitter.setDividerLocation((int)(getPreferredSize().getWidth() * 0.75f));
		add(splitter);
	}
	
	public void setScrollToBottom(boolean scrollToBottom){
		scroll.setScrollToBottom(scrollToBottom);
	}
	
	public void append(String text){
		logArea.append(text);
		logArea.repaint();
	}
	
	public void addMessage(final int startIndex, final int endIndex, int severity){
		Color backgroundColor = ColorUtil.getBackgroundColor(severity);
		final Color borderColor = ColorUtil.getBorderColor(severity);
		Color textColor = ColorUtil.getForegroundColor(severity);
		
		final JAnimationPanel messagePanel = new JAnimationPanel(new MigLayout("insets 20px 10px 20px 10px", "[]5px[grow, fill]", "[grow, fill]"));
		messagePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, borderColor, borderColor.darker()));
		Backgrounds.set(messagePanel, backgroundColor, ColorUtil.changeBrightness(backgroundColor, 0.9f), ColorUtil.changeBrightness(backgroundColor, 1.1f));
		messagePanel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if(currentSelection != null) currentSelection.setSelected(false);
					currentSelection = new Selection(messagePanel);
					currentSelection.setSelected(true);
					logArea.scrollRectToVisible(logArea.modelToView(startIndex));
					if(messageHighlight != null) logArea.getHighlighter().removeHighlight(messageHighlight);
					messageHighlight = logArea.getHighlighter().addHighlight(startIndex, endIndex, new DefaultHighlighter.DefaultHighlightPainter(borderColor));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		int messageLineNumber = 0;
		String messageText = "No text available";

		try {
			Element root = logArea.getDocument().getDefaultRootElement();
			messageLineNumber = root.getElementIndex(startIndex) + 1;
			messageText = logArea.getDocument().getText(startIndex, endIndex - startIndex);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		JLabel lineNrLabel = new JLabel(messageLineNumber + " | ");
		lineNrLabel.setForeground(textColor);
		messagePanel.add(lineNrLabel);
		JLabel messageTextLabel = new JLabel(messageText);
		messageTextLabel.setForeground(textColor);
		messageTextLabel.setFont(messageTextLabel.getFont().deriveFont(Font.BOLD));
		messagePanel.add(messageTextLabel);
		messagePanel.setVisible(false);
		
		if(noMessageLabel.getParent() != null){
			messageArea.remove(noMessageLabel);
		}
		messagePanels.put(messagePanel, severity);
		messageArea.add(messagePanel);
		
		revalidate();
		repaint();
		
		new ExtendedThread() {
			
			@Override
			public void run() {
				int waitCount = 0;
				while(messagePanel.getWidth() == 0){
					if(waitCount >= 100){ // just to be safe
						messagePanel.setVisible(true);
						return;
					}else{
						waitCount++;
						sleepSilent(10);
					}
				}
				messagePanel.setSize(messagePanel.getWidth(), 0);
				messagePanel.open(250, JAnimationPanel.VERTICAL);
			}
		}.start();
	}
	
	public int getMessageCount(){
		return messagePanels.size();
	}
	
	public int getMessageCount(int... severities){
		int count = 0;
		for(int messageSeverity : messagePanels.values()){
			for(int severity : severities){
				if(messageSeverity == severity){
					count++;
					break;
				}
			}
		}
		return count;
	}
	
	private void filter() {
		if(filterThread != null){
			filterThread.terminate();
			while(filterThread.isAlive()){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// does not matter
				}
			}
		}
		filterThread = new ExtendedThread() {
			
			@Override
			public void run() {
				// Remove message highlight
				if(messageHighlight != null){
					logArea.getHighlighter().removeHighlight(messageHighlight);
					messageHighlight = null;
				}
				if(currentSelection != null){
					currentSelection.setSelected(false);
					currentSelection = null;
				}
				
				// get filter
				String oldFilter = currentFilter;
				String filter = filterField.getText();
				if(filter.isEmpty()){
					clearFilter();
					filterCountLabel.setText("enter to filter");
					filterCountLabel.setForeground(Color.GRAY);
					return;
				}
				
				// save index of old filter position if still using same filter
				int currentMatchStart = -1;
				if(currentMatchIndex >= 0 && filter.equals(oldFilter) && highlights.size() > currentMatchIndex){
					currentMatchStart = highlights.get(currentMatchIndex).getStart();
				}
				
				// can abort here
				if(!running) return;
				
				// refresh filter
				clearFilter();
				currentFilter = filter;
				findFilterOccurences(logArea.getText(), this);
				
				// set to old filter position (if there is one)
				if(currentMatchStart >= 0){
					for(HighlightWrapper highlight : highlights){
						if(highlight.getStart() == currentMatchStart){
							currentMatchIndex = highlights.indexOf(highlight);
							break;
						}
					}
				}
				
				if(filter.equals(oldFilter)){
					// Go to next selection
					if(currentMatchIndex < highlights.size() - 1) currentMatchIndex++;
					else currentMatchIndex = 0;
				}else{
					currentMatchIndex = 0;
				}
				
				// can abort here
				if(!running) return;
				
				if(currentMatchIndex >= 0 && highlights.size() > currentMatchIndex){
					// Change newly selected highlight to selected color
					final HighlightWrapper highlight = highlights.get(currentMatchIndex);
					highlight.setColor(ColorUtil.INFO_FOREGROUND_COLOR);
					
					// Scroll to selection
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							Rectangle viewRect;
							try {
								viewRect = logArea.modelToView(highlight.getStart());
								logArea.scrollRectToVisible(viewRect);
							} catch (BadLocationException e) {
								e.printStackTrace();
							}
							filterCountLabel.setText((currentMatchIndex + 1) + " / " + highlights.size());
							filterCountLabel.setForeground(ColorUtil.INFO_FOREGROUND_COLOR);
						}
					});
				}else{
					// nothing found (or something went wrong)
					currentMatchIndex = -1;
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							filterCountLabel.setText(0 + " / " + highlights.size());
							filterCountLabel.setForeground(ColorUtil.ERROR_FOREGROUND_COLOR);
						}
					});
				}
			}
		};
		filterThread.start();
	}
	
	private void findFilterOccurences(String text, ExtendedThread runningIn){
		if(text == null || text.isEmpty()) return;
		
		boolean regex = regexCheckBox.isSelected();
		boolean dotAll = dotAllCheckBox.isSelected();
		boolean caseSensitive = caseSensitiveCheckBox.isSelected();
		
		Highlighter highlighter = logArea.getHighlighter();
		
		if(regex){
			int flags = 0;
			if(!caseSensitive) flags += Pattern.CASE_INSENSITIVE;
			if(dotAll) flags += Pattern.DOTALL;
			Pattern pattern = Pattern.compile("[\\n\\x0B\\x0C\\r\\u0085\\u2028\\u2029.]*(" + currentFilter + ")[\\n\\x0B\\x0C\\r\\u0085\\u2028\\u2029.]*", flags);
			Matcher matcher = pattern.matcher(text);
			int index = -1;
			while(matcher.find()){
				String match = matcher.group();
				index = text.indexOf(match, index + 1);
				highlights.add(new HighlightWrapper(highlighter, index, index + match.length(), ColorUtil.INFO_BACKGROUND_COLOR));
				// can abort here
				if(!runningIn.isRunning()) return;
			}
		}else{
			String filter = currentFilter;
			if(!caseSensitive){
				filter = filter.toLowerCase();
				text = text.toLowerCase();
			}
			
			int index = text.indexOf(filter);
			while (index >= 0) {
				highlights.add(new HighlightWrapper(highlighter, index, index + filter.length(), ColorUtil.INFO_BACKGROUND_COLOR));
				// can abort here
				if(!runningIn.isRunning()) return;
			    index = text.indexOf(filter, index + 1);
			}
		}
	}
	
	public void clear(){
		// Clear filter
		clearFilter();
		
		// Clear log
		logArea.setText("");
		
		// Clear messages
		for(JPanel messagePanel : messagePanels.keySet()) messageArea.remove(messagePanel);
		noMessageLabel.setVisible(true);
		messagePanels.clear();
	}
	
	public void clearFilter(){
		while(!highlights.isEmpty()){
			highlights.get(0).setHighlighter(null);
			highlights.remove(0);
		}
		currentFilter = null;
		currentMatchIndex = 0;
	}

	public int length() {
		return logArea.getDocument().getLength();
	}
	
	
	private class Selection {
		
		private final JAnimationPanel messagePanel;
		private Border originalBorder;
		private boolean selected = false;
		
		public Selection(JAnimationPanel messagePanel){
			this.messagePanel = messagePanel;
		}

		public void setSelected(boolean selected) {
			if(selected != this.selected){
				if(selected){
					originalBorder = messagePanel.getBorder();
					messagePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(66, 157, 255), new Color(0, 123, 255)));
				}else{
					messagePanel.setBorder(originalBorder);
				}
				this.selected = selected;
			}
		}
	}
	
}

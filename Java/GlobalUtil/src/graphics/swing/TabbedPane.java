package graphics.swing;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.DragGridPane.DragAdapter;
import graphics.swing.colors.Backgrounds;
import net.miginfocom.swing.MigLayout;
import util.ColorUtil;
import util.GeneralListener;

public class TabbedPane<T extends Component> extends JPanel {
	
	private boolean canEditName = true;
	private boolean canRemove = true;
	private boolean canAdd = false;
	private Callable<T> tabConstructor = null;

	private final List<Tab> tabs = new ArrayList<Tab>();
	private final List<Tab> tabHistory = new ArrayList<Tab>();
	private int currentTabIndex = 0;
	
	private DragGridPane tabsContainer;
	private JPanel currentTabPanel = null;
	private Map<JPanel, Tab> tabPanelMapping = new HashMap<JPanel, Tab>();
	private JPanel contentContainer = new JPanel(new MigLayout("insets 0, fill", "[grow, fill]", "[grow, fill]"));
	
	public TabbedPane(){
		super(new MigLayout("wrap 1, insets 0, gap 0", "[grow, fill]", "[][grow, fill]"));
		
		tabsContainer = new DragGridPane(new MigLayout("insets 0, gap 0", "", "[grow, fill]")){
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(Color.DARK_GRAY);
				((Graphics2D) g).setStroke(new BasicStroke(1));
				int width = tabsContainer.getWidth();
				int height = tabsContainer.getHeight();
				if(currentTabPanel == null){
					g.drawLine(0, height - 1, width, height - 1);
				}else{
					int tabX = currentTabPanel.getX();
					if(getCurrentlyDragged() == currentTabPanel) tabX += getCurrentDragOffset().width;
					int tabWidth = currentTabPanel.getWidth();
					g.drawLine(0, height - 1, tabX, height - 1);
					g.drawLine(tabX + tabWidth, height - 1, width, height - 1);
				}
			}
		};
		
		tabsContainer.setVerticalDragEnabled(false);
		tabsContainer.setBackground(ColorUtil.changeBrightness(Color.LIGHT_GRAY, 1.0f));
		JScrollPane tabsScroll = new JScrollPane(tabsContainer);
		tabsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		tabsScroll.setBorder(null);
		tabsScroll.setOpaque(false);
		
		add(tabsScroll);
		
		add(contentContainer);

		generateContents();
		
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			
			@Override
			public void eventDispatched(AWTEvent event) {
				if(event instanceof KeyEvent){
					KeyEvent keyEvent = (KeyEvent) event;
					if(keyEvent.getID() == KeyEvent.KEY_PRESSED){
						if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_T){
							if(keyEvent.isShiftDown()){
								// open last closed tab
								if(!tabHistory.isEmpty()){
									int index = tabHistory.size() - 1;
									Tab tab = tabHistory.get(index);
									add(tab.content, tab.name);
									tabHistory.remove(index);
									setCurrentTab(tabs.size() - 1);
								}
							}else{
								// open new tab
								try {
									add(tabConstructor.call(), "Tab " + (tabs.size() + 1));
									setCurrentTab(tabs.size() - 1);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);
		
		tabsContainer.addDragListener(new DragAdapter() {
			@Override
			public void onDrop(Component droppedComponent) {
				tabs.clear();
				for(Component comp : tabsContainer.getComponents()){
					Tab tab = tabPanelMapping.get(comp);
					if(tab != null){
						tabs.add(tab);
					}
				}
				if(currentTabPanel != null) currentTabIndex = tabs.indexOf(tabPanelMapping.get(currentTabPanel));
				onChange();
			}
		});
	}
	
	public void enableAdd(Callable<T> tabConstructor){
		this.tabConstructor = tabConstructor;
		canAdd = true;
		generateContents();
	}
	
	public void disableAdd(){
		canAdd = false;
		generateContents();
	}
	
	public void add(T tabContent, String tabName, int tabIndex){
		tabs.add(new Tab(tabContent, tabName));
		onChange();
	}
	
	public void add(T tabContent, String tabName){
		add(tabContent, tabName, tabs.size());
	}
	
	@Override
	public void remove(int tabIndex){
		if(tabIndex >= 0 && tabIndex < tabs.size()){
			tabHistory.add(tabs.remove(tabIndex));
			if(currentTabIndex >= tabIndex && currentTabIndex > 0){
				currentTabIndex--;
			}
			onChange();
		}else{
			throw new IndexOutOfBoundsException("Tab Index " + tabIndex + " out of bounds.");
		}
	}
	
	@Override
	public void remove(Component comp){
		for(int i = 0; i < tabs.size(); i++){
			if(tabs.get(i).content == comp){
				remove(i);
			}
		}
	}
	
	public List<T> getTabContents(){
		List<T> contents = new ArrayList<T>(tabs.size());
		for(Tab tab : tabs) contents.add(tab.content);
		return contents;
	}
	
	public List<String> getTabNames(){
		List<String> names = new ArrayList<String>(tabs.size());
		for(Tab tab : tabs) names.add(tab.name);
		return names;
	}
	
	public void setCurrentTab(int tabIndex){
		if((tabIndex < tabs.size() || tabIndex == 0) && tabIndex >= 0){
			currentTabIndex = tabIndex;
			onChange();
		}else{
			throw new IndexOutOfBoundsException("Tab Index " + tabIndex + " out of bounds.");
		}
	}

	public int getCurrentTab() {
		return currentTabIndex;
	}
	
	protected void onChange(){
		generateContents();
	}
	
	private void generateContents(){
		// generate tabs
		tabsContainer.removeAll();
		
		// small gap at the start
		JPanel bufferTab = new JPanel();
		bufferTab.setOpaque(false);
		bufferTab.setMinimumSize(new Dimension(10, 0));
		tabsContainer.add(bufferTab);
		tabsContainer.setExcludedFromDrag(bufferTab, true);
		
		for(int i = 0; i < tabs.size(); i++){
			final int tabIndex = i;
			final Tab tab = tabs.get(i);
			final JPanel tabPanel = new JPanel(new MigLayout("insets 10", "[]10px[]", "[grow, fill]"));
			tabPanelMapping.put(tabPanel, tab);
			if(i == currentTabIndex){
				tabPanel.setBorder(new RoundedMatteBorder(1, 1, 0, 1, 5, Color.DARK_GRAY));
				tabPanel.setBackground(UIManager.getColor("Panel.background"));
				tabPanel.setOpaque(false); // false so outside the rounded corners it is transparent (RoundedMatteBorder will paint the background)
				currentTabPanel = tabPanel;
			}else{
				tabPanel.setBorder(new PartialMatteBorder(0, (i == currentTabIndex - 1 ? 0 : 1), 0, 0, 0, 50, 100, 0, PartialMatteBorder.PERCENTAGE, Color.DARK_GRAY));
				Backgrounds.set(tabPanel, Color.LIGHT_GRAY, ColorUtil.changeBrightness(Color.LIGHT_GRAY, 1.1f), ColorUtil.changeBrightness(Color.LIGHT_GRAY, 0.9f), new GeneralListener() {
					
					@Override
					public void actionPerformed() {
						tabsContainer.repaint();
					}
				});
				tabPanel.setOpaque(true);
			}
			
			final JTextField tabField = new JTextField(tab.name) {
				@Override
				public Dimension getPreferredSize(){
					// fix text cutting off slightly by making the field a bit larger
					Dimension superPref = super.getPreferredSize();
					return new Dimension(superPref.width + 2, superPref.height);
				}
			};
			tabField.setEditable(false);
			tabField.setFocusable(false);
			tabField.setOpaque(false);
			tabField.setBorder(null);
			tabPanel.add(tabField);
			

			final ActionListener submitListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					tabField.setEditable(false);
					tabField.setFocusable(false);
					tabField.setBorder(null);
					tabsContainer.revalidate();
					tabsContainer.repaint();
					tabs.get(tabIndex).name = tabField.getText();
				}
			};
			tabField.addActionListener(submitListener);
			tabField.addFocusListener(new FocusAdapter() {
				
				@Override
				public void focusLost(FocusEvent e) {
					submitListener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "submit"));
				}
			});

			SwingUtil.addRecursiveMouseListener(tabPanel, new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if(tabPanel.getComponentCount() == 2 && SwingUtil.containsScreenLocation(tabPanel.getComponent(1), e.getLocationOnScreen())) return;
					
					if(e.getButton() == MouseEvent.BUTTON1){
						if(e.getClickCount() > 1 && canEditName && !tabField.isEditable()){
							tabField.setEditable(true);
							tabField.setFocusable(true);
							tabField.setBorder(UIManager.getBorder("TextField.border"));
							tabField.requestFocus();
						}else if(currentTabIndex != tabIndex){
							setCurrentTab(tabIndex);
						}
					}else if(e.getButton() == MouseEvent.BUTTON2){
						if(canRemove){
							remove(tabIndex);
						}
					}
				}
			});
			
			if(canRemove){
				JImage removeButton = new JImage(FontAwesomeRegular.TIMES_CIRCLE, Color.GRAY);
				Image hoveredImage = FontIcon.of(FontAwesomeSolid.TIMES_CIRCLE, Color.GRAY).toImage();
				removeButton.setHoveredImage(hoveredImage);
				removeButton.setClickedImage(hoveredImage);
				removeButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						remove(tabIndex);
					}
				});
				removeButton.setFocusable(false);
				tabPanel.add(removeButton);
			}
			
			tabsContainer.add(tabPanel);
		}
		
		if(canAdd && tabConstructor != null){
			JPanel tabPanel = new JPanel(new MigLayout("", "[grow, fill]", "[grow, fill]"));
			tabPanel.setOpaque(false);
			tabPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
			JImage addButton = new JImage(FontAwesomeRegular.PLUS_SQUARE);
			addButton.generateStateImages();
			Image hoveredImage = FontIcon.of(FontAwesomeSolid.PLUS_SQUARE).toImage();
			addButton.setHoveredImage(hoveredImage);
			addButton.setClickedImage(hoveredImage);
			addButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						add(tabConstructor.call(), "Tab " + (tabs.size() + 1));
						setCurrentTab(tabs.size() - 1);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			addButton.setFocusable(false);
			tabPanel.add(addButton);
			tabsContainer.add(tabPanel);
			tabsContainer.setExcludedFromDrag(tabPanel, true);
		}
		
		JPanel fillerTab = new JPanel();
		fillerTab.setOpaque(false);
		fillerTab.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
		tabsContainer.add(fillerTab, "push, grow");
		tabsContainer.setExcludedFromDrag(fillerTab, true);
		
		
		// generate content
		contentContainer.removeAll();
		if(tabs.size() > currentTabIndex) contentContainer.add(tabs.get(currentTabIndex).content);
		
		Window window = SwingUtilities.getWindowAncestor(this);
		if(window != null) window.pack();
		revalidate();
		repaint();
	}
	
	private class Tab {
		public T content;
		public String name;
		
		public Tab(T content, String name){
			this.content = content;
			this.name = name;
		}
	}

}

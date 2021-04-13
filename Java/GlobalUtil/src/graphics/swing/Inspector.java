package graphics.swing;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;

import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.HollowRectangle;
import graphics.ImageUtil;
import graphics.swing.components.JImage;
import graphics.swing.components.MouseEventBlocker;
import graphics.swing.components.TypeEditor;
import net.miginfocom.swing.MigLayout;
import util.ColorUtil;
import util.GetterSetterAccess;
import util.LoopThread;
import util.StringComparator;

public class Inspector extends JPanel implements AWTEventListener {
	
	private static final Color CONTENT_OVERLAY_COLOR = new Color(56, 165, 255, 150);
	private static final Color BORDER_OVERLAY_COLOR = new Color(255, 136, 56, 150);

	private static final Image INVISIBLE_RETRACTED_ICON = FontIcon.of(FontAwesomeSolid.CARET_RIGHT, new Color(0, 0, 0, 0)).toImage();
	private static final Image RETRACTED_ICON = FontIcon.of(FontAwesomeSolid.CARET_RIGHT).toImage();
	private static final Image EXPANDED_ICON = FontIcon.of(FontAwesomeSolid.CARET_DOWN).toImage();
	
	private static final Image SELECT_ICON = ImageUtil.merge(
			ImageUtil.merge(new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB), FontIcon.of(FontAwesomeRegular.SQUARE, 25).toImage()),
			FontIcon.of(FontAwesomeSolid.MOUSE_POINTER, 20).toImage(), new Dimension(15, 10)
	);
	private static final Image SELECT_ICON_SELECTED = ImageUtil.merge(
			ImageUtil.merge(new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB), FontIcon.of(FontAwesomeRegular.SQUARE, 25, ColorUtil.INFO_FOREGROUND_COLOR).toImage()),
			FontIcon.of(FontAwesomeSolid.MOUSE_POINTER, 20, ColorUtil.INFO_FOREGROUND_COLOR).toImage(), new Dimension(15, 10)
	);
	private static final Image SETTINGS_ICON = FontIcon.of(FontAwesomeSolid.ELLIPSIS_V).toImage();
	private static final Image CLOSE_ICON = FontIcon.of(FontAwesomeSolid.TIMES).toImage();
	
	public static final int DEFAULT_TOGGLE_KEY = KeyEvent.VK_F12;
	public static int toggleKey = DEFAULT_TOGGLE_KEY;
	
	private static AWTEventListener currentListener = null;
	
	private final Component content;
	
	private JPanel inspectionPanel = new JPanel();
	private JPanel resizeBorder = new JPanel();
	private JPanel inspectionHeader = new JPanel(new MigLayout("fillx, insets 0 5 0 5", "[grow][]10px[]", "[grow, fill]"));
	private JSplitPane inspectionContent = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JPanel contentTree = new JPanel(new BorderLayout());
	private ComponentNode rootNode = null;
	private JPanel selectionPanel = new JPanel(new MigLayout("wrap 2, insets 5 0 0 0", "[][grow, fill]", ""));
	private JLabel noSelectionLabel = new JLabel("no selection");
	private JTextField selectionFilterField = new JTextField();
	
	private JImage selectComponentButton = new JImage(SELECT_ICON);
	private JImage settingsButton = new JImage(SETTINGS_ICON);
	private JImage closeButton = new JImage(CLOSE_ICON);
	
	private JScrollPane contentScroll = new JScrollPane();
	private JScrollPane contentTreeScroll = new JScrollPane();
	
	private int inspectorSize = 250;
	private boolean currentlyLocating = false;
	private ComponentNode currentlyHovered = null;
	private ComponentNode currentlySelected = null;
	
	public Inspector(Component content){
		super(new BorderLayout());
		this.content = content;
		
		inspectionPanel = new JPanel(new MigLayout("fillx, wrap 1, insets 0, gap 0", "[grow, fill]", "[][][grow, fill]")){
			@Override
			public Dimension getPreferredSize() {
				Container parent = Inspector.this.getParent();
				return new Dimension(super.getPreferredSize().width, parent != null ? Math.min(inspectorSize, (int) ((parent.getHeight() * 0.8))) : inspectorSize);
			}
		};
		
		add(content, BorderLayout.CENTER);
		add(inspectionPanel, BorderLayout.SOUTH);
		
		contentScroll.setBorder(null);
		contentScroll.getVerticalScrollBar().setUnitIncrement(16);
		
		contentTreeScroll.setViewportView(contentTree);
		contentTreeScroll.setBorder(null);
		contentTreeScroll.getVerticalScrollBar().setUnitIncrement(16);
		inspectionContent.setLeftComponent(contentTreeScroll);
		
		noSelectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		noSelectionLabel.setFont(noSelectionLabel.getFont().deriveFont(Font.ITALIC));
		noSelectionLabel.setForeground(Color.GRAY);
		selectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		selectionPanel.add(noSelectionLabel, BorderLayout.NORTH);
		JScrollPane selectionScroll = new JScrollPane(selectionPanel);
		selectionScroll.getVerticalScrollBar().setUnitIncrement(16);
		selectionScroll.setBorder(null);
		JPanel rightSide = new JPanel(new BorderLayout());
		selectionFilterField.setVisible(false);
		rightSide.add(selectionFilterField, BorderLayout.NORTH);
		rightSide.add(selectionScroll, BorderLayout.CENTER);
		inspectionContent.setRightComponent(rightSide);
		
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
		
		resizeBorder.setOpaque(false);
		resizeBorder.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		resizeBorder.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		resizeBorder.addMouseMotionListener(new MouseAdapter() {

		    @Override
		    public void mouseDragged(MouseEvent e) {
		        int y = e.getY();
		        inspectorSize -= y;
		        revalidate();
		        repaint();
		    }
		});
		
		inspectionHeader.add(selectComponentButton, "left");
		inspectionHeader.add(settingsButton);
		inspectionHeader.add(closeButton);
		inspectionHeader.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY), BorderFactory.createEmptyBorder(0, 0, 2, 0)));

		inspectionPanel.add(resizeBorder, "hmax 3px, hmin 3px");
		inspectionPanel.add(inspectionHeader);
		inspectionPanel.add(inspectionContent);
		inspectionPanel.setVisible(false);
		
		selectComponentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentlyLocating(!currentlyLocating);
			}
		});
		
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toggle();
			}
		});
		
		selectionFilterField.getDocument().addDocumentListener(new DocumentChangeListener() {
			
			@Override
			protected void onChange(DocumentEvent e) {
				generateSelectionContent(selectionFilterField.getText());
			}
		});
		

		new LoopThread(5) {
			
			@Override
			public void loopedRun() {
				if(Inspector.this.getSize().width > 0){
					// inspector panel is now displayed, size specific initialization can start
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							inspectionContent.setDividerLocation((int)(Inspector.this.getSize().width * 0.8));
						}
					});
					terminate();
				}
			}
		}.start();
	}
	
	public static void setActive(boolean active){
		if(active && currentListener == null){
			currentListener = new AWTEventListener() {
				
				@Override
				public void eventDispatched(AWTEvent event) {
					if(event instanceof KeyEvent && event.getID() == KeyEvent.KEY_RELEASED){
						KeyEvent keyEvent = (KeyEvent) event;
						if(keyEvent.getKeyCode() == toggleKey && keyEvent.getComponent() != null){
							Component source = keyEvent.getComponent();
							Window window = source instanceof Window ? (Window) source : SwingUtilities.getWindowAncestor(source);
							if(window != null && window instanceof RootPaneContainer){
								RootPaneContainer root = (RootPaneContainer) window;
								Container contentPane = root.getContentPane();
								Inspector inspector;
								if(contentPane instanceof Inspector){
									inspector = ((Inspector) contentPane);
								}else{
									inspector = new Inspector(contentPane);
									root.setContentPane(inspector);
								}
								inspector.toggle();
							}
						}
					}
				}
			};
			Toolkit.getDefaultToolkit().addAWTEventListener(currentListener, AWTEvent.KEY_EVENT_MASK);
		}else if(!active && currentListener != null){
			Toolkit.getDefaultToolkit().removeAWTEventListener(currentListener);
			currentListener = null;
		}
	}
	
	public void toggle(){
		if(rootNode == null){
			generateContent();
		}else{
			rootNode.updateNode();
		}
		
		inspectionPanel.setVisible(!inspectionPanel.isVisible());
		
		if(inspectionPanel.isVisible()){
			remove(content);
			contentScroll.setViewportView(content);
			add(contentScroll, BorderLayout.CENTER);
		}
		
		revalidate();
		repaint();
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		if(event instanceof KeyEvent && event.getID() == KeyEvent.KEY_RELEASED){
			KeyEvent keyEvent = (KeyEvent) event;
			if(keyEvent.getComponent() != null){
				Component source = keyEvent.getComponent();
				Window root = SwingUtilities.getWindowAncestor(this);
				if(root != source && root != SwingUtilities.getWindowAncestor(source)){
					return;
				}
			}
			if(keyEvent.getKeyCode() == KeyEvent.VK_DELETE){
				if(currentlySelected != null && currentlySelected.hasFocus()){
					currentlySelected.delete();
				}
			}
		}
		if(event instanceof MouseEvent){
			MouseEvent mouseEvent = (MouseEvent) event;
			if(event.getID() == MouseEvent.MOUSE_MOVED){
				if(SwingUtil.containsScreenLocation(contentTree, mouseEvent.getLocationOnScreen())){
					ComponentNode node = getNode(mouseEvent.getLocationOnScreen().y);
					if(node != null){
						setCurrentlyHovered((ComponentNode) node);
					}else{
						setCurrentlyHovered(null);
					}
				}else if(currentlyLocating && content.isShowing()){
					Point contentLocation = content.getLocationOnScreen();
					Component comp = SwingUtil.getDeepestComponentAt(content, mouseEvent.getXOnScreen() - contentLocation.x, mouseEvent.getYOnScreen() - contentLocation.y);
					if(currentlyHovered == null || currentlyHovered.component != comp) setCurrentlyHovered(getNode(comp));
				}else{
					setCurrentlyHovered(null);
				}
			}else if(event.getID() == MouseEvent.MOUSE_PRESSED){
				if(SwingUtil.containsScreenLocation(contentTree, mouseEvent.getLocationOnScreen())){
					ComponentNode node = getNode(mouseEvent.getLocationOnScreen().y);
					if(node != null){
						node.requestFocus();
						setCurrentlySelected(node);
					}
				}else if(currentlyLocating && content.isShowing()){
					Point contentLocation = content.getLocationOnScreen();
					Component comp = SwingUtil.getDeepestComponentAt(content, mouseEvent.getXOnScreen() - contentLocation.x, mouseEvent.getYOnScreen() - contentLocation.y);
					if(comp != null){
						if(currentlySelected == null || currentlySelected.component != comp) setCurrentlySelected(getNode(comp));
					}
				}
			}
		}
	}
	
	private void generateContent(){
		rootNode = new ComponentNode(content, null);
		contentTree.add(rootNode, BorderLayout.NORTH);
		JPanel filler = new JPanel();
		filler.setOpaque(false);
		contentTree.add(filler, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
	private void setCurrentlyHovered(ComponentNode node){
		currentlyHovered = node;
		repaint();
	}
	
	private void setCurrentlySelected(ComponentNode node){
		setCurrentlyLocating(false);
		currentlyHovered = null;
		
		currentlySelected = node;
		
		if(currentlySelected != null){
			// open and scroll to node
			ComponentNode parent = currentlySelected.parent;
			while(parent != null){
				parent.setExpanded(true);
				parent = parent.parent;
			}
			new LoopThread(5) {
				
				@Override
				public void loopedRun() {
					// wait until tree is open at the node
					if(currentlySelected.isShowing() && currentlySelected.getHeight() > 0){
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								JScrollBar scrollBar = contentTreeScroll.getVerticalScrollBar();
								Point location = SwingUtilities.convertPoint(currentlySelected.getParent(), currentlySelected.getLocation(), contentTree);
								int y = location.y;
								int height = currentlySelected.getHeight();
								int scrollY = scrollBar.getValue();
								int scrollHeight = scrollBar.getVisibleAmount();
								if(y < scrollY || y + height > scrollY + scrollHeight){
									contentTreeScroll.getVerticalScrollBar().setValue(y + scrollHeight / 2 - height / 2);
								}
							}
						});
						terminate();
					}
				}
			}.start();
			
			
			
		}
		selectionFilterField.setVisible(currentlySelected != null);
		generateSelectionContent("");
	}
	
	private void setCurrentlyLocating(boolean currentlyLocating){
		this.currentlyLocating = currentlyLocating;
		selectComponentButton.setImage(currentlyLocating ? SELECT_ICON_SELECTED : SELECT_ICON);
		
		if(currentlyLocating){
			MouseEventBlocker.connect(content);
		}else{
			MouseEventBlocker.disconnect(content);
		}
	}
	
	private ComponentNode getNode(Component comp){
		if(rootNode != null && comp != null){
			return findNode(rootNode, comp);
		}else{
			return null;
		}
	}
	
	public static ComponentNode findNode(ComponentNode rootNode, Component comp){
		if(rootNode.component == comp){
			return rootNode;
		}else{
			for(Component child : rootNode.children){
				ComponentNode childNode = rootNode.nodes.get(child);
				ComponentNode find = findNode(childNode, comp);
				if(find != null) return find;
			}
			return null;
		}
	}
	
	public ComponentNode getNode(int screenY){
		return getNode(screenY, rootNode);
	}
	
	public ComponentNode getNode(int screenY, ComponentNode parent){
		if(parent != null){
			Point parentLocation = parent.getLocationOnScreen();
			int y = screenY - parentLocation.y;
			for(Component childComp : parent.children){
				ComponentNode child = parent.nodes.get(childComp);
				if(y >= child.getY() && y < child.getY() + child.getHeight()){
					return getNode(screenY, child);
				}
			}
			if(y >= 0 && y < parent.getHeight()){
				return parent;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	
	private void generateSelectionContent(String filter){
		filter = filter.toLowerCase();
		selectionPanel.removeAll();
		if(currentlySelected != null){
			final Component currentlySelectedComponent = currentlySelected.component;
			final GetterSetterAccess attributeAccess = new GetterSetterAccess(currentlySelectedComponent.getClass());
			List<String> editableAttributes = new ArrayList<String>();
			List<String> uneditableAttributes = new ArrayList<String>();
			for(String attribute : attributeAccess.getAttributes()){
				if(TypeEditor.getEditor(attributeAccess.getAttributeType(attribute)) == null){
					uneditableAttributes.add(attribute);
				}else{
					editableAttributes.add(attribute);
				}
			}
			Collections.sort(editableAttributes, new StringComparator());
			Collections.sort(uneditableAttributes, new StringComparator());
			
			for(final String attribute : editableAttributes){
				String attributeType = attributeAccess.getAttributeType(attribute).getSimpleName();
				Object value = attributeAccess.getValue(currentlySelectedComponent, attribute);
				if(!filter.isEmpty() && !attribute.toLowerCase().contains(filter) && !attributeType.toLowerCase().contains(filter) && !("" + value).toLowerCase().contains(filter)){
					continue;
				}
				
				final JLabel attributeNameLabel = new JLabel("<html><font color='#2B91AF'>" + attributeType + "</font> <font color='#303336'>" + attribute + "</font></html>");
				attributeNameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
				selectionPanel.add(attributeNameLabel);
				final TypeEditor<?> editor = TypeEditor.getEditor(attributeAccess.getAttributeType(attribute));
				editor.trySetInput(value);
				selectionPanel.add(editor);
				
				editor.addSubmitListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						boolean success = false;
						if(editor.isInputValid()){
							try{
								attributeAccess.setValue(currentlySelectedComponent, attribute, editor.getInput());
								currentlySelected.revalidate();
								currentlySelected.repaint();
								success = true;
							}catch(Throwable t){
								System.err.println("Failed to set attribute " + attribute + " to value " + editor.getInput().toString() + ". Cause:");
								t.printStackTrace();
							}
						}
						
						if(success){
							attributeNameLabel.setIcon(null);
							attributeNameLabel.setText("<html><font color='#2B91AF'>" + attributeAccess.getAttributeType(attribute).getSimpleName() + "</font> <font color='#303336'>" + attribute + "</font></html>");
						}else{
							attributeNameLabel.setIcon(FontIcon.of(FontAwesomeSolid.EXCLAMATION_TRIANGLE, ColorUtil.WARNING_FOREGROUND_COLOR));
							attributeNameLabel.setText("<html><strike><font color='#2B91AF'>" + attributeAccess.getAttributeType(attribute).getSimpleName() + "</font> <font color='#303336'>" + attribute + "</font></strike></html>");
						}
					}
				});
			}
			
			selectionPanel.add(new JSeparator(), "span 2, grow");
			
			for(String attribute : uneditableAttributes){
				String attributeType = attributeAccess.getAttributeType(attribute).getSimpleName();
				Object value = attributeAccess.getValue(currentlySelectedComponent, attribute);
				String valueText;
				if(value == null) valueText = "null";
				else{
					Class<?> enclosingClass = value.getClass().getEnclosingClass();
					if (enclosingClass != null) {
						valueText = enclosingClass.getSimpleName();
					} else {
						valueText = value.getClass().getSimpleName();
					}
				}
				if(!filter.isEmpty() && !attribute.toLowerCase().contains(filter) && !attributeType.toLowerCase().contains(filter) && !valueText.toLowerCase().contains(filter)){
					continue;
				}
				
				selectionPanel.add(new JLabel("<html><font color='#2B91AF'>" + attributeType + "</font> <font color='#303336'>" + attribute + "</font></html>"));
				JLabel valueLabel = new JLabel(valueText);
				valueLabel.setForeground(Color.GRAY);
				valueLabel.setFont(valueLabel.getFont().deriveFont(Font.ITALIC));
				selectionPanel.add(valueLabel);
			}
		}else{
			selectionPanel.add(noSelectionLabel, "span 2, grow, center");
		}
		selectionPanel.revalidate();
		selectionPanel.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		Component currentlyHovered = this.currentlyHovered != null ? this.currentlyHovered.component : null;
		
		if(currentlyHovered != null && currentlyHovered.isShowing()){
			Point location = currentlyHovered.getLocationOnScreen();
			location.x -= getLocationOnScreen().x;
			location.y -= getLocationOnScreen().y;
			
			Insets borderInsets = new Insets(0, 0, 0, 0);
			if(currentlyHovered instanceof JComponent){
				Border border = ((JComponent) currentlyHovered).getBorder();
				if(border != null){
					borderInsets = border.getBorderInsets(currentlyHovered);
				}
			}
			
			// overlay for content
			g.setColor(CONTENT_OVERLAY_COLOR);
			Rectangle contentShape = new Rectangle(location.x + borderInsets.left, location.y + borderInsets.top,
					currentlyHovered.getWidth() - borderInsets.left - borderInsets.right, currentlyHovered.getHeight()  - borderInsets.top - borderInsets.bottom);
			g2d.fill(contentShape);
			
			// overlay for border
			g.setColor(BORDER_OVERLAY_COLOR);
			Shape borderShape;
			if(borderInsets.left + borderInsets.right < currentlyHovered.getWidth() && borderInsets.top + borderInsets.bottom < currentlyHovered.getHeight()){
				borderShape = new HollowRectangle(new Rectangle(location.x, location.y, currentlyHovered.getWidth(), currentlyHovered.getHeight()), borderInsets);
			}else{
				borderShape = new Rectangle(location.x, location.y, currentlyHovered.getWidth(), currentlyHovered.getHeight());
			}
			g2d.fill(borderShape);
			
			// size text
			String text = currentlyHovered.getWidth() + " x " + currentlyHovered.getHeight();
			int textHeight = g.getFontMetrics().getAscent();
			int textWidth = g.getFontMetrics().stringWidth(text);
			int x = location.x + 5;
			int y = location.y + textHeight + 5;
			int maxX = content.getX() + content.getWidth();
			int maxY = content.getY() + content.getHeight();
			
			if(x + textWidth > maxX){
				x = Math.max(0, maxX - textWidth);
			}
			if(y > maxY){
				y = Math.max(textHeight, maxY);
			}
			
			g.setColor(Color.BLACK);
			g.drawString(text, x, y);
		}
	}
	
	private class ComponentNode extends JPanel {
		
		private final ComponentNode parent;
		
		private final Component component;
		private Component[] children = {};
		private final Map<Component, ComponentNode> nodes = new HashMap<Component, ComponentNode>();
		
		private JImage expandButton = new JImage(INVISIBLE_RETRACTED_ICON);
		private JLabel nameLabel = new JLabel();
		private boolean expanded = false;
		
		public ComponentNode(Component component, ComponentNode parent){
			super(new MigLayout("fillx, wrap 2, hidemode 3, insets 5 5 0 0, gapy 0", "[][grow, fill]", ""));
			this.parent = parent;
			this.component = component;
			setOpaque(false);
			setFocusable(true);
			expandButton.setFocusable(true);
			
			updateNode();
			
			expandButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					setExpanded(!expanded);
				}
			});
		}
		
		public String getComponentName(){
			Class<?> enclosingClass = component.getClass().getEnclosingClass();
			if (enclosingClass != null) {
				Class<?> superClass = component.getClass().getSuperclass();
				return superClass == null ? enclosingClass.getSimpleName() : superClass.getSimpleName();
			} else {
				return component.getClass().getSimpleName();
			}
		}
		
		public void updateNode(){
			nameLabel.setText(getComponentName());

			if(component instanceof Container){
				removeAll();
				add(expandButton, "gapy 0 5");
				add(nameLabel, "gapy 0 5");
				children = ((Container) component).getComponents();
				for(Component child : children){
					ComponentNode node;
					if(nodes.containsKey(child)){
						node = nodes.get(child);
						node.updateNode();
					}else{
						node = new ComponentNode(child, this);
						nodes.put(child, node);
					}
					add(node, "skip 1");
					node.setVisible(expanded);
				}
				for(Component nodeOwner : new ArrayList<Component>(nodes.keySet())){
					boolean contained = false;
					for(Component child : children){
						if(child == nodeOwner) contained = true;
					}
					if(!contained) nodes.remove(nodeOwner);
				}
			}
			
			expandButton.setImage(children.length > 0 ? (expanded ? EXPANDED_ICON : RETRACTED_ICON) : INVISIBLE_RETRACTED_ICON);
			
			revalidate();
			repaint();
		}
		
		public void setExpanded(boolean expanded){
			this.expanded = expanded;
			updateNode();
			expandButton.setImage(children.length > 0 ? (expanded ? EXPANDED_ICON : RETRACTED_ICON) : INVISIBLE_RETRACTED_ICON);
		}
		
		public void delete(){
			Container componentParent = component.getParent();
			if(componentParent != null){
				componentParent.remove(component);
				SwingUtil.revalidate(componentParent);
				componentParent.repaint();
			}
			parent.updateNode();
		}
		
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			
			// draw on the root node only
			if(parent == null){
				Point screenLocation = getLocationOnScreen();
				if(currentlySelected != null){
					Point location = currentlySelected.isShowing() ? currentlySelected.getLocationOnScreen() : null;
					if(location != null){
						location.x -= screenLocation.x;
						location.y -= screenLocation.y;
						
						int height = currentlySelected.getHeight();
						if(currentlySelected.children.length > 0 && currentlySelected.expanded){
							height = currentlySelected.nodes.get(currentlySelected.children[0]).getLocationOnScreen().y - currentlySelected.getLocationOnScreen().y;
						}
						
						g.setColor(currentlySelected.hasFocus() ? ColorUtil.INFO_BORDER_COLOR : Color.LIGHT_GRAY);
						g.fillRect(0, location.y, getWidth(), height);
					}
				}
				
				if(currentlyHovered != null && currentlyHovered != currentlySelected){
					Point location = currentlyHovered.isShowing() ? currentlyHovered.getLocationOnScreen() : null;
					if(location != null){
						location.x -= screenLocation.x;
						location.y -= screenLocation.y;
						
						int height = currentlyHovered.getHeight();
						if(currentlyHovered.children.length > 0 && currentlyHovered.expanded){
							height = currentlyHovered.nodes.get(currentlyHovered.children[0]).getLocationOnScreen().y - currentlyHovered.getLocationOnScreen().y;
						}
						
						g.setColor(ColorUtil.INFO_BACKGROUND_COLOR);
						g.fillRect(0, location.y, getWidth(), height);
					}
				}
			}
			
			if(expanded && children.length > 0){
				g.setColor(currentlySelected == this ? ColorUtil.INFO_BORDER_COLOR : Color.GRAY);
				g2d.setStroke(new BasicStroke(2));
				int x = expandButton.getX() + expandButton.getWidth() / 2;
				ComponentNode lastChild = nodes.get(children[children.length - 1]);
				g.drawLine(x, expandButton.getY() + expandButton.getHeight() / 2, x, lastChild.getY() + lastChild.getHeight());
			}
			
			super.paintComponent(g);
		}
		
	}

}

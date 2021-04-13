package graphics.swing.components;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import graphics.swing.SwingUtil;
import util.ColorUtil;

@Deprecated
public class DragContainer extends JPanel {
	
	private static final float DEFAULT_GHOST_OPACITY = 0.3f;
	
	private Component currentlyDragged = null;
	private Point lastMousePosition = null;
	private final Point ghostPosition = new Point();
	private boolean hasMoved = false;
	private Cursor oldCursor;
	
	private final List<DragListener> dragListeners = new ArrayList<DragListener>();
	
	private boolean enableXDrag = true;
	private boolean enableYDrag = true;
	private float ghostOpacity = DEFAULT_GHOST_OPACITY;
	
	public DragContainer() {
		super();
		init();
	}

	public DragContainer(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public DragContainer(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public DragContainer(LayoutManager layout) {
		super(layout);
		init();
	}
	
	private void init(){
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			
			@Override
			public void eventDispatched(AWTEvent event) {
				if(event instanceof MouseEvent){
					MouseEvent mouseEvent = (MouseEvent) event;
					Point mouseLocation = mouseEvent.getLocationOnScreen();
					SwingUtilities.convertPointFromScreen(mouseLocation, DragContainer.this);
					if(mouseEvent.getID() == MouseEvent.MOUSE_PRESSED && mouseEvent.getButton() == MouseEvent.BUTTON1){
						currentlyDragged = getComponentAt(mouseLocation);
						
						
						
						if(currentlyDragged != null){
							// if another component is overlapping currentlyDragged, currentlyDragged will be set to null again
							if(SwingUtil.containsScreenLocation(currentlyDragged, mouseEvent.getLocationOnScreen())){
								oldCursor = getCursor();
								setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
								lastMousePosition = mouseLocation;
								ghostPosition.setLocation(currentlyDragged.getLocation());
								hasMoved = false;
								repaint();
							}else{
								currentlyDragged = null;
							}
							
						}
					}else if(mouseEvent.getID() == MouseEvent.MOUSE_RELEASED){
						if(currentlyDragged != null){
							setCursor(oldCursor);
							repaint();

							for(DragListener listener : dragListeners){
								listener.onDrop(currentlyDragged, mouseLocation, ghostPosition);
							}
							currentlyDragged = null;
						}
					}else if(mouseEvent.getID() == MouseEvent.MOUSE_DRAGGED){
						if(currentlyDragged != null){
							int xDist = isEnableXDrag() ? mouseLocation.x - lastMousePosition.x : 0;
							int yDist = isEnableYDrag() ? mouseLocation.y - lastMousePosition.y : 0;
							lastMousePosition = mouseLocation;
							ghostPosition.setLocation(ghostPosition.x + xDist, ghostPosition.y + yDist);
							hasMoved = true;
							repaint();
							
							for(DragListener listener : dragListeners){
								listener.onDrag(currentlyDragged, mouseLocation, ghostPosition);
							}
						}
					}
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK + AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
	
	@Override
	protected void paintChildren(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		super.paintChildren(g);
		
		if(currentlyDragged != null && currentlyDragged != this && hasMoved){
			Composite oldComposite = g2d.getComposite();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getGhostOpacity()));
			
			// draw ghost of component
			BufferedImage ghost = new BufferedImage(currentlyDragged.getWidth(), currentlyDragged.getHeight(), BufferedImage.TYPE_INT_ARGB);
			currentlyDragged.printAll(ghost.createGraphics());
			g2d.drawImage(ghost, ghostPosition.x, ghostPosition.y, null);
			
			// fill component bounds
			g2d.setColor(ColorUtil.INFO_BACKGROUND_COLOR);
			g2d.fillRect(currentlyDragged.getX(), currentlyDragged.getY(), currentlyDragged.getWidth(), currentlyDragged.getHeight());
			
			// fill component bounds over ghost
			g2d.setColor(ColorUtil.INFO_BACKGROUND_COLOR);
			g2d.fillRect(ghostPosition.x, ghostPosition.y, currentlyDragged.getWidth(), currentlyDragged.getHeight());
			
			g2d.setComposite(oldComposite);
			
			// draw component bounds
			g2d.setColor(ColorUtil.INFO_BORDER_COLOR);
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
			g2d.drawRect(currentlyDragged.getX(), currentlyDragged.getY(), currentlyDragged.getWidth(), currentlyDragged.getHeight());
		}
	}
	
	public void addDragListener(DragListener listener){
		dragListeners.add(listener);
	}
	
	public boolean removeDragListener(DragListener listener){
		return dragListeners.remove(listener);
	}

	public boolean isEnableXDrag() {
		return enableXDrag;
	}

	public void setEnableXDrag(boolean enableXDrag) {
		this.enableXDrag = enableXDrag;
		repaint();
	}

	public boolean isEnableYDrag() {
		return enableYDrag;
	}

	public void setEnableYDrag(boolean enableYDrag) {
		this.enableYDrag = enableYDrag;
		repaint();
	}

	public float getGhostOpacity() {
		return ghostOpacity;
	}

	public void setGhostOpacity(float ghostOpacity) {
		this.ghostOpacity = ghostOpacity;
	}
	
	public static interface DragListener {
		public void onDrag(Component component, Point mousePosition, Point ghostPosition);
		public void onDrop(Component component, Point mousePosition, Point ghostPosition);
	}
	
}

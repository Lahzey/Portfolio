package graphics.swing.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;

import graphics.swing.TestFrame;

/**
 * A JPanel that can be zoomed and dragged around (like a map).
 * @author A469627
 *
 */
public class ZoomPanel extends JPanel{
	
	private static final double ZOOM_SPEED = 0.05;
	
	public boolean allowDrag = true;
	public boolean allowZoom = true;
	
	private Dimension offset;
	private double currentZoom = 1;
	
	private Dimension currentDragOffset = new Dimension();
	private Point dragPoint;
	
	/**
	 * Creates a new panel with a zero offset, meaning the 0/0 coordinate is at 0/0 (top left corner).
	 */
	public ZoomPanel(){
		this(new Dimension());
	}
	
	/**
	 * Creates a new panel with the given offset. The offset is <b>added</b> to the normal coordinate, so if the offset width is 200, the lowest visible x coordinate is 200.
	 */
	public ZoomPanel(Dimension offset){
		this.offset = offset;
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(dragPoint != null && allowDrag){
					currentDragOffset.setSize(e.getX() - dragPoint.x, e.getY() - dragPoint.y);
					repaint();
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				dragPoint = null;
				if(allowDrag){
					ZoomPanel.this.offset.width += currentDragOffset.width;
					ZoomPanel.this.offset.height += currentDragOffset.height;
					currentDragOffset.setSize(0, 0);;
				}
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				dragPoint = e.getPoint();
			}
		});
		
		addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(allowZoom){
					Point prevMousePos = convertPointFromScreen(MouseInfo.getPointerInfo().getLocation());
					
					currentZoom *= -e.getWheelRotation() * ZOOM_SPEED + 1;
					
					Point mousePos = convertPointFromScreen(MouseInfo.getPointerInfo().getLocation());
					
					int xMovement = (int) ((mousePos.x - prevMousePos.x) * currentZoom);
					int yMovement = (int) ((mousePos.y - prevMousePos.y) * currentZoom);
					ZoomPanel.this.offset.setSize(ZoomPanel.this.offset.width + xMovement, ZoomPanel.this.offset.height + yMovement);
					
					repaint();
				}
			}
		});
	}
	
	private Point convertPointFromScreen(Point point){
		SwingUtilities.convertPointFromScreen(point, ZoomPanel.this);
		point.setLocation((point.x - ZoomPanel.this.offset.width) / currentZoom, (point.y - ZoomPanel.this.offset.height) / currentZoom);
		return point;
	}
	
	public double getZoom(){
		return currentZoom;
	}
	
	public Dimension getOffset(){
		return new Dimension(offset);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		Dimension offset;
		offset = new Dimension(this.offset.width + currentDragOffset.width, this.offset.height + currentDragOffset.height);
		offset.width /= currentZoom;
		offset.height /= currentZoom;
		
		g2.scale(currentZoom, currentZoom);
		g.translate(offset.width, offset.height);
		
		int width = (int) (getWidth() / currentZoom);
		int height = (int) (getHeight() / currentZoom);
		int minX = -offset.width;
		int maxX = minX + width;
		int minY = -offset.height;
		int maxY = minY + height;
		drawBounds(g, minX, minY, maxX, maxY);
	}
	
	/**
	 * Override this method to do custom painting in the given bounds.
	 * Those bounds are limited to what is visible and the offset is already added to the coordinate values.
	 * @param g the Graphics object to paint with.
	 * @param minX the lowest visible x coordinate
	 * @param minY the lowest visible y coordinate
	 * @param maxX the highest visible x coordinate
	 * @param maxY the highest visible y coordinate
	 */
	public void drawBounds(Graphics g, int minX, int minY, int maxX, int maxY){
		
	}
	
	public static void main(String[] args) {
		TestFrame testFrame = new TestFrame();
		ZoomPanel panel = new ZoomPanel();
		panel.setPreferredSize(new Dimension(500, 500));
		testFrame.add(panel);
		testFrame.pack();
		JImage image1 = new JImage(FontAwesomeRegular.ADDRESS_BOOK);
		image1.setPreferredSize(new Dimension(50, 50));
		panel.add(image1);
		JImage image2 = new JImage(FontAwesomeRegular.ANGRY, Color.RED);
		image2.setPreferredSize(new Dimension(100, 100));
		panel.add(image2);
		JImage image3 = new JImage(FontAwesomeRegular.BELL, Color.YELLOW);
		image3.setPreferredSize(new Dimension(20, 20));
		panel.add(image3);
		panel.revalidate();
		panel.repaint();
	}
	
	
}

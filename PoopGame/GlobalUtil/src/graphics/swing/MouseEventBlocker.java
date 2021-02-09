package graphics.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

public class MouseEventBlocker extends JPanel implements MouseListener, MouseMotionListener, FocusListener {
	
	private JRootPane rootPane;
	private Component blockedComponent;
	
	private MouseEventBlocker(JRootPane rootPane, Component blockedComponent){
		super(new BorderLayout());
		this.rootPane = rootPane;
		this.blockedComponent = blockedComponent;
		
		setOpaque(false);
		addMouseListener(this);
		addMouseMotionListener(this);
	    addFocusListener(this);
	}
	
	public static void connect(Component comp){
		Window window = SwingUtilities.getWindowAncestor(comp);
		if(window instanceof RootPaneContainer){
			JRootPane rootPane = ((RootPaneContainer) window).getRootPane();
			rootPane.setGlassPane(new MouseEventBlocker(rootPane, comp));
			rootPane.getGlassPane().setVisible(true);
			rootPane.revalidate();
			rootPane.repaint();
		}else{
			throw new IllegalArgumentException("Given component is not inside a RootPaneContainer (like JFrame or JDialog).");
		}
	}
	
	public static boolean disconnect(Component comp){
		Window window = SwingUtilities.getWindowAncestor(comp);
		if(window instanceof RootPaneContainer){
			JRootPane rootPane = ((RootPaneContainer) window).getRootPane();
			if(rootPane.getGlassPane() instanceof MouseEventBlocker){
				rootPane.setGlassPane(new EmptyPanel());
				rootPane.revalidate();
				rootPane.repaint();
				return true;
			}else{
				return false;
			}
		}else{
			throw new IllegalArgumentException("Given component is not inside a RootPaneContainer (like JFrame or JDialog).");
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Component destinationComponent = SwingUtilities.getDeepestComponentAt(rootPane.getContentPane(), e.getX(), e.getY());
		
		boolean insideBlockedComponent = false;
		Component comp = destinationComponent;
		while(comp != null && !insideBlockedComponent){
			if(comp == blockedComponent) insideBlockedComponent = true;
			else comp = comp.getParent();
		}
		
		if(!insideBlockedComponent && destinationComponent != null){
			destinationComponent.dispatchEvent(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Component destinationComponent = SwingUtilities.getDeepestComponentAt(rootPane.getContentPane(), e.getX(), e.getY());
		
		boolean insideBlockedComponent = false;
		Component comp = destinationComponent;
		while(comp != null && !insideBlockedComponent){
			if(comp == blockedComponent) insideBlockedComponent = true;
			else comp = comp.getParent();
		}
		
		if(!insideBlockedComponent && destinationComponent != null){
			destinationComponent.dispatchEvent(e);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Component destinationComponent = SwingUtilities.getDeepestComponentAt(rootPane.getContentPane(), e.getX(), e.getY());
		
		boolean insideBlockedComponent = false;
		Component comp = destinationComponent;
		while(comp != null && !insideBlockedComponent){
			if(comp == blockedComponent) insideBlockedComponent = true;
			else comp = comp.getParent();
		}
		
		if(!insideBlockedComponent && destinationComponent != null){
			destinationComponent.dispatchEvent(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Component destinationComponent = SwingUtilities.getDeepestComponentAt(rootPane.getContentPane(), e.getX(), e.getY());
		
		boolean insideBlockedComponent = false;
		Component comp = destinationComponent;
		while(comp != null && !insideBlockedComponent){
			if(comp == blockedComponent) insideBlockedComponent = true;
			else comp = comp.getParent();
		}
		
		if(!insideBlockedComponent && destinationComponent != null){
			destinationComponent.dispatchEvent(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Component destinationComponent = SwingUtilities.getDeepestComponentAt(rootPane.getContentPane(), e.getX(), e.getY());
		
		boolean insideBlockedComponent = false;
		Component comp = destinationComponent;
		while(comp != null && !insideBlockedComponent){
			if(comp == blockedComponent) insideBlockedComponent = true;
			else comp = comp.getParent();
		}
		
		if(!insideBlockedComponent && destinationComponent != null){
			destinationComponent.dispatchEvent(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Component destinationComponent = SwingUtilities.getDeepestComponentAt(rootPane.getContentPane(), e.getX(), e.getY());
		
		boolean insideBlockedComponent = false;
		Component comp = destinationComponent;
		while(comp != null && !insideBlockedComponent){
			if(comp == blockedComponent) insideBlockedComponent = true;
			else comp = comp.getParent();
		}
		
		if(!insideBlockedComponent && destinationComponent != null){
			destinationComponent.dispatchEvent(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Component destinationComponent = SwingUtilities.getDeepestComponentAt(rootPane.getContentPane(), e.getX(), e.getY());
		
		boolean insideBlockedComponent = false;
		Component comp = destinationComponent;
		while(comp != null && !insideBlockedComponent){
			if(comp == blockedComponent) insideBlockedComponent = true;
			else comp = comp.getParent();
		}
		
		if(!insideBlockedComponent && destinationComponent != null){
			destinationComponent.dispatchEvent(e);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {}

	@Override
	public void focusLost(FocusEvent e) {
		if(isVisible()){
			requestFocus();
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if(aFlag){
			requestFocus();
		}
		super.setVisible(aFlag);
	}
	
	private static class EmptyPanel extends JPanel {
		@Override
		public Dimension getMaximumSize() {
			return new Dimension();
		}
		@Override
		public Dimension getPreferredSize() {
			return new Dimension();
		}
		@Override
		public boolean contains(int x, int y) {
			return false;
		}
		@Override
		public boolean isOpaque() {
			return false;
		}
	}

}

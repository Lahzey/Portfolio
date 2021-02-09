package com.creditsuisse.graphics.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.apache.commons.collections4.map.MultiKeyMap;

public class SwingUtil {
	
	private static final Map<Component, MouseListener> MOUSE_LISTENERS = Collections.synchronizedMap(new HashMap<Component, MouseListener>());
	private static final MultiKeyMap<Object, MouseListener> RECURSIVE_MOUSE_EXITED_LISTENERS = new MultiKeyMap<Object, MouseListener>();
	
	static{
		Toolkit.getDefaultToolkit().addAWTEventListener(new MouseEventProcessor(), AWTEvent.MOUSE_EVENT_MASK);
	}

	public static void removeAllActionListener(JButton button) {
		for (ActionListener al : button.getActionListeners()) {
			button.removeActionListener(al);
		}
	}

	public static void showInFrame(Component component) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(component);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static JButton createIconButton(Icon normal, Icon hover, Icon pressed, Icon disabled) {
		JButton button = new JButton();
		button.setBorder(null);
		button.setContentAreaFilled(false);
		button.setIcon(normal);
		button.setRolloverIcon(hover);
		button.setPressedIcon(pressed);
		button.setDisabledIcon(disabled);
		return button;
	}

	public static int indexOf(Component component) {
		if (component != null && component.getParent() != null) {
			for (int i = 0; i < component.getParent().getComponents().length; i++) {
				if (component.getParent().getComponents()[i] == component)
					return i;
			}
		}
		return -1;
	}

	/**
	 * Copied from Java 1.7:<br/>
	 * Revalidates the component hierarchy up to the nearest validate root.
	 * <p>
	 * This method first invalidates the component hierarchy starting from this
	 * component up to the nearest validate root. Afterwards, the component
	 * hierarchy is validated starting from the nearest validate root.
	 * <p>
	 * This is a convenience method supposed to help application developers
	 * avoid looking for validate roots manually. Basically, it's equivalent to
	 * first calling the {@link Component#invalidate()} method on this
	 * component, and then calling the {@link Component#validate()} method on
	 * the nearest validate root.
	 *
	 * @param component
	 *            the component to revalidate
	 * @see Container#isValidateRoot
	 */
	public static void revalidate(Component component) {
		revalidateSynchronously(component);
	}

	/**
	 * Revalidates the component synchronously.
	 */
	private static final void revalidateSynchronously(Component component) {
		synchronized (component.getTreeLock()) {
			component.invalidate();

			Container root = component.getParent();
			if (root == null) {
				// There's no parents. Just validate itself.
				component.validate();
			} else {
				while (root.getParent() != null) {
					root = root.getParent();
				}
				root.validate();
			}
		}
	}

	/**
	 * Creates an {@link AWTEventListener} that will call the given listener if
	 * the {@link MouseEvent} occurred inside the given component, one of its
	 * children or the children's children etc. (recursive).
	 * 
	 * @param component
	 *            the component the {@link MouseEvent} has to occur inside
	 * @param listener
	 *            the listener to be called if that is the case
	 */
	public static void addRecursiveMouseListener(final Component component, final MouseListener listener) {
		MOUSE_LISTENERS.put(component, listener);
		MouseListener exitedListener = new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				listener.mouseExited(e);
			}
		};
		component.addMouseListener(exitedListener);
		RECURSIVE_MOUSE_EXITED_LISTENERS.put(component, listener, exitedListener);
	}
	
	public static boolean removeRecursiveMouseListener(final Component component, final MouseListener listener) {
		if(MOUSE_LISTENERS.containsKey(component)){
			MOUSE_LISTENERS.remove(component);
			component.removeMouseListener(RECURSIVE_MOUSE_EXITED_LISTENERS.get(component, listener));
			RECURSIVE_MOUSE_EXITED_LISTENERS.removeMultiKey(component, listener);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Checks if the given location (relative to the screen) is inside the given component
	 * @param component the component to check with
	 * @param screenLocation the location, relative to the screen
	 * @return true if it is inside the component, false otherwise
	 */
	public static boolean containsScreenLocation(Component component, Point screenLocation){
		Window window = SwingUtilities.getWindowAncestor(component);
		if(window == null){
			return false;
		}else{
			Component compAtLocation = getDeepestComponentAt(window, screenLocation);
			if(compAtLocation == null) return false;
			else return compAtLocation == component || containsComponent(component, compAtLocation);
		}
	}
	
	public static boolean containsComponent(Component container, Component child){
		if(container == null || child == null) return false;
		else{
			Container parent = child.getParent();
			if(parent == null){
				return false;
			}else if(parent == container){
				return true;
			}else{
				return containsComponent(container, parent);
			}
		}
	}
	
	public static Component getDeepestComponentAt(Component comp, Point screenLocation){
		Window window = SwingUtilities.getWindowAncestor(comp);
		if(window != null){
			return getDeepestComponentAt(window, screenLocation);
		}else{
			return null;
		}
	}
	
	public static Component getDeepestComponentAt(Window window, Point screenLocation){
		Container contentPane = null;
		if(window instanceof JFrame){
			contentPane = ((JFrame) window).getContentPane();
		}else if(window instanceof JWindow){
			contentPane = ((JWindow) window).getContentPane();
		}else if(window instanceof JDialog){
			contentPane = ((JDialog) window).getContentPane();
		}
		if(contentPane != null){
			if(!contentPane.isShowing()) return null;
			Point windowLocation = contentPane.getLocationOnScreen();
			Point relativeLocation = new Point(screenLocation.x - windowLocation.x, screenLocation.y - windowLocation.y);
			return getDeepestComponentAt(contentPane, relativeLocation.x, relativeLocation.y);
		}else{
			Point windowLocation = window.getLocationOnScreen();
			Point relativeLocation = new Point(screenLocation.x - windowLocation.x, screenLocation.y - windowLocation.y);
			if(relativeLocation.x >= 0 && relativeLocation.y >= 0 && relativeLocation.x < window.getWidth() && relativeLocation.y < window.getHeight()){
				Component comp = window.getComponentAt(relativeLocation);
				if(comp == null) return null;
				else return getDeepestComponentAt(comp, relativeLocation.x - comp.getX(), relativeLocation.y - comp.getY());
			}else{
				return null;
			}
		}
	}
	
	public static Component getDeepestComponentAt(Component container, int x, int y){
		return SwingUtilities.getDeepestComponentAt(container, x, y);
//		
//		if(x >= 0 && y >= 0 && x < container.getWidth() && y < container.getHeight()){
//			Component comp = container.getComponentAt(x, y);
//			if(comp != container){
//				return getDeepestComponentAt(comp, x - comp.getX(), y - comp.getY());
//			}else{
//				return comp;
//			}
//		}else{
//			return null;
//		}
	}
	
	private static class MouseEventProcessor implements AWTEventListener {
		
		private final List<Component> affectedComponents = new ArrayList<Component>();
		
		@Override
		public void eventDispatched(AWTEvent event) {
			if(event instanceof MouseEvent) {
				MouseEvent mouseEvent = (MouseEvent) event;
				affectedComponents.clear();
				Component comp = SwingUtilities.getDeepestComponentAt(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				while(comp != null){
					affectedComponents.add(comp);
					comp = comp.getParent();
				}
				
				Set<Component> listeners;
				synchronized (MOUSE_LISTENERS) {
					listeners = new HashSet<Component>(MOUSE_LISTENERS.keySet());
				}
				
				for(Component listening : listeners){
					if(affectedComponents.contains(listening)){
						MouseListener listener = MOUSE_LISTENERS.get(listening);
						if(event.getID() == MouseEvent.MOUSE_PRESSED) {
							listener.mousePressed(mouseEvent);
						}
						if(event.getID() == MouseEvent.MOUSE_RELEASED) {
							listener.mouseReleased(mouseEvent);
						}
						if(event.getID() == MouseEvent.MOUSE_ENTERED) {
							listener.mouseEntered(mouseEvent);
						}
						if(event.getID() == MouseEvent.MOUSE_EXITED) {
							listener.mouseExited(mouseEvent);
						}
						if(event.getID() == MouseEvent.MOUSE_CLICKED){
							listener.mouseClicked(mouseEvent);
						}
					}
				}
			}
		}
		
	}
}

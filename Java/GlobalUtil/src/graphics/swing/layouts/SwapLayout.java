package graphics.swing.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import util.LoopThread;

public class SwapLayout implements LayoutManager, PropertyChangeListener {

	public static int HORIZONTAL = 0;
	public static int VERTICAL = 0;
	
	private long swapDuration;
	private int orientation;

	private Map<Container, SwapData> swapData = new HashMap<Container, SwapData>();

	private KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

	public SwapLayout(long swapDuration) {
		this(swapDuration, HORIZONTAL);
	}

	public SwapLayout(long swapDuration, int orientation) {
		this.swapDuration = swapDuration;
		this.orientation = orientation;
		focusManager.addPropertyChangeListener(this);

	}
	
	private SwapData getOrCreateData(Container parent) {
		SwapData data = swapData.get(parent);
		if (data == null) {
			data = new SwapData();
			swapData.put(parent, data);
		}
		return data;
	}

	public void swap(final Container parent, int swapToIndex) {
		final SwapData data = getOrCreateData(parent);
		data.currentComponentIndex = swapToIndex;
		data.swappedAt = System.currentTimeMillis();
		data.lastUpdate = data.swappedAt;
		
		transferFocusIfMisplaced(focusManager.getFocusOwner());

		new LoopThread(30, 1000 / 30) {

			@Override
			public void loopedRun() {
				if (data.lastUpdate > data.swappedAt + swapDuration) {
					terminate();
				}

				parent.revalidate();
				parent.repaint();
			}
		}.start();
	}

	public int getCurrentComponentIndex(Container parent) {
		return getOrCreateData(parent).currentComponentIndex;
	}

	@Override
	public void layoutContainer(Container parent) {
		final SwapData data = getOrCreateData(parent);
		long time = System.currentTimeMillis();
		boolean inTransition = data.swappedAt + swapDuration > time;
		long deltaTime = time - data.lastUpdate;
		double progress = Math.min(1, deltaTime / (double) (data.swappedAt + swapDuration - data.lastUpdate));

		int i = 0;
		int currentPos = 0;
		for (Component component : parent.getComponents()) {
			boolean shouldDisplay = i == data.currentComponentIndex && component.isVisible();
			if (inTransition) {
				int sizeTarget = shouldDisplay ? getSize(parent) : 0;
				int sizeDelta = sizeTarget - getSize(component);
				int sizeProgress = (int) (sizeDelta * progress);
				int newSize = getSize(component) + sizeProgress;

				setBounds(parent, component, currentPos, newSize);
				currentPos += newSize;
			} else {
				if (shouldDisplay) {
					setBounds(parent, component, 0, getSize(parent));
				} else {
					setBounds(parent, component, getSize(parent), 5); // ensuring component stays active, useful for LibGDX Swing Canvases
				}
			}

			i++;
		}

		data.lastUpdate = time;
	}

	private int getSize(Component component) {
		return orientation == HORIZONTAL ? component.getWidth() : component.getHeight();
	}

	private void setBounds(Container parent, Component component, int position, int size) {
		if (orientation == HORIZONTAL) {
			component.setBounds(position, 0, size, parent.getHeight());
		} else {
			component.setBounds(0, position, parent.getWidth(), size);
		}
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		final SwapData data = getOrCreateData(parent);
		if (parent.getComponentCount() > data.currentComponentIndex) {
			return parent.getComponent(data.currentComponentIndex).getPreferredSize();
		} else {
			return new Dimension();
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		final SwapData data = getOrCreateData(parent);
		if (parent.getComponentCount() > data.currentComponentIndex) {
			return parent.getComponent(data.currentComponentIndex).getMinimumSize();
		} else {
			return new Dimension();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
        if ("focusOwner".equals(propertyName) && evt.getNewValue() != null) {
            Component focusOwner = (Component) evt.getNewValue();
            transferFocusIfMisplaced(focusOwner);
        }
	}
	
	private void transferFocusIfMisplaced(final Component focusOwner) {
		if (focusOwner == null)  {
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				synchronized (focusOwner.getTreeLock()) {
					for (Container parent : swapData.keySet()) {
			        	SwapData data = swapData.get(parent);
			        	for (int i = 0; i < parent.getComponentCount(); i++) {
			        		Component child = parent.getComponent(i);
			        		if (SwingUtilities.isDescendingFrom(focusOwner, child)) {
			        			if (i != data.currentComponentIndex) {
			        				focusOwner.transferFocus();
			        			}
			        			break;
			        		}
			        	}
			        }
				}
			}
		});
	}
	
	private class SwapData {
		public int currentComponentIndex = 0;
		public long swappedAt = 0;
		public long lastUpdate = 0;
	}

}

package graphics.swing.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import util.LoopThread;

public class SwapLayout implements LayoutManager {
	
	public static int HORIZONTAL = 0;
	public static int VERTICAL = 0;

	private long swapDuration;
	private int orientation;
	
	private int currentComponentIndex = 0;
	private long swappedAt = 0;
	private long lastUpdate = 0;

	public SwapLayout(long swapDuration) {
		this(swapDuration, HORIZONTAL);
	}

	public SwapLayout(long swapDuration, int orientation) {
		this.swapDuration = swapDuration;
		this.orientation = orientation;
	}
	
	public void swap(final Component container, int swapToIndex) {
		currentComponentIndex = swapToIndex;
		swappedAt = System.currentTimeMillis();
		lastUpdate = swappedAt;
		
		new LoopThread(30, 1000 / 30) {
			
			@Override
			public void loopedRun() {
				if (lastUpdate > swappedAt + swapDuration) {
					terminate();
				}
				
				container.revalidate();
				container.repaint();
			}
		}.start();
	}
	
	public int getCurrentComponentIndex() {
		return currentComponentIndex;
	}

	@Override
	public void layoutContainer(Container parent) {
		long time = System.currentTimeMillis();
		boolean inTransition = swappedAt + swapDuration > time;
		long deltaTime = time - lastUpdate;
		double progress = Math.min(1, deltaTime / (double) (swappedAt + swapDuration - lastUpdate));
		
		int i = 0;
		int currentPos = 0;
		for (Component component : parent.getComponents()) {
			boolean shouldDisplay = i == currentComponentIndex && component.isVisible();
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
		
		lastUpdate = time;
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
	public void addLayoutComponent(String name, Component comp) {}

	@Override
	public void removeLayoutComponent(Component comp) {}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		if (parent.getComponentCount() > currentComponentIndex) {
			return parent.getComponent(currentComponentIndex).getPreferredSize();
		} else {
			return new Dimension();
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		if (parent.getComponentCount() > currentComponentIndex) {
			return parent.getComponent(currentComponentIndex).getMinimumSize();
		} else {
			return new Dimension();
		}
	}

}

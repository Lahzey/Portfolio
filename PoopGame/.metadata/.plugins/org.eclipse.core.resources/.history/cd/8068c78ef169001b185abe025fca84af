package com.creditsuisse.graphics.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.accessibility.Accessible;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.ToolTipUI;
import javax.swing.plaf.basic.BasicPanelUI;

public class ContainerToolTip extends JToolTip {
	
    private static final String uiClassID = "ContainerToolTipUI";
	
	private static ContainerToolTipUI ui = new ContainerToolTipUI();
	
	public ContainerToolTip() {
		this(new BorderLayout());
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		Border gap = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		setBorder(BorderFactory.createCompoundBorder(border, gap));
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	public ContainerToolTip(LayoutManager mgr) {
		setLayout(mgr);
	}

	@Override
    public void updateUI() {
        setUI(ui);
    }

	@Override
    public String getUIClassID() {
        return uiClassID;
    }
	
	@Override
	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}
	
	private static class ContainerToolTipUI extends ToolTipUI {

		private BasicPanelUI delegateUI = new BasicPanelUI();

		@Override
	    public void installUI(JComponent c) {
	        super.installUI(c);
	        installDefaults(c);
	    }

		@Override
	    public void uninstallUI(JComponent c) {
	        uninstallDefaults(c);
	        super.uninstallUI(c);
	    }

	    protected void installDefaults(JComponent p) {
	        LookAndFeel.installColorsAndFont(p,
	                                         "Panel.background",
	                                         "Panel.foreground",
	                                         "Panel.font");
	        LookAndFeel.installBorder(p,"Panel.border");
	        LookAndFeel.installProperty(p, "opaque", Boolean.TRUE);
	    }

	    protected void uninstallDefaults(JComponent p) {
	        LookAndFeel.uninstallBorder(p);
	    }

		@Override
		public int getBaseline(JComponent c, int width, int height) {
			return delegateUI.getBaseline(c, width, height);
		}

		@Override
		public BaselineResizeBehavior getBaselineResizeBehavior(JComponent c) {
			return delegateUI.getBaselineResizeBehavior(c);
		}

		@Override
		public void paint(Graphics g, JComponent c) {
			delegateUI.paint(g, c);
		}

		@Override
		public void update(Graphics g, JComponent c) {
			delegateUI.update(g, c);
		}

		@Override
		public Dimension getPreferredSize(JComponent c) {
			return delegateUI.getPreferredSize(c);
		}

		@Override
		public Dimension getMinimumSize(JComponent c) {
			return delegateUI.getMinimumSize(c);
		}

		@Override
		public Dimension getMaximumSize(JComponent c) {
			return delegateUI.getMaximumSize(c);
		}

		@Override
		public boolean contains(JComponent c, int x, int y) {
			return delegateUI.contains(c, x, y);
		}

		@Override
		public int getAccessibleChildrenCount(JComponent c) {
			return delegateUI.getAccessibleChildrenCount(c);
		}

		@Override
		public Accessible getAccessibleChild(JComponent c, int i) {
			return delegateUI.getAccessibleChild(c, i);
		}
		
		
	}

}

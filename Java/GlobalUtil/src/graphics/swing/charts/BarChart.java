package graphics.swing.charts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import graphics.swing.components.ContainerToolTip;
import net.miginfocom.swing.MigLayout;

public class BarChart extends JPanel {
	
	private Map<Integer, Integer> data = new HashMap<Integer, Integer>(); // <xValue, yValue>
	private int minXValue = Integer.MAX_VALUE;
	private int maxXValue = Integer.MIN_VALUE;
	private int minYValue = Integer.MAX_VALUE;
	private int maxYValue = Integer.MIN_VALUE;
	
	private int barGap = 1;
	private boolean drawHorizontalLines = true;
	
	public BarChart() {
		super(new MigLayout("insets 0, fill, gapx 1", "[grow, fill]", "[grow, fill]"));
	}
	
	public void addValue(int xValue, int yValue) {
		addValueInternal(xValue, yValue);
		onDataUpdate();
	}
	
	public void addValues(Map<Integer, Integer> values) {
		for(Integer xValue : values.keySet()) {
			addValueInternal(xValue, values.get(xValue));
		}
		onDataUpdate();
	}
	
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return super.getPreferredSize();
	}
	
	private void addValueInternal(int xValue, int yValue) {
		data.put(xValue, yValue);
		
		if(xValue < minXValue) {
			minXValue = xValue;
		}
		if(xValue > maxXValue) {
			maxXValue = xValue;
		}
		
		if(yValue < minYValue) {
			minYValue = yValue;
		}
		if(yValue > maxYValue) {
			maxYValue = yValue;
		}
	}
	
	public void clear() {
		data.clear();
		minXValue = Integer.MAX_VALUE;
		maxXValue = Integer.MIN_VALUE;
		minYValue = Integer.MAX_VALUE;
		maxYValue = Integer.MIN_VALUE;
		onDataUpdate();
	}
	
	private void setLayout() {
		setLayout(new MigLayout("insets 0, fill, gapx 1", "[grow, fill]", "[grow, fill]"));
	}
	
	private void onDataUpdate() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				removeAll();
				
				JPanel yAxisPanel = new JPanel(new MigLayout("wrap 1, insets 0, fill", "[grow, fill]", "[grow, fill][]"));
				yAxisPanel.setOpaque(false);
				yAxisPanel.add(new YAxisLabelPanel(getYMeasurePoints()));
				yAxisPanel.add(new JLabel(" "));
				add(yAxisPanel);
				
				for(int x = getMinX(); x <= getMaxX(); x += getXStep()) {
					final int xValue = x;
					final int yValue = data.getOrDefault(x, 0); // should always have a value
					JPanel xValuePanel = new JPanel(new MigLayout("wrap 1, insets 0, fill", "[grow, fill]", "[grow, fill][]")) {
						@Override
						public JToolTip createToolTip() {
							ContainerToolTip toolTip = new ContainerToolTip();
							toolTip.add(getTooltipContent(xValue, yValue));
							return toolTip;
						}
					};
					xValuePanel.setToolTipText("");
					xValuePanel.setOpaque(false);
					
					xValuePanel.add(new Bar(x, yValue, Color.ORANGE, Color.ORANGE, 3));
					add(xValuePanel);
					
					xValuePanel.add(new JLabel(x + "", SwingConstants.CENTER));
				}
				
				revalidate();
				repaint();
			}
		});
	}
	
	protected String getToolTipText(int xValue, int yValue) {
		return "" + yValue;
	}
	
	protected JComponent getTooltipContent(int xValue, int yValue) {
		return new JLabel(getToolTipText(xValue, yValue));
	}
	 
	protected int getXStep() {
		return 1;
	}
	
	protected int getYStep() {
		return 1;
	}
	
	protected int getLeftPadding() {
		return 5;
	}
	
	protected int getBottomPadding() {
		return 5;
	}
	
	protected int getYStepHeight(Graphics g) {
		return g.getFontMetrics().getHeight() * 2;
	}
	
	protected int getXStepWidth(Graphics g) {
		int amountOfXSteps = (maxXValue - minXValue) / getXStep();
		int totalWidth = getWidth();
		int gapWidth = (amountOfXSteps - 1) * getBarGap();
		return (totalWidth - gapWidth) / amountOfXSteps;
	}
	
	protected int getMinX() {
		return minXValue;
	}
	
	protected int getMaxX() {
		return maxXValue;
	}
	
	protected int getMinY() {
		return minYValue;
	}
	
	protected int getMaxY() {
		return maxYValue;
	}
	
	protected int getBarGap() {
		return barGap;
	}
	
	protected void setBarGap(int barGap) {
		this.barGap = barGap;
		setLayout();
	}
	
	protected int getBarHeight(int x, int y, int fullHeight) {
		float ascent = y - getMinY();
		float maxAscent = getMaxY() - getMinY();
		float precentageHeight = ascent / maxAscent;
		int barHeight = Math.round(fullHeight * precentageHeight);
		if(precentageHeight > 0 && barHeight == 0) {
			barHeight = 1;
		}
		return barHeight;
	}
	
	private List<Integer> getYMeasurePoints() {
		List<Integer> yMeasurePoints = new ArrayList<Integer>();
		for(int yValue = getMaxY(); yValue >= getMinY(); yValue -= getYStep()) {
			yMeasurePoints.add(yValue);
		}
		return yMeasurePoints;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Component[] children = getComponents();
		if(drawHorizontalLines && children.length > 1) {
			Container firstColumn = (Container) children[1]; // first child is y-axis
			Container lastColumn = (Container) children[children.length - 1];
			Component firstBar = firstColumn.getComponents()[0];
			int startX = firstColumn.getBounds().x;
			int endX = lastColumn.getBounds().x + lastColumn.getBounds().width;
			int startY = firstBar.getBounds().y + firstColumn.getBounds().y;
			int endY = startY + firstBar.getBounds().height;
			
			List<Integer> measurePoints = getYMeasurePoints();
			float increment = ((float) firstBar.getBounds().height) / Math.max(measurePoints.size() - 1, 1);
			
			g.setColor(getBackground().darker());
			for(float y = startY; y <= endY; y += increment) {
				g.drawLine(startX, (int) Math.ceil(y), endX, (int) Math.ceil(y));
			}
		}
	}
	
	private class Bar extends JComponent {
		
		private int x;
		private int y;
		
		private Color fillColor;
		private Color borderColor;
		private int borderThickness;
		
		public Bar(int x, int y, Color fillColor, Color borderColor, int borderThickness) {
			this.x = x;
			this.y = y;
			this.fillColor = fillColor;
			this.borderColor = borderColor;
			this.borderThickness = borderThickness;
			
			setOpaque(false);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int barHeight = getBarHeight(x, y, getHeight());
			int x = 0;
			int y = getHeight() - barHeight;

			g.setColor(borderColor);
			g.fillRect(x, y, getWidth(), barHeight);
			
			g.setColor(fillColor);
			g.fillRect(x + borderThickness, y + borderThickness, Math.max(getWidth() - borderThickness * 2, 0), Math.max(barHeight - borderThickness * 2, 0));
		}
	}
	
	private class YAxisLabelPanel extends JPanel {
		
		private List<JLabel> labels = new ArrayList<JLabel>();
		
		public YAxisLabelPanel(List<Integer> yMeasurePoints) {
			setLayout(null);
			for(int measurePoint : yMeasurePoints) {
				JLabel label = new JLabel(measurePoint + "", SwingConstants.CENTER);
				add(label);
				labels.add(label);
			}
		}
		
		@Override
		public void layout() {
			int totalHeight = getHeight();
			int totalWidth = getWidth();
			int heightPerLabel = totalHeight / (labels.size() - 1);
			int yPosition = 0;
			for(JLabel label : labels) {
				Dimension size = label.getMinimumSize();
				int y = Math.min(Math.max(yPosition - size.height / 2, 0), totalHeight - size.height);
				label.setBounds(getLeftPadding(), y, totalWidth - getLeftPadding() * 2, size.height);
				yPosition += heightPerLabel;
			}
		}
		
		@Override
		public Dimension getMinimumSize() {
			return new Dimension((labels.size() > 0 ? labels.get(0).getMinimumSize().width + getLeftPadding() * 2 : 0), super.getMinimumSize().height);
		}
		
	}
	
}

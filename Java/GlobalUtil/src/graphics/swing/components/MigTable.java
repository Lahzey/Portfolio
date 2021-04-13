package graphics.swing.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.Inspector;
import graphics.swing.SwingUtil;
import graphics.swing.TestFrame;
import graphics.swing.layouts.CustomMigLayout;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import util.Cloner;
import util.ColorUtil;

public abstract class MigTable<T> extends JPanel {
	
	public static final int NO_SORT = 0;
	public static final int DESCENDING_SORT = 1;
	public static final int ASCENDING_SORT = 2;
	
	private static final int SORT_ICON_GAP = 3;
	
	// the amount of pixels around the divider at which he still can be dragged (may not work if there is focusable content at that location)
	private static final int DIVIDER_DRAG_RADIUS = 2;

	private CustomMigLayout layout;
	
	private AC columnConstraints = new AC();
	private AC rowConstraints = new AC();
	private int horizontalDividerWidth = 1;
	private int verticalDividerWidth = 1;
	
	private Map<Integer, Float> fixedWidths = new HashMap<Integer, Float>();
	private Map<Integer, Float> fixedHeights = new HashMap<Integer, Float>();
	
	private Color headerBackground = Color.LIGHT_GRAY;
	private Color horizontalDividerColor = Color.DARK_GRAY;
	private Color verticalDividerColor = Color.DARK_GRAY;
	
	private final List<T> data = new ArrayList<T>();

	private int currentlySortedColumn = -1;
	private int currentSort = NO_SORT;
	
	private int currentlyDraggedDivider = -1;
	private int dividerOriginalDragX = -1;
	private int dividerCurrentDragX = -1;
	
	private boolean resizable = true;
	private Map<Integer, Comparator<T>> sort = new HashMap<Integer, Comparator<T>>();
	
	public MigTable(){
		setLayout();
		addHeader();
		
		SwingUtil.addRecursiveMouseListener(this, new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point tableScreenLocation = getLocationOnScreen();
				Point relativeLocation = e.getLocationOnScreen();
				relativeLocation.x -= tableScreenLocation.x;
				relativeLocation.y -= tableScreenLocation.y;
				
				// if click is inside a header, try to sort it
				for(int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++){
					if(sort.get(columnIndex) != null){
						Rectangle cellBounds = layout.getCellBounds(columnIndex, 0);
						if(cellBounds.contains(relativeLocation)){
							int sort = currentlySortedColumn == columnIndex && currentSort == DESCENDING_SORT ? ASCENDING_SORT : DESCENDING_SORT;
							currentSort = sort;
							currentlySortedColumn = columnIndex;
							List<T> sorted = new ArrayList<T>(data);
							sort(sorted, columnIndex, sort);
							clear();
							addAll(sorted);
							return;
						}
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(!isResizable()){
					return;
				}
				
				Point tableScreenLocation = getLocationOnScreen();
				Point relativeLocation = e.getLocationOnScreen();
				relativeLocation.x -= tableScreenLocation.x;
				relativeLocation.y -= tableScreenLocation.y;
				
				// if click is on a divide (gap between cell bounds), mark it so dragging will resize it
				int divider = getDividerAt(relativeLocation.x);
				if(divider >= 0 && divider < getColumnCount() - 1){
					currentlyDraggedDivider = divider;
					dividerOriginalDragX = relativeLocation.x;
					setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(currentlyDraggedDivider >= 0){
					int divider = currentlyDraggedDivider;
					currentlyDraggedDivider = -1;
					setCursor(Cursor.getDefaultCursor());
					
					int xDif = dividerCurrentDragX - dividerOriginalDragX;
					int leftWidth = layout.getCellBounds(divider, 0).width;
					int rightWidth = layout.getCellBounds(divider + 1, 0).width;
					leftWidth += xDif;
					rightWidth -= xDif;
					
					float tableWidth = (float) getWidth();
					
					fixedWidths.put(divider, leftWidth / tableWidth);
					fixedWidths.put(divider + 1, rightWidth / tableWidth);
					setLayout();
					revalidate();
					repaint();
				}
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(currentlyDraggedDivider < 0){
					int divider = getDividerAt(e.getX());
					if(divider >= 0 && divider < getColumnCount() - 1){
						setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					}else{
						setCursor(Cursor.getDefaultCursor());
					}
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(currentlyDraggedDivider >= 0){
					Rectangle rightBounds = layout.getCellBounds(currentlyDraggedDivider + 1, 0);
					int maxX = rightBounds.x + rightBounds.width - 1;
					Rectangle leftBounds = layout.getCellBounds(currentlyDraggedDivider, 0);
					int minX = leftBounds.x + 1;
					dividerCurrentDragX = Math.max(Math.min(e.getX(), maxX), minX);
					repaint();
				}
			}
		});
	}
	
	private int getDividerAt(int x){
		int leftSideColumn = -1;
		for(int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++){
			Rectangle cellBounds = layout.getCellBounds(columnIndex, 0);
			int maxCellX = cellBounds.x + cellBounds.width;
			if(maxCellX < x + DIVIDER_DRAG_RADIUS){
				leftSideColumn = columnIndex;
			}else if(cellBounds.x < x - DIVIDER_DRAG_RADIUS){
				// is inside a cell
				leftSideColumn = -1;
				break;
			}
		}
		if(leftSideColumn >= 0 && leftSideColumn < getColumnCount() - 1){
			return leftSideColumn;
		}else{
			return -1;
		}
	}
	
	private void addHeader(){
		int columnCount = getColumnCount();
		for(int i = 0; i < columnCount; i++){
			add(wrap(getHeaderComponentAt(i), i, 0), getHeaderComponentConstraints(i));
		}
	}
	
	public void clear(){
		this.data.clear();
		removeAll();
		setLayout();
		addHeader();
		revalidate();
		repaint();
	}
	
	public void setData(T[] data){
		clear();
		if(data != null) add(data);
	}
	
	public List<T> getData(){
		return new ArrayList<T>(data);
	}
	
	public int getRowCount(){
		return data.size();
	}
	
	public void add(T... data){
		addAll(Arrays.asList(data));
	}
	
	public void addAll(List<T> data){
		this.data.addAll(data);
		
		int columnCount = getColumnCount();
		int rowIndex = 0;
		for(T element : data){
			for(int columnIndex = 0; columnIndex < columnCount; columnIndex++){
				add(wrap(getComponentAt(element, columnIndex), columnIndex, rowIndex), getComponentConstraints(element, columnIndex));
			}
			rowIndex++;
		}
		
		revalidate();
		repaint();
	}
	
	private JPanel wrap(Component component, final int columnIndex, final int rowIndex){
		JPanel wrapper = new JPanel(new BorderLayout()){
			@Override
			public Dimension getMinimumSize() {
				Dimension size = super.getMinimumSize();
				int tableWidth = MigTable.this.getWidth();
				if(fixedWidths.containsKey(columnIndex)){
					size.width = (int) Math.min(fixedWidths.get(columnIndex) *  tableWidth, size.width);
				}
				int tableHeight = MigTable.this.getHeight();
				if(fixedHeights.containsKey(rowIndex)){
					size.height = (int) Math.min(fixedHeights.get(rowIndex) * tableHeight, size.height);
				}
				return size;
			}
		};
		wrapper.setOpaque(false);
		wrapper.add(component);
		return wrapper;
	}
	
	private void setLayout(){
		// prevent NullPointerException on synchronized block
		if(layout == null){
			layout = new CustomMigLayout();
		}
		
		synchronized(layout){
			AC columnConstraints = this.columnConstraints;
			AC rowConstraints = this.rowConstraints;
			
			if(!fixedWidths.isEmpty()){
				columnConstraints = Cloner.clone(columnConstraints, true);
				for(Integer columnIndex : fixedWidths.keySet()){
					float width = fixedWidths.get(columnIndex) * 100;
					System.out.println(columnIndex + ": " + width + "%");
					columnConstraints.size(width + "%!", columnIndex);
				}
			}
			
			if(!fixedHeights.isEmpty()){
				rowConstraints = Cloner.clone(rowConstraints, true);
				for(Integer rowIndex : fixedHeights.keySet()){
					float height = fixedHeights.get(rowIndex) * 100;
					rowConstraints.size(height + "%!", rowIndex);
				}
			}
			
			String layoutContstraintsString = "fillx, wrap " + getColumnCount() + ", insets 0, gapx " + getVerticalDividerWidth() + ", gapy " + getHorizontalDividerWidth();
			LC layoutContstraints = ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutContstraintsString));
			
			layout = new CustomMigLayout(layoutContstraints, columnConstraints, rowConstraints);
			setLayout(layout);
			layout.layoutContainer(this);
		}
	}
	
	public AC getColumnConstraints() {
		return columnConstraints;
	}

	public void setColumnConstraints(String columnConstraints) {
		setColumnConstraints(ConstraintParser.parseColumnConstraints(ConstraintParser.prepare(columnConstraints)));
	}

	public void setColumnConstraints(AC columnConstraints) {
		this.columnConstraints = columnConstraints;
		setLayout();
	}

	public AC getRowConstraints() {
		return rowConstraints;
	}

	public void setRowConstraints(String rowConstraints) {
		setRowConstraints(ConstraintParser.parseRowConstraints(ConstraintParser.prepare(rowConstraints)));
	}

	public void setRowConstraints(AC rowConstraints) {
		this.rowConstraints = rowConstraints;
		setLayout();
	}

	public int getHorizontalDividerWidth() {
		return horizontalDividerWidth;
	}

	public void setHorizontalDividerWidth(int horizontalGap) {
		this.horizontalDividerWidth = horizontalGap;
		setLayout();
	}

	public int getVerticalDividerWidth() {
		return verticalDividerWidth;
	}

	public void setVerticalDividerWidth(int verticalGap) {
		this.verticalDividerWidth = verticalGap;
		setLayout();
	}
	
	public Color getHeaderBackground() {
		return headerBackground;
	}

	public void setHeaderBackground(Color headerColor) {
		this.headerBackground = headerColor;
	}

	public Color getHorizontalDividerColor() {
		return horizontalDividerColor;
	}

	public void setHorizontalDividerColor(Color horizontalDividerColor) {
		this.horizontalDividerColor = horizontalDividerColor;
	}

	public Color getVerticalDividerColor() {
		return verticalDividerColor;
	}

	public void setVerticalDividerColor(Color verticalDividerColor) {
		this.verticalDividerColor = verticalDividerColor;
	}
	
	public boolean isResizable(){
		return resizable;
	}
	
	public void setResizable(boolean resizable){
		this.resizable = resizable;
	}
	
	public Map<Integer, Comparator<T>> getSort(){
		return sort;
	}
	
	public Comparator<T> getSort(int columnIndex){
		return sort.get(columnIndex);
	}
	
	public void setSort(int columnIndex, Comparator<T> sort){
		this.sort.put(columnIndex, sort);
	}
	
	public void setSort(Comparator<T>... sort){
		for(int columnIndex = 0; columnIndex < sort.length; columnIndex++){
			setSort(columnIndex, sort[columnIndex]);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int height = getHeight();
		int headerHeight = height;
		if(getComponentCount() > getColumnCount()){
			headerHeight = getComponents()[0].getHeight() + getHorizontalDividerWidth() / 2;
		}
		g.setColor(getHeaderBackground());
		g.fillRect(0, 0, getWidth(), headerHeight);
		
		// draw header sort icon
		if(currentlySortedColumn >= 0 && (currentSort == ASCENDING_SORT || currentSort == DESCENDING_SORT)){
			Rectangle cellBounds = layout.getCellBounds(currentlySortedColumn, 0);
			Icon icon = FontIcon.of(currentSort == ASCENDING_SORT ? FontAwesomeSolid.CARET_UP : FontAwesomeSolid.CARET_DOWN, getComponent(currentlySortedColumn).getForeground());
			int x = cellBounds.x + cellBounds.width - SORT_ICON_GAP - icon.getIconWidth();
			int y = cellBounds.y + cellBounds.height / 2 - icon.getIconHeight() / 2;
			icon.paintIcon(this, g, x, y);
		}

		// draw horizontal dividers
		g.setColor(getHorizontalDividerColor());
		for(int rowIndex = 0; rowIndex < layout.getRowCount() - 1; rowIndex++){
			Rectangle beforeBounds = layout.getCellBounds(0, rowIndex);
			Rectangle afterBounds = layout.getCellBounds(0, rowIndex + 1);
			
			int dividerY = beforeBounds.y + beforeBounds.height;
			g.fillRect(0, dividerY, getWidth(), afterBounds.y - dividerY);
		}

		// draw vertical dividers
		g.setColor(getVerticalDividerColor());
		for(int columnIndex = 0; columnIndex < layout.getColumnCount() - 1; columnIndex++){
			Rectangle beforeBounds = layout.getCellBounds(columnIndex, 0);
			Rectangle afterBounds = layout.getCellBounds(columnIndex + 1, 0);
			
			int dividerX = beforeBounds.x + beforeBounds.width;
			g.fillRect(dividerX, 0, afterBounds.x - dividerX, getHeight());
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// draw line where currently dragged divider would be
		if(currentlyDraggedDivider >= 0){
			g.setColor(Color.BLACK);
			g.fillRect(dividerCurrentDragX - 1, 0, 3, getHeight());
		}
		if(currentlySortedColumn >= 0 && (currentSort == ASCENDING_SORT || currentSort == DESCENDING_SORT)){
			Rectangle cellBounds = layout.getCellBounds(currentlySortedColumn, 0);
			Icon icon = FontIcon.of(currentSort == ASCENDING_SORT ? FontAwesomeSolid.CARET_UP : FontAwesomeSolid.CARET_DOWN, getComponent(currentlySortedColumn).getForeground());
			int x = cellBounds.x + cellBounds.width - SORT_ICON_GAP - icon.getIconWidth();
			int y = cellBounds.y + cellBounds.height / 2 - icon.getIconHeight() / 2;
			icon.paintIcon(this, g, x, y);
		}
		
		// draw header sort icon
		if(currentlySortedColumn >= 0 && (currentSort == ASCENDING_SORT || currentSort == DESCENDING_SORT)){
			Rectangle cellBounds = layout.getCellBounds(currentlySortedColumn, 0);
			Icon backgroundFiller = FontIcon.of(currentSort == ASCENDING_SORT ? FontAwesomeSolid.CARET_UP : FontAwesomeSolid.CARET_DOWN, 24, getHeaderBackground());
			Icon icon = FontIcon.of(currentSort == ASCENDING_SORT ? FontAwesomeSolid.CARET_UP : FontAwesomeSolid.CARET_DOWN, 16, getComponent(currentlySortedColumn).getForeground());
			
			int x = cellBounds.x + cellBounds.width - SORT_ICON_GAP - backgroundFiller.getIconWidth() / 2 - icon.getIconWidth() / 2;
			int y = cellBounds.y + cellBounds.height / 2 - backgroundFiller.getIconHeight() / 2;
			backgroundFiller.paintIcon(this, g, x, y);

			x = cellBounds.x + cellBounds.width - SORT_ICON_GAP - icon.getIconWidth();
			y = cellBounds.y + cellBounds.height / 2 - icon.getIconHeight() / 2;
			icon.paintIcon(this, g, x, y);
		}
	}

	protected abstract Component getComponentAt(T element, int columnIndex);
	
	protected abstract Component getHeaderComponentAt(int columnIndex);
	
	protected abstract int getColumnCount();
	
	protected void sort(List<T> data, int columnIndex, int sort){
		Comparator<T> comparator = getSort(columnIndex);
		if(comparator != null){
			if(sort == ASCENDING_SORT){
				Collections.sort(data, comparator);
			}else if(sort == DESCENDING_SORT){
				Collections.sort(data, Collections.reverseOrder(comparator));
			}
		}
	}
	
	protected String getComponentConstraints(T element, int columnIndex){
		return "";
	}
	protected String getHeaderComponentConstraints(int columnIndex){
		return "";
	}
	
	
	
	public static void main(String[] args) {
		int dataCount = 10;
		List<TestObject> data = new ArrayList<TestObject>();
		for(int i = 0; i < dataCount; i++){
			data.add(new TestObject());
		}
		
		MigTable<TestObject> table = new MigTable<TestObject>() {

			@Override
			protected Component getComponentAt(TestObject element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return new JLabel(element.name);
				case 1:
					return new JImage(element.image);
				case 2:
					JCheckBox check = new JCheckBox();
					check.setSelected(element.bool);
					return check;
				default:
					return new JLabel();
				}
			}

			@Override
			protected Component getHeaderComponentAt(int columnIndex) {
				JLabel label = new JLabel();
				label.setOpaque(false);
				switch (columnIndex) {
				case 0:
					label.setText("Name");
					return label;
				case 1:
					label.setText("Image");
					return label;
				case 2:
					label.setText("Boolean");
					return label;
				default:
					return label;
				}
			}

			@Override
			protected int getColumnCount() {
				return 3;
			}
		};
		table.setData(data.toArray(new TestObject[dataCount]));
//		table.setSort(0, new Comparator<TestObject>() {
//			
//			@Override
//			public int compare(TestObject o1, TestObject o2) {
//				return o1.name.compareTo(o2.name);
//			}
//		});
//		table.setSort(2, new Comparator<TestObject>() {
//			
//			@Override
//			public int compare(TestObject o1, TestObject o2) {
//				return o1.bool ? (o2.bool ? 0 : 1) : (o2.bool ? -1 : 0);
//			}
//		});
//		table.setColumnConstraints("[fill][grow, center][center]");
//		table.setVerticalDividerWidth(15);
//		table.setVerticalDividerColor(ColorUtil.INFO_BORDER_COLOR);
		
		new TestFrame(new JScrollPane(new Inspector(table)));
	}
	
	private static class TestObject {
		public String name = "";
		public Image image;
		public boolean bool;
		
		public TestObject(){
			Random r = new Random();
			int charCount = r.nextInt(15);
			for(int i = 0; i < charCount; i++){
				name += (char)(r.nextInt(26) + 'a');
			}
			
			image = FontIcon.of(FontAwesomeSolid.values()[r.nextInt(FontAwesomeSolid.values().length)]).toImage();
			
			bool = r.nextBoolean();
		}
	}
}

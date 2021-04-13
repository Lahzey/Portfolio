package graphics.swing.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.LayoutUtil;
import net.miginfocom.layout.PlatformDefaults;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;
import net.miginfocom.swing.SwingComponentWrapper;
import net.miginfocom.swing.SwingContainerWrapper;
import util.NumberComparator;

/**
 * A copy of {@link MigLayout} with some customizations.
 * @author A469627
 *
 */
public class CustomMigLayout implements LayoutManager2, Externalizable {


	// ******** Custom part ********
	
	private Field debugRectsField;

	private final MultiKeyMap<Integer, Rectangle> cellBounds = new MultiKeyMap<Integer, Rectangle>();

	
	
	// ******** Instance part ********

	/**
	 * The component to string constraints mappings.
	 */
	private final Map<Component, Object> scrConstrMap = new IdentityHashMap<Component, Object>(8);

	/**
	 * Hold the serializable text representation of the constraints.
	 */
	private Object layoutConstraints = "", colConstraints = "", rowConstraints = ""; // Should
																						// never
																						// be
																						// null!

	// ******** Transient part ********

	private transient ContainerWrapper cacheParentW = null;

	private transient final Map<ComponentWrapper, CC> ccMap = new HashMap<ComponentWrapper, CC>(8);
	private transient javax.swing.Timer debugTimer = null;

	private transient LC lc = null;
	private transient AC colSpecs = null, rowSpecs = null;
	private transient Grid grid = null;
	private transient int lastModCount = PlatformDefaults.getModCount();
	private transient int lastHash = -1;
	private transient Dimension lastInvalidSize = null;
	private transient boolean lastWasInvalid = false; // Added in 3.7.1. May
														// have regressions
	private transient Dimension lastParentSize = null;

	private transient ArrayList<LayoutCallback> callbackList = null;

	private transient boolean dirty = true;

	/**
	 * Constructor with no constraints.
	 */
	public CustomMigLayout() {
		this("", "", "");
	}

	/**
	 * Constructor.
	 * 
	 * @param layoutConstraints
	 *            The constraints that concern the whole layout.
	 *            <code>null</code> will be treated as "".
	 */
	public CustomMigLayout(String layoutConstraints) {
		this(layoutConstraints, "", "");
	}

	/**
	 * Constructor.
	 * 
	 * @param layoutConstraints
	 *            The constraints that concern the whole layout.
	 *            <code>null</code> will be treated as "".
	 * @param colConstraints
	 *            The constraints for the columns in the grid. <code>null</code>
	 *            will be treated as "".
	 */
	public CustomMigLayout(String layoutConstraints, String colConstraints) {
		this(layoutConstraints, colConstraints, "");
	}

	/**
	 * Constructor.
	 * 
	 * @param layoutConstraints
	 *            The constraints that concern the whole layout.
	 *            <code>null</code> will be treated as "".
	 * @param colConstraints
	 *            The constraints for the columns in the grid. <code>null</code>
	 *            will be treated as "".
	 * @param rowConstraints
	 *            The constraints for the rows in the grid. <code>null</code>
	 *            will be treated as "".
	 */
	public CustomMigLayout(String layoutConstraints, String colConstraints, String rowConstraints) {
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);
		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param layoutConstraints
	 *            The constraints that concern the whole layout.
	 *            <code>null</code> will be treated as an empty constraint.
	 */
	public CustomMigLayout(LC layoutConstraints) {
		this(layoutConstraints, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param layoutConstraints
	 *            The constraints that concern the whole layout.
	 *            <code>null</code> will be treated as an empty constraint.
	 * @param colConstraints
	 *            The constraints for the columns in the grid. <code>null</code>
	 *            will be treated as an empty constraint.
	 */
	public CustomMigLayout(LC layoutConstraints, AC colConstraints) {
		this(layoutConstraints, colConstraints, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param layoutConstraints
	 *            The constraints that concern the whole layout.
	 *            <code>null</code> will be treated as an empty constraint.
	 * @param colConstraints
	 *            The constraints for the columns in the grid. <code>null</code>
	 *            will be treated as an empty constraint.
	 * @param rowConstraints
	 *            The constraints for the rows in the grid. <code>null</code>
	 *            will be treated as an empty constraint.
	 */
	public CustomMigLayout(LC layoutConstraints, AC colConstraints, AC rowConstraints) {
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);
		init();
	}

	protected void init() {
		try {
			debugRectsField = Grid.class.getDeclaredField("debugRects");
			debugRectsField.setAccessible(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public AC getColumnSpecs(){
		return colSpecs;
	}
	
	public AC getRowSpecs(){
		return rowSpecs;
	}

	@SuppressWarnings("unchecked")
	protected List<int[]> getCellRectangles() {
		if (grid != null) {
			try {
				return (List<int[]>) debugRectsField.get(grid);
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
		} else {
			System.out.println("grid null");
			return null;
		}
	}
	
	public int getColumnCount(){
		int maxColumnIndex = -1;
		for(MultiKey<? extends Integer> key : cellBounds.keySet()){
			int columnIndex = key.getKey(0);
			if(columnIndex > maxColumnIndex) maxColumnIndex = columnIndex;
		}
		return maxColumnIndex + 1;
	}
	
	public int getRowCount(){
		int maxRowIndex = -1;
		for(MultiKey<? extends Integer> key : cellBounds.keySet()){
			int rowIndex = key.getKey(1);
			if(rowIndex > maxRowIndex) maxRowIndex = rowIndex;
		}
		return maxRowIndex + 1;
	}
	
	public Collection<Rectangle> getCellBounds(){
		return cellBounds.values();
	}

	public Rectangle getCellBounds(int columnIndex, int rowIndex) {
		return cellBounds.get(columnIndex, rowIndex);
	}

	/**
	 * Returns layout constraints either as a <code>String</code> or
	 * {@link net.miginfocom.layout.LC} depending what was sent in to the
	 * constructor or set with {@link #setLayoutConstraints(Object)}.
	 * 
	 * @return The layout constraints either as a <code>String</code> or
	 *         {@link net.miginfocom.layout.LC} depending what was sent in to
	 *         the constructor or set with
	 *         {@link #setLayoutConstraints(Object)}. Never <code>null</code>.
	 */
	public Object getLayoutConstraints() {
		return layoutConstraints;
	}

	/**
	 * Sets the layout constraints for the layout manager instance as a String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * 
	 * @param constr
	 *            The layout constraints as a String representation.
	 *            <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException
	 *             if the constraint was not valid.
	 */
	public void setLayoutConstraints(Object constr) {
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			lc = ConstraintParser.parseLayoutConstraint((String) constr);
		} else if (constr instanceof LC) {
			lc = (LC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		layoutConstraints = constr;
		dirty = true;
	}

	/**
	 * Returns the column layout constraints either as a <code>String</code> or
	 * {@link net.miginfocom.layout.AC}.
	 * 
	 * @return The column constraints either as a <code>String</code> or
	 *         {@link net.miginfocom.layout.LC} depending what was sent in to
	 *         the constructor or set with
	 *         {@link #setLayoutConstraints(Object)}. Never <code>null</code>.
	 */
	public Object getColumnConstraints() {
		return colConstraints;
	}

	/**
	 * Sets the column layout constraints for the layout manager instance as a
	 * String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * 
	 * @param constr
	 *            The column layout constraints as a String representation.
	 *            <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException
	 *             if the constraint was not valid.
	 */
	public void setColumnConstraints(Object constr) {
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			colSpecs = ConstraintParser.parseColumnConstraints((String) constr);
		} else if (constr instanceof AC) {
			colSpecs = (AC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		colConstraints = constr;
		dirty = true;
	}

	/**
	 * Returns the row layout constraints as a String representation. This
	 * string is the exact string as set with {@link #setRowConstraints(Object)}
	 * or sent into the constructor.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * 
	 * @return The row layout constraints as a String representation. Never
	 *         <code>null</code>.
	 */
	public Object getRowConstraints() {
		return rowConstraints;
	}

	/**
	 * Sets the row layout constraints for the layout manager instance as a
	 * String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * 
	 * @param constr
	 *            The row layout constraints as a String representation.
	 *            <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException
	 *             if the constraint was not valid.
	 */
	public void setRowConstraints(Object constr) {
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			rowSpecs = ConstraintParser.parseRowConstraints((String) constr);
		} else if (constr instanceof AC) {
			rowSpecs = (AC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		rowConstraints = constr;
		dirty = true;
	}

	/**
	 * Returns a shallow copy of the constraints map.
	 * 
	 * @return A shallow copy of the constraints map. Never <code>null</code>.
	 */
	public Map<Component, Object> getConstraintMap() {
		return new IdentityHashMap<Component, Object>(scrConstrMap);
	}

	/**
	 * Sets the constraints map.
	 * 
	 * @param map
	 *            The map. Will be copied.
	 */
	public void setConstraintMap(Map<Component, Object> map) {
		scrConstrMap.clear();
		ccMap.clear();
		for (Map.Entry<Component, Object> e : map.entrySet())
			setComponentConstraintsImpl(e.getKey(), e.getValue(), true);
	}

	/**
	 * Returns the component constraints as a String representation. This string
	 * is the exact string as set with
	 * {@link #setComponentConstraints(java.awt.Component, Object)} or set when
	 * adding the component to the parent component.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * 
	 * @param comp
	 *            The component to return the constraints for.
	 * @return The component constraints as a String representation or
	 *         <code>null</code> if the component is not registered with this
	 *         layout manager. The returned values is either a String or a
	 *         {@link net.miginfocom.layout.CC} depending on what constraint was
	 *         sent in when the component was added. May be <code>null</code>.
	 */
	public Object getComponentConstraints(Component comp) {
		synchronized (comp.getParent().getTreeLock()) {
			return scrConstrMap.get(comp);
		}
	}

	/**
	 * Sets the component constraint for the component that already must be
	 * handled by this layout manager.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * 
	 * @param constr
	 *            The component constraints as a String or
	 *            {@link net.miginfocom.layout.CC}. <code>null</code> is ok.
	 * @param comp
	 *            The component to set the constraints for.
	 * @throws RuntimeException
	 *             if the constraint was not valid.
	 * @throws IllegalArgumentException
	 *             If the component is not handling the component.
	 */
	public void setComponentConstraints(Component comp, Object constr) {
		setComponentConstraintsImpl(comp, constr, false);
	}

	/**
	 * Sets the component constraint for the component that already must be
	 * handled by this layout manager.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * 
	 * @param constr
	 *            The component constraints as a String or
	 *            {@link net.miginfocom.layout.CC}. <code>null</code> is ok.
	 * @param comp
	 *            The component to set the constraints for.
	 * @param noCheck
	 *            Doe not check if the component is handled if true
	 * @throws RuntimeException
	 *             if the constraint was not valid.
	 * @throws IllegalArgumentException
	 *             If the component is not handling the component.
	 */
	private void setComponentConstraintsImpl(Component comp, Object constr, boolean noCheck) {
		Container parent = comp.getParent();
		synchronized (parent != null ? parent.getTreeLock() : new Object()) { // 3.7.2.
																				// No
																				// sync
																				// if
																				// not
																				// added
																				// to
																				// a
																				// hierarchy.
																				// Defeats
																				// a
																				// NPE.
			if (noCheck == false && scrConstrMap.containsKey(comp) == false)
				throw new IllegalArgumentException("Component must already be added to parent!");

			ComponentWrapper cw = new SwingComponentWrapper(comp);

			if (constr == null || constr instanceof String) {
				String cStr = ConstraintParser.prepare((String) constr);

				scrConstrMap.put(comp, constr);
				ccMap.put(cw, ConstraintParser.parseComponentConstraint(cStr));

			} else if (constr instanceof CC) {

				scrConstrMap.put(comp, constr);
				ccMap.put(cw, (CC) constr);

			} else {
				throw new IllegalArgumentException("Constraint must be String or ComponentConstraint: " + constr.getClass().toString());
			}

			dirty = true;
		}
	}

	/**
	 * Returns if this layout manager is currently managing this component.
	 * 
	 * @param c
	 *            The component to check. If <code>null</code> then
	 *            <code>false</code> will be returned.
	 * @return If this layout manager is currently managing this component.
	 */
	public boolean isManagingComponent(Component c) {
		return scrConstrMap.containsKey(c);
	}

	/**
	 * Adds the callback function that will be called at different stages of the
	 * layout cylce.
	 * 
	 * @param callback
	 *            The callback. Not <code>null</code>.
	 */
	public void addLayoutCallback(LayoutCallback callback) {
		if (callback == null)
			throw new NullPointerException();

		if (callbackList == null)
			callbackList = new ArrayList<LayoutCallback>(1);

		callbackList.add(callback);
	}

	/**
	 * Removes the callback if it exists.
	 * 
	 * @param callback
	 *            The callback. May be <code>null</code>.
	 */
	public void removeLayoutCallback(LayoutCallback callback) {
		if (callbackList != null)
			callbackList.remove(callback);
	}

	/**
	 * Sets the debugging state for this layout manager instance. If debug is
	 * turned on a timer will repaint the last laid out parent with debug
	 * information on top.
	 * <p>
	 * Red fill and dashed red outline is used to indicate occupied cells in the
	 * grid. Blue dashed outline indicate component bounds set.
	 * <p>
	 * Note that debug can also be set on the layout constraints. There it will
	 * be persisted. The value set here will not. See the class JavaDocs for
	 * information.
	 * 
	 * @param parentW
	 *            The parent to set debug for.
	 * @param b
	 *            <code>true</code> means debug is turned on.
	 */
	private void setDebug(final ComponentWrapper parentW, boolean b) {
		if (b && (debugTimer == null || debugTimer.getDelay() != getDebugMillis())) {
			if (debugTimer != null)
				debugTimer.stop();

			ContainerWrapper pCW = parentW.getParent();
			final Component parent = pCW != null ? (Component) pCW.getComponent() : null;

			debugTimer = new Timer(getDebugMillis(), new MyDebugRepaintListener());

			if (parent != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Container p = parent.getParent();
						if (p != null) {
							if (p instanceof JComponent) {
								((JComponent) p).revalidate();
							} else {
								parent.invalidate();
								p.validate();
							}
						}
					}
				});
			}

			debugTimer.setInitialDelay(100);
			debugTimer.start();

		} else if (!b && debugTimer != null) {
			debugTimer.stop();
			debugTimer = null;
		}
	}

	/**
	 * Returns the debug millis. Combines the value from
	 * {@link net.miginfocom.layout.LC#getDebugMillis()} and
	 * {@link net.miginfocom.layout.LayoutUtil#getGlobalDebugMillis()}
	 * 
	 * @return The combined value.
	 */
	private int getDebugMillis() {
		int globalDebugMillis = LayoutUtil.getGlobalDebugMillis();
		return globalDebugMillis > 0 ? globalDebugMillis : lc.getDebugMillis();
	}

	/**
	 * Check if something has changed and if so recreate it to the cached
	 * objects.
	 * 
	 * @param parent
	 *            The parent that is the target for this layout manager.
	 */
	private void checkCache(Container parent) {
		if (parent == null)
			return;

		if (dirty)
			grid = null;

		// Check if the grid is valid
		int mc = PlatformDefaults.getModCount();
		if (lastModCount != mc) {
			grid = null;
			lastModCount = mc;
		}

		if (parent.isValid() == false) {
			if (lastWasInvalid == false) {
				lastWasInvalid = true;

				int hash = 0;
				boolean resetLastInvalidOnParent = false; // Added in 3.7.3 to
															// resolve a timing
															// regression
															// introduced in
															// 3.7.1
				for (ComponentWrapper wrapper : ccMap.keySet()) {
					Object component = wrapper.getComponent();
					if (component instanceof JTextArea || component instanceof JEditorPane)
						resetLastInvalidOnParent = true;
					hash ^= wrapper.getLayoutHashCode();
					hash += 285134905;
				}
				if (resetLastInvalidOnParent)
					resetLastInvalidOnParent(parent);

				if (hash != lastHash) {
					grid = null;
					lastHash = hash;
				}

				Dimension ps = parent.getSize();
				if (lastInvalidSize == null || !lastInvalidSize.equals(ps)) {
					if (grid != null)
						grid.invalidateContainerSize();
					lastInvalidSize = ps;
				}
			}
		} else {
			lastWasInvalid = false;
		}

		ContainerWrapper par = checkParent(parent);

		setDebug(par, getDebugMillis() > 0);

		if (grid == null)
			grid = new Grid(par, lc, rowSpecs, colSpecs, ccMap, callbackList);

		dirty = false;
	}

	/**
	 * @since 3.7.3
	 */
	private void resetLastInvalidOnParent(Container parent) {
		while (parent != null) {
			LayoutManager layoutManager = parent.getLayout();
			if (layoutManager instanceof MigLayout) {
				try {
					Field lastWasInvalidField = MigLayout.class.getDeclaredField("lastWasInvalid");
					lastWasInvalidField.setAccessible(true);
					lastWasInvalidField.set(layoutManager, false);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			parent = parent.getParent();
		}
	}

	private ContainerWrapper checkParent(Container parent) {
		if (parent == null)
			return null;

		if (cacheParentW == null || cacheParentW.getComponent() != parent)
			cacheParentW = new SwingContainerWrapper(parent);

		return cacheParentW;
	}

	private long lastSize = 0;

	public void layoutContainer(final Container parent) {
		synchronized (parent.getTreeLock()) {
			checkCache(parent);

			Insets i = parent.getInsets();
			int[] b = new int[] { i.left, i.top, parent.getWidth() - i.left - i.right, parent.getHeight() - i.top - i.bottom };

			if (grid.layout(b, lc.getAlignX(), lc.getAlignY(), true, true)) {
				grid = null;
				checkCache(parent);
				grid.layout(b, lc.getAlignX(), lc.getAlignY(), true, false);
			}

			long newSize = grid.getHeight()[1] + (((long) grid.getWidth()[1]) << 32);
			if (lastSize != newSize) {
				lastSize = newSize;
				final ContainerWrapper containerWrapper = checkParent(parent);
				Window win = ((Window) SwingUtilities.getAncestorOfClass(Window.class, (Component) containerWrapper.getComponent()));
				if (win != null) {
					if (win.isVisible()) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								adjustWindowSize(containerWrapper);
							}
						});
					} else {
						adjustWindowSize(containerWrapper);
					}
				}
			}
			lastInvalidSize = null;
		}
		
		List<int[]> cellRects = getCellRectangles();
		if(cellRects != null){
			cellBounds.clear();
			List<Integer> columns = new ArrayList<Integer>();
			List<Integer> rows = new ArrayList<Integer>();
			for(int[] rect : cellRects){
				if(!columns.contains(rect[0])) columns.add(rect[0]);
				if(!rows.contains(rect[1])) rows.add(rect[1]);
			}
			Collections.sort(columns, new NumberComparator());
			Collections.sort(rows, new NumberComparator());
			for(int[] rect : cellRects){
				cellBounds.put(columns.indexOf(rect[0]), rows.indexOf(rect[1]), new Rectangle(rect[0], rect[1], rect[2], rect[3]));
			}
		}else{
			System.out.println("cellRects null");
		}
	}

	/**
	 * Checks the parent window if its size is within parameters as set by the
	 * LC.
	 * 
	 * @param parent
	 *            The parent who's window to possibly adjust the size for.
	 */
	private void adjustWindowSize(ContainerWrapper parent) {
		BoundSize wBounds = lc.getPackWidth();
		BoundSize hBounds = lc.getPackHeight();

		if (wBounds == null && hBounds == null)
			return;

		Window win = ((Window) SwingUtilities.getAncestorOfClass(Window.class, (Component) parent.getComponent()));
		if (win == null)
			return;

		Dimension prefSize = win.getPreferredSize();
		int targW = constrain(checkParent(win), win.getWidth(), prefSize.width, wBounds);
		int targH = constrain(checkParent(win), win.getHeight(), prefSize.height, hBounds);

		int x = Math.round(win.getX() - ((targW - win.getWidth()) * (1 - lc.getPackWidthAlign())));
		int y = Math.round(win.getY() - ((targH - win.getHeight()) * (1 - lc.getPackHeightAlign())));

		win.setBounds(x, y, targW, targH);
	}

	private int constrain(ContainerWrapper parent, int winSize, int prefSize, BoundSize constrain) {
		if (constrain == null)
			return winSize;

		int retSize = winSize;
		UnitValue wUV = constrain.getPreferred();
		if (wUV != null)
			retSize = wUV.getPixels(prefSize, parent, parent);

		retSize = constrain.constrain(retSize, prefSize, parent);

		return constrain.getGapPush() ? Math.max(winSize, retSize) : retSize;
	}

	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			return getSizeImpl(parent, LayoutUtil.MIN);
		}
	}

	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			if (lastParentSize == null || !parent.getSize().equals(lastParentSize)) {
				for (ComponentWrapper wrapper : ccMap.keySet()) {
					Component c = (Component) wrapper.getComponent();
					if (c instanceof JTextArea || c instanceof JEditorPane || (c instanceof JComponent && Boolean.TRUE.equals(((JComponent) c).getClientProperty("migLayout.dynamicAspectRatio")))) {
						layoutContainer(parent);
						break;
					}
				}
			}

			lastParentSize = parent.getSize();
			return getSizeImpl(parent, LayoutUtil.PREF);
		}
	}

	public Dimension maximumLayoutSize(Container parent) {
		return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
	}

	// Implementation method that does the job.
	private Dimension getSizeImpl(Container parent, int sizeType) {
		checkCache(parent);

		Insets i = parent.getInsets();

		int w = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, sizeType) + i.left + i.right;
		int h = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, sizeType) + i.top + i.bottom;

		return new Dimension(w, h);
	}

	public float getLayoutAlignmentX(Container parent) {
		return lc != null && lc.getAlignX() != null ? lc.getAlignX().getPixels(1, checkParent(parent), null) : 0;
	}

	public float getLayoutAlignmentY(Container parent) {
		return lc != null && lc.getAlignY() != null ? lc.getAlignY().getPixels(1, checkParent(parent), null) : 0;
	}

	public void addLayoutComponent(String s, Component comp) {
		addLayoutComponent(comp, s);
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		synchronized (comp.getParent().getTreeLock()) {
			setComponentConstraintsImpl(comp, constraints, true);
		}
	}

	public void removeLayoutComponent(Component comp) {
		synchronized (comp.getParent().getTreeLock()) {
			scrConstrMap.remove(comp);
			ccMap.remove(new SwingComponentWrapper(comp));
		}
	}

	public void invalidateLayout(Container target) {
		// if (lc.isNoCache()) // Commented for 3.5 since there was too often
		// that the "nocache" was needed and the user did not know.
		dirty = true;

		// the validity of components is maintained automatically.
	}

	// ************************************************
	// Persistence Delegate and Serializable combined.
	// ************************************************

	private Object readResolve() throws ObjectStreamException {
		return LayoutUtil.getSerializedObject(this);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if (getClass() == CustomMigLayout.class)
			LayoutUtil.writeAsXML(out, this);
	}

	private class MyDebugRepaintListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (grid != null) {
				Component comp = (Component) grid.getContainer().getComponent();
				if (comp.isShowing()) {
					grid.paintDebug();
					return;
				}
			}
			debugTimer.stop();
			debugTimer = null;
		}
	}
}

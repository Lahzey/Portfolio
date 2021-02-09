package graphics.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class SelectionPanel<T> extends JPanel implements AdjustmentListener {

	private static final int ANIMATION_TIME = 250;
	private static final int DEFAULT_BATCH_SIZE = 25;

	private String filter = "";
	public int batchSize = DEFAULT_BATCH_SIZE;
	private int currentBatchCount = 1;
	private boolean loadingMore = false;

	private List<T> sortedElements = new ArrayList<T>();
	private List<T> filteredElements = new ArrayList<T>();
	private Map<T, JAnimationPanel> containers = new HashMap<T, JAnimationPanel>();

	public final JTextField searchField = new JTextField(filter);
	public final JPanel contentContainer = new JPanel(new WrapLayout());
	public final JScrollPane contentScroll = new JScrollPane(contentContainer);

	public SelectionPanel(T... elements) {
		setLayout(new BorderLayout());

		add(searchField, BorderLayout.NORTH);

		contentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentScroll.getVerticalScrollBar().setUnitIncrement(16);
		contentScroll.getVerticalScrollBar().addAdjustmentListener(this);
		add(contentScroll, BorderLayout.CENTER);

		for (T element : elements)
			add(element, false);

		searchField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				filter(searchField.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				filter(searchField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filter(searchField.getText());
			}
		});
	}

	public abstract Component createComponent(T element);

	public abstract boolean matchesFilter(T element, String filter);

	public abstract void onSelection(T selection);

	public void add(T element) {
		add(element, true);
	}

	public void add(T element, final boolean animated) {
		sortedElements.add(element);
		boolean visible = matchesFilter(element, filter);
		if (visible)
			filteredElements.add(element);

		if (visible && contentContainer.getComponentCount() < batchSize * currentBatchCount) {
			JAnimationPanel container = createContainer(element);
			contentContainer.add(container);
			if (animated)
				container.appear(ANIMATION_TIME);
			else
				container.setVisible(true);
		}
	}

	public void filter(String filter) {
		boolean changed = this.filter != filter;
		this.filter = filter;

		if (changed) {
			contentScroll.getVerticalScrollBar().setValue(0);
			filteredElements.clear();
			currentBatchCount = 1;

			for (T element : sortedElements) {
				if (matchesFilter(element, filter))
					filteredElements.add(element);
			}

			generateContents();
		}
	}

	public void sort(Comparator<T> comparator) {
		currentBatchCount = 1;
		Collections.sort(sortedElements, comparator);
		generateContents();
	}

	private void loadMore() {
		loadingMore = true;
		if (sortedElements.size() > batchSize * currentBatchCount) {
			currentBatchCount++;
			generateContents();
		}
		loadingMore = false;
	}

	private void checkLoadMore() {
		new Thread() {

			@Override
			public void run() {
				if (!loadingMore) {
					int componentCount = contentContainer.getComponentCount();
					boolean shouldLoadMore;
					if (componentCount > 0) {
						int maxY = contentScroll.getVerticalScrollBar().getValue()
								+ contentScroll.getVisibleRect().height;
						int lastComponentY = contentContainer.getComponent(componentCount - 1).getY();
						shouldLoadMore = lastComponentY < maxY; // checks if
																// last
																// component is
																// visible
					} else {
						shouldLoadMore = filteredElements.size() > 0;
					}

					if (shouldLoadMore) {
						loadMore();
						checkLoadMore();
					}
				}
			}
		}.start();
	}

	private void generateContents() {
		contentContainer.removeAll();

		int elementCount = 0;
		for (T element : sortedElements) {
			boolean batchEndReached = elementCount >= batchSize * currentBatchCount;
			boolean visible = filteredElements.contains(element) && !batchEndReached;
			JAnimationPanel container = containers.get(element);
			if (container == null && visible) {
				container = createContainer(element);
			}

			if (container != null) {
				contentContainer.add(container);
				if (visible) {
					elementCount++;
					if (!container.isVisible())
						container.appear(ANIMATION_TIME);
				} else {
					if (container.isVisible()) {
						container.disappear(ANIMATION_TIME);
					}
				}
			}
		}

		contentContainer.revalidate();
		contentContainer.repaint();
	}

	private JAnimationPanel createContainer(final T element) {
		final JAnimationPanel container = new JAnimationPanel(new BorderLayout());
		container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		container.setOpaque(false);
		Component content = createComponent(element);
		container.add(content);
		container.setVisible(false);
		content.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				onSelection(element);
			}
		});
		containers.put(element, container);
		return container;
	}

	public void adjustmentValueChanged(final AdjustmentEvent e) {
		checkLoadMore();
	}

	@Override
	public void paint(Graphics g) {
		// usually called when size / layout changes
		super.paint(g);
		checkLoadMore();
	}

	public static abstract class SelectionDialog<T> extends JDialog {

		public final SelectionPanel<T> selectionPanel;
		private T selection = null;
		private boolean closed = false;
		
		public SelectionDialog(Component relativeTo, T... elements) {
			this(relativeTo, false, elements);
		}

		public SelectionDialog(Component relativeTo, boolean undecorated, T... elements) {
			super(SwingUtilities.windowForComponent(relativeTo));
			setUndecorated(undecorated);
			setModal(true);
			setPreferredSize(new Dimension(400, 400));

			selectionPanel = new SelectionPanel<T>(elements) {

				@Override
				public Component createComponent(T element) {
					return SelectionDialog.this.createComponent(element);
				}

				@Override
				public boolean matchesFilter(T element, String filter) {
					return SelectionDialog.this.matchesFilter(element, filter);
				}

				@Override
				public void onSelection(T selection) {
					SelectionDialog.this.selection = selection;
					SelectionDialog.this.dispose();
				}
			};

			setContentPane(selectionPanel);
			pack();
			setLocationRelativeTo(relativeTo);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					e.getWindow().dispose();
					closed = true;
				}
			});
		}

		public T open() {
			setVisible(true);

			// wait until selection is made
			while (selection == null && !closed) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

			return selection;
		}

		public abstract Component createComponent(T element);

		public abstract boolean matchesFilter(T element, String filter);
	}
}

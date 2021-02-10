package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.DashedBorder;
import graphics.swing.DragGridPane;
import graphics.swing.FileDrop;
import graphics.swing.JAnimationPanel;
import graphics.swing.JAnimationPanel.AnimationCallback;
import util.ColorUtil;

import logic.FileLaunchable;
import logic.SaveState.Item;
import logic.SaveState.Tab;
import logic.Status;
import main.ServerLauncher;
import net.miginfocom.swing.MigLayout;

public class LauncherPanel extends DragGridPane {
	private static final long serialVersionUID = 1L;
	public static final Border SELECTED_BORDER = BorderFactory.createCompoundBorder(new DashedBorder(ColorUtil.INFO_BORDER_COLOR, 2, 5), BorderFactory.createEmptyBorder(1, 1, 1, 1));
	
	// stores the information about this launcher panel tab
	private final Tab tab;
	
	private Map<Position, JAnimationPanel> serverPanelContainers = new HashMap<Position, JAnimationPanel>();

	public LauncherPanel(Tab tab){
		super(new MigLayout("wrap 1", "[grow, fill]", ""));
		this.tab = tab;
		setHorizontalDragEnabled(false);
		
		for(Position position : new Position[]{Position.TOP, Position.MIDDLE, Position.BOTTOM}){ // not using Position.values() to guarantee correct order
			JAnimationPanel container = new JAnimationPanel(new BorderLayout());
			serverPanelContainers.put(position, container);
			add(container);
			
			Item item = tab.get(position);
			
			if(item == null){
				remove(position);
			}else{
				add(new FileLaunchable(new File(item.absolutePath), item.type), position);
			}
		}
		
		addDragListener(new DragAdapter() {

			@Override
			public void onDrop(Component droppedComponent) {
				Position pos = Position.TOP;
				Map<JAnimationPanel, Position> newPositions = new HashMap<JAnimationPanel, Position>();
				for(Component comp : getComponents()){
					if(comp instanceof JAnimationPanel){
						JAnimationPanel panel = (JAnimationPanel) comp;
						newPositions.put(panel, pos);
						pos = pos.next();
						if(pos == null) break;
					}
				}
				
				Map<Position, Item> itemMapping = new HashMap<>();
				for(Position position : new Position[]{Position.TOP, Position.MIDDLE, Position.BOTTOM}){
					itemMapping.put(position, tab.get(position));
				}

				for(Position position : new Position[]{Position.TOP, Position.MIDDLE, Position.BOTTOM}){
					Position newPosition = newPositions.get(serverPanelContainers.get(position));
					tab.put(newPosition, itemMapping.get(position));
				}
			}
		});
		
//		JImage testImage = new JImage(ImageUtil.color(new ImageIcon(LauncherPanel.class.getResource("images/colortest.png")).getImage(), Color.RED));
//		testImage.setPreferredSize(new Dimension(500, 500));
//		add(testImage);
	}
	
	public Tab getTab(){
		return tab;
	}
	
	
	private void add(FileLaunchable launchable, final Position position){
		final ServerPanel panel = new ServerPanel(launchable);
		panel.setComponentPopupMenu(createRightClickMenu(panel, position));
		Color bg = UIManager.getColor("Panel.background");
		panel.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 0));
		add(panel, position);
	}
	
	private void add(ServerPanel panel, final Position position){
		replace(position, panel);
		
		ServerLauncher.setStatusOwner(panel, tab, position);
		tab.put(position, new Item(panel.getLaunchable().getFile().getAbsolutePath(), panel.getLaunchable().getType()));
	}
	
	private void remove(final Position position){
		final AddServerPanel addPanel = new AddServerPanel(null);
		addPanel.setListener(new FileDrop.Listener() {
			
			@Override
			public void filesDropped(final File[] files) {
				new Thread() {
					
					@Override
					public void run() {
						File server = (files != null && files.length > 0) ? files[0] : null;
						FileLaunchable launchable = AddServerDialog.showDialog(server, LauncherPanel.this);
						if(launchable != null){
							add(launchable, getPosition(addPanel));
						}
					}
				}.start();
			}
		});
		
		replace(position, addPanel);
		
		ServerLauncher.setStatusOwner(null, tab, position);
		tab.remove(position);
	}
	
	public void shutdown(){
		List<ServerPanel> toStop = new ArrayList<ServerPanel>();
		for(JPanel container : serverPanelContainers.values()){
			Component content = container.getComponent(0);
			if(content instanceof ServerPanel){
				ServerPanel serverPanel = (ServerPanel) content;
				if(serverPanel.getStatus() != Status.STOPPED){
					serverPanel.stop();
					toStop.add(serverPanel);
				}
			}
		}
		while(!toStop.isEmpty()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// does not matter
			}
			for(int i = 0; i < toStop.size(); i++){
				if(toStop.get(i).getStatus() == Status.STOPPED) toStop.remove(i);
			}
		}
	}
	
	private JPopupMenu createRightClickMenu(final ServerPanel panel, final Position position){
		JPopupMenu rightClickMenu = new JPopupMenu(){
			private static final long serialVersionUID = 1L;
			
			Border oldBorder;

			@Override
			public void setVisible(boolean visible){
				super.setVisible(visible);
				if(visible){
					oldBorder = panel.getBorder();
					panel.setBorder(BorderFactory.createCompoundBorder(SELECTED_BORDER, oldBorder));
				}else{
					panel.setBorder(oldBorder);
					oldBorder = null;
				}
			}
		};
		
		if(panel.getLaunchable() instanceof FileLaunchable){
			JMenuItem showItem = new JMenuItem("show in explorer", new ImageIcon(FontIcon.compound(FontAwesomeSolid.FOLDER_OPEN, Color.WHITE, FontAwesomeRegular.FOLDER_OPEN, Color.DARK_GRAY)));
			showItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Runtime.getRuntime().exec("explorer.exe /select," + ((FileLaunchable) panel.getLaunchable()).getFile().getAbsolutePath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			rightClickMenu.add(showItem);
		}
		
		if(panel.getLaunchable() instanceof FileLaunchable){
			JMenuItem copyItem = new JMenuItem("copy path", new ImageIcon(FontIcon.compound(FontAwesomeSolid.COPY, Color.WHITE, FontAwesomeRegular.COPY, Color.DARK_GRAY)));
			copyItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(((FileLaunchable) panel.getLaunchable()).getFile().getAbsolutePath()), null);
				}
			});
			rightClickMenu.add(copyItem);
		}
		
		JMenuItem removeItem = new JMenuItem("remove", new ImageIcon(FontIcon.compound(FontAwesomeSolid.TRASH_ALT, Color.WHITE, FontAwesomeRegular.TRASH_ALT, Color.DARK_GRAY)));
		removeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(getPosition(panel));
			}
		});
		rightClickMenu.add(removeItem);
		
		return rightClickMenu;
	}
	
	private void replace(final Position position, final JAnimationPanel replacement){
		AnimationCallback firstCallback = null;
		for(final Component content : serverPanelContainers.get(position).getComponents()){
			if(content instanceof JAnimationPanel){
				AnimationCallback callback = ((JAnimationPanel) content).close(200, JAnimationPanel.VERTICAL);
				if(firstCallback == null){
					firstCallback = callback;
					callback.then(new Runnable() {
						
						@Override
						public void run() {
							serverPanelContainers.get(position).removeAll();
							serverPanelContainers.get(position).add(replacement);
							replacement.open(200, JAnimationPanel.VERTICAL).then(new Runnable() {
								
								@Override
								public void run() {
									SwingUtilities.invokeLater(new Runnable() {
										
										@Override
										public void run() {
											updateUI();
											if(SwingUtilities.getWindowAncestor(LauncherPanel.this) != null){
												SwingUtilities.getWindowAncestor(LauncherPanel.this).pack();
											}
											repaint();
										}
									});
								}
							});
						}
					});
				}
			}
		}
		
		// if no animation was played (should never happen after the initialization)
		if(firstCallback == null){
			serverPanelContainers.get(position).removeAll();
			serverPanelContainers.get(position).add(replacement);
			revalidate();
			if(SwingUtilities.getWindowAncestor(LauncherPanel.this) != null){
				SwingUtilities.getWindowAncestor(LauncherPanel.this).pack();
			}
			repaint();
		}
	}
	
	private Position getPosition(Component component){
		Container parent = component.getParent();
		for(Position position : serverPanelContainers.keySet()){
			if(serverPanelContainers.get(position).equals(component) || serverPanelContainers.get(position).equals(parent)){
				return position;
			}
		}
		return null;
	}
	
	
	public static enum Position {
		TOP, MIDDLE, BOTTOM;
		
		public Position next(){
			switch(this){
			case TOP:
				return MIDDLE;
			case MIDDLE:
				return BOTTOM;
			case BOTTOM:
			default:
				return null;
			}
		}
	}
}

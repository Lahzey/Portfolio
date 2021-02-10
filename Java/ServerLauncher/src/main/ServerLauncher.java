package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang3.exception.ExceptionUtils;

import graphics.ImageUtil;
import graphics.swing.CollapsibleTextPane;
import graphics.swing.Inspector;
import graphics.swing.JLamp;
import util.ListUtil;
import util.LoopThread;

import logic.SaveState.Tab;
import logic.Status;
import net.miginfocom.swing.MigLayout;
import ui.LauncherPanel;
import ui.LauncherPanel.Position;
import ui.TabsPanel;

public class ServerLauncher {
	
	public static File RESOURCE_DIR;
	
	private static final long BLINK_DURATION = 750;
	private static final long BLINK_PAUSE_DURATION = BLINK_DURATION;
	private static final int BLINK_COUNT = 4;
	
	private static JFrame frame;
	private static TabsPanel tabsPanel;
	private static Status overallStatus = null;
	private static Map<Object, Status> statusMap = new HashMap<Object, Status>();
	private static Map<Tab, Object> topStatusOwner = new HashMap<>();
	private static Map<Tab, Object> middleStatusOwner = new HashMap<>();
	private static Map<Tab, Object> bottomStatusOwner = new HashMap<>();
	private static Tab currentTab;
	
	private static Image overallStatusImage = JLamp.LAMP_IMAGE;
	private static Image applicationIcon = new ImageIcon(LauncherPanel.class.getResource("images/server.png")).getImage();
	private static Image applicationIconSmall = new ImageIcon(LauncherPanel.class.getResource("images/server_small.png")).getImage();
	private static Image topBlink = ImageUtil.mask(new ImageIcon(LauncherPanel.class.getResource("images/server_top_part.png")).getImage(), new Color(255, 216, 0));
	private static Image middleBlink = ImageUtil.mask(new ImageIcon(LauncherPanel.class.getResource("images/server_middle_part.png")).getImage(), new Color(255, 216, 0));
	private static Image bottomBlink = ImageUtil.mask(new ImageIcon(LauncherPanel.class.getResource("images/server_bottom_part.png")).getImage(), new Color(255, 216, 0));
	private static Image topLamp = new ImageIcon(LauncherPanel.class.getResource("images/server_top_lamp.png")).getImage();
	private static Image middleLamp = new ImageIcon(LauncherPanel.class.getResource("images/server_middle_lamp.png")).getImage();
	private static Image bottomLamp = new ImageIcon(LauncherPanel.class.getResource("images/server_bottom_lamp.png")).getImage();
	
	private static LoopThread blinkThread;
	private static Map<Object, Integer> blinks = new HashMap<Object, Integer>();
	private static List<Object> activeBlinks = new ArrayList<Object>();
	
	
	static{
		String binariesLocation = ServerLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		RESOURCE_DIR = new File(new File(binariesLocation).getParent() + "/resources");
		System.out.println("Using " + RESOURCE_DIR.getAbsolutePath() + " as resource directory.");
	}

	public static void main(String[] args) {
		Inspector.setActive(true);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				init();
			}
		});
	}
	
	private static void init(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frame = new JFrame("Server Launcher");
		final JPanel contentPane = new JPanel(new MigLayout("fill, wrap 1, insets 0, gap 0", "[grow, fill]", "[grow, fill][]"));
		frame.setContentPane(contentPane);
		frame.setIconImage(applicationIcon);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		final JLabel startupProgressLabel = new JLabel("Starting Server Launcher...");
		startupProgressLabel.setHorizontalAlignment(SwingConstants.CENTER);
		startupProgressLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.add(startupProgressLabel);
		
		// create focus dummy at the bottom so if no buttons can be focused, the focus system does not break
		JPanel focusDummy = new JPanel();
		focusDummy.setFocusable(true);
		contentPane.add(focusDummy, "hmax 0");
		
		frame.setMinimumSize(new Dimension(500, 0));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			
			boolean cancelPressed;
			
			@Override
			public void windowClosing(WindowEvent e) {
				new Thread(){
					public void run(){
						JFrame shutdownFrame = new JFrame("Server Launcher - shutting down");
						shutdownFrame.setMinimumSize(frame.getSize());
	
						frame.setVisible(false);
						
						final JLabel infoLabel = new JLabel("shutting down...");
						infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
						shutdownFrame.add(infoLabel);
						
						final JButton cancelButton = new JButton("Cancel");
						cancelPressed = false;
						cancelButton.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								cancelPressed = true;
								infoLabel.setText("shutdown cancelled");
								cancelButton.setText("please wait...");
								cancelButton.setEnabled(false);
							}
						});
						shutdownFrame.add(cancelButton, BorderLayout.SOUTH);
	
						shutdownFrame.pack();
						shutdownFrame.setLocationRelativeTo(frame);
						shutdownFrame.setVisible(true);
						if(tabsPanel != null) tabsPanel.shutdown();
						
						if(cancelPressed){
							shutdownFrame.dispose();
							frame.setVisible(true);
						}else{
							System.exit(0);
						}
					}
				}.start();
			}
		});
		

		new Thread() {
			
			@Override
			public void run() {
				try{
					tabsPanel = new TabsPanel(new TabsPanel.ProgressListener() {
						
						@Override
						public void onProgress(String message) {
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									startupProgressLabel.setText(message);
									frame.pack();
								}
							});
						}
					});
					contentPane.remove(0);
					contentPane.add(tabsPanel, 0);
					frame.pack();
				}catch(Throwable e){
					contentPane.remove(0);
					
					String title = "<b>Failed to start Server Launcher.</b>";
					if(e instanceof IOException || (e.getMessage() != null && e.getMessage().toLowerCase().contains("access denied"))){
						title += "<br/><i>Try running the application with elevated access rights.</i>";
					}
					JLabel titleLabel = new JLabel("<html>" + title + "</html>");
					titleLabel.setBorder(startupProgressLabel.getBorder());
					contentPane.add(titleLabel, 0);

					CollapsibleTextPane errorPane = new CollapsibleTextPane("Stack Trace:", ExceptionUtils.getStackTrace(e));
					errorPane.setEditable(false);
					errorPane.setCollapsed(true);
					errorPane.setBorder(startupProgressLabel.getBorder());
					errorPane.setPreferredSize(new Dimension(errorPane.getPreferredSize().width, 100));
					contentPane.add(errorPane, 1);

					frame.pack();
				}
			}
		}.start();
	}
	
	public static void putStatus(Object key, Status status){
		statusMap.put(key, status);
		updateStatus();
		
		boolean blink = status == Status.RUNNING;
		if(blink){
			blinks.put(key, BLINK_COUNT);
			
			if(blinkThread == null || !blinkThread.isRunning()){
				blinkThread = new LoopThread() {
					
					boolean active = true;
					
					@Override
					public void loopedRun() {
						activeBlinks.clear();
						List<Object> servers = new ArrayList<Object>(blinks.keySet());
						for(Object server : servers){
							int remainingBlinks = blinks.get(server);
							if(remainingBlinks > 0){
								if(active){
									activeBlinks.add(server);
									blinks.put(server, remainingBlinks - 1);
								}
							}else{
								blinks.remove(server);
							}
						}
						updateStatus();
						try {
							sleep(active ? BLINK_DURATION : BLINK_PAUSE_DURATION);
							active = !active;
						} catch (InterruptedException e) {
							// new blink added, revert changes made by this cycle and 
							active = true;
							for(Object blinkedServer : activeBlinks){
								blinks.put(blinkedServer, blinks.get(blinkedServer) + 1);
							}
						}
						if(blinks.isEmpty()){
							terminate();
						}
					}
				};
				blinkThread.start();
			}else{
				if(blinkThread.getState() == State.TIMED_WAITING){
					blinkThread.interrupt(); // wake back up so the blinking starts immediately
				}
			}
		}
	}
	
	private static void updateStatus(){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Image base = applicationIcon;
				
				Image small = applicationIconSmall; // icon for JFrame
				Image medium = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB); // icon for taskbar
				Image large; // full size just in case

				
				// load top, middle and bottom blinks
				if(activeBlinks.contains(topStatusOwner.get(currentTab))){
					base = ImageUtil.merge(base, topBlink);
				}
				if(activeBlinks.contains(middleStatusOwner.get(currentTab))){
					base = ImageUtil.merge(base, middleBlink);
				}
				if(activeBlinks.contains(bottomStatusOwner.get(currentTab))){
					base = ImageUtil.merge(base, bottomBlink);
				}
				
				// load top, middle and bottom status
				if(statusMap.containsKey(topStatusOwner.get(currentTab))){
					Image lamp = ImageUtil.mask(topLamp, statusMap.get(topStatusOwner.get(currentTab)).getColor());
					base = ImageUtil.merge(base, lamp);
				}
				if(statusMap.containsKey(middleStatusOwner.get(currentTab))){
					Image lamp = ImageUtil.mask(middleLamp, statusMap.get(middleStatusOwner.get(currentTab)).getColor());
					base = ImageUtil.merge(base, lamp);
				}
				if(statusMap.containsKey(bottomStatusOwner.get(currentTab))){
					Image lamp = ImageUtil.mask(bottomLamp, statusMap.get(bottomStatusOwner.get(currentTab)).getColor());
					base = ImageUtil.merge(base, lamp);
				}
				
				
				// load overall status
				overallStatus = null;
				for(Object statusOwner : new Object[]{topStatusOwner.get(currentTab), middleStatusOwner.get(currentTab), bottomStatusOwner.get(currentTab)}){
					Status status = statusMap.get(statusOwner);
					if(status != null){
						int priority = status.getPriority();
						int overallPriority = overallStatus == null ? -1 : overallStatus.getPriority();
						if(priority > overallPriority) overallStatus = status;
					}
				}
				
				int height = base.getHeight(null) / 3;
				if(overallStatus != null && height > 0){
					Image led = ImageUtil.getHeightScaledImage(overallStatusImage, height); // scale
					led = ImageUtil.color(led, overallStatus.getColor()); // set color
					Dimension ledOffset = new Dimension(base.getWidth(null) - led.getWidth(null) - 5, 5);
					large = ImageUtil.merge(base, led, ledOffset);
				}else{
					large = base;
				}
				
				medium.getGraphics().drawImage(large, 0, 0, 40, 40, null);
				
				frame.setIconImages(ListUtil.createList(small, medium, large));
			}
		});
	}
	
	public static void testEclipseLaunches(){
		boolean createDomain = false;
		String[] dmbCreateDomainArgs = new String[]{
				"C:\\Apps\\Tip80\\cs\\java\\jdk170_181\\bin\\javaw.exe",
				"-Dmaven.home=C:\\Apps\\Tip80\\cs\\maven\\3.2.3",
				"-Dclassworlds.conf=C:\\data\\projects\\workspace_dmx\\.metadata\\.plugins\\org.eclipse.m2e.launching\\launches\\m2conf327071764740347191.tmp",
				"-Djavax.net.ssl.trustStore=C:/Apps/Tip80/cs/maven/3.2.3/conf/trust.jks",
				"-Dfile.encoding=Cp1252",
				"-classpath",
				"C:\\Apps\\Tip80\\cs\\maven\\3.2.3\\boot\\plexus-classworlds-2.5.1.jar",
				"org.codehaus.plexus.classworlds.launcher.Launcher",
				"-B",
				"-U",
				"-gs",
				"C:\\Apps\\Tip80\\cs\\maven\\3.2.3\\conf\\settings.xml",
				"jap:createDomain antrun:run"
		};
		String[] dmbBuildArgs = new String[]{
				"cmd",
				"/c",
				"start",
				"/wait",
				"\"Building DMB\"",
				"C:\\Apps\\Tip80\\cs\\java\\jdk170_181\\bin\\javaw.exe",
				"-Dmaven.home=C:\\Apps\\Tip80\\cs\\maven\\3.2.3",
				"-Dclassworlds.conf=C:\\data\\projects\\workspace_dmx\\.metadata\\.plugins\\org.eclipse.m2e.launching\\launches\\m2conf5243764932241865917.tmp",
				"-Djavax.net.ssl.trustStore=C:/Apps/Tip80/cs/maven/3.2.3/conf/trust.jks",
				"-Dfile.encoding=Cp1252",
				"-classpath",
				"C:\\Apps\\Tip80\\cs\\maven\\3.2.3\\boot\\plexus-classworlds-2.5.1.jar",
				"org.codehaus.plexus.classworlds.launcher.Launcher",
				"-Pdisable-approval-queue",
				"-B",
				"-U",
				"-Dmaven.test.skip=true",
				"-DskipTests",
				"-gs",
				"C:\\Apps\\Tip80\\cs\\maven\\3.2.3\\conf\\settings.xml",
				"clean",
				"install",
				"jap:deployLocal"
		};
		File dmbDir = new File("C:\\data\\projects\\workspace_dmx\\DMB");
		try {
			Process p = Runtime.getRuntime().exec(createDomain ? dmbCreateDomainArgs : dmbBuildArgs, null, dmbDir);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while ((s = stdInput.readLine()) != null) {
			    System.out.println(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setTopStatusOwner(Object topStatusOwner, Tab tab) {
		ServerLauncher.topStatusOwner.put(tab, topStatusOwner);
		updateStatus();
	}

	public static void setMiddleStatusOwner(Object middleStatusOwner, Tab tab) {
		ServerLauncher.middleStatusOwner.put(tab, middleStatusOwner);
		updateStatus();
	}

	public static void setBottomStatusOwner(Object bottomStatusOwner, Tab tab) {
		ServerLauncher.bottomStatusOwner.put(tab, bottomStatusOwner);
		updateStatus();
	}
	
	public static void setCurrentTab(Tab currentTab){
		ServerLauncher.currentTab = currentTab;
		updateStatus();
	}
	
	public static void setStatusOwner(Object statusOwner, Tab tab, Position position){
		switch (position) {
		case TOP:
			setTopStatusOwner(statusOwner, tab);
			break;
		case MIDDLE:
			setMiddleStatusOwner(statusOwner, tab);
			break;
		case BOTTOM:
			setBottomStatusOwner(statusOwner, tab);
			break;
		}
	}
	
	
	
}

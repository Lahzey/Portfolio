package ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.ImageUtil;
import graphics.swing.components.JAnimationPanel;
import graphics.swing.components.JImage;
import graphics.swing.components.JLamp;
import util.ColorUtil;
import util.ExtendedThread;
import util.LoopThread;
import util.ProcessUtil;

import logic.FileLaunchable;
import logic.Status;
import main.ServerLauncher;
import net.miginfocom.swing.MigLayout;

public class ServerPanel extends JAnimationPanel implements FocusListener {
	private static final long serialVersionUID = 1L;
	private static final Pattern PROCESS_ID_PATTERN = Pattern.compile(".*PID:([0-9]+).*", Pattern.DOTALL);
	
	private static final Color STOP_COLOR = new Color(160, 0, 0);
	private static final Color START_COLOR = new Color(33, 127, 0);
	
	// Parameter
	private final FileLaunchable launchable;
	
	// Current Process
	private Process currentProcess;
	private Integer processID = null;
	protected Status status;
	
	// Output
	private BufferedInputStream outputReader;
	private File outputFile;
	private final StringBuilder cashedOutput = new StringBuilder(); // used to store output until the process id has been found
	
	// Output Parsing
	private Map<String, Integer> startedPatterns = new HashMap<String, Integer>();
	private Map<String, Integer> startedPatternMatchStarts = new HashMap<String, Integer>();
	private MatchListener startedMatchListener;
	
	private Map<String, Integer> nonCriticalErrorPatterns = new HashMap<String, Integer>();
	private Map<String, Integer> nonCriticalErrorPatternMatchStarts = new HashMap<String, Integer>();
	private MatchListener nonCriticalErrorMatchListener;
	
	private Map<String, Integer> criticalErrorPatterns = new HashMap<String, Integer>();
	private Map<String, Integer> criticalErrorPatternMatchStarts = new HashMap<String, Integer>();
	private MatchListener criticalErrorMatchListener;
	
	
	// GUI
	private JLamp statusLamp = new JLamp();
	private JImage iconImage;
	private JLabel nameLabel;
	private JButton viewLogButton;
	protected JImage stopButton;
	protected JImage startButton;
	
	private LogPanel logPanel;
	private JFrame logFrame;
	
	// Wave animation
	private float waveProgress = 0f;
	private int waveWidth = 10;
	private int waveBlendRadius = 250;
	private Color waveColor = Color.GREEN;
	
	
	public ServerPanel(FileLaunchable launchable){
		super(new MigLayout("insets 0 5 0 5", "[]5px[]1px[grow, fill]15px[]15px[]10px[]", ""));
		this.launchable = launchable;
		setStatus(Status.STOPPED);
		setOpaque(false);
		
		iconImage = new JImage(launchable.getIcon());
		nameLabel = new JLabel(launchable.getName());
		viewLogButton = new JButton("View Log", FontIcon.of(FontAwesomeSolid.EYE, nameLabel.getFontMetrics(nameLabel.getFont()).getHeight(), Color.DARK_GRAY));
		viewLogButton.setFocusable(false);
		viewLogButton.setOpaque(false);
		stopButton = new JImage(FontAwesomeSolid.STOP, STOP_COLOR);
		stopButton.scalePreferredSize(1.2f);
		stopButton.generateStateImages();
		stopButton.addFocusListener(this);
		stopButton.setEnabled(false);
		startButton = new JImage(FontAwesomeSolid.PLAY, START_COLOR);
		startButton.scalePreferredSize(1.2f);
		startButton.generateStateImages();
		startButton.addFocusListener(this);
		
		add(statusLamp);
		add(iconImage);
		add(nameLabel);
		add(viewLogButton);
		add(stopButton);
		add(startButton);
		
		viewLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewLog();
			}
		});
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		
		logFrame = new JFrame("Log of " + launchable.getName());
		Image logImage;
		if(launchable.getIcon() != null){
			Image background = FontIcon.of(FontAwesomeSolid.CLIPBOARD, 40).toImage();
			Image foreground = ImageUtil.trim(ImageUtil.getWidthScaledImage(launchable.getIcon(), 25), 100);
			Dimension foregroundOffset = new Dimension(background.getWidth(null) / 2 - foreground.getWidth(null) / 2, background.getHeight(null) - 3 - foreground.getHeight(null));
			logImage = ImageUtil.blend(background, foreground, foregroundOffset, 4, 4);
		}else{
			logImage = FontIcon.of(FontAwesomeSolid.CLIPBOARD_LIST, 40).toImage();
		}
		logFrame.setIconImage(logImage);
		logPanel = new LogPanel(this);
		logFrame.add(logPanel);
		logFrame.pack();
		logFrame.setLocationRelativeTo(this);
	}
	
	public void start(){
		stop();
		logPanel.clear();
		viewLogButton.setText("View Log");
		if(startButton.hasFocus()) startButton.transferFocus();
		startButton.setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		new ExtendedThread() {
			
			@Override
			public void run() {
				try {
					setStatus(Status.STARTING);
					outputFile = File.createTempFile("ServerLauncherOutput", null);
					currentProcess = launchable.launch(outputFile);
					setCursor(Cursor.getDefaultCursor());
					logPanel.repaint();
					startStreams();
					currentProcess.waitFor();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					currentProcess = null;
					processID = null;
					logPanel.repaint();
					stopStreams();
					if(outputFile != null){
						outputFile.delete();
						outputFile = null;
					}
					setStatus(Status.STOPPED);
				}
			}
		}.start();

		waveColor = Status.RUNNING.getColor();
		startAnimation(new Animation(1000) {
			
			@Override
			public void apply(JAnimationPanel panel, float progress) {
				waveProgress = progress;
				repaint();
			}
		}).then(new Runnable() {
			
			@Override
			public void run() {
				waveProgress = 0f;
				repaint();
			}
		});
	}
	
	public void stop(){
		new Thread(){
			public void run(){
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if(stopButton.hasFocus()) stopButton.transferFocus();
				stopButton.setEnabled(false);
				logPanel.repaint();
				if(currentProcess != null){
					if(ProcessUtil.isAlive(currentProcess)){
						while(processID == null){
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// does not matter
							}
						}
		                try {
							Runtime.getRuntime().exec(new String[]{ "cmd", "/c", "taskkill /f /t /pid " + processID}).waitFor();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				setCursor(Cursor.getDefaultCursor());
			}
		}.start();

		waveColor = Status.ERROR.getColor();
		startAnimation(new Animation(1000) {
			
			@Override
			public void apply(JAnimationPanel panel, float progress) {
				waveProgress = progress;
				repaint();
			}
		}).then(new Runnable() {
			
			@Override
			public void run() {
				waveProgress = 0f;
				repaint();
			}
		});
	}
	
	public void setStatus(Status status){
		this.status = status;
		statusLamp.setColor(status.getColor());
		statusLamp.setToolTipText(status.name().toLowerCase());
		if(logPanel != null) logPanel.repaint();
		ServerLauncher.putStatus(this, status);
	}
	
	public Status getStatus(){
		return status;
	}
	
	public Image getStatusImage(){
		return statusLamp.getTintedImage();
	}
	
	public void viewLog(){
		if(!logFrame.isVisible()){
			logFrame.setVisible(true);
			logFrame.addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosed(WindowEvent e) {
					System.out.println("closed");
					logFrame.removeWindowListener(this);
				}
			});
			logPanel.setScrollToBottom(true);
		}
		logFrame.toFront();
		logFrame.setState(JFrame.NORMAL);
		
		
	}
	
	private void startStreams() throws FileNotFoundException{
		for(String pattern : launchable.getStartedLogPatterns()) startedPatterns.put(pattern.toLowerCase(), 0);
		for(String pattern : launchable.getNonCriticalErrorLogPatterns()) nonCriticalErrorPatterns.put(pattern.toLowerCase(), 0);
		for(String pattern : launchable.getCriticalErrorLogPatterns()) criticalErrorPatterns.put(pattern.toLowerCase(), 0);
		startedMatchListener = new MatchListener() {
			
			@Override
			public void onMatch(String pattern, int startIndex, int endIndex) {
				logPanel.addMessage(startIndex, endIndex, LogPanel.SUCCESS);
				setStatus(Status.RUNNING);
			}
		};
		nonCriticalErrorMatchListener = new MatchListener() {
			
			@Override
			public void onMatch(String pattern, int startIndex, int endIndex) {
				logPanel.addMessage(startIndex, endIndex, LogPanel.WARNING);
				viewLogButton.setText("View Log (" + logPanel.getMessageCount(LogPanel.WARNING, LogPanel.ERROR) + ")");
			}
		};
		criticalErrorMatchListener = new MatchListener() {
			
			@Override
			public void onMatch(String pattern, int startIndex, int endIndex) {
				logPanel.addMessage(startIndex, endIndex, LogPanel.ERROR);
				setStatus(Status.ERROR);
				viewLogButton.setText("View Log (" + logPanel.getMessageCount(LogPanel.WARNING, LogPanel.ERROR) + ")");
			}
		};
		
		outputReader = new BufferedInputStream(new FileInputStream(outputFile));
		new LoopThread() {

			@Override
			public void loopedRun() {
				try {
					if(outputReader == null) terminate();
					else if (outputReader.available() > 0) {
						addOutput((char) outputReader.read());
					}else{
						sleepSilent(100);
					}
				} catch (IOException e) {
					e.printStackTrace();
					terminate();
				}
			}
		}.start();
	}
	
	private void stopStreams() {
		try {
			if(outputReader != null) outputReader.close();
			outputReader = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addOutput(char output){
		if(processID == null){
			if(output == '\n'){
				Matcher m = PROCESS_ID_PATTERN.matcher(cashedOutput.toString());
				if(m.matches()){
					processID = Integer.parseInt(m.group(1));
					stopButton.setEnabled(true);
					logPanel.repaint();
					cashedOutput.setLength(0); // not needed anymore
				}
			}else{
				cashedOutput.append(output);
			}
		}
		
		logPanel.append("" + output);
		
		int index = logPanel.length() - 1;
		char lowercaseOutput = Character.toLowerCase(output);
		parseOutput(lowercaseOutput, index, startedPatterns, startedPatternMatchStarts, startedMatchListener);
		parseOutput(lowercaseOutput, index, nonCriticalErrorPatterns, nonCriticalErrorPatternMatchStarts, nonCriticalErrorMatchListener);
		parseOutput(lowercaseOutput, index, criticalErrorPatterns, criticalErrorPatternMatchStarts, criticalErrorMatchListener);
	}
	
	private static void parseOutput(char lowercaseOutput, int logIndex, Map<String, Integer> patterns, Map<String, Integer> patternStarts, MatchListener matchListener){
		for(String pattern : patterns.keySet()){
			int index = patterns.get(pattern);
			char nextPatternChar = pattern.charAt(index);
			if(nextPatternChar == lowercaseOutput){
				// If pattern has just started matching, map start index
				if(index == 0){
					patternStarts.put(pattern, logIndex);
				}
				
				index++;
				if(pattern.length() > index){
					// Next char of pattern matched
					patterns.put(pattern, index);
				} else {
					// Full pattern matched
					patterns.put(pattern, 0);
					matchListener.onMatch(pattern, patternStarts.get(pattern), logIndex + 1); // + 1 because end indexes are always excluded
					
				}
			}else if(nextPatternChar != '*' && index > 0){
				int lastWildcardIndex = pattern.lastIndexOf('*', index);
				if(lastWildcardIndex < 0){
					// Reset pattern
					patterns.put(pattern, 0);
				}else{
					// Jump back to last wildcard
					patterns.put(pattern, lastWildcardIndex);
				}
			} // else just skip the char
		}
	}
	
	private static interface MatchListener{
		public void onMatch(String pattern, int startIndex, int endIndex);
	}
	
	public FileLaunchable getLaunchable(){
		return launchable;
	}
	
	@Override
	protected void paintChildren(Graphics g){
		super.paintChildren(g);
		if(waveProgress > 0){
			Graphics2D g2d = (Graphics2D) g;
			Paint paint = g2d.getPaint();
			Composite composite = g2d.getComposite();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			
			Color transparent = new Color(waveColor.getRed(), waveColor.getGreen(), waveColor.getBlue(), 0);
			int width = getWidth();
			int height = getHeight();
			int totalWaveWidth = waveBlendRadius * 2 + waveWidth;
			int minWaveX = -totalWaveWidth;
			int maxWaveX = width + totalWaveWidth;
			int totalWaveDist = maxWaveX - minWaveX;
			int waveX = minWaveX + (int) (totalWaveDist * waveProgress);
			
			GradientPaint startGradient = new GradientPaint(waveX, 0, transparent, waveX + waveBlendRadius, 0, waveColor);
			g2d.setPaint(startGradient);
			g2d.fillRect(waveX, 0, waveBlendRadius, height);
			
			g2d.setPaint(waveColor);
			g2d.fillRect(waveX + waveBlendRadius, 0, waveWidth, height);
			
			GradientPaint endGradient = new GradientPaint(waveX + waveBlendRadius + waveWidth, 0, waveColor, waveX + totalWaveWidth, 0, transparent);
			g2d.setPaint(endGradient);
			g2d.fillRect(waveX + waveBlendRadius + waveWidth, 0, waveBlendRadius, height);
			
			g2d.setPaint(paint);
			g2d.setComposite(composite);
		}
		if(hasFocus() || startButton.hasFocus() || stopButton.hasFocus()){
			Graphics2D g2d = (Graphics2D) g;
			Paint paint = g2d.getPaint();
			
			Color color = Color.LIGHT_GRAY;
			Color transparent = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
			GradientPaint gp = new GradientPaint(0, 0, color, 5, 0, transparent);
			g2d.setPaint(gp);
			g.fillRect(0, 0, 10, getHeight());

			g2d.setPaint(paint);
		}
	}
	
//	@Override
//	public boolean isOpaque() {
//		return false;
//	}

	@Override
	public void focusGained(FocusEvent e) {
		repaint();
	}

	@Override
	public void focusLost(FocusEvent e) {
		repaint();
	}
}

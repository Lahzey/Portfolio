package ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JFileChooser;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

import graphics.swing.TabbedPane;
import graphics.swing.TextFieldFileChooser;
import util.ObjectStore;
import util.zip.ZipRootFile;

import logic.FileLaunchable;
import logic.FileLaunchable.Type;
import logic.SaveState;
import logic.SaveState.Item;
import logic.SaveState.Tab;
import main.ServerLauncher;
import mslinks.ShellLink;
import ui.LauncherPanel.Position;

public class TabsPanel extends TabbedPane<LauncherPanel> {

	private ObjectStore<SaveState> stateStore = new ObjectStore<SaveState>(SaveState.class, new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/server_launcher_data.json"));
	private SaveState saveState;
	
	private boolean preventOnChange = false;
	private long lastPersistTime = System.currentTimeMillis();
	
	public TabsPanel(ProgressListener progressListener){
		saveState = stateStore.get();
		if(saveState == null){
			firstTimeSetup(progressListener);
		}
		File unxUtilsFolder = new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/UnxUtils");
		if(!unxUtilsFolder.exists()){
			progressListener.onProgress("Extracting Unx Utils...");
			try {
				File unxUtilsZip = new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/UnxUtils.zip");
				Files.copy(FileLaunchable.class.getResourceAsStream("scripts/UnxUtils.zip"), unxUtilsZip.toPath());
				ZipRootFile zip = new ZipRootFile(unxUtilsZip);
				zip.extract(unxUtilsFolder);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		preventOnChange = true;
		for(Tab tab : saveState.tabs){
			LauncherPanel launcherPanel = new LauncherPanel(tab);
			add(launcherPanel, tab.getName());
		}
		setCurrentTab(saveState.currentTabIndex);
		preventOnChange = false;
		updateFromState();
		
		enableAdd(new Callable<LauncherPanel>() {
			
			@Override
			public LauncherPanel call() throws Exception {
				Tab tab = new Tab("Tab " + (saveState.tabs.size() + 1));
				return new LauncherPanel(tab);
			}
		});
	}
	
	private void firstTimeSetup(ProgressListener progressListener){
		progressListener.onProgress("Performing First-Time-Setup...");
		
		SaveState saveState = new SaveState();
		Tab dmxTab = new Tab("DMX");
		saveState.tabs.add(dmxTab);
		stateStore.store(saveState);
		this.saveState = saveState;

		// DMB Link
		try {
			progressListener.onProgress("Performing First-Time-Setup: Creating DMB Start...");
			File dmbIcon = new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/DMB Icon.ico");
			if(!dmbIcon.exists()){
				Files.copy(getClass().getResourceAsStream("images/dmblogo_icon.ico"), dmbIcon.toPath());
			}
			
			File dmbLinkFile = new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/DMB Start.lnk");
			if(!dmbLinkFile.exists()){
				ShellLink dmbLink = ShellLink.createLink("C:\\Apps\\Tip80\\cs\\appsrv\\domains\\dmb_v00_8002_localhost\\startWebLogic.cmd");
				dmbLink.setCMDArgs("-debug");
				dmbLink.setWorkingDir("C:\\Apps\\Tip80\\cs\\appsrv\\domains\\dmb_v00_8002_localhost");
				dmbLink.setIconLocation(dmbIcon.getAbsolutePath());
				dmbLink.saveTo(dmbLinkFile.getAbsolutePath());
			}
			
			dmxTab.put(Position.TOP, new Item(dmbLinkFile.getAbsolutePath(), Type.SERVER));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// DMB Link
		try {
			progressListener.onProgress("Performing First-Time-Setup: Creating DMF Start...");
			File dmfIcon = new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/DMF Icon.ico");
			if(!dmfIcon.exists()){
				Files.copy(getClass().getResourceAsStream("images/dmflogo_icon.ico"), dmfIcon.toPath());
			}
			
			File dmfLinkFile = new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/DMF Start.lnk");
			if(!dmfLinkFile.exists()){
				ShellLink dmfLink = ShellLink.createLink("C:\\Apps\\Tip80\\cs\\appsrv\\domains\\dmf_v00_7002_localhost\\startWebLogic.cmd");
				dmfLink.setCMDArgs("-debug");
				dmfLink.setWorkingDir("C:\\Apps\\Tip80\\cs\\appsrv\\domains\\dmf_v00_7002_localhost");
				dmfLink.setIconLocation(dmfIcon.getAbsolutePath());
				dmfLink.saveTo(dmfLinkFile.getAbsolutePath());
			}

			dmxTab.put(Position.MIDDLE, new Item(dmfLinkFile.getAbsolutePath(), Type.SERVER));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// WatchApp CMD File
		try {
			progressListener.onProgress("Performing First-Time-Setup: Creating WatchApp Start...");
			File cmdFile = new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/WatchApp Start.cmd");
			if(!cmdFile.exists()){
				String workspaceLocation = "C:\\data\\projects\\workspace_dmx\\DMF";
				if(!new File(workspaceLocation).isDirectory()){
					File workspaceDir = TextFieldFileChooser.showDialog("Select DMF Workspace", workspaceLocation, JFileChooser.OPEN_DIALOG, DirectoryFileFilter.DIRECTORY, null);
					workspaceLocation = workspaceDir == null ? null : workspaceDir.getAbsolutePath();
				}
				if(workspaceLocation != null){
					cmdFile.createNewFile();
					FileOutputStream output = new FileOutputStream(cmdFile);
					String command = "cd \"" + workspaceLocation + "\\dmt-pl-core-war\\src\\main\\js\"\nnpm run watch-app\npause";
					output.write(command.getBytes());
					output.close();

					dmxTab.put(Position.BOTTOM, new Item(cmdFile.getAbsolutePath(), Type.WATCHAPP));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onChange(){
		super.onChange();
		if(!preventOnChange){
			preventOnChange = true;
			new Thread() {
				
				@Override
				public void run() {
					storeSettings(lastPersistTime < System.currentTimeMillis() - (1000 * 60));
					updateFromState();
					preventOnChange = false;
				}
			}.start();
		}
	}
	
	private void updateFromState(){
		ServerLauncher.setCurrentTab(saveState.tabs.size() > saveState.currentTabIndex ? saveState.tabs.get(saveState.currentTabIndex) : null);
		
	}
	
	private void storeSettings(boolean persist){
		synchronized(saveState.tabs){
			saveState.currentTabIndex = getCurrentTab();
			saveState.tabs.clear();
			List<LauncherPanel> tabContents = getTabContents();
			List<String> tabNames = getTabNames();
			for(int i = 0; i < tabContents.size(); i++){
				Tab tab = tabContents.get(i).getTab();
				tab.setName(tabNames.get(i));
				saveState.tabs.add(tab);
			}
			if(persist) stateStore.store(saveState);
		}
		if(persist) lastPersistTime = System.currentTimeMillis();
	}

	public void shutdown() {
		storeSettings(true);
		for(LauncherPanel tabContent : getTabContents()){
			tabContent.shutdown();
		}
	}
	
	
	public static interface ProgressListener {
		public void onProgress(String message);
	}
}

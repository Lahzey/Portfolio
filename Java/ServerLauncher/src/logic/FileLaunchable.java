package logic;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import main.ServerLauncher;
import me.marnic.jiconextract.extractor.IconSize;
import me.marnic.jiconextract.extractor.JIconExtractor;

public class FileLaunchable implements Launchable{
	
	private File toLaunch;
	private Type type;
	private Image icon;
	
	public FileLaunchable(File toLaunch, Type type){
		if(!toLaunch.exists()) throw new IllegalArgumentException("File must exist.");
		this.toLaunch = toLaunch;
		this.type = type;
		icon = JIconExtractor.getJIconExtractor().extractIconFromFile(toLaunch, IconSize.EXTRALARGE);
	}
	
	public Type getType() {
		return type;
	}
	
	public File getFile() {
		return toLaunch;
	}

	@Override
	public Process launch(File output) throws IOException {
		String linkPath = toLaunch.getAbsolutePath();
		File startCommandFile = new File(ServerLauncher.RESOURCE_DIR.getAbsolutePath() + "/startAndGetPID.bat");
		if(!startCommandFile.exists()){
			copyStartCmd(startCommandFile);
		}
        return Runtime.getRuntime().exec(new String[]{ "cmd", "/c", "start /min /wait \"Starting " + toLaunch.getName() + "\" " + startCommandFile.getAbsolutePath() + " \"" + linkPath + "\" \"" + output.getAbsolutePath() + "\""});
	}

	@Override
	public String getName() {
		return toLaunch.getName();
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	@Override
	public String[] getStartedLogPatterns() {
		switch(type){
		case WATCHAPP:
			return new String[]{"bytes written to"};
		default:
			return new String[]{"Server state changed to RUNNING"};
		}
	}

	@Override
	public String[] getNonCriticalErrorLogPatterns() {
		switch(type){
		case WATCHAPP:
			return new String[]{};
		default:
			return new String[]{"Exception"};
		}
	}

	@Override
	public String[] getCriticalErrorLogPatterns() {
		switch(type){
		case WATCHAPP:
			return new String[]{"Error"};
		default:
			return new String[]{"FATAL ERROR", "FATAL EXCEPTION"};
		}
	}
	
	
	
	private static void copyStartCmd(File destination){
		try {
			Files.copy(FileLaunchable.class.getResourceAsStream("scripts/startAndGetPID.bat"), destination.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static enum Type {
		SERVER("Server"), WATCHAPP("Watch App");
		
		private String displayName;
		
		private Type(String displayName){
			this.displayName = displayName;
		}
		
		public String getDisplayName(){
			return displayName;
		}
	}

}

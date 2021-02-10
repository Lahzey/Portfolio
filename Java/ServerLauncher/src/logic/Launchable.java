package logic;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

public interface Launchable {

	
	public Process launch(File output) throws IOException;
	
	public String getName();
	public Image getIcon();
	
	public String[] getStartedLogPatterns();
	public String[] getNonCriticalErrorLogPatterns();
	public String[] getCriticalErrorLogPatterns();
	
}

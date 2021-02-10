package graphics.swing;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.LoopThread;

/**
Create a screenshot or video (BufferedImage List) of a component.
@author created by Andrew Thompson (https://stackoverflow.com/a/5853992/5784265)
@author expanded by Arno Rohner
*/
public class ComponentRecorder {
	
	private static final Map<Component, List<BufferedImage>> CURRENTLY_RECORDING = new HashMap<Component, List<BufferedImage>>();
	private static LoopThread recordThread;
	public static final int RECORDING_FPS = 30;

  static final String HELP =
    "Type Ctrl-0 to get a screenshot of the current GUI.\n" +
    "The screenshot will be saved to the current directory as 'screenshot.png'.";

  public static BufferedImage getScreenShot(Component component) {
    BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
    // call the Component's paint method, using
    // the Graphics object of the image.
    component.paint(image.getGraphics()); // alternately use .printAll(..)
    return image;
  }
  
  public static boolean startRecording(Component component){
	  if(!CURRENTLY_RECORDING.containsKey(component)){
		  CURRENTLY_RECORDING.put(component, new ArrayList<BufferedImage>());
		  if(recordThread == null || !recordThread.isRunning()){
			  recordThread = new LoopThread(RECORDING_FPS) {
				
				@Override
				public void loopedRun() {
					for(Component toRecord : CURRENTLY_RECORDING.keySet()){
						CURRENTLY_RECORDING.get(toRecord).add(getScreenShot(toRecord));
					}
					if(CURRENTLY_RECORDING.isEmpty()) terminate();
				}
			};
			recordThread.start();
		  }
		  return true;
	  }else return false;
  }
  
  public static List<BufferedImage> stopRecording(Component component){
	  if(CURRENTLY_RECORDING.containsKey(component)){
		  List<BufferedImage> frames = CURRENTLY_RECORDING.get(component);
		  CURRENTLY_RECORDING.remove(component);
		  return frames;
	  }else return null;
  }
} 
package logic;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class RobotController {
	
	public static final RobotController INSTANCE = new RobotController();

	private Robot rb;
	private Date date_stop;
	private boolean active;
	
	private List<ChangeListener> changeListeners = new ArrayList<>();
	
	private Random random = new Random();

	public RobotController() {
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isActive(){
		return active;
	}

	public void startMouseMoving() {
		System.out.println("Started mouse moving at: " + new Date().toString());
		active = true;
		new Thread() {
			
			@Override
			public void run() {
				int counter = 0;
				while (active) {
					Date date = new Date();
					if (date_stop != null) {
						System.out.println("not null");
						if (date_stop.getHours() == date.getHours() && date_stop.getMinutes() == date.getMinutes()) {
							active = false;
						}
					}
					rb.mouseMove((int) (random.nextFloat() * 1000), (int)(random.nextFloat() * 1000));
					counter++;
					System.out.println("Mouse has been moved "+counter+" times" );
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Stopped mouse moving at: " + new Date().toString());
			}
		}.start();
	}

	public void stopMouseMoving() {
		active = false;
	}

	public void setDateStop(String hours, String minutes) {
		if (!(hours.isEmpty() && minutes.isEmpty())) {
			date_stop = new Date();
			date_stop.setSeconds(0);
			date_stop.setHours(Integer.parseInt(hours));
			date_stop.setMinutes(Integer.parseInt(minutes));
		}else{
			date_stop = null;
		}
			
	}
	
	
	public static interface ChangeListener {
		public void onChange();
	}
}

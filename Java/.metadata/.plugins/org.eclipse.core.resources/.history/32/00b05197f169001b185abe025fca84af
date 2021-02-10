package com.creditsuisse.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A timer that measures time passed in millisecond accuracy. This timer works with <code>System.currentTimeMillis()</code>.
 * <br/><br/>With a speed multiplier (see {@link #setSpeedMult(double)}) of 1.0, 1.0 millisecond in this timer equals 1.0 millisecond in the real world.
 * With a multiplier of 0.5, 1.0 millisecond in this timer equals 2.0 milliseconds in the real world.
 * <br/>The multiplier may be negative, causing the time to go back.
 * <br/><br/>The timer can be paused, effectively setting the speed multiplier to 0. Resuming it will set it to the previous amount again.
 * @author A469627
 *
 */
public class Timer implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * A map containing all the start times with the speed multiplier. Every time the speed multiplier is changed,
	 * the time is mapped with the new multiplier.
	 */
	protected Map<Long, Double> timeSpeedHistory = Collections.synchronizedMap(new HashMap<Long, Double>());
	
	/**
	 * A list of all the start times. Every time the speed multiplier is changed, the current time is added to the end,
	 * marking the start of a new section with its own multiplier. To get that multiplier, use {@link #timeSpeedHistory}.
	 */
	protected List<Long> timeHistory = Collections.synchronizedList(new ArrayList<Long>());
	
	/**
	 * The time elapsed before the last speed multiplier change. That way, the time only has to be calculated with the latest speed multiplier, improving the performance.
	 */
	protected long timeElapsed = 0;
	
	/**
	 * The speed multiplier currently active. Can also be fetched by calling <code>timeSpeedHistory.get(timeHistory.get(timeHistory.size()-1))</code>, but not as quickly.
	 */
	protected double currentSpeedMult;
	
	/**
	 * The speed multiplier before the last pause. Used to resume with the same speed again, as pausing changes the speed multiplier.
	 */
	protected double speedMultBeforePause;
	
	/**
	 * If the timer is currently paused.
	 */
	protected boolean paused;
	
	
	
	

	/**
	 * Starts the timer with a speed multiplier of 1 / normal time (this will reset any previous counters).
	 */
	public void start(){
		start(1);
	}
	
	/**
	 * Starts the timer with a given speed multiplier (this will reset any previous counters).
	 * @param speedMult the speed at which time will pass. For more information, see {@link #setSpeedMult(double)}.
	 */
	public void start(double speedMult){
		reset();
		setSpeedMult(speedMult);
	}
	
	/**
	 * Resets the timer. It will have to be started again.
	 */
	public void reset(){
		timeSpeedHistory = Collections.synchronizedMap(new HashMap<Long, Double>());
		timeHistory = Collections.synchronizedList(new ArrayList<Long>());
		timeElapsed = 0;
	}
	
	/**
	 * The milliseconds passed, taking the speed multiplier in account.
	 * @return
	 */
	public long getMillis(){
		return timeElapsed + getRecentTimeUntil(System.currentTimeMillis());
	}
	
	/**
	 * Gets the time elapsed between the last change of the speed multiplier (taking the speed multiplier in account) and the given time.
	 * @param until until when the elapsed time should be counted.
	 * @return the time elapsed, taking the speed multiplier in account.
	 */
	protected long getRecentTimeUntil(long until){
		int historySize = timeHistory.size();
		if(historySize == 0){
			return 0;
		}else{
			long start = timeHistory.get(historySize - 1);
			return (long) ((until - start) * timeSpeedHistory.get(start));
		}
	}
	
	/**
	 * Sets the speed multiplier to the given value.
	 * <br/>Time elapsed will now be multiplied with the given value.
	 * <br/>So setting the multiplier to 2 will make the time go twice as fast.
	 * Negative values will make the time go back.
	 * <br/><br/>Time elapsed before changing the speed multiplier will still be calculated with the old speed.
	 * When extending this class, the sorted list timeHistory (old elements first) and the map timeSpeedHistory
	 * could be used to create a graph displaying the speed changes over time.
	 * @param speedMult the new multiplier to be set.
	 */
	public void setSpeedMult(double speedMult){
		long now = System.currentTimeMillis();
		System.out.println("Setting speed mult to " + speedMult);
		timeElapsed += getRecentTimeUntil(now);
		currentSpeedMult = speedMult;
		timeHistory.add(now);
		timeSpeedHistory.put(now, speedMult);
		paused = false;
	}
	
	/**
	 * @return the speed multiplier that is currently active.
	 * <br/>For more information about the speed multiplier, see {@link #setSpeedMult(double)}.
	 */
	public double getSpeedMult(){
		return currentSpeedMult;
	}
	
	/**
	 * Pauses the timer, effectively setting the speed multiplier to 0.
	 * <br/>This will remember the previous speed multiplier so when calling resume, it will be set back to that.
	 * @see #setSpeedMult(double)
	 */
	public void pause(){
		speedMultBeforePause = getSpeedMult();
		setSpeedMult(0);
		paused = true;
	}
	
	/**
	 * Resumes the timer the the speed multiplier that was active before pausing.
	 * <br/>This will only work, if the timer was previously paused and the speed multiplier was not changed since then.
	 * @see #pause()
	 * @see #setSpeedMult(double)
	 */
	public void resume(){
		if(paused){
			setSpeedMult(speedMultBeforePause);
			paused = false;
		}
	}
}

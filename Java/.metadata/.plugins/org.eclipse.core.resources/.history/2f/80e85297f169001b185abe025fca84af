package com.creditsuisse.util;

/**
 * A Thread that will repeatedly call <code>loopedRun()</code> .
 * <br/>The amount of loops per second can be defined, but may also be undefined (infinite).
 * <br/>It may happen that not that many loops are made per second because the loopedRun itself takes too long.
 * @author A469627
 *
 */
public abstract class LoopThread extends ExtendedThread{
	
	protected float loopsPerSec;
	protected long startTime;
	private float actualLoopsPerSec;
	protected Integer nextSleepTime = null;
	private int loopTime;

	/**
	 * Creates a new LoopThread that loops as fast as possible.
	 */
	public LoopThread(){
		this(0);
	}
	
	/**
	 * Creates a new LoopThread that loops with the defined speed.
	 * @param loopsPerSec the amount of loops that should be done per second.
	 */
	public LoopThread(float loopsPerSec){
		this(loopsPerSec, 0);
	}
	
	/**
	 * Creates a new LoopThread that loops with the defined speed.
	 * @param loopsPerSec the amount of loops that should be done per second.
	 * @param millisUntilStart the wait time before the first loop.
	 */
	public LoopThread(float loopsPerSec, int millisUntilStart){
		this.loopsPerSec = loopsPerSec;
		this.startTime = System.currentTimeMillis() + millisUntilStart;
	}

	/**
	 * @return the amount of loops that should be done per second.
	 * <br/>Call getActualLoopsPerSec() for the actual value
	 */
	public float getLoopsPerSec(){
		return loopsPerSec;
	}
	
	/**
	 * Sets the amount of loops per second this thread can do at max.
	 * @param loopsPerSec the amount of loops
	 */
	public void setLoopsPerSec(float loopsPerSec) {
		this.loopsPerSec = loopsPerSec;
		if(loopsPerSec <= 0) loopTime = 0;
		else loopTime = (int)(1000 / loopsPerSec);
	}

	/**
	 * @return the time at which the first loop should be performed.
	 */
	public long getStartTime(){
		return startTime;
	}
	
	/**
	 * @return the amount of loops actually done per second.
	 */
	public float getActualLoopsPerSec(){
		return actualLoopsPerSec;
	}
	
	@Override
	public void run(){
		onStart();
		if(loopsPerSec <= 0) loopTime = 0;
		else loopTime = (int)(1000 / loopsPerSec);
		long millisUntilLoop = startTime - System.currentTimeMillis();
		if(millisUntilLoop > 0) sleepSilent(millisUntilLoop);
		while(running){
			long timeStarted = System.currentTimeMillis();
			loopedRun();
			long timeTaken = System.currentTimeMillis() - timeStarted;
			long timeToSleep;
			if(nextSleepTime != null){
				timeToSleep = nextSleepTime;
				nextSleepTime = null;
			}else{
				timeToSleep = loopTime - timeTaken;
			}
			try {
				if(timeToSleep > 0) sleep(timeToSleep);
			} catch (InterruptedException e) {
				//Go to the next loop
			}
			long totalLoopTime = System.currentTimeMillis() - timeStarted;
			actualLoopsPerSec = 1000f/totalLoopTime;
		}
		onExit();
	}
	
	/**
	 * This method will be called in every loop.
	 */
	public abstract void loopedRun();
	
	/**
	 * Will be called when the thread starts (before the fist loop)
	 */
	public void onStart(){}
	
	/**
	 * Will be called when the thread terminates (after the last loop)
	 */
	public void onExit(){}
	
	/**
	 * Checks if the thread is running.
	 * <br/>This will return false if the thread has been terminated, but is still executing the last loop or the onExit method.
	 * @return true if the thread is running, false otherwise.
	 */
	public boolean isRunning(){
		return running;
	}
}

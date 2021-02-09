package com.creditsuisse.util;

/**
 * Thread with some personal tweaks.
 * @author A469627
 *
 */
public abstract class ExtendedThread extends Thread{

	protected boolean running = false;
	
	@Override
	public void start(){
		running = true;
		super.start();
	}
	
	@Override
	public abstract void run();
	
	/**
	 * Sets running to false.
	 * <br/>The purpose is that the run method contains a <code>while(running)</code> loop or some other handling to abort the thread.
	 * <br/>Therefore, this should terminate the thread (maybe has to complete some work before though).
	 */
	public void terminate(){
		running = false;
	}
	
	/**
	 * Sleeps and catches any InterruptedException
	 * @param millis
	 */
	public static void sleepSilent(long millis){
		try {
			sleep(millis);
		} catch (InterruptedException e) {
			//Do nothing, its silent
		}
	}

	public boolean isRunning() {
		return running;
	}
}

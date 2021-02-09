package com.creditsuisse.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThreadLooper extends LoopThread implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Map<LoopThread, Long> controlledThreads;
	
	public ThreadLooper() {
		controlledThreads = Collections.synchronizedMap(new HashMap<LoopThread, Long>());
	}

	public void addThread(LoopThread thread){
		controlledThreads.put(thread, thread.getStartTime());
	}

	public void removeThread(LoopThread thread){
		controlledThreads.remove(thread);
	}
	
	public int getThreadCount(){
		return controlledThreads.size();
	}

	@Override
	public void loopedRun() {
		Map<LoopThread, Long> controlledThreads;
		synchronized (this.controlledThreads) {
			controlledThreads = new HashMap<LoopThread, Long>(this.controlledThreads);
		}
		for(LoopThread thread : controlledThreads.keySet()){
			long executionTime = controlledThreads.get(thread);
			if(executionTime <= System.currentTimeMillis()){
				thread.loopedRun();
				long waitTime = (long) (1000 / thread.getLoopsPerSec());
//				if(waitTime != 100.0f)System.out.println(executionTime + ": Wating " + waitTime);
				this.controlledThreads.put(thread, executionTime + waitTime);
			}
		}
	}
	
	
	public static void main(String[] args) {
		final ThreadLooper looper = new ThreadLooper();
		looper.start();
		looper.addThread(new LoopThread(2) {
			
			@Override
			public void loopedRun() {
				System.out.println("0.5 Sec Intervall: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
			}
		});
		looper.addThread(new LoopThread(1) {
			
			@Override
			public void loopedRun() {
				System.out.println("1 Sec Intervall: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
			}
		});
		looper.addThread(new LoopThread(0.5f) {
			
			@Override
			public void loopedRun() {
				System.out.println("2 Sec Intervall: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
				System.out.println("Thread Count: " + looper.getThreadCount());
			}
		});
		for(int i = 0; i < 100000; i++){
			looper.addThread(new LoopThread(10) {
				
				@Override
				public void loopedRun() {
					for(int i = 0; i < 10; i++){
						Math.pow(Math.random(), Math.random());
					}
				}
			});
		}
	}
	
}


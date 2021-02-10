package util;

import java.util.ArrayList;
import java.util.List;

public class HelloWorld {
	
	public int value;
	public String stringValue;
	
	public HelloWorld(int value){
		this.value = value;
		stringValue = "" + value;
	}
	
	public static void main(String[] args) {
		final List<HelloWorld> list = new ArrayList<HelloWorld>();
		int nr = 2;
		new LoopThread(1) {
			
			@Override
			public void loopedRun() {
				System.out.println(list.size() + " primenumbers found");
			}
		}.start();
		while(true){
			boolean divisable = false;
			for(HelloWorld obj : list){
				if(nr % obj.value == 0){
					divisable = true;
					break;
				}
			}
			if(!divisable){
				list.add(new HelloWorld(nr));
			}
			nr++;
		}
	}
}

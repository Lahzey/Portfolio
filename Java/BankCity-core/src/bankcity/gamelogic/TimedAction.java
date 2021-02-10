package bankcity.gamelogic;

public abstract class TimedAction {

	public long time;
	
	public TimedAction(int time){
		this.time = time;
	}
	
	public abstract boolean perform(long currentTime);
	
	
	
	public static abstract class RecurringAction extends TimedAction{

		private long performEvery;
		private long end;
		
		public RecurringAction(long performEvery, int start, int end) {
			super(start);
			this.performEvery = performEvery;
			this.end = end;
		}
		
		public boolean perform(long currentTime){
			while(time < currentTime){
				performRecurring(currentTime);
				time += performEvery;
				if(time > end){
					return true;
				}
			}
			return false;
		}
		
		public abstract void performRecurring(long currentTime);
	}
}

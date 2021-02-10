package bankcity.gamelogic;

public enum Achievement {

	GETTING_STARTED(1), POST(1), RECRUITER(10), DIGITAL_REVOULUTION(1), PLAYFUL(1);
	
	public final int maxLevel;
	
	private Achievement(int maxLevel){
		this.maxLevel = maxLevel;
	}
}

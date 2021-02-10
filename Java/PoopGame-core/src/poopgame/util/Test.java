package poopgame.util;

public class Test {
	
	private static final Object LOCK = new Object();

	
	public static void main(String[] args) {
		synchronized (LOCK) {
			System.out.println("1");
			synchronized (LOCK) {
				System.out.println("2");
			}
		}
	}
}

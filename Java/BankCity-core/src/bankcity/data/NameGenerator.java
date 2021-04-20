package bankcity.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import util.ArrayUtil;
import util.StringUtil;

public class NameGenerator {
	
	private static String[] MALE_NAMES;
	private static String[] FEMALE_NAMES;
	private static String[] SURNAMES;
	
	public static void init() {
		MALE_NAMES = getNameListFromFile("male_names.txt");
		FEMALE_NAMES = getNameListFromFile("female_names.txt");
		SURNAMES = getNameListFromFile("surnames.txt");
	}
	
	private static String[] getNameListFromFile(String fileName) {
		FileHandle fileHandle = Gdx.files.internal(fileName);
		String fileContent = fileHandle.readString();
		String[] lines = fileContent.split("\n");
		for (int i = 0; i < lines.length; i++) {
			lines[i] = StringUtil.capitalizeAt(lines[i].toLowerCase(), 0);
		}
		return lines;
	}

	public static String generatePrename(boolean female) {
		return ArrayUtil.randomElementFrom(female ? FEMALE_NAMES : MALE_NAMES);
	}
	
	public static String generateSurname() {
		return ArrayUtil.randomElementFrom(SURNAMES);
	}
	
}

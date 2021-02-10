package graphics.swing;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import util.ColorUtil;
import util.StringUtil;

public class DurationInputField extends JTextField {

	private static final String[] MILLISECOND_NAMES = {"ms", "milli", "millis", "millisecond", "milliseconds"};
	private static final String[] SECOND_NAMES = {"s", "sec", "secs", "second", "seconds"};
	private static final String[] MINUTE_NAMES = {"m", "min", "mins", "minute", "minutes"};
	private static final String[] HOUR_NAMES = {"h", "hour", "hours"};
	private static final String[] DAY_NAMES = {"d", "day", "days"};
	private static final String[] YEAR_NAMES = {"y", "year", "years"};
	private static final String[][] UNIT_NAMES = {YEAR_NAMES, DAY_NAMES, HOUR_NAMES, MINUTE_NAMES, SECOND_NAMES, MILLISECOND_NAMES};
	private static final Long[] UNIT_SIZES = {1000l * 60 * 60 * 24 * 365, 1000l * 60 * 60 * 24, 1000l * 60 * 60, 1000l * 60, 1000l, 1l};

	private static final Pattern INPUT_PATTERN;
	
	private final Border defaultBorder;
	private final Color defaultBackground;
	
	static{
		String[] patterns = new String[UNIT_NAMES.length];
		for(int i = 0; i < UNIT_NAMES.length; i++){
			patterns[i] = "\\s*(\\d+)\\s*(?:" + StringUtil.join("|", UNIT_NAMES[i]) + ")\\s*";
		}
		String tP = "(?:" + StringUtil.join(")?(?:", patterns) + ")?";
		INPUT_PATTERN = Pattern.compile(tP, Pattern.CASE_INSENSITIVE);
	}
	
	public DurationInputField(String value){
		super(value);
		getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				validateInput();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				validateInput();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				validateInput();
			}
		});
		defaultBorder = getBorder();
		defaultBackground = getBackground();
	}
	
	private void validateInput(){
		if(isInputValid()){
			setBorder(defaultBorder);
			setBackground(defaultBackground);
		}else{
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, ColorUtil.ERROR_BORDER_COLOR, ColorUtil.ERROR_FOREGROUND_COLOR));
			setBackground(ColorUtil.ERROR_BACKGROUND_COLOR);
		}
	}
	
	public boolean isInputValid(){
		return INPUT_PATTERN.matcher(getText()).matches();
	}
	
	
	public static long getMillis(String input){
		long millis = 0;
		Matcher totalMatcher = INPUT_PATTERN.matcher(input);
		if(totalMatcher.matches()){
			for(int i = 0; i < UNIT_NAMES.length; i++){
				String unitInput = totalMatcher.group(i + 1);
				if(unitInput != null){
					long value = Long.parseLong(unitInput);
					value *= UNIT_SIZES[i];
					millis += value;
				}
			}
		} else {
			throw new IllegalArgumentException("'" + input + "' is not a valid duration.");
		}
		return millis;
	}
}

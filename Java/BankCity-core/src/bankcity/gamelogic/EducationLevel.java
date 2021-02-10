package bankcity.gamelogic;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;

import bankcity.BankCity;
import bankcity.data.Text;
import bankcity.util.ColorDrawable;
import bankcity.util.ImageLabel;
import bankcity.util.ImageLabel.ImagePosition;

public enum EducationLevel {
	UNEDUCATED(0, Text.UNEDUCATED, BankCity.getIcon("uneducated.png")), LOW(1, Text.POORLY_EDUCATED, BankCity.getIcon("poorly_educated.png")), MEDIUM(2, Text.EDUCATED, BankCity.getIcon("educated.png")), HIGH(3, Text.WELL_EDUCATED, BankCity.getIcon("well_educated.png"));
	
	private static final Map<Integer, EducationLevel> MAPPED_LEVELS;
	
	static{
		MAPPED_LEVELS = new HashMap<>();
		for(EducationLevel level : values()){
			MAPPED_LEVELS.put(level.value, level);
		}
	}
	
	public final int value;
	public final Text text;
	public final TextureRegion icon;
	
	private EducationLevel(int value, Text text, TextureRegion icon){
		this.value = value;
		this.text = text;
		this.icon = icon;
	}
	
	public EducationLevel lower(){
		return MAPPED_LEVELS.get(value - 1);
	}
	
	public EducationLevel higher(){
		return MAPPED_LEVELS.get(value + 1);
	}
	
	private static EducationLevel[] valuesDescending = {HIGH, MEDIUM, LOW, UNEDUCATED};
	
	
	public static EducationLevel[] valuesDescending(){
		return valuesDescending;
	}
	
	@Override
	public String toString(){
		return text.get();
	}
	
	public Tooltip<ImageLabel> getTooltip(){
		ImageLabel tooltipLabel = new ImageLabel(toString(), icon, ImagePosition.BEFORE);
		tooltipLabel.setBackground(new ColorDrawable(Color.LIGHT_GRAY, Color.DARK_GRAY, 2));
		tooltipLabel.pad(4);
		return new Tooltip<ImageLabel>(tooltipLabel);
	}
}

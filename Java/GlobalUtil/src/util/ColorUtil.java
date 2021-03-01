package util;

import java.awt.Color;
import java.util.Random;

public class ColorUtil {
	
	private static final double GRAYSCALE_RED   = 0.299;
	private static final double GRAYSCALE_GREEN = 0.587;
	private static final double GRAYSCALE_BLUE  = 0.114;
	
	
	
	// message colors (taken from bootstrap)
	public static final Color INFO_BACKGROUND_COLOR = new Color(217, 237, 247);
	public static final Color INFO_BORDER_COLOR = new Color(188, 223, 241);
	public static final Color INFO_FOREGROUND_COLOR = new Color(49, 112, 143);
	
	public static final Color SUCCESS_BACKGROUND_COLOR = new Color(223, 240, 216);
	public static final Color SUCCESS_BORDER_COLOR = new Color(208, 233, 198);
	public static final Color SUCCESS_FOREGROUND_COLOR = new Color(60, 118, 61);
	
	public static final Color WARNING_BACKGROUND_COLOR = new Color(252, 248, 227);
	public static final Color WARNING_BORDER_COLOR = new Color(250, 242, 204);
	public static final Color WARNING_FOREGROUND_COLOR = new Color(138, 109, 59);
	
	public static final Color ERROR_BACKGROUND_COLOR = new Color(242, 222, 222);
	public static final Color ERROR_BORDER_COLOR = new Color(235, 204, 204);
	public static final Color ERROR_FOREGROUND_COLOR = new Color(169, 68, 66);
	
	// severity (for message colors)
	public static final int INFO = 0;
	public static final int SUCCESS = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	
	
	public static Color getBackgroundColor(int severity){
		switch(severity){
		case INFO:
			return INFO_BACKGROUND_COLOR;
		case SUCCESS:
			return SUCCESS_BACKGROUND_COLOR;
		case WARNING:
			return WARNING_BACKGROUND_COLOR;
		case ERROR:
			return ERROR_BACKGROUND_COLOR;
		default:
			throw new IllegalArgumentException(severity + " is not a valid severity");
			
		}
	}
	
	public static Color getBorderColor(int severity){
		switch(severity){
		case INFO:
			return INFO_BORDER_COLOR;
		case SUCCESS:
			return SUCCESS_BORDER_COLOR;
		case WARNING:
			return WARNING_BORDER_COLOR;
		case ERROR:
			return ERROR_BORDER_COLOR;
		default:
			throw new IllegalArgumentException(severity + " is not a valid severity");

		}
	}
	
	public static Color getForegroundColor(int severity){
		switch(severity){
		case INFO:
			return INFO_FOREGROUND_COLOR;
		case SUCCESS:
			return SUCCESS_FOREGROUND_COLOR;
		case WARNING:
			return WARNING_FOREGROUND_COLOR;
		case ERROR:
			return ERROR_FOREGROUND_COLOR;
		default:
			throw new IllegalArgumentException(severity + " is not a valid severity");
			
		}
	}
	
	

	/**
	 * Calculates a color based on the given temperature.
	 * <br/>Red is for hot, dark blue for cold. 
	 * @param celsius the temperature to get the color of
	 * @param minCelsius the coldest temperature. If celsius is that or lower, it's dark blue
	 * @param maxCelsius the hottest temperature. If celsius is that or higher, it's black
	 * @return the created color
	 */
	public static Color getColorByCelsius(float celsius, float minCelsius, float maxCelsius){
		if(minCelsius > maxCelsius) throw new IllegalArgumentException();
		if(celsius > maxCelsius) celsius = maxCelsius;
		else if(celsius < minCelsius) celsius = minCelsius;
		float range = maxCelsius - minCelsius;
		float step = range/4;
		
		int r = 0;
		int g = 0;
		int b = 0;
		if(celsius <= minCelsius + step*1){
			r = 0;
			b = 255;
			
			float min = minCelsius + step*0;
			float advance = celsius - min;
			g = (int)((advance / step) * 255);
		}
		else if(celsius <= minCelsius + step*2){
			r = 0;
			g = 255;
			
			float min = minCelsius + step*1;
			float advance = celsius - min;
			b = 255 - (int)((advance / step) * 255);
		}
		else if(celsius <= minCelsius + step*3){
			b = 0;
			g = 255;
			
			float min = minCelsius + step*2;
			float advance = celsius - min;
			r = (int)((advance / step) * 255);
		}
		else if(celsius <= minCelsius + step*4){
			b = 0;
			r = 255;
			
			float min = minCelsius + step*3;
			float advance = celsius - min;
			g = 255 - (int)((advance / step) * 255);
		}
		return new Color(r, g, b, 255);
		
	}
	
	/**
	 * Converts the given color to the matching gray scale.
	 * @param color the color to convert (will no modify original color)
	 * @return a new color instance that is now gray
	 */
	public static Color grayScale(Color color){
		int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        red = green = blue = (int)(red * GRAYSCALE_RED + green * GRAYSCALE_GREEN + blue * GRAYSCALE_BLUE);
        return new Color(red, green, blue);
	}
	
	/**
	 * Creates a new color with a lower or higher brightness than the given color.
	 * @param color the color to copy and modify.
	 * @param mult the multiplier for the color. R, G and B will not be over 255 or under 0.
	 * @return a new color similar to the given one, but with a different brightness.
	 */
	public static Color changeBrightness(Color color, float mult){
		if(mult < 0) mult = 0;
		int r = Math.min((int)(color.getRed() * mult), 255);
		int g = Math.min((int)(color.getGreen() * mult), 255);
		int b = Math.min((int)(color.getBlue() * mult), 255);
		return new Color(r, g, b, color.getAlpha());
	}
	
	/**
	 * Creates a new color with a lower or higher transparency (alpha) than the given color.
	 * @param color the color to copy and modify.
	 * @param mult the multiplier for the transparency.
	 * @return a new color similar to the given one, but with a different transparency.
	 */
	public static Color changeTransparency(Color color, float mult){
		if(mult < 0) mult = 0;
		int a = Math.min((int)(color.getAlpha() * mult), 255);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
	}
	
	/**
	 * Mixes the given colors together.
	 * @param colors to colors to mix together
	 * @return a new color with red, green, blue and alpha values equal to the average of all given colors (or 0 if no colors are passed).
	 */
	public static Color mix(Color... colors){
		int r = 0;
		int g = 0;
		int b = 0;
		int a = 0;
		for(Color color : colors){
			r += color.getRed();
			g += color.getGreen();
			b += color.getBlue();
			a += color.getAlpha();
		}
		if(colors.length > 0){
			r /= colors.length;
			g /= colors.length;
			b /= colors.length;
			a /= colors.length;
		}
		return new Color(r, g, b, a);
	}
	
	public static Color[] randomDistinctColors(Random random, int count) {
		Color[] colors = new Color[count];
		for(int i = 0; i < count; i++) {
		    float hue = (1f / count) * i;
		    float saturation = 0.6f + 0.2f * random.nextFloat();
		    float brightness = 0.6f + 0.2f * random.nextFloat();
		    colors[i] = Color.getHSBColor(hue, saturation, brightness);
		}
		return colors;
	}
}

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.GIF;
import graphics.ImageUtil;

public class Assets {

	public static final Color BACKGROUND_COLOR = new Color(28, 17, 0);
	public static final Color VIRUS_COLOR = new Color(0, 89, 19);
	
	public static Image BACKGROUND = null;
	public static Image VIRUS_ICON = null;
	public static Image TIME_ICON = null;
	public static GIF WALK_ANIMATION = null;
	
	public static Font FONT = null;
	
	static {
		try {
			BACKGROUND = ImageIO.read(Assets.class.getResource("/resources/led_pattern.jpg"));
			BACKGROUND = ImageUtil.color(BACKGROUND, BACKGROUND_COLOR, 255);

			VIRUS_ICON = ImageIO.read(Assets.class.getResource("/resources/virus.png"));
			VIRUS_ICON = ImageUtil.color(VIRUS_ICON, VIRUS_COLOR, 255);
			
			TIME_ICON = FontIcon.of(FontAwesomeSolid.CLOCK, 100, Color.WHITE).toImage();
			
			WALK_ANIMATION = new GIF(ImageIO.read(Assets.class.getResource("/resources/walk.png")), 12, 1, 100);

			FONT = Font.createFont(Font.TRUETYPE_FONT, Assets.class.getResourceAsStream("/resources/digital_bold.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(FONT);
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
package poopgame.gamelogic;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import poopgame.gamelogic.abilities.passive.KimPassive;
import poopgame.gamelogic.abilities.passive.NinjaPassive;
import poopgame.gamelogic.abilities.passive.PassiveAbility;
import poopgame.gamelogic.abilities.passive.TrumpPassive;
import poopgame.gamelogic.abilities.special.KimSpecial;
import poopgame.gamelogic.abilities.special.NinjaSpecial;
import poopgame.gamelogic.abilities.special.SpecialAbility;
import poopgame.gamelogic.abilities.special.TrumpSpecial;

public enum Champion {

	TRUMP("TRUMP", 0.75f, 1.208f, new TrumpSpecial(), new TrumpPassive(), "Politician that discovered Twitter"),
	KIM("KIM", 0.7f, 1.05f, new KimSpecial(), new KimPassive(), "Loves his Nukes"),
//	SNOOP("SNOOP", 0.2f, 1f, new SnoopSpecial(), new SnoopPassive(), "Outsmoke that!"),
	NINJA("NINJA", 0.3f, 0.6f, new NinjaSpecial(), new NinjaPassive(), "Master of the Body Replacement Technique");
	
	private final String name;
	private final float width;
	private final float height;
	private final SpecialAbility specialAbility;
	private final PassiveAbility passiveAbility;
	
	private final String description;
	
	private Image splash;
	private Image icon;
	private Sound[] jumpSounds;
	
	private Champion(String name, float width, float height, SpecialAbility specialAbility, PassiveAbility passiveAbility, String description) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.specialAbility = specialAbility;
		this.passiveAbility = passiveAbility;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public SpecialAbility getSpecialAbility() {
		return specialAbility;
	}
	
	public PassiveAbility getPassiveAbility() {
		return passiveAbility;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getFolderName() {
		return "champions/" + toString().toLowerCase() + "/";
	}
	
	public Image getSplash() {
		if (splash == null) {
			try {
				splash = ImageIO.read(Gdx.files.internal(getFolderName() + "splash.png").read());
			} catch (IOException e) {
				throw new RuntimeException("Failed to load splash art  for " + getName() + ".", e);
			}
		}
		return splash;
	}
	
	public Image getIcon() {
		if (icon == null) {
			try {
				icon = ImageIO.read(Gdx.files.internal(getFolderName() + "icon.png").read());
			} catch (IOException e) {
				throw new RuntimeException("Failed to load icon for " + getName() + ".", e);
			}
		}
		return icon;
	}
	
	public Sound[] getJumpSounds() {
		if (jumpSounds == null) {
			List<Sound> sounds = new ArrayList<>();
			int i = 1;
			while(true) {
				FileHandle fileHandle = Gdx.files.internal(getFolderName() + "jump_" + i + ".mp3");
				if (fileHandle.exists()) {
					sounds.add(Gdx.audio.newSound(fileHandle));
				} else {
					break;
				}
				i++;
			}
			jumpSounds = sounds.toArray(new Sound[sounds.size()]);
		}
		return jumpSounds;
	}
	
}

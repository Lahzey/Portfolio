package poopgame.gamelogic;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import poopgame.ui.InGameStage;

public class PlayerAnimations {

	// state animations (low priority, looped)
	public final Animation<TextureRegion> idleAnimation; // played while standing sill
	public final Animation<TextureRegion> idleCrouchAnimation; // played while standing sill and crouching
	public final Animation<TextureRegion> movingAnimation; // played while moving
	public final Animation<TextureRegion> movingCrouchAnimation; // played while moving and crouching
	public final Animation<TextureRegion> fallingAnimation; // played while in the air
	

	// action animations (high priority, one-time)
	public final Animation<TextureRegion> poopAnimation; // played when player performs a basic attack
	public final Animation<TextureRegion> jumpAnimation; // played when player jumps
	public final Animation<TextureRegion> castAnimation; // played when player uses his ability
	
	// stores additional character specific animations
	public final Map<String, Animation<TextureRegion>> additionalAnimations = new HashMap<>();
	
	
	public PlayerAnimations(String folder){
		this (
			InGameStage.TEXTURE_MANAGER.getAnimation(folder + "/idle.gif", (Animation.PlayMode.LOOP)),
			InGameStage.TEXTURE_MANAGER.getAnimation(folder + "/idle_crouch.gif", (Animation.PlayMode.LOOP)),
			InGameStage.TEXTURE_MANAGER.getAnimation(folder + "/moving.gif", (Animation.PlayMode.LOOP)),
			InGameStage.TEXTURE_MANAGER.getAnimation(folder + "/moving_crouch.gif", (Animation.PlayMode.LOOP)),
			InGameStage.TEXTURE_MANAGER.getAnimation(folder + "/falling.gif", (Animation.PlayMode.LOOP)),
			InGameStage.TEXTURE_MANAGER.getAnimation(folder + "/poop.gif", (Animation.PlayMode.NORMAL)),
			InGameStage.TEXTURE_MANAGER.getAnimation(folder + "/jump.gif", (Animation.PlayMode.NORMAL)),
			InGameStage.TEXTURE_MANAGER.getAnimation(folder + "/cast.gif", (Animation.PlayMode.NORMAL))
		);
	}

	public PlayerAnimations(Animation<TextureRegion> idleAnimation, Animation<TextureRegion> idleCrouchAnimation,
			Animation<TextureRegion> movingAnimation, Animation<TextureRegion> movingCrouchAnimation,
			Animation<TextureRegion> fallingAnimation, Animation<TextureRegion> poopingAnimation,
			Animation<TextureRegion> jumpingAnimation, Animation<TextureRegion> castingAnimation) {
		this.idleAnimation = idleAnimation;
		this.idleCrouchAnimation = idleCrouchAnimation;
		this.movingAnimation = movingAnimation;
		this.movingCrouchAnimation = movingCrouchAnimation;
		this.fallingAnimation = fallingAnimation;
		this.poopAnimation = poopingAnimation;
		this.jumpAnimation = jumpingAnimation;
		this.castAnimation = castingAnimation;
	}
	
	
	
	
}

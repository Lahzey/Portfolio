package poopgame.gamelogic;

import java.util.HashMap;

public class Stats extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	private static final String ID_INDEX = "idIndex"; // for creating IDs for entities created by this player

	private static final String HEALTH = "health";
	private static final String ENERGY = "energy";
	private static final String ENERGY_REGEN = "energyRegen";
	private static final String DAMAGE = "damage";
	private static final String SPEED = "speed";
	private static final String JUMP_FORCE = "jumpForce";
	private static final String SHOOT_UP_FORCE = "shootUpForce";
	private static final String POOPING_SPEED = "poppingSpeed";
	private static final String MOVING_LEFT = "movingLeft";
	private static final String MOVING_RIGHT = "movingRight";
	private static final String SHOOT_START = "shootStart";
	private static final String LAST_SHOT = "lastShot";

	private static final String OWNER_ID = "ownerId";
	private static final String REMAINING_TIME = "remainingTime";

	public Stats() {
		setIdIndex(0);
	}

	public void setPlayerDefaults(Champion champ) {
		setHealth(100f);
		setEnergy(100f);
		setEnergyRegen(100 / 30f);
		setDamage(10f);
		setSpeed(7.5f);
		setJumpForce(10f);
		setShootUpForce(10);
		setPoopingSpeed(1f);
		setMovingLeft(false);
		setMovingRight(false);
		setShootStart(0);
		setLastShot(0);
		
		switch(champ) {
		case KIM:
			break;
		case NINJA:
			setSpeed(8.5f);
			setJumpForce(11f);
			setEnergyRegen(50f);
			setPoopingSpeed(5f);
			break;
//		case SNOOP:
//			break;
		case TRUMP:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getStat(String statName, Class<T> statType) {
		Object statValue = get(statName);
		if (statValue == null) {
			return null;
		} else {
			if (statType.isAssignableFrom(statValue.getClass())) {
				return (T) get(statName);
			} else {
				throw new IllegalArgumentException("getStat with name " + statName + " expected type " + statType.getName() + " but value is of type " + statValue.getClass().getName());
			}
		}
	}

	public <T> void setStat(String statName, T statValue) {
		put(statName, statValue);
	}

	public int getIdIndex() {
		return getStat(ID_INDEX, Integer.class);
	}

	public void setIdIndex(int idIndex) {
		setStat(ID_INDEX, idIndex);
	}

	public float getHealth() {
		return getStat(HEALTH, Float.class);
	}

	public void setHealth(float health) {
		setStat(HEALTH, health);
	}

	public float getEnergy() {
		return getStat(ENERGY, Float.class);
	}

	public void setEnergy(float energy) {
		if (energy > 100) {
			energy = 100;
		}
		setStat(ENERGY, energy);
	}

	public float getEnergyRegen() {
		return getStat(ENERGY_REGEN, Float.class);
	}

	public void setEnergyRegen(float energyRegen) {
		setStat(ENERGY_REGEN, energyRegen);
	}

	public float getDamage() {
		return getStat(DAMAGE, Float.class);
	}

	public void setDamage(float damage) {
		setStat(DAMAGE, damage);
	}

	public float getSpeed() {
		return getStat(SPEED, Float.class);
	}

	public void setSpeed(float speed) {
		setStat(SPEED, speed);
	}

	public float getJumpForce() {
		return getStat(JUMP_FORCE, Float.class);
	}

	public void setJumpForce(float jumpForce) {
		setStat(JUMP_FORCE, jumpForce);
	}

	public float getShootUpForce() {
		return getStat(SHOOT_UP_FORCE, Float.class);
	}

	public void setShootUpForce(float shootUpForce) {
		setStat(SHOOT_UP_FORCE, shootUpForce);
	}

	public float getPoopingSpeed() {
		return getStat(POOPING_SPEED, Float.class);
	}

	public void setPoopingSpeed(float poopingSpeed) {
		setStat(POOPING_SPEED, poopingSpeed);
	}

	public boolean isMovingLeft() {
		return getStat(MOVING_LEFT, Boolean.class);
	}

	public void setMovingLeft(boolean movingLeft) {
		setStat(MOVING_LEFT, movingLeft);
	}

	public boolean isMovingRight() {
		return getStat(MOVING_RIGHT, Boolean.class);
	}

	public void setMovingRight(boolean movingRight) {
		setStat(MOVING_RIGHT, movingRight);
	}

	public long getShootStart() {
		return getStat(SHOOT_START, Long.class);
	}

	public void setShootStart(long shootStart) {
		setStat(SHOOT_START, shootStart);
	}

	public long getLastShot() {
		return getStat(LAST_SHOT, Long.class);
	}

	public void setLastShot(long lastShot) {
		setStat(LAST_SHOT, lastShot);
	}

	public String getOwnerId() {
		return getStat(OWNER_ID, String.class);
	}

	public void setOwnerId(String id) {
		setStat(OWNER_ID, id);
	}

	public float getRemainingTime() {
		return getStat(REMAINING_TIME, Float.class);
	}

	public void setRemainingTime(float time) {
		setStat(REMAINING_TIME, time);
	}

}

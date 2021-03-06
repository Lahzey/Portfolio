package poopgame.gamelogic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import poopgame.gamelogic.components.IdComponent;
import poopgame.gamelogic.components.MovementComponent;
import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.components.StatsComponent;
import poopgame.gamelogic.components.UpdateComponent;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.gamelogic.engine.actions.Action;
import poopgame.graphics.components.AnimationComponent;
import poopgame.graphics.components.TransformComponent;
import poopgame.physics.BodyInfo;
import poopgame.physics.FixtureInfo;
import util.ArrayUtil;
import util.ExtendedThread;

public class Player extends GameEntity {
	
	private static final Family PLAYER_COMP_FAMILY = Family.all(PlayerComponent.class).get();

	private static final long MAX_CHARGE_TIME = 3000;
	private static final float MAX_CHARGE_DMG_AMP = 3f;
	private static final float MAX_CHARGE_SPEED_AMP = 2.5f;

	private PlayerComponent playerComponent;

	public Player(PlayerComponent playerComponent) {
		super(playerComponent.champ.getWidth(), playerComponent.champ.getHeight(), false);
		this.playerComponent = playerComponent;
	}

	@Override
	public String generateId() {
		return generateIdForPlayer(playerComponent.id);
	}

	public static String generateIdForPlayer(long playerId) {
		return "Player[" + playerId + "]";
	}

	@Override
	protected String getTextureName() {
		return playerComponent.champ.getFolderName() + "splash.png"; // will be overwritten by animation
	}

	@Override
	public Entity create(TimeEngine engine, Vector2 position) {
		Entity entity = super.create(engine, position);
		entity.getComponent(StatsComponent.class).stats.setPlayerDefaults(playerComponent.champ);
		entity.add(playerComponent);
		entity.getComponent(TransformComponent.class).mustBeInFrame = true;
		entity.add(new UpdateComponent(Player.class));
		
		MovementComponent movementComp = new MovementComponent(playerComponent.champ.getFolderName());
		entity.add(movementComp);
		
		AnimationComponent animationComp = new AnimationComponent(movementComp.standRight);
		entity.add(animationComp);
		
		return entity;
	}

	// Called by UpdateComponent
	public static void update(TimeEngine engine, Entity entity, float delta) {
		Stats stats = getStats(entity);

		// check if dead
		if (stats.getHealth() <= 0) {
			engine.removeEntity(entity);
			checkForWin(engine, entity);
			return;
		}

		// check if fallen out of map
		Vector2 position = getBody(entity).getPosition();
		if (position.y < -entity.getComponent(PlayerComponent.class).champ.getHeight()) {
			stats.setHealth(0);
			engine.removeEntity(entity);
			checkForWin(engine, entity);
			return;
		}

		// wrap around
		float maxX = PoopGame.getInstance().mapDimensions.x;
		if (position.x < 0) {
			getBody(entity).setTransform(maxX, position.y, 0);
		} else if (position.x > maxX) {
			getBody(entity).setTransform(0, position.y, 0);
		}

		// regenerate energy
		stats.setEnergy(stats.getEnergy() + (stats.getEnergyRegen() * delta));
	}

	public static void executeAction(TimeEngine engine, Entity player, Action action) {
		if (getStats(player).getHealth() <= 0) {
			return;
		}
		
		System.out.println("executing " + action.getType() + " for " + player.getComponent(IdComponent.class).id);
		
		MovementComponent movementComp = player.getComponent(MovementComponent.class);
		AnimationComponent animationComp = player.getComponent(AnimationComponent.class);

		switch (action.getType()) {
		case JUMP:
			tryJump(player);
			break;
		case MOVE_LEFT_END:
			movementComp.moveLeft = false;
			animationComp.setAnimation(movementComp.moveRight ? movementComp.walkRight : movementComp.standLeft);
			break;
		case MOVE_LEFT_START:
			movementComp.moveLeft = true;
			animationComp.setAnimation(movementComp.moveRight ? movementComp.standLeft : movementComp.walkLeft);
			break;
		case MOVE_RIGHT_END:
			movementComp.moveRight = false;
			animationComp.setAnimation(movementComp.moveLeft ? movementComp.walkLeft : movementComp.standRight);
			break;
		case MOVE_RIGHT_START:
			movementComp.moveRight = true;
			animationComp.setAnimation(movementComp.moveLeft ? movementComp.standRight : movementComp.walkRight);
			break;
		case POOP_END:
			tryShoot(engine, player);
			break;
		case POOP_START:
			getStats(player).setShootStart(engine.getTime());
			break;
		case SPECIAL:
			trySpecial(engine, player);
			break;
		}
	}

	private static void tryJump(Entity player) {
		Body body = getBody(player);
		if (isOnGround(body)) {
			body.applyLinearImpulse(new Vector2(0, getStats(player).getJumpForce() * body.getMass()), body.getPosition(), true);
			ArrayUtil.randomElementFrom(player.getComponent(PlayerComponent.class).champ.getJumpSounds()).play();
		}
	}

	private static boolean isOnGround(Body body) {
		Fixture mainFixture = ((BodyInfo) body.getUserData()).mainFixture;
		FixtureInfo fixInfo = (FixtureInfo) mainFixture.getUserData();
		return Math.abs(body.getLinearVelocity().y) < 0.01 && fixInfo.colliding.size() > 0;
	}

	private static void tryShoot(TimeEngine engine, Entity player) {
		Stats stats = getStats(player);
		long chargeTime = stats.getShootStart() > 0 ? engine.getTime() - stats.getShootStart() : 0;
		stats.setShootStart(0);

		long poopCooldown = (long) (1000 / stats.getPoopingSpeed());
		if (stats.getLastShot() + poopCooldown > engine.getTime()) {
			// cannot shoot yet
			return;
		}

		Poop poop;
		Entity poopEntity;
		if (chargeTime < 500) {
			// normal shot
			poop = new Poop(player);
			poopEntity = poop.create(engine, getBody(player).getPosition());
		} else {
			// charged shot
			chargeTime = Math.min(chargeTime, 5000);
			float chargePercentage = chargeTime / (float) MAX_CHARGE_TIME;
			poop = new Poop(player);
			poopEntity = poop.create(engine, getBody(player).getPosition());
			getStats(poopEntity).setDamage(getStats(poopEntity).getDamage() * (((MAX_CHARGE_DMG_AMP - 1) * chargePercentage) + 1));
			getStats(poopEntity).setStat(Poop.DAMAGE_AMP, 0f);

			Body poopBody = getBody(poopEntity);
			float shootUpForce = stats.getShootUpForce() * poopBody.getMass();
			shootUpForce *= ((MAX_CHARGE_SPEED_AMP - 1) * chargePercentage) + 1;
			poopBody.applyLinearImpulse(new Vector2(0, shootUpForce), poopBody.getPosition(), true);
		}

		stats.setLastShot(engine.getTime());

		Champion champ = player.getComponent(PlayerComponent.class).champ;
		champ.getPassiveAbility().onShoot(engine, player, poopEntity);
		champ.getSpecialAbility().onShoot(engine, player, poopEntity);
	}

	private static void trySpecial(TimeEngine engine, Entity player) {
		if (getStats(player).getEnergy() >= 100) {
			getStats(player).setEnergy(0);
			player.getComponent(PlayerComponent.class).champ.getSpecialAbility().cast(engine, player);
		}
	}
	
	private static void checkForWin(TimeEngine engine, Entity justDied) {
		ImmutableArray<Entity> players = engine.getEntitiesFor(PLAYER_COMP_FAMILY);
		
		List<Entity> alive = new ArrayList<Entity>();
		for(Entity player : players) {
			if (player != justDied) {
				alive.add(player);
			}
		}
		
		if (alive.size() <= 1) {
			Entity winner = alive.size() == 1 ? alive.get(0) : null;
			
			new ExtendedThread() {
				
				@Override
				public void run() {
					sleepSilent(1000);
					Gdx.app.postRunnable(() -> {
						PoopGame.getInstance().goToWinScreen(winner);
					});
				}
			}.start();
		}
	}
}

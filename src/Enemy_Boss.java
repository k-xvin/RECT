
import javafx.scene.image.Image;

public class Enemy_Boss extends Enemy {

	/**
	 * long value that holds the time of the last shot of the boss
	 */
	private long lastShot = System.currentTimeMillis();
	/**
	 * long value that holds the time of the last attack of the boss
	 */
	private long lastAttack = System.currentTimeMillis();
	/**
	 * long value that holds the last move of the boss
	 */
	private long lastMove = 0;

	/**
	 * boolean value of whether the boss is shooting
	 */
	private boolean isShooting = false;

	/**
	 * int value of the number of shots taken by the boss
	 */
	private int numShots;

	// private boolean isMoving = false;

	/**
	 * constructor that initializes the boss
	 * @param double of starting x position
	 * @param double of starting y position
	 * @param int of starting speed
	 * @param int of starting health
	 */
	public Enemy_Boss(double startX, double startY, int speed, int health) {
		super(startX, startY, speed, 128);
		setImage(new Image(getClass().getClassLoader().getResource("resources/walkerFront.png").toString(), 32 * 6, 0, true, false));
		
	}

	@Override
	public void act(long now) {

		gracePeriod();

		if (getOneIntersectingObject(Player.class) != null) {
			getWorld().getPlayer().setHit(true);
		}

		if (isShooting) {
			shoot();
		} else if (System.currentTimeMillis() - lastMove >= 1000) {
			walkDirection();
		}

		if (System.currentTimeMillis() - lastAttack >= 1000) {
			isShooting = true;
		}

		if (((RoomManager) getWorld()).getCurrentRoom().outOfRoom(this)) {
			setDx(-getDx());
			setDy(-getDy());
		}
		if (!isShooting) {
			move(getDx(), getDy());
		}
		

		ifHitByProjectile();
	}

	/**
	 * method that allows the enemy to shoot
	 */
	private void shoot() {

		if (System.currentTimeMillis() - lastShot >= 500) {
			
			getWorld().add(new Projectile(this, 0, -2, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, -2, 0, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, 2, 0, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, 0, 2, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, 2, 2, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, 2, -2, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, -2, 2, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, -2, -2, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)));
			numShots++;
			lastShot = System.currentTimeMillis();
		}

		if (numShots > 1) {
			numShots = 0;
			lastAttack = System.currentTimeMillis();
			isShooting = false;
		}

	}

	/**
	 * method that allows the enemy to walk
	 */
	private void walkDirection() {

		double angle = Math.random() * 2 * Math.PI;
		double directionx = Math.cos(angle);
		double directiony = Math.sin(angle);

		setDx(getSpeed() * directionx);
		setDy(getSpeed() * directiony);

		lastMove = System.currentTimeMillis();
	}

}

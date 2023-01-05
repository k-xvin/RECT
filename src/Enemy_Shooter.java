
import javafx.scene.image.Image;

public class Enemy_Shooter extends Enemy {

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

	/**
	 * constructor that initializes the boss
	 * 
	 * @param double
	 *            of starting x position
	 * @param double
	 *            of starting y position
	 * @param int
	 *            of starting speed
	 * @param int
	 *            of starting health
	 */
	public Enemy_Shooter(double startX, double startY, double speed, int health) {
		super(startX, startY, 1, health);
		setImage(new Image(getClass().getClassLoader().getResource("resources/shooterFront.png").toString(), 48, 0, true, false)); 
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

		if (System.currentTimeMillis() - lastAttack >= 3000) {
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
	 * method that allows enemy to shoot at the player
	 */
	private void shoot() {

		if (System.currentTimeMillis() - lastShot >= 500) {
			setImage(new Image(getClass().getClassLoader().getResource("resources/shooterOpen.png").toString(), 48, 0, true, false));

			double dx = (((getWorld().getPlayer().getX() + getWorld().getPlayer().getWidth() / 2)
					- (getX() + getWidth() / 2))
					/ Math.hypot(getX() - getWorld().getPlayer().getX(), getY() - getWorld().getPlayer().getY())) * 3;
			double dy = (((getWorld().getPlayer().getY() + getWorld().getPlayer().getHeight() / 2)
					- (getY() + getHeight() / 2))
					/ Math.hypot(getX() - getWorld().getPlayer().getX(), getY() - getWorld().getPlayer().getY())) * 3;

			getWorld().add(
					new Projectile(this, dx, dy, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/ball3.png").toString(), 16, 0, true, false)));
			numShots++;
			lastShot = System.currentTimeMillis();
		}

		if (numShots > 4) {
			numShots = 0;
			lastAttack = System.currentTimeMillis();
			isShooting = false;
		}

	}

	/**
	 * method that allows enemy to walk
	 */
	private void walkDirection() {

		setImage(new Image(getClass().getClassLoader().getResource("resources/shooterFront.png").toString(), 48, 0, true, false));

		double angle = Math.random() * 2 * Math.PI;
		double directionx = Math.cos(angle);
		double directiony = Math.sin(angle);

		setDx(getSpeed() * directionx);
		setDy(getSpeed() * directiony);

		lastMove = System.currentTimeMillis();
	}

}

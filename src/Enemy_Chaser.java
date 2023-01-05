import javafx.scene.image.Image;

public class Enemy_Chaser extends Enemy {
	
	/**
	 * long value storing the time of the last bounce
	 */
	private long lastBounce = System.currentTimeMillis();
	/**
	 * boolean value of whether the bounce has occurred
	 */
	private boolean hasBounced = false;

	/**
	* constructor that initializes the boss
	 * @param double of starting x position
	 * @param double of starting y position
	 * @param int of starting speed
	 * @param int of starting health
	 */
	public Enemy_Chaser(double startX, double startY, double speed, int health) {
		super(startX, startY, speed, health);
		setImage(new Image(getClass().getClassLoader().getResource("resources/chaser.png").toString())); 
		setCollisionTime(1000);
	}

	@Override
	public void act(long now) {

		gracePeriod();

		if (getOneIntersectingObject(Player.class) != null) {
			getWorld().getPlayer().setHit(true);
		} else if (getOneIntersectingObject(Enemy.class) != null || getCollided()) {
			enemyToEnemyCollisionMove();
		} else { // enemy is not touching the player or any
			// other enemies
			chaseEnemy();
		} 
		

		ifHitByProjectile();

	}

	/**
	 * method that allows the enemy to chose the player
	 */
	public void chaseEnemy() {
		if(hasBounced) {
			move(getDx(), getDy());
			if(System.currentTimeMillis() - lastBounce >= 500) {
				hasBounced = false;
			}
		}
		else if (((RoomManager) getWorld()).getCurrentRoom().outOfRoom(this) && !hasBounced) {
			//System.out.println("out");
			if (((RoomManager) getWorld()).getCurrentRoom().onTopWall(this)) {
				setDy(getSpeed());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onBottomWall(this)) {
				setDy(-getSpeed());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onLeftWall(this)) {
				setDx(getSpeed());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onRightWall(this)) {
				setDx(-getSpeed());
			}
			move(getDx(), getDy());
			hasBounced = true;
			lastBounce = System.currentTimeMillis();
		} 
		else {
			if (getWorld().getPlayer().getX() - getX() >= 0) {
				setDx(getSpeed());
				// System.out.println("right");
			} else {
				setDx(-getSpeed());
				// System.out.println("left");
			}
			if (getWorld().getPlayer().getY() - getY() >= 0) {
				setDy(getSpeed());
				// System.out.println("down");
			} else {
				setDy(-getSpeed());
				// System.out.println("up");
			}
			if (Math.abs(getX() - getWorld().getPlayer().getX()) < 5) { // eliminates "jitter" when enemy chases
				setDx(0);
			}
			if (Math.abs(getY() - getWorld().getPlayer().getY()) < 5) { // eliminates "jitter" when enemy chases
				setDy(0);
			}
			move(getDx(), getDy());
		}

		// enemy moves towards the player
	}

}

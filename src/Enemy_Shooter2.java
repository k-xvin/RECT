
import javafx.scene.image.Image;

public class Enemy_Shooter2 extends Enemy {

	/**
	 * long value that holds the time of the last shot of the boss
	 */
	private long lastShot = System.currentTimeMillis();
	/**
	 * long value that holds the time of the last attack of the boss
	 */
	private long lastAttack = System.currentTimeMillis();

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
	 * @param double of starting x position
	 * @param double of starting y position
	 * @param int of starting speed
	 * @param int of starting health
	 */
	public Enemy_Shooter2(double startX, double startY, double speed, int health) {
		super(startX, startY, 1, health);
		setImage(new Image(getClass().getClassLoader().getResource("resources/shooterEnemy2.png").toString(), 64, 0, true, false)); 
	}

	@Override
	public void act(long now) {

		gracePeriod();

		if (getOneIntersectingObject(Player.class) != null) {
			getWorld().getPlayer().setHit(true);
		}
		
		if(isShooting) {
			shoot();
		} 
//		else if (System.currentTimeMillis() - lastMove >= 500) {
//			walkDirection();
//		}
		
		if (System.currentTimeMillis() - lastAttack >= 2000) {
			isShooting = true;
		}
		
		if(((RoomManager) getWorld()).getCurrentRoom().outOfRoom(this)) {
			setDx(-getDx());
			setDy(-getDy());
		}
//		if(!isShooting) {
//			move(getDx(), getDy());
//		}
		

		ifHitByProjectile();
	}

	/**
	 * method that allows enemy to shoot at the player
	 */
	private void shoot() {
		
		if(System.currentTimeMillis() - lastShot >= 500) {


			
			double dx = (((getWorld().getPlayer().getX() + getWorld().getPlayer().getWidth() / 2)
					- (getX() + getWidth() / 2))
					/ Math.hypot(getX() - getWorld().getPlayer().getX(), getY() - getWorld().getPlayer().getY())) * 3;
			double dy = (((getWorld().getPlayer().getY() + getWorld().getPlayer().getHeight() / 2)
					- (getY() + getHeight() / 2))
					/ Math.hypot(getX() - getWorld().getPlayer().getX(), getY() - getWorld().getPlayer().getY())) * 3;
			
			getWorld().add(new Projectile(this, dx-1, dy-1, 0.5, 200, new Image(getClass().getClassLoader().getResource("resources/enemyProjectile2.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, dx-1, dy+1, 0.5, 200, new Image(getClass().getClassLoader().getResource("resources/enemyProjectile2.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, dx+1, dy+1, 0.5, 200, new Image(getClass().getClassLoader().getResource("resources/enemyProjectile2.png").toString(), 16, 0, true, false)));
			getWorld().add(new Projectile(this, dx+1, dy-1, 0.5, 200, new Image(getClass().getClassLoader().getResource("resources/enemyProjectile2.png").toString(), 16, 0, true, false)));
			numShots++;
			lastShot = System.currentTimeMillis();
		}
		
		if(numShots>1) {
			numShots = 0;
			lastAttack = System.currentTimeMillis();
			isShooting = false;
		}
		
	}

//	private void walkDirection() {
//		
//		Room r = ((RoomManager) getWorld()).getCurrentRoom();
//		Random ran = new Random();
//		double angle = Math.random()*2*Math.PI;
//		double directionx = Math.cos(angle);
//		double directiony = Math.sin(angle);
//		
//		setDx(getSpeed()*directionx);
//		setDy(getSpeed()*directiony);
//		
//		
//		lastMove = System.currentTimeMillis();
//	}

}

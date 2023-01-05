import javafx.animation.FadeTransition;
import javafx.util.Duration;

public abstract class Enemy extends Actor {

	/**
	 * double value that contains the speed of the enemy
	 */
	private double speed;
	/**
	 * double value that contains the original speed of the enemy
	 */
	private double originalSpeed;
	
	/**
	 * double value that contains the health of the enemy
	 */
	private double health;
	/**
	 * double value containing the starting health of the enemy
	 */
	private double startingHealth;
	
	/**
	 * long value that contains the value of a collision with another enemy
	 */
	private long enemyToEnemyCollision;
	/**
	 * boolean that tells whether enemy has collided with another enemy
	 */
	private boolean collided;
	/**
	 * int value that gives the collision time
	 */
	private int collisionTime = 250;
	
	/**
	 * long value that gives the time of spawning
	 */
	private long spawnTime;
	/**
	 * boolean value of whether the player is within the grace period
	 */
	private boolean inGrace = false;

	/**
	 * value of the change in the x position
	 */
	private double dx;
	/**
	 * value of the change in the y position
	 */
	private double dy;
	
	/**
	 * constructor that initializes the boss
	 * @param double of starting x position
	 * @param double of starting y position
	 * @param int of starting speed
	 * @param int of starting health
	 */
	public Enemy(double startX, double startY, double speed, int health) {
		//setImage(new Image("file:images/walkerFront.png"));
		this.speed = speed;
		originalSpeed = speed;
		setX(startX);
		setY(startY);
		this.health = health;
		startingHealth = health;
		spawnTime = System.currentTimeMillis();
	}

	/**
	 * method that returns the speed of the enemy
	 * @return the double value of the speed of the enemy
	 */
	public double getSpeed(){
		return speed;
	}
	
	/**
	 * method that sets the speed of the enemy
	 * @param the double value of the intended speed
	 */
	public void setSpeed(double speed){
		this.speed = speed;
	}
	
	@Override
	public abstract void act(long now);
	
	/**
	 * method that sets the health
	 * @param double value of the intended health
	 */
	public void setHealth(double health){
		this.health = health;
	}
	
	/**
	 * method that returns the health
	 * @return the double value of the health
	 */
	public double getHealth(){
		return health;
	}
	
	/**
	 * method that changes the enemy based on when it is hit by a projectile
	 */
	public void ifHitByProjectile(){
		if(inGrace) return;
				
		if (getPlayerProjectile() != null /*&& getOneIntersectingObject(Projectile.class) != null*/
				&& !getPlayerProjectile().hasEnemyHit(this)) {
			Projectile proj = getPlayerProjectile();
			
			//deal damage to the enemy
			health -= proj.getDamage();
			
			//tell projectile that it has hit this enemy
			proj.addEnemyHit(this);
			
			
			//remove the projectile that hit the enemy, if needed
			if(proj.getRemoveOnHit()) {
				getWorld().remove(proj);
			}
			
			//enemy knockback on hit (looks a little stutter-y, so probably don't use)
//			if(proj.getDx()<0) {
//				setX(getX() - 10);
//			} else if(proj.getDx()>0){
//				setX(getX() + 10);
//			}
//			
//			if(proj.getDy()<0) {
//				setY(getY() - 10);
//			} else if(proj.getDy()>0){
//				setY(getY() + 10);
//			}
			
			//blink the enemy when they are hit
			FadeTransition blink = new FadeTransition(Duration.millis(100), this);
			blink.setFromValue(1.0);
			blink.setToValue(0.1);
			blink.setCycleCount(2);
			blink.setAutoReverse(true);
			blink.play();
			
			//kills enemy if less than 0 health
			if (health <= 0){
				((RoomManager)getWorld()).getCurrentRoom().removeEnemy(this);
				getWorld().remove(this);
			} 
			
		}
	}
	
	/**
	 * method that allows enemies to bounce off one another when they collide
	 */
	public void enemyToEnemyCollisionMove() {
		if (!collided) {
			collided = true;
			enemyToEnemyCollision = System.currentTimeMillis();
		}
		if (getOneIntersectingObject(Enemy.class) != null) {
			if (getOneIntersectingObject(Enemy.class).getX() - getX() < 0) {
				dx = getSpeed();
			} else {
				dx = -getSpeed();
			}
			if (getOneIntersectingObject(Enemy.class).getY() - getY() < 0) {
				dy = getSpeed();
			} else {
				dy = -getSpeed();
			}
		}
		if(((RoomManager) getWorld()).getCurrentRoom().outOfRoom(this)) {
			if (((RoomManager) getWorld()).getCurrentRoom().onTopWall(this)) {
				dy = getSpeed();
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onBottomWall(this)) {
				dy = -getSpeed();
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onLeftWall(this)) {
				dx = getSpeed();
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onRightWall(this)) {
				dx = -getSpeed();
			}
			move(dx, dy);
		} else {
			move(dx, dy);
		}
		
		if (System.currentTimeMillis() - enemyToEnemyCollision >= collisionTime) {
			collided = false;
		}
	}
	
	/**
	 * method that controls the grace period, when enemies don't move for 0.5 seconds upon spawning
	 */
	public void gracePeriod() {
		if(System.currentTimeMillis() - spawnTime <= 500) {
			speed = 0;
			inGrace = true;
		} else {
			speed = originalSpeed;
			inGrace = false;
		}
	}
	
	/**
	 * method that returns the projectile shot by the player
	 * @return the Projectile shot by the player
	 */
	public Projectile getPlayerProjectile() {
		for(Projectile p : getIntersectingObjects(Projectile.class)) {
			if(p.getSender().equals(getWorld().getPlayer())) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * method that tells whether the player is within the grace period
	 * @return boolean value of whether the player is within the grace period
	 */
	public boolean getInGrace() {
		return inGrace;
	}
	
	/**
	 * method that sets the collision time
	 * @param int value of the collision time
	 */
	public void setCollisionTime(int t) {
		collisionTime = t;
	}
	
	/**
	 * method that returns whether the enemy has collided
	 * @return boolean value of whether the enemy has collided
	 */
	public boolean getCollided(){
		return collided;
	}

	/**
	 * method that returns the change in x coordinate
	 * @return double value of the change in x coordinate
	 */
	public double getDx() {
		return dx;
	}

	/**
	 * method that sets the change in x coordinate
	 * @param double value of the change in x coordinate
	 */
	public void setDx(double dx) {
		this.dx = dx;
	}

	/**
	 * method that returns the change in y coordinate
	 * @return double value of the change in y coordinate
	 */
	public double getDy() {
		return dy;
	}

	/**
	 * method that sets the change in y coordinate
	 * @param double value of the change in y coordinate
	 */
	public void setDy(double dy) {
		this.dy = dy;
	}

	/**
	 * method that gives starting health
	 * @return double value of the enemy's starting health
	 */
	public double getStartingHealth() {
		return startingHealth;
	}
	

}

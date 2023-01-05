import java.util.Random;

import javafx.scene.image.Image;

public class Enemy_Dasher extends Enemy {

	/**
	 * boolean value of whether the enemy is dashing up
	 */
	private boolean isDashingUp = false;
	/**
	 * boolean value of whether the enemy is dashing right
	 */
	private boolean isDashingRight = false;
	/**
	 * boolean value of whether the enemy is dashing down
	 */
	private boolean isDashingDown = false;
	/**
	 * boolean value of whether the enemy is dashing left
	 */
	private boolean isDashingLeft = false;

	/**
	 * boolean value of whether the first dash has occurred
	 */
	private boolean firstDash = true; 
	
	/**
	 * long value of when the last shot occurred
	 */
	private long lastShot = System.currentTimeMillis();

	//private double dx;
	//private double dy;


	/**
	 * constructor that initializes the boss
	 * @param double of starting x position
	 * @param double of starting y position
	 * @param int of starting speed
	 * @param int of starting health
	 */
	public Enemy_Dasher(double startX, double startY, double speed, int health) {
		super(startX, startY, 3, health);
		setImage(new Image(getClass().getClassLoader().getResource("resources/dasherFront.png").toString(), 32 * 2, 0, true, false));
	}

	@Override
	public void act(long now) {
		gracePeriod();

		if (getOneIntersectingObject(Player.class) != null) {
			getWorld().getPlayer().setHit(true);
		} else { // enemy is not touching the player, so move
			dash();
		}

		ifHitByProjectile();
	}

	/**
	 * method that allows the enemy to dash
	 */
	private void dash() {

		if (getInGrace())
			return;

		Room curr = ((RoomManager) getWorld()).getCurrentRoom();

		Random ran = new Random();
		int direc = ran.nextInt(3);
		if (isDashingUp && curr.onTopWall(this)) {
			isDashingUp = false;
			if (direc == 0 && !curr.onLeftWall(this))
				dashLeft();
			else if (direc == 1 && !curr.onRightWall(this))
				dashRight();
			else
				dashDown();
		} else if (isDashingRight && curr.onRightWall(this)) {
			isDashingRight = false;
			if (direc == 0 && !curr.onBottomWall(this))
				dashDown();
			else if (direc == 1 && !curr.onTopWall(this))
				dashUp();
			else
				dashLeft();
		} else if (isDashingDown && curr.onBottomWall(this)) {
			isDashingDown = false;
			if (direc == 0 && !curr.onLeftWall(this))
				dashLeft();
			else if (direc == 1 && !curr.onRightWall(this))
				dashRight();
			else
				dashUp();
		} else if (isDashingLeft && curr.onLeftWall(this)) {
			isDashingLeft = false;
			if (direc == 0 && !curr.onTopWall(this))
				dashUp();
			else if (direc == 1 && !curr.onBottomWall(this))
				dashDown();
			else
				dashRight();
		} 
		else {
			move(getDx(), getDy());
			if(System.currentTimeMillis() - lastShot >= 1000) {
				//shoots at player every second
				double dx = ( ( (getWorld().getPlayer().getX() + getWorld().getPlayer().getWidth()/2) - (getX() + getWidth()/2))/Math.hypot(getX() - getWorld().getPlayer().getX(), getY() - getWorld().getPlayer().getY()) )*3;
				double dy = ( ( (getWorld().getPlayer().getY() + getWorld().getPlayer().getHeight()/2) - (getY() + getHeight()/2))/Math.hypot(getX() - getWorld().getPlayer().getX(), getY() - getWorld().getPlayer().getY()) )*3;
				//System.out.println(dx + " " + dy);
				getWorld().add(new Projectile(this, dx, dy, 0.5, 300, new Image(getClass().getClassLoader().getResource("resources/square.png").toString(), 16, 0, true, false)) );
				
				lastShot = System.currentTimeMillis();
			}
			if (firstDash) {
				Random ran2 = new Random();
				int num = ran2.nextInt(4);
				if (num == 0) {
					dashUp();
				} else if (num == 1) {
					dashRight();
				} else if (num == 2) {
					dashDown();
				} else {
					dashLeft();
				}
				firstDash = false;
			}

		}

	}

	/**
	 * method that allows the enemy to dash up
	 */
	private void dashUp() {
		setDy(-getSpeed());
		setDx(0);
		setImage(new Image(getClass().getClassLoader().getResource("resources/dasherUp.png").toString(), 32 * 2, 0, true, false));
		isDashingUp = true;
		
		
	}

	/**
	 * method that allows the enemy to dash right
	 */
	private void dashRight() {
		setDx(getSpeed());
		setDy(0);
		setImage(new Image(getClass().getClassLoader().getResource("resources/dasherRight.png").toString(), 32 * 2, 0, true, false));
		isDashingRight = true;
	}

	/**
	 * method that allows the enemy to dash down
	 */
	private void dashDown() {
		setDy(getSpeed());
		setDx(0);
		setImage(new Image(getClass().getClassLoader().getResource("resources/dasherDown.png").toString(), 32 * 2, 0, true, false));
		isDashingDown = true;
	}

	/**
	 * method that allows the enemy to dash left
	 */
	private void dashLeft() {
		setDx(-getSpeed());
		setDy(0);
		setImage(new Image(getClass().getClassLoader().getResource("resources/dasherLeft.png").toString(), 32 * 2, 0, true, false));
		isDashingLeft = true;
	}

}

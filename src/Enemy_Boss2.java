
import javafx.scene.image.Image;

public class Enemy_Boss2 extends Enemy {

	/**
	 * long value storing the value of the last attack
	 */
	private long lastAttack;

	/**
	 * long value storing the value of the last shot attack
	 */
	private long lastShotAttack;
	
	/**
	 * long value storing the value of the last second shot attack
	 */
	private long lastShotAttack2;
	
	/**
	 * int value storing the number of attacks
	 */
	private int numAttacks = 0;

	/**
	 * boolean value of whether the enemy is attacking
	 */
	private boolean isAttacking = false;

	/**
	 * boolean value of whether the enemy is "enraged"
	 */
	private boolean enraged = false;

	/**
	 * constructor that initializes the boss
	 * @param double of starting x position
	 * @param double of starting y position
	 * @param int of starting speed
	 * @param int of starting health
	 */
	public Enemy_Boss2(double startX, double startY, double speed, int health) {
		super(startX, startY, speed, health);
		
		setImage(new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 32 * 4, 0, true, false));
		lastAttack = System.currentTimeMillis();
		// lastWalk = System.currentTimeMillis();

		setDx(getSpeed());
		setDy(getSpeed());
	}

	@Override
	public void act(long now) {

		gracePeriod();
		
		if(getHealth() <= getStartingHealth()/2) {
			//System.out.println(getHealth() + " " + startHealth);
			enraged = true;
		}

		if (getOneIntersectingObject(Player.class) != null) {
			getWorld().getPlayer().setHit(true);
		}

		if (isAttacking) {
			attack();
		} else {
			walk();
		}

		if (System.currentTimeMillis() - lastAttack >= 3000) {
			isAttacking = true;
		}

		ifHitByProjectile();

	}

	private void walk() {
		if(!enraged) {
			
			setImage(new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 32 * 4, 0, true, false));
			if (((RoomManager) getWorld()).getCurrentRoom().onTopWall(this)) {
				setDy(-getDy());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onBottomWall(this)) {
				setDy(-getDy());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onLeftWall(this)) {
				setDx(-getDx());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onRightWall(this)) {
				setDx(-getDx());
			}
		}
		else {
			
			setImage(new Image(getClass().getClassLoader().getResource("resources/boss2Enraged.png").toString(), 32 * 4, 0, true, false));
			if (((RoomManager) getWorld()).getCurrentRoom().onTopWall(this)) {
				setDy(-getDy());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onBottomWall(this)) {
				setDy(-getDy());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onLeftWall(this)) {
				setDx(-getDx());
			}
			if (((RoomManager) getWorld()).getCurrentRoom().onRightWall(this)) {
				setDx(-getDx());
			}
		}
		
		

		move(getDx(), getDy());
	}

	/**
	 * method that allows the boss to attack the player
	 */
	private void attack() {
		isAttacking = true;

		if(!enraged) {
			
			setImage(new Image(getClass().getClassLoader().getResource("resources/boss2Attack.png").toString(), 32 * 4, 0, true, false));
			if (System.currentTimeMillis() - lastShotAttack >= 500) {
				getWorld().add(
						new Projectile(this, 2, -2, 0.5, 500, new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false)));
				getWorld().add(
						new Projectile(this, 2, 2, 0.5, 500, new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false)));
				getWorld().add(
						new Projectile(this, -2, 2, 0.5, 500, new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false)));
				getWorld().add(new Projectile(this, -2, -2, 0.5, 500,
						new Image("file:images/boss2Front.png", 16, 0, true, false)));
				lastShotAttack = System.currentTimeMillis();
				numAttacks++;
			}
			if (System.currentTimeMillis() - lastShotAttack2 >= 500) {
				getWorld().add(new Projectile(this, 1, -2, 0.5, 500,
						new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false), true));
				getWorld().add(new Projectile(this, 2, 1, 0.5, 500,
						new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false), true));
				getWorld().add(new Projectile(this, -1, 2, 0.5, 500,
						new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false), true));
				getWorld().add(new Projectile(this, -2, -1, 0.5, 500,
						new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false), true));

				getWorld().add(new Projectile(this, 0, -1, 0.5, 500,
						new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false), true));
				getWorld().add(new Projectile(this, 1, 0, 0.5, 500,
						new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false), true));
				getWorld().add(new Projectile(this, 0, 1, 0.5, 500,
						new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false), true));
				getWorld().add(new Projectile(this, -1, 0, 0.5, 500,
						new Image(getClass().getClassLoader().getResource("resources/boss2Front.png").toString(), 16, 0, true, false), true));
				lastShotAttack2 = System.currentTimeMillis();
			}
			if (numAttacks > 10) {
				isAttacking = false;
				numAttacks = 0;
				lastAttack = System.currentTimeMillis();
			}
		}
		else {
			
			setImage(new Image(getClass().getClassLoader().getResource("resources/boss2EnragedAttack.png").toString(), 32 * 4, 0, true, false));
			if (System.currentTimeMillis() - lastShotAttack >= 500) {
				getWorld().add(new Enemy_Chaser(getX()+getWidth()/2, getY()+getHeight()/2, 1.5, 1));
				lastShotAttack = System.currentTimeMillis();
				numAttacks++;
			}
			if (numAttacks > 3) {
				isAttacking = false;
				numAttacks = 0;
				lastAttack = System.currentTimeMillis();
			}
			
		}

	

		

	}

}

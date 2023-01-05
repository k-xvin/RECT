import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Node;
import javafx.scene.image.Image;

public class Projectile extends Actor {

	/**
	 * double of the speed of the projectile
	 */
	private double dx, dy;
	/**
	 * double of starting point of projectile
	 */
	private double x, y;
	/**
	 * double of the damage of the projectile
	 */
	private double damage;
	/**
	 * double of the range of the projectile
	 */
	private double range;
	
	/**
	 * boolean of whether the projectile bounces
	 */
	private boolean bounces = false;
	/**
	 * boolean of whether the projectile hasBounced
	 */
	private boolean hasBounced = false;
	
	/**
	 * ArrayList of the enemies hit
	 */
	private ArrayList<Enemy> enemiesHit;
	/**
	 * boolean of whether the projectile is to be removed upon being hit
	 */
	private boolean removeOnHit = true;
	
	/**
	 * the node of the projectile
	 */
	private Node node;

	/**
	 * the first constructor of the projectile
	 * @param node
	 * @param x speed
	 * @param y speed
	 * @param damage
	 * @param range
	 * @param image
	 */
	public Projectile(Node n, double dx, double dy, double damage, double range, Image i) {
		double xCenter = n.getBoundsInParent().getMinX() + n.getBoundsInParent().getWidth() / 2;
		double yCenter = n.getBoundsInParent().getMinY() + n.getBoundsInParent().getHeight() / 2;
		this.x = xCenter;
		this.y = yCenter;
		this.dx = dx;
		this.dy = dy;
		this.damage = damage;
		this.range = range;
		node = n;
		setImage(i);
		enemiesHit = new ArrayList<>();
		
		//if the projectile came from the player, and has the specified item
		if (n == ((RoomManager) n.getParent()).getPlayer()) {
			Player p = (Player) n;
			if (p.hasItem("Jawbreaker")) {
				setImage(new Image(getClass().getClassLoader().getResource("resources/jawbreakerProj.png").toString(), 16 + damage*2, 0, true, false)); 
				
			}
			if(p.hasItem("Party Hat")) {
				bounces = true;
				removeOnHit = false;
			}
			Random ran = new Random();
			
			if(p.hasItem("Pointy Stick")) {
				removeOnHit = false;
			}
			if(p.hasItem("Bubbler")) {
				this.dx += ran.nextInt(3);
				this.dy += ran.nextInt(3);
				this.dx -= ran.nextInt(3);
				this.dy -= ran.nextInt(3);
				this.damage *= 0.2;
				setImage(new Image(getClass().getClassLoader().getResource("resources/ball3.png").toString(), 8 + damage/2, 0, true, false));
				if (p.hasItem("Jawbreaker")) {
					setImage(new Image(getClass().getClassLoader().getResource("resources/jawbreakerProj.png").toString(), 8 + damage/2, 0, true, false));
					
				}
			}
			if(p.hasItem("Glass Shard") && (int) ran.nextInt(4) == 0) {
				damage *= 3;
				setImage(new Image(getClass().getClassLoader().getResource("resources/glassShardV2.png").toString(), 16 + damage*2, 0, true, false)); 
			}
			
		}
						
		setX(x - getWidth() / 2);
		setY(y - getHeight() / 2);
	}
	
	/**
	 * the second constructor of the projectile
	 * @param node
	 * @param x speed
	 * @param y speed
	 * @param damage
	 * @param range
	 * @param image
	 */
	public Projectile(Node n, double dx, double dy, double damage, double range, Image i, boolean bounce) {
		double xCenter = n.getBoundsInParent().getMinX() + n.getBoundsInParent().getWidth() / 2;
		double yCenter = n.getBoundsInParent().getMinY() + n.getBoundsInParent().getHeight() / 2;
		this.x = xCenter;
		this.y = yCenter;
		this.dx = dx;
		this.dy = dy;
		this.damage = damage;
		this.range = range;
		node = n;
		setImage(i);
		enemiesHit = new ArrayList<>();
		bounces = bounce;
		removeOnHit = false;
		
		setX(x - getWidth() / 2);
		setY(y - getHeight() / 2);
	}

	@Override
	public void act(long now) {
		
		//player projectile collision
		if (getOneIntersectingObject(Player.class) != null && node != getOneIntersectingObject(Player.class)) {
			 getOneIntersectingObject(Player.class).setHit(true);
		}
		
		if (calcDistance() >= range) {
			getWorld().getChildren().remove(this);
		} 
		else if(bounces && !hasBounced) {
			if(((RoomManager) getWorld()).getCurrentRoom().onTopWall(this)) {
				dy = -dy;//makes it go the other direction in the y axis
				hasBounced = true;

			}
			if(((RoomManager) getWorld()).getCurrentRoom().onRightWall(this)) {
				dx = -dx;//makes it go the other direction in the x axis
				hasBounced = true;

			}
			if(((RoomManager) getWorld()).getCurrentRoom().onBottomWall(this)) {
				dy = -dy;//makes it go the other direction in the y axis
				hasBounced = true;

			}
			if(((RoomManager) getWorld()).getCurrentRoom().onLeftWall(this)) {		
				dx = -dx;//makes it go the other direction in the x axis
				hasBounced = true;

			}
			
			if (getOneIntersectingObject(Enemy.class) != null && node != getOneIntersectingObject(Enemy.class)) {
				if (getOneIntersectingObject(Enemy.class).getX() - getX() < 0) {
					dx = -dx;
					hasBounced = true;

				} else {
					dx = -dx;
					hasBounced = true;

				}
				if (getOneIntersectingObject(Enemy.class).getY() - getY() < 0) {
					dy = -dy;
					hasBounced = true;

				} else {
					dy = -dy;
					hasBounced = true;

				}
			}
			
			move(dx, dy);
		}
		else if (((RoomManager) getWorld()).getCurrentRoom().outOfRoom(this)) {
			Room curr = ((RoomManager) getWorld()).getCurrentRoom();
			
			//prevents projectile from disappearing immediately if it is spawned outside the room (large projectiles)
			//doesn't work for corners
			if(!(dy > 0) && curr.onBottomWall(this) && !(curr.onRightWall(this)) && !(curr.onLeftWall(this)) ) {
				move(dx, dy);
			}
			else if(!(dy < 0) && curr.onTopWall(this) && !(curr.onRightWall(this)) && !(curr.onLeftWall(this)) ) {
				move(dx, dy);
			}
			else if(!(dx < 0) && curr.onLeftWall(this) && !(curr.onTopWall(this)) && !(curr.onBottomWall(this)) ) {
				move(dx, dy);
			}
			else if(!(dx > 0) && curr.onRightWall(this)  && !(curr.onTopWall(this)) && !(curr.onBottomWall(this)) ) {
				move(dx, dy);
			}
			else {
				getWorld().getChildren().remove(this);	
			}
							
		}		
		 else {
			move(dx, dy);
		}
	}

	/**
	 * method that calculates the distance between the starting x y and the current x y
	 * @return distance as a double
	 */
	private double calcDistance() {
		double currX = getBoundsInParent().getMinX() + getWidth() / 2;
		double currY = getBoundsInParent().getMinY() + getHeight() / 2;

		return Math.sqrt(Math.pow(x - currX, 2) + Math.pow(y - currY, 2));
	}
	
	/**
	 * returns the damage of the projectile
	 * @return the damage of the projectile as a double
	 */
	public double getDamage(){
		return damage;
	}
	
	/**
	 * method that adds an enemy to the enemiesHit list
	 * @param Enemy that is to be added to teh enemiesHit list
	 */
	public void addEnemyHit(Enemy e) {
		enemiesHit.add(e);
	}
	
	/**
	 * method that checks if an enemy is in the enemiesHit list
	 * @param Enemy to be checked
	 * @return whether the enemy is in the list
	 */
	public boolean hasEnemyHit(Enemy e) {
		return enemiesHit.contains(e);
	}
	
	/**
	 * method that returns whether or not the projectile should be removed on hit
	 * @return boolean of whether the projectile should be removed
	 */
	public boolean getRemoveOnHit() {
		return removeOnHit;
	}
	
	/**
	 * method that returns whether or not the projectile has bounced yet
	 * @return boolean of whether the projectile has bounced
	 */
	public boolean getHasBounced() {
		return hasBounced;
	}
	
	/**
	 * method that returns whether or not the projectile bounces
	 * @return boolean of whether the projectile bounces
	 */
	public boolean getBounces() {
		return bounces;
	}
	
	/**
	 * returns dx
	 * @return the x speed
	 */
	public double getDx() {
		return dx;
	}
	
	/**
	 * returns dy
	 * @return the y speed
	 */
	public double getDy() {
		return dy;
	}
	
	/**
	 * returns the node of the sender of the projectile
	 * @return the node of the sender of the projectile
	 */
	public Node getSender() {
		return node;
	}
}

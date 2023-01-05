import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Room extends Actor {

	private boolean isLocked = false;

	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<Item> items = new ArrayList<Item>(); 

	/**
	 * boolean of whether the player is at the topDoor
	 */
	private boolean topDoor;
	/**
	 * boolean of whether the player is at the bottomDoor
	 */
	private boolean bottomDoor;
	/**
	 * boolean of whether the player is at the leftDoor
	 */
	private boolean leftDoor;
	/**
	 * boolean of whether the player is at the rightDoor
	 */
	private boolean rightDoor;

	/**
	 * String of the type of room
	 */
	private String type;

	/**
	 * boolean of whether the room has been generated
	 */
	private boolean beenGenerated = true;
	
	/**
	 * boolean of whether the room has been cleared
	 */
	private boolean roomCleared = false;
	
	/**
	 * the rectangle health bar
	 */
	private Rectangle healthBar;
	/**
	 * the backing of the health bar
	 */
	private Rectangle healthBarBacking;
	/**
	 * the boss enemy
	 */
	private Enemy boss;
	
	/**
	 * the stairs to the next floor
	 */
	private ImageView stairs = new ImageView(new Image(getClass().getClassLoader().getResource("resources/stairsV2.png").toString(), 32, 0, true, false));
	/**
	 * boolean of whether the player is on stairs
	 */
	private boolean isOnStairs = false;
	/**
	 * amount of time that the player is on the stairs
	 */
	private long timeOnStairs;
	/**
	 * boolean of whether the player is first on stairs
	 */
	private boolean firstOnStairs = true;

	/**
	 * constructor that generates a new room with corresponding doorways
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param t
	 */
	public Room(boolean left, boolean top, boolean right, boolean bottom, String t) {

		leftDoor = left;
		topDoor = top;
		rightDoor = right;
		bottomDoor = bottom;
		type = t;
		
		if (t.equals("start")) {
			
			setImage(new Image(getClass().getClassLoader().getResource("resources/startRoomV2.png").toString()));
			toBack();
		} else if (t.equals("game over")) {
			setImage(new Image(getClass().getClassLoader().getResource("resources/GAME-OVER.png").toString()));  
		} else if (t.equals("boss")) {
			setImage(new Image(getClass().getClassLoader().getResource("resources/bossRoom.png").toString())); 
			toBack();
			healthBarBacking = new Rectangle(0, 0, 40, 550);
			healthBarBacking.setFill(Color.BLACK);
			healthBar = new Rectangle(0, 0, 30, 540);
			healthBar.setFill(Color.WHITE);
			
		} else if (t.equals("item")) {
			setImage(new Image(getClass().getClassLoader().getResource("resources/itemRoom.png").toString())); 
			toBack();
			
		} else {
			setImage(new Image( getClass().getClassLoader().getResource("resources/room_locked.png").toString()));
			toBack();
		}
	}

	@Override
	public void act(long now) {
		if (beenGenerated) {
			if (type.equals("item")) {
				Random ran = new Random();
				int num = ran.nextInt(3);

				if (num != 0) {
					Item i = ((RoomManager) getWorld()).getItemFromPool();
					if (i != null) {
						getWorld().getChildren().add(i);
						i.setX(350 - (i.getWidth() / 2));
						i.setY(275 - (i.getHeight() / 2));
						items.add(i); 
					} else {
						Item_StatBox s = new Item_StatBox();
						getWorld().getChildren().add(s);
						s.setX(350 - (s.getWidth() / 2));
						s.setY(275 - (s.getHeight() / 2));
						items.add(s); 
					}
				} else {
					Item_StatBox s = new Item_StatBox();
					getWorld().getChildren().add(s);
					s.setX(350 - (s.getWidth() / 2));
					s.setY(275 - (s.getHeight() / 2));
					items.add(s); 
				}

				setLocked(false);
			} else if (type.equals("boss")) {
				generateBossEnemy();
			} else if (!getType().equals("start")) {
				generateEnemies();
			}
			beenGenerated = false;
		}
		
		if(type.equals("boss")) {
			if(!getWorld().getChildren().contains(healthBarBacking))getWorld().getChildren().add(healthBarBacking);
			if(!getWorld().getChildren().contains(healthBar))getWorld().getChildren().add(healthBar);			
			healthBar.setX(-40);
			healthBar.setY(5 + (540-((boss.getHealth()/boss.getStartingHealth())*540)));
			healthBarBacking.setX(-45);
			healthBar.setHeight(((boss.getHealth()/boss.getStartingHealth())*540));
			
			if (enemies.size() == 0) {
				if(!getWorld().getChildren().contains(stairs))getWorld().getChildren().add(stairs);
				stairs.setX(350-stairs.getBoundsInParent().getWidth()/2);
				stairs.setY(200);
				if(stairs.intersects(getWorld().getPlayer().getBoundsInLocal())) {
					if(firstOnStairs) {
						timeOnStairs = System.currentTimeMillis();
						firstOnStairs = false;
					}
					if(System.currentTimeMillis() - timeOnStairs >= 1000) {
						isOnStairs = true;
					}
					
				}
			}
		}

		//sets the image of the room after enemies are cleared
		//every room has  30% chance to spawn a heart after ending
		if (enemies.size() == 0 && (!getType().equals("start") && !getType().equals("item") && !getType().equals("boss"))  && !roomCleared ) {
			setImage(new Image(getClass().getClassLoader().getResource("resources/room.png").toString()));  
			setLocked(false);
			roomCleared = true;
			
			Random ran = new Random();
			if(ran.nextInt(3)==0) {
				Item_Food heart = new Item_Food();
				getWorld().getChildren().add(heart);
				heart.setX(getWidth()/2 - heart.getWidth()/2);
				heart.setY(getHeight()/2 - heart.getHeight()/2);
				items.add(heart); 
			}
		}
		
	}

	/**
	 * whether the node is out of the room
	 * @param node to be checked
	 * @return whether the node is outside the room
	 */
	public boolean outOfRoom(Node n) {
		return onTopWall(n) || onLeftWall(n) || onRightWall(n) || onBottomWall(n);
	}

	/**
	 * whether the node is on the top wall
	 * @param node to be checked
	 * @return whether the node is on the top wall
	 */
	public boolean onTopWall(Node n) {
		// upper wall check
		if (n.getBoundsInParent().getMinY() <= getBoundsInParent().getMinY() + 21) {
			// System.out.println("out of top wall");
			return true;
		}
		return false;
	}

	/**
	 * whether the node is on the left wall
	 * @param node to be checked
	 * @return whether the node is on the left wall
	 */
	public boolean onLeftWall(Node n) {
		// left wall check
		if (n.getBoundsInParent().getMinX() <= getBoundsInParent().getMinX() + 21) {
			// System.out.println("out of left wall");
			return true;
		}
		return false;
	}

	/**
	 * whether the node is on the right wall
	 * @param node to be checked
	 * @return whether the node is on the right wall
	 */
	public boolean onRightWall(Node n) {
		// right wall check
		if (n.getBoundsInParent().getMaxX() >= getBoundsInParent().getMaxX() - 21) {
			// System.out.println("out of right wall");
			return true;
		}
		return false;
	}

	/**
	 * whether the node is on the bottom wall
	 * @param node to be checked
	 * @return whether the node is on the bottom wall
	 */
	public boolean onBottomWall(Node n) {
		// bottom wall check
		if (n.getBoundsInParent().getMaxY() >= getBoundsInParent().getMaxY() - 21) {
			// System.out.println("out of bottom wall");
			return true;
		}
		return false;
	}

	/**
	 * whether the node is on the top door
	 * @param node to be checked
	 * @return whether the node is on the top door
	 */
	public boolean atTopDoor(Node n) {
		if (isLocked || !topDoor)
			return false;
		if (onTopWall(n) && n.getBoundsInParent().getMinX() > 300 && n.getBoundsInParent().getMaxX() < 400) {
			// System.out.println("at top door");
			return true;
		}
		return false;

	}

	/**
	 * whether the node is on the right door
	 * @param node to be checked
	 * @return whether the node is on the right door
	 */
	public boolean atRightDoor(Node n) {
		if (isLocked || !rightDoor)
			return false;
		if (onRightWall(n) && n.getBoundsInParent().getMinY() > 225 && n.getBoundsInParent().getMaxY() < 325) {
			// System.out.println("at right door");
			return true;
		}
		return false;

	}

	/**
	 * whether the node is on the left door
	 * @param node to be checked
	 * @return whether the node is on the left door
	 */
	public boolean atLeftDoor(Node n) {
		if (isLocked || !leftDoor)
			return false;
		if (onLeftWall(n) && n.getBoundsInParent().getMinY() > 225 && n.getBoundsInParent().getMaxY() < 325) {
			// System.out.println("at left door");
			return true;
		}
		return false;

	}

	/**
	 * whether the node is on the bottom door
	 * @param node to be checked
	 * @return whether the node is on the bottom door
	 */
	public boolean atBottomDoor(Node n) {
		if (isLocked || !bottomDoor)
			return false;
		if (onBottomWall(n) && n.getBoundsInParent().getMinX() > 300 && n.getBoundsInParent().getMaxX() < 400) {
			// System.out.println("at bottom door");
			return true;
		}
		return false;

	}

	/**
	 * method that sets whether the room is locked
	 * @param locked
	 */
	public void setLocked(boolean locked) {
		isLocked = locked;
	}

	/**
	 * returns the type of the room
	 * @return String of what type the room is
	 */
	public String getType() {
		return type;
	}

	/**
	 * method that generates enemies
	 */
	public void generateEnemies() {

		// creates new enemies
		int roomCounter = ((RoomManager) getWorld()).getRoomCounter();
		int floor = ((RoomManager) getWorld()).getFloor();
		
		for (int i = 0; i < (roomCounter / 2)*floor;) {
			Enemy e = new Enemy_Chaser((int) (Math.random() * 801), (int) (Math.random() * 601),
					2.5 + 1*(floor/2), (int) (3)*floor );
			Enemy e2 = new Enemy_Dasher((int) (Math.random() * 801), (int) (Math.random() * 601),
					4 + 1*(floor/2), (int) (3)*floor );
			Enemy e3 = new Enemy_Shooter((int) (Math.random() * 801), (int) (Math.random() * 601),
					4 + 1*(floor/2), (int) (3)*floor );
			Enemy e4 = new Enemy_Shooter2((int) (Math.random() * 801), (int) (Math.random() * 601),
					4 + 1*(floor/2), (int) (5)*floor );
			getWorld().getChildren().addAll(e, e2, e3, e4);
			if (outOfRoom(e)) {
				getWorld().getChildren().remove(e);
			} else if (Math.abs(e.getX() - getWorld().getPlayer().getX()) < 50 || Math.abs(e.getY() - getWorld().getPlayer().getY()) < 50) {
				getWorld().getChildren().remove(e);
			} else {
				enemies.add(e);
				i++;
			}
			
			if (outOfRoom(e2)) {
				getWorld().getChildren().remove(e2);
			} else if (Math.abs(e2.getX() - getWorld().getPlayer().getX()) < 50 || Math.abs(e2.getY() - getWorld().getPlayer().getY()) < 50) {
				getWorld().getChildren().remove(e2);
			} else {
				enemies.add(e2);
				i++;
			}
			
			if (outOfRoom(e3)) {
				getWorld().getChildren().remove(e3);
			} else if (Math.abs(e3.getX() - getWorld().getPlayer().getX()) < 50 || Math.abs(e3.getY() - getWorld().getPlayer().getY()) < 50) {
				getWorld().getChildren().remove(e3);
			} else {
				enemies.add(e3);
				i++;
			}
			
			if (outOfRoom(e4)) {
				getWorld().getChildren().remove(e4);
			} else if (Math.abs(e4.getX() - getWorld().getPlayer().getX()) < 50 || Math.abs(e4.getY() - getWorld().getPlayer().getY()) < 50) {
				getWorld().getChildren().remove(e4);
			} else {
				enemies.add(e4);
				i++;
			}
		}
	}

	/**
	 * method that removes an enemy
	 * @param enemy to be removed
	 */
	public void removeEnemy(Enemy e) {
		enemies.remove(e);
	}
	
	/**
	 * method that generates a boss enemy
	 */
	private void generateBossEnemy(){
		if(((RoomManager) getWorld()).getFloor() == 1) {
			boss = new Enemy_Boss(getWidth()/2, getHeight()/2, 2, 50 + (int)(5*getWorld().getPlayer().getDamage()*((RoomManager) getWorld()).getFloor()) );
		} else {
			boss = new Enemy_Boss2(getWidth()/2, getHeight()/2, 2, 50 + (int)(5*getWorld().getPlayer().getDamage()*((RoomManager) getWorld()).getFloor()) );
		}
		
		enemies.add(boss); 
		getWorld().getChildren().add(boss);
	}
	
	/**
	 * method that checks whether the boss is dead
	 * @return boolean of whether the boss is dead
	 */
	public boolean isBossDead() {
		if(type.equals("boss")) {
			//System.out.println(enemies.contains(boss));
			return !enemies.contains(boss);
		}
		return false;
	}
	
	/**
	 * method that checks whether the player is on the stairs
	 * @return boolean of whether the player is on the stairs
	 */
	public boolean isOnStairs() {
		//System.out.println("stairs " + isOnStairs);
		return isOnStairs;
	}

	public ArrayList<Enemy> getEnemies(){
		return enemies; 
	}
	
	public ArrayList<Item> getItems(){
		return items; 
	}
	
	public void removeItem(Item i) {
		for(int j = 0; j < items.size(); j++) {
			if(items.get(j).equals(i)) {
				items.remove(j);
				return; 
			}
		}
	}
}

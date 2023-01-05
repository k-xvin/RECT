import java.util.ArrayList;
import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Player extends Actor {

	/**
	 * ArrayList where player holds all their items
	 */
	private ArrayList<Item> inv;

	/**
	 * double of how far the player's projectile can travel
	 */
	private double range = 200;
	/**
	 * double of how fast the player moves
	 */
	private double moveSpeed = 4;
	/**
	 * double of the speed of firing (lower fire rate, faster shooting)
	 */
	private double fireRate = 750;
	/**
	 * long that keeps track of the last time fired
	 */
	private long lastShot = System.currentTimeMillis();
	/**
	 * double of how much damage the player's projectile does
	 */
	private double damage = 1;
	/**
	 * double of how fast the projectile travels
	 */
	private double shotSpeed = 7;

	/**
	 * double containing the maximum health of the player
	 */
	private double maxHealth = 3;
	/**
	 * double containing the current health of the player
	 */
	private double currentHealth = 3;
	
	/**
	 * the transition when the player moves into a different room
	 */
	private FadeTransition ft; 

	/**
	 * the HBox displaying the health of the player
	 */
	private HBox healthDisplay;
	
	/**
	 * the VBox displaying the items
	 */
	private VBox itemDisplay;
	/**
	 * the int of the number of items the player has
	 */
	private int numItems;

	/**
	 * String of the projectile image
	 */
	private String projectileImage;

	/**
	 * boolean of whether the player has been hit
	 */
	private boolean hit = false;
	/**
	 * long of when the last hit was
	 */
	private long lastHit = System.currentTimeMillis();

	private boolean itemAdded; 
	private Item item; 
	
	/**
	 * default constructor creating the player
	 */
	public Player() {
		// sets the image
		setImage( new Image(getClass().getClassLoader().getResource("resources/playerFront.png").toString()) );
		projectileImage = getClass().getClassLoader().getResource("resources/ball3.png").toString(); 
		inv = new ArrayList<>();
		
//		inv.add(new CrossbeamFocus());
//		inv.add(new DiagonalMatrix());
//		inv.add(new Bubbler());
//		inv.add(new Jawbreaker());
//		inv.add(new PartyHat());
	}

	@Override
	public void act(long now) {
		Room currentRoom = ((RoomManager) getWorld()).getCurrentRoom();
		// Movement controls
		if (getWorld().isKeyDown(KeyCode.A) && (!currentRoom.onLeftWall(this) || currentRoom.atLeftDoor(this))) { // player
																													// moves
																													// left
			move(-moveSpeed, 0);
			if (!isShooting()) { // player's sprite turns left if not shooting (shooting has animation priority)
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerLeft.png").toString()) );
			}
		}
		if (getWorld().isKeyDown(KeyCode.D) && (!currentRoom.onRightWall(this) || currentRoom.atRightDoor(this))) { // player
																													// moves
																													// right
			move(moveSpeed, 0);
			if (!isShooting()) { // player's sprite turns right if not shooting (shooting has animation priority)
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerRight.png").toString()) );
			}
		}
		if (getWorld().isKeyDown(KeyCode.W) && (!currentRoom.onTopWall(this) || currentRoom.atTopDoor(this))) { // player
																												// moves
																												// up
			move(0, -moveSpeed);
			if (!isShooting()) { // player's sprite turns up if not shooting (shooting has animation priority)
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerBack.png").toString()) );
			}
		}
		if (getWorld().isKeyDown(KeyCode.S) && (!currentRoom.onBottomWall(this) || currentRoom.atBottomDoor(this))) { // player
																														// moves
																														// down
			move(0, moveSpeed);
			if (!isShooting()) { // player's sprite turns down if not shooting (shooting has animation priority)
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerFront.png").toString()) );
			}
		}

		// you can only fire once per every fireRate interval (default 750ms)
		if (System.currentTimeMillis() - lastShot >= fireRate) {			
			shoot();
			
		}

		// health decreases when hit
		if (hit) {

			// makes sure it has been at least 1 second since the last hit
			if (System.currentTimeMillis() - lastHit >= 1000) {
				hit = false;
				if(hasItem("Metal Plates")) {
					Random ran = new Random();
					if(ran.nextInt(4)!=0) { //25% to block dmg
						currentHealth -= 0.5;
					} else { //displays notification that u blocked dmg
						Text block = new Text("BLOCKED");
						block.setFont(new Font(14));
						getWorld().getChildren().add(block);
						block.setX(getX() - (block.getBoundsInParent().getWidth()/4));
						block.setY(getY() - 10);
						
						FadeTransition blockFade = new FadeTransition(Duration.millis(1000), block);
						blockFade.setFromValue(1.0);
						blockFade.setToValue(0);
						blockFade.play();
					}
					
				} else {
					currentHealth -= 0.5;
				}
				
				displayHealth();
				lastHit = System.currentTimeMillis();
				
				// player flashes during for 1 second after it has been hit 
				ft = new FadeTransition(Duration.millis(100), this);
				ft.setFromValue(1.0);
				ft.setToValue(0.1);
				ft.setCycleCount(10);
				ft.setAutoReverse(true);
				ft.play();
			} 
			else {
				hit = false; 
			}
		}
		//displays the player's items on the right side
		displayItems();	
		
		toFront();
	}

	/**
	 * method that makes a new projectile, starting at the center of the player
	 */
	private void shoot() {

		
		// projectile size scales with damage
		Image projectile = new Image(projectileImage, 16 + damage*2, 0, true, false);
		
		if(hasItem("Diagonal Matrix")) {
			if (getWorld().isKeyDown(KeyCode.UP)) { // up arrow is pressed
				// makes a new a new projectile that goes up with parameters
				getWorld().add(new Projectile(this, shotSpeed, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, shotSpeed, shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, shotSpeed, damage, range, projectile));
				// sets the image to be the player's back
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerBack.png").toString()) );
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.DOWN)) {
				getWorld().add(new Projectile(this, shotSpeed, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, shotSpeed, shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerFront.png").toString()) );
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.LEFT)) {
				getWorld().add(new Projectile(this, shotSpeed, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, shotSpeed, shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerLeft.png").toString()) );
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.RIGHT)) {
				getWorld().add(new Projectile(this, shotSpeed, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, shotSpeed, shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerRight.png").toString()) );
				lastShot = System.currentTimeMillis();
			}
		} 
		
		if(hasItem("Crossbeam Focus")) {
			if (getWorld().isKeyDown(KeyCode.UP)) { // up arrow is pressed
				// makes a new a new projectile that goes up with parameters
				getWorld().add(new Projectile(this, 0, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, shotSpeed, 0, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, 0, damage, range, projectile));
				getWorld().add(new Projectile(this, 0, shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerBack.png").toString()) ); // sets the image to be the player's back
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.DOWN)) {
				getWorld().add(new Projectile(this, 0, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, shotSpeed, 0, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, 0, damage, range, projectile));
				getWorld().add(new Projectile(this, 0, shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerFront.png").toString()) );
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.LEFT)) {
				getWorld().add(new Projectile(this, 0, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, shotSpeed, 0, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, 0, damage, range, projectile));
				getWorld().add(new Projectile(this, 0, shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerLeft.png").toString()) );
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.RIGHT)) {
				getWorld().add(new Projectile(this, 0, -shotSpeed, damage, range, projectile));
				getWorld().add(new Projectile(this, shotSpeed, 0, damage, range, projectile));
				getWorld().add(new Projectile(this, -shotSpeed, 0, damage, range, projectile));
				getWorld().add(new Projectile(this, 0, shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerRight.png").toString()) );
				lastShot = System.currentTimeMillis();
			}
		}
		if(!hasItem("Crossbeam Focus") && !(hasItem("Diagonal Matrix"))) {
			if (getWorld().isKeyDown(KeyCode.UP)) { // up arrow is pressed
				// makes a new a new projectile that goes up with parameters
				getWorld().add(new Projectile(this, 0, -shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerBack.png").toString()) ); // sets the image to be the player's back
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.DOWN)) {
				getWorld().add(new Projectile(this, 0, shotSpeed, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerFront.png").toString()) );
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.LEFT)) {
				getWorld().add(new Projectile(this, -shotSpeed, 0, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerLeft.png").toString()) );
				lastShot = System.currentTimeMillis();
			} else if (getWorld().isKeyDown(KeyCode.RIGHT)) {
				getWorld().add(new Projectile(this, shotSpeed, 0, damage, range, projectile));
				setImage( new Image(getClass().getClassLoader().getResource("resources/playerRight.png").toString()) );
				lastShot = System.currentTimeMillis();
			}
		}
		
		
		
	}

	/**
	 * method that returns whether or not the player is shooting (arrow keys down)
	 * @return boolean of whether or not the player is shooting
	 */
	private boolean isShooting() {
		return getWorld().isKeyDown(KeyCode.UP) || getWorld().isKeyDown(KeyCode.DOWN)
				|| getWorld().isKeyDown(KeyCode.LEFT) || getWorld().isKeyDown(KeyCode.RIGHT);
	}

	/**
	 * method that displays player health in the top right corner as hearts and half hearts
	 */
	public void displayHealth() {

		getWorld().getChildren().remove(healthDisplay);

		double numCircles = currentHealth;
		healthDisplay = new HBox();

		//glow behind the hearts to make it more visible
		DropShadow borderGlow = new DropShadow();
		borderGlow.setColor(Color.WHITE);
		borderGlow.setOffsetX(0);
		borderGlow.setOffsetY(0);
		borderGlow.setHeight(10);
		
		// fills in the currentHealth
		while (numCircles > 0) {
			
			

			if (numCircles == 0.5) {//displays a half heart
				HBox temp = new HBox();
				
				ImageView halfHeart = new ImageView(new Image(getClass().getClassLoader().getResource("resources/halfHeart.png").toString()) );
				halfHeart.setEffect(borderGlow);
				
				temp.getChildren().add(halfHeart);
				temp.setPadding(new Insets(15, 15, 15, 15));
				healthDisplay.getChildren().add(temp);
				numCircles--;
			} else {//displays full hearts
				HBox temp = new HBox();
				
				ImageView heart = new ImageView(new Image(getClass().getClassLoader().getResource("resources/heart.png").toString()) );
				heart.setEffect(borderGlow);
				
				temp.getChildren().add(heart);
				temp.setPadding(new Insets(15, 15, 15, 15));
				healthDisplay.getChildren().add(temp);
				numCircles--;
			}

		}

		// fills in the empty health with empty hearts
		for (int i = 0; i < (int) (maxHealth - currentHealth); i++) {
			HBox temp = new HBox();
			
			ImageView emptyHeart = new ImageView(new Image(getClass().getClassLoader().getResource("resources/emptyHeart.png").toString()) );
			emptyHeart.setEffect(borderGlow);
			
			temp.getChildren().add(emptyHeart);
			temp.setPadding(new Insets(15, 15, 15, 15));
			healthDisplay.getChildren().add(temp);
		}

		getWorld().add(healthDisplay);
	}
	
	/**
	 * method that displays the player's item inventory on the side
	 */
	public void displayItems() {
		
		//removes old display
		getWorld().getChildren().remove(itemDisplay);
		
		numItems = inv.size();
		
		//creates the VBox to hold the item pictures
		itemDisplay = new VBox();	
		getWorld().getChildren().add(itemDisplay);
		itemDisplay.setLayoutX(700);
		
		//adds items to main VBox
		for(int i=numItems-1;i>=0;i--) {
			VBox item = new VBox();
			item.setPadding(new Insets(15, 15, 15, 15));
			ImageView pic = new ImageView(inv.get(i).getImage());
			pic.setOpacity(0.8);			
			item.getChildren().add(pic);
			itemDisplay.getChildren().add(item);
		}
	}

	/**
	 * method that increases damage by amt (called by items)
	 * @param int amt by which damage increases
	 */
	public void increaseDamage(int amt) {
		damage += amt;
	}

	/**
	 * method that increases fireRate by amt (called by items)
	 * @param int amt by which the fire rate increases
	 */
	public void increaseFireRate(int amt) {
		if(fireRate - amt <= 100) fireRate = 100;
		else fireRate -= amt;
	}

	/**
	 * increases moveSpeed by d
	 * @param double by which moveSpeed in increased
	 */
	public void increaseMoveSpeed(double d) {
		moveSpeed += d;
	}

	/**
	 * increases range by amt (called by items)
	 * @param int amt by which range is increased
	 */
	public void increaseRange(int amt) {
		range += amt;
	}

	/**
	 * increases shot speed by amt (called by items)
	 * @param int amt by which shot speed is increased
	 */
	public void increaseShotSpeed(int amt) {
		shotSpeed += amt;
	}
	
	/**
	 * method that adds an item to the player's inventory
	 * @param Item that is added to the inventory
	 */
	public void addInv(Item i) {
		inv.add(i);
		itemAdded = true;
		item = i; 
		
	}
	
	public void setItemAdded(boolean val) {
		itemAdded = val; 
	}
	
	public boolean getItemAdded() {
		return itemAdded; 
	}
	
	public Item getItem() {
		return item; 
	}
	
	public void setItem(Item i) {
		item = i; 
	}
	
	/**
	 * method that allows player to check if they have the item by checking the names
	 * @param name of item to be checked
	 * @return whether the player has the item
	 */
	public boolean hasItem(String name) {
		for(int i=0;i<inv.size();i++) {
			if(inv.get(i).getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * method that gives whether the player has been hit
	 * @return boolean of whether the player has been hit
	 */
	public boolean isHit() {
		return hit;
	}

	/**
	 * method that sets whether the player has been hit
	 * @param boolean of whether the player has been hit
	 */
	public void setHit(boolean hit) {
		this.hit = hit;
	}
	
	/**
	 * method that gives the current health of the player
	 * @return double of the current health of the player
	 */
	public double getCurrentHealth() {
		return currentHealth; 
	}
	
	/**
	 * method that sets the maximum health of the player
	 * @param double of the maximum health of the player
	 */
	public void setMaxHealth(double max) {
		maxHealth = max; 
		displayHealth(); 
	}
	
	/**
	 * method that gives the maximum health of the player
	 * @return double of the maximum health of the player
	 */
	public double getMaxHealth() {
		return maxHealth; 
	}
	
	/**
	 * method that gives the damage on the player
	 * @return double of the damage on the player
	 */
	public double getDamage() {
		return damage;
	}
	
	/**
	 * method that sets the current health (called by items) 
	 * @param double current of health of the player
	 */
	public void setCurrentHealth(double current) {
		
		if(current > maxHealth) {
			currentHealth = maxHealth;
		}
		else { 
			currentHealth = current; 

		}
		
		displayHealth(); 
	}

}

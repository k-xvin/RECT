import java.util.ArrayList;
import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class RoomManager extends World {

	/**
	 * the current Room
	 */
	private Room currentRoom;
	/**
	 * the current floor
	 */
	private int floor = 1;
	/**
	 * the number of rooms passed so far
	 */
	private int roomCounter = 1;

	private Rectangle cover1;
	private Rectangle cover2;
	private Rectangle cover3;
	private Rectangle cover4;

	private Rectangle fadeScreen;

	/**
	 * the pool of items
	 */
	private ArrayList<Item> itemPool;

	/**
	 * the player
	 */
	private Player p;
	/**
	 * x value of player
	 */
	private double playerX;
	/**
	 * y value of player
	 */
	private double playerY; 
	
	/**
	 * the replay button
	 */
	private Button replay;
	/**
	 * the continue to play button
	 */
	private Button continuePlay;
	
	/**
	 * the Media Player for the background music
	 */
	private MediaPlayer bgMusic;
	
	private boolean left = true;
	private boolean top = true;
	private boolean right = true;
	private boolean bottom = true;

	/**
	 * constructor that creates a new room manager
	 */
	public RoomManager() {
		currentRoom = new Room(true, true, true, true, "start");
		setCenter(currentRoom);
		itemPool = new ArrayList<>();
		fillItemPool();
		bgMusic = new MediaPlayer(new Media(getClass().getClassLoader().getResource("resources/BOI_Cave_Theme.mp3").toString()));
		bgMusic.setCycleCount(MediaPlayer.INDEFINITE);
		bgMusic.setVolume(0.3);
		bgMusic.play();
	}

	// checks if player has gone through a doorway, and places player in a new
	// room
	@Override
	public void act(long now) {

		// checks if the player is null, for safety
		if (getObjects(Player.class).size() != 0 && getObjects(Player.class).get(0) != null) {

			// stores the player object for easy access
			p = getObjects(Player.class).get(0);

			// game over screen when player health equals 0
			if (p.getCurrentHealth() == 0) {
				genNewRoom("game over"); 
			}

			// checks if the player has left through the left doorway
			if (currentRoom.atLeftDoor(p) && isKeyDown(KeyCode.A)) {
				// System.out.println("out left");
				genNewRoom("right");
				p.setX(650);
				roomCounter++;
				p.displayHealth();
			}

			// checks if the player has left through the top doorway
			if (currentRoom.atTopDoor(p) && isKeyDown(KeyCode.W)) {
				// System.out.println("out top");
				genNewRoom("bottom");
				p.setY(500);
				roomCounter++;
				p.displayHealth();
			}

			// checks if the player has left through the right doorway
			if (currentRoom.atRightDoor(p) && isKeyDown(KeyCode.D)) {
				// System.out.println("out right");
				genNewRoom("left");
				p.setX(25);
				roomCounter++;
				p.displayHealth();
			}

			// checks if the player has left through the bottom doorway
			if (currentRoom.atBottomDoor(p) && isKeyDown(KeyCode.S)) {
				// System.out.println("out bottom");
				genNewRoom("top");
				p.setY(25);
				roomCounter++;
				p.displayHealth();
			}
			
			if(keysDown.contains(KeyCode.P)) {
				
				
				
				playerX = p.getX(); 
				playerY = p.getY(); 
				
				getChildren().clear();
				
				fadeScreen = new Rectangle(700, 550);
				getChildren().add(fadeScreen);
				FadeTransition rectFade = new FadeTransition(Duration.millis(1000), fadeScreen);
				rectFade.setFromValue(0.9);
				rectFade.setToValue(0);
				rectFade.play();
				
				// setting paused image as the center of the root 
				ImageView pauseImage = new ImageView(new Image("resources/PAUSED.png")); 
				HBox pauseBox = new HBox(); 
				pauseBox.setPadding(new Insets(30, 12, 30, 12));
				pauseBox.getChildren().add(pauseImage); 
				pauseBox.setAlignment(Pos.CENTER);
				
				continuePlay = new Button("", new ImageView(new Image("resources/PLAY.png"))); 
				HBox contPlayBox = new HBox(); 
				contPlayBox.getChildren().add(continuePlay);
				contPlayBox.setAlignment(Pos.CENTER);
				
				VBox pauseScreen = new VBox(); 
				pauseScreen.getChildren().addAll(pauseBox, contPlayBox); 
				pauseScreen.setAlignment(Pos.CENTER);
				
				setCenter(pauseScreen);
				
				ButtonHandler bh = new ButtonHandler(); 
				continuePlay.setOnAction(bh);
			}
			
			if(p.getItemAdded() == true) {
				currentRoom.removeItem(p.getItem()); 
				p.setItemAdded(false);
			}

		}
		
		//transition to next floor
		if(currentRoom.isBossDead() && currentRoom.isOnStairs()) {
			Rectangle fadeScreen = new Rectangle(700, 550);
			getChildren().add(fadeScreen);
			FadeTransition rectFadeIn = new FadeTransition(Duration.millis(2000), fadeScreen);
			rectFadeIn.setFromValue(0);
			rectFadeIn.setToValue(1.0);
			
			FadeTransition rectFadeOut = new FadeTransition(Duration.millis(2000), fadeScreen);
			rectFadeOut.setFromValue(1.0);
			rectFadeOut.setToValue(0);
			rectFadeOut.play();
			
			SequentialTransition fades = new SequentialTransition(rectFadeOut, rectFadeIn);
			fades.play();
			
			genNewRoom("next");	
			
			roomCounter = 0;
			floor++;
			
			Text floorTitle = new Text("Floor " + floor);
			floorTitle.setFont(new Font(50));
			
			getChildren().addAll(floorTitle);
			
			TranslateTransition nameSlideIn = new TranslateTransition(Duration.millis(500), floorTitle);
			nameSlideIn.setFromY(200);
			nameSlideIn.setFromX(-300);
			nameSlideIn.setToX(350 - (floorTitle.getBoundsInParent().getWidth()/2));
			
			PauseTransition pause = new PauseTransition(Duration.millis(1000));
			
			TranslateTransition nameSlideOut = new TranslateTransition(Duration.millis(500), floorTitle);
			nameSlideOut.setFromY(200);
			nameSlideOut.setFromX(350 - (floorTitle.getBoundsInParent().getWidth()/2));
			nameSlideOut.setToX(900);
			
			SequentialTransition display = new SequentialTransition(nameSlideIn, pause, nameSlideOut);
			display.play();
			
			getPlayer().displayHealth();
		}		
	}

	/**
	 * method that generates a new room and transition, given the door that should always exist
	 * @param door through which the player exited the previous room
	 */
	private void genNewRoom(String door) {

		// remove the old rectangles
		// getChildren().removeAll(fadeScreen, cover1, cover2, cover3, cover4,
		// currentRoom);

		// remove everything
		getChildren().clear();
		
		// game over screen
		if (door.equals("game over")) {
			bgMusic.stop();
			getChildren().clear();
			
			currentRoom = new Room(false, false, false, false, "game over");
			HBox roomBox = new HBox(); 
			roomBox.getChildren().add(currentRoom); 
			roomBox.setAlignment(Pos.CENTER);
			roomBox.setPadding(new Insets(20, 12, 20, 12));
			
			Image replayImage = new Image(getClass().getClassLoader().getResource("resources/Restart.png").toString()); 
			replay = new Button("", new ImageView(replayImage));
			
			ButtonHandler bh = new ButtonHandler(); 
			replay.setOnAction(bh);
			
			HBox replayBox = new HBox(); 
			replayBox.getChildren().addAll(replay);
			replayBox.setAlignment(Pos.CENTER);
			
			VBox gameOverBox = new VBox(); 
			gameOverBox.getChildren().addAll(roomBox, replayBox); 
			gameOverBox.setAlignment(Pos.CENTER);
			
			fadeScreen = new Rectangle(700, 550);
			getChildren().add(fadeScreen);
			FadeTransition rectFade = new FadeTransition(Duration.millis(1000), fadeScreen);
			rectFade.setFromValue(0.9);
			rectFade.setToValue(0);
			rectFade.play();
			
			setCenter(gameOverBox); 
			
		} else {
			
			// add back the player 
			getChildren().add(p);
			
			// randomize doorways
			Random ran = new Random();
			boolean left = true;
			boolean top = true;
			boolean right = true;
			boolean bottom = true;
			if(!door.equals("next")){
				if (!door.equals("left") && ran.nextInt(2) == 0) {
					// System.out.println("no left");
					left = false;
					this.left = false; 
				}
				if (!door.equals("top") && ran.nextInt(2) == 0) {
					// System.out.println("no top");
					top = false;
					this.top = false; 
				}
				if (!door.equals("right") && ran.nextInt(2) == 0) {
					// System.out.println("no right");
					right = false;
					this.right = false; 
				}
				if (!door.equals("bottom") && ran.nextInt(2) == 0) {
					// System.out.println("no bottom");
					bottom = false;
					this.bottom = false; 
				}
			}
			// initialize new room
			if (roomCounter % 3 == 0) {
				// item room every 3 rooms
				currentRoom = new Room(left, top, right, bottom, "item");
			} else if (roomCounter == 10) {
				// boss room every 10 rooms
				currentRoom = new Room(false, false, false, false, "boss");
				
				
			} else {
				currentRoom = new Room(left, top, right, bottom, "normal");
			}
			
			// sets the room as the center
			setCenter(currentRoom);
			
			// block doorways that do not exist with black rectangles
			if (!left) {
				cover1 = new Rectangle(21, 100);
				// cover1.setFill(Color.BLUE);
				getChildren().add(cover1);
				cover1.setX(currentRoom.getX());
				cover1.setY(currentRoom.getHeight() / 2 - 50);
			}
			if (!top) {
				cover2 = new Rectangle(100, 21);
				// cover2.setFill(Color.BLUE);
				getChildren().add(cover2);
				cover2.setX(currentRoom.getWidth() / 2 - 50);
				cover2.setY(currentRoom.getY());
			}
			if (!right) {
				cover3 = new Rectangle(21, 100);
				// cover3.setFill(Color.BLUE);
				getChildren().add(cover3);
				cover3.setX(currentRoom.getWidth() - 21);
				cover3.setY(currentRoom.getHeight() / 2 - 50);
			}
			if (!bottom) {
				cover4 = new Rectangle(100, 21);
				// cover4.setFill(Color.BLUE);
				getChildren().add(cover4);
				cover4.setX(currentRoom.getWidth() / 2 - 50);
				cover4.setY(currentRoom.getHeight() - 21);

			}

			//generateEnemies();

			currentRoom.setLocked(true);
		}

		// fade in to the new room
		if(!door.equals("game over")) {
			fadeScreen = new Rectangle(700, 550);
			getChildren().add(fadeScreen);
			FadeTransition rectFade = new FadeTransition(Duration.millis(1000), fadeScreen);
			rectFade.setFromValue(0.9);
			rectFade.setToValue(0);
			rectFade.play();
		}
	}

	/**
	 * method that fills the item pool with items
	 */
	private void fillItemPool() {
		itemPool.add(new Item_Jawbreaker());
		itemPool.add(new Item_PartyHat());
		itemPool.add(new Item_Stick());
		itemPool.add(new Item_DriedHeart());
		itemPool.add(new Item_MetalPlating());
		// itemPool.add(new Food());
		itemPool.add(new Item_Tar());
		itemPool.add(new Item_Finger());
		itemPool.add(new Item_Coffee());
		itemPool.add(new Item_DiagonalMatrix());
		itemPool.add(new Item_Popsicle());
		itemPool.add(new Item_GlassShard());
		itemPool.add(new Item_CrossbeamFocus());
		itemPool.add(new Item_Bubbler());
	}

	/**
	 * method that gets an item from the pool
	 * @return the item from the pool
	 */
	public Item getItemFromPool() {
		Random ran = new Random();
		if (itemPool.size() != 0) {
			Item i = itemPool.remove(ran.nextInt(itemPool.size()));
			System.out.println(i.getName());
			return i;
		}
		return null;
	}
 
	/**
	 * method that returns the current room
	 * @return the current room
	 */
	public Room getCurrentRoom() {
		return currentRoom;
	}
	
	/**
	 * method that gets the room counter
	 * @return the room counter
	 */
	public int getRoomCounter(){
		return roomCounter;
	}
	
	/**
	 * method that gets the floor number
	 * @return the floor number
	 */
	public int getFloor() {
		return floor;
	}
	
	private class KeyboardHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {

			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				addKeyCode(event.getCode());
			}
			if (event.getEventType() == KeyEvent.KEY_RELEASED) {
				removeKeyCode(event.getCode());
			}
		}

	}

	private class ButtonHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent event) {
			if(event.getSource() == replay) {
				
				RoomManager roomManager = new RoomManager();
				roomManager.setPrefSize(800,600);
				roomManager.setMaxHeight(550);
				roomManager.setMaxWidth(700);
				setCenter(roomManager);
				
				// Creates player and sets its coordinates
				Player player = new Player();
				player.setX(350);
				player.setY(275);
				 
				// Adds player to the world and calls start on the world
				roomManager.getChildren().addAll(player);
				roomManager.start();
				
				//Displays the player's health 
				player.displayHealth();
				
				class MyKeyboardHandler implements EventHandler<KeyEvent> {
					@Override
					public void handle(KeyEvent event) {

						if (event.getEventType() == KeyEvent.KEY_PRESSED) {
							roomManager.addKeyCode(event.getCode());
						}
						if (event.getEventType() == KeyEvent.KEY_RELEASED) {
							roomManager.removeKeyCode(event.getCode());
						}
					}

				}

				// Keyboard input
				MyKeyboardHandler kh = new MyKeyboardHandler();
				roomManager.setOnKeyPressed(kh);
				roomManager.setOnKeyReleased(kh);
				roomManager.requestFocus();

			}
			else if(event.getSource() == continuePlay) {
				
				fadeScreen = new Rectangle(700, 550);
				getChildren().add(fadeScreen);
				FadeTransition rectFade = new FadeTransition(Duration.millis(1000), fadeScreen);
				rectFade.setFromValue(0.9);
				rectFade.setToValue(0);
				rectFade.play();
				
				// Sets the center back to the room 
				setCenter(currentRoom); 
				
				// Adds back the room 
				getChildren().add(p); 
				p.setX(playerX);
				p.setY(playerY);
				
				start(); 
				
				//Displays the player's health 
				p.displayHealth();
				
				// Keyboard input
				KeyboardHandler kh = new KeyboardHandler();
				setOnKeyPressed(kh);
				setOnKeyReleased(kh);
				requestFocus();
				
				// Adds back the enemies
				getChildren().addAll(currentRoom.getEnemies());
				
				//Adds back items 
				getChildren().addAll(currentRoom.getItems());
				
				// block doorways that do not exist with black rectangles
				if (!left) {
					cover1 = new Rectangle(21, 100);
					// cover1.setFill(Color.BLUE);
					getChildren().add(cover1);
					cover1.setX(currentRoom.getX());
					cover1.setY(currentRoom.getHeight() / 2 - 50);
				}
				if (!top) {
					cover2 = new Rectangle(100, 21);
					// cover2.setFill(Color.BLUE);
					getChildren().add(cover2);
					cover2.setX(currentRoom.getWidth() / 2 - 50);
					cover2.setY(currentRoom.getY());
				}
				if (!right) {
					cover3 = new Rectangle(21, 100);
					// cover3.setFill(Color.BLUE);
					getChildren().add(cover3);
					cover3.setX(currentRoom.getWidth() - 21);
					cover3.setY(currentRoom.getHeight() / 2 - 50);
				}
				if (!bottom) {
					cover4 = new Rectangle(100, 21);
					// cover4.setFill(Color.BLUE);
					getChildren().add(cover4);
					cover4.setX(currentRoom.getWidth() / 2 - 50);
					cover4.setY(currentRoom.getHeight() - 21);

				}
			}
		}
		
	}
}
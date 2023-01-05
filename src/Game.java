
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Game extends Application {

	/**
	 * the BorderPane for the game
	 */
	private BorderPane root;
	/**
	 * the player of the game whom the user controls
	 */
	private Player player;
	/**
	 * the class that controls all of the rooms
	 */
	private RoomManager roomManager;
	/**
	 * the button that when pushed, allows the game to start
	 */
	private Button play; 

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Sets the title and initializes the root
		primaryStage.setTitle("RECT");
		primaryStage.setResizable(false);
		root = new BorderPane();
		
		// Creates an ImageView and adds it to a HBox
		ImageView rectImage = new ImageView(new Image(getClass().getClassLoader().getResource("resources/RECT.png").toString())); 
		HBox rectImageBox = new HBox(); 
		rectImageBox.setPadding(new Insets(30, 12, 30, 12));
		rectImageBox.getChildren().add(rectImage); 
		rectImageBox.setAlignment(Pos.CENTER);
		
		// Creates a play button and adds it to a HBox
		Image playImage = new Image(getClass().getClassLoader().getResource("resources/PLAY.png").toString()); 
		play = new Button("", new ImageView(playImage)); 
		HBox playBox = new HBox(); 
		playBox.getChildren().add(play); 
		playBox.setAlignment(Pos.CENTER);

		// Starts game when button is clicked
		ButtonHandler bh = new ButtonHandler();
		play.setOnAction(bh);
		
		// Adds 2 HBoxes to a VBox, and sets VBox center of the root
		VBox menu = new VBox(); 
		menu.getChildren().addAll(rectImageBox, playBox); 
		menu.setAlignment(Pos.CENTER);
		menu.setStyle("-fx-background-color: black");
		root.setCenter(menu);

		// Creates a scene with the root
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);

		primaryStage.show();	

	}
	
	private class ButtonHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent event) {
			if(event.getSource() == play) {
				// Creates a Room object and sets it as center of root
				roomManager = new RoomManager();
				roomManager.setPrefSize(800,600);
				roomManager.setMaxHeight(550);
				roomManager.setMaxWidth(700);
				root.setCenter(roomManager);
				
				// Creates player and sets its coordinates
				player = new Player();
				player.setX(350);
				player.setY(275);
				 
				// Adds player to the world and calls start on the world
				roomManager.getChildren().addAll(player);
				roomManager.start();
				
				//Displays the player's health 
				player.displayHealth();
				
				// Keyboard input
				KeyboardHandler kh = new KeyboardHandler();
				roomManager.setOnKeyPressed(kh);
				roomManager.setOnKeyReleased(kh);
				roomManager.requestFocus();
			}
		}
	}

	class KeyboardHandler implements EventHandler<KeyEvent> {
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

}

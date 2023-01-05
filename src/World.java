
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import javafx.scene.layout.HBox;

import javafx.scene.layout.BorderPane;

public abstract class World extends BorderPane{

	/**
	 * HashSet of keys that are down 
	 */
	Set<KeyCode> keysDown = new HashSet<KeyCode>();
	private AnimationTimer timer;
	
	/**
	 * default constructor for World
	 */
	public World() {		
		timer = new MyAnimationTimer();
		
	}
	
	class MyAnimationTimer extends AnimationTimer{
		@Override
		public void handle(long now) {
			act(now);
			ObservableList<Node> list = getChildren();
			for(int i = list.size()-1; i>=0 ; i--) {
				Node n = list.get(i);
				if(n instanceof Actor) {
					((Actor) n).act(now); 
				}
			}
			
		}
		
	}
	
	/**
	 * abstract method that makes all the subclasses have an act method for whatever task they do
	 * @param now
	 */
	public abstract void act(long now);
	
	/**
	 * method that adds an actor to the world
	 * @param actor that is added to world
	 */
	public void add(Actor actor) {
		getChildren().add(actor);
	}
	
	/**
	 * method that adds an HBox to the world
	 * @param HBox that is added to world
	 */
	public void add(HBox hbox) {
		getChildren().add(hbox); 
	}
	
	/**
	 * method that removes an actor from the world
	 * @param actor that is removed from the world
	 */
	public void remove(Actor actor) {
		getChildren().remove(actor);
	}
	
	/**
	 * method that starts the timer for the world
	 */
	public void start() {
		timer.start();
	}
	
	/**
	 * method that stops the timer for the world
	 */
	public void stop() {
		timer.stop();
	}
	
	/**
	 * method that gets the list of objects of a certain class from world
	 * @param specified class of objects that is to be returned
	 * @return the list of the specified objects from world
	 */
	public <A extends Actor> java.util.List<A> getObjects(java.lang.Class<A> cls){
		ArrayList<A> list = new ArrayList<>();
		for(Node n : getChildren()) {
			if(cls.isInstance(n)) { 
				list.add(cls.cast(n));
			}
		}
		return list;
		
	}
	
	/**
	 * method that gets the player
	 * @return the Player
	 */
	public Player getPlayer(){
		for(Node n : getChildren()){
			if(n != this && n.getClass().equals(Player.class)){
				return Player.class.cast(n);
			}
		}
		return null;
	}
	
	/**
	 * method that adds the key to the keysDown HashSet
	 * @param the key code
	 */
	public void addKeyCode(KeyCode k) {
		keysDown.add(k); 
	}
	
	/**
	 * method that removes the key from the keysDown HashSet
	 * @param the key code
	 */
	public void removeKeyCode(KeyCode k) {
		keysDown.remove(k);
	}
	
	/**
	 * method that returns whether the key is down or not 
	 * @param the key code
	 * @return boolean of whether the key is down or not
	 */
	public boolean isKeyDown(KeyCode k) {
		return keysDown.contains(k);
	}
	
}



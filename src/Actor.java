
import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

public abstract class Actor extends ImageView{
	
	/**
	 * abstract method that is implemented by all subclasses of Actor
	 * @param now
	 */
	public abstract void act(long now);
	
	/**
	 * moves the actor dx pixels down and dy pixels right
	 * @param the double representing the number of pixels by which the actor moves down
	 * @param the double representing the number of pixels by which the actor moves right
	 */
	public void move(double dx, double dy) {
		setX(getX()+dx);
		setY(getY()+dy);
	}
	
	/**
	 * method that returns the world within which the actor exists
	 * @return the World within which the actor exists
	 */
	public World getWorld() {
		return (World) getParent();
		
	}
	
	/**
	 * method that gives the height of the actor
	 * @return the height of the actor as a double
	 */
	public double getHeight() {
		return getBoundsInParent().getHeight();
	}
	
	/**
	 * method that gives the width of the actor
	 * @return the width of the actor as a double
	 */
	public double getWidth() {
		return getBoundsInParent().getWidth();		
	}
	
	/**
	 * method that returns a list of actors of a specified class intersecting the actor
	 * @param the class which is to be found intersecting the actor
	 * @return the list of objects of the specified class interesting the actor
	 */
	public <A extends Actor> java.util.List<A> getIntersectingObjects(java.lang.Class<A> cls){
		ArrayList<A> list = new ArrayList<>();
		for(Node n : getWorld().getChildren()){
			if(n != this && cls.isInstance(n) && n.intersects(this.getBoundsInParent()) ){/*n.getClass().getName().equals(cls.getName()) && n.intersects(this.getBoundsInParent())) {*/
				list.add(cls.cast(n));
			}
		}
		return list;	
	}
	
	/**
	 * method that returns the first actor of a specified class intersecting the actor
	 * @param the class which is to be found intersecting the actor
	 * @return the first actor of a specified class intersecting the actor
	 */
	public <A extends Actor> A getOneIntersectingObject(java.lang.Class<A> cls) {
		for(Node n : getWorld().getChildren()){
			if(n != this) {
				//System.out.println("not this");
				//System.out.println(cls.getName());
				if(cls.isInstance(n)) { 
					//System.out.println("correct class");
					if(n.intersects(this.getBoundsInParent())) {
						//System.out.println("intersected");
						return cls.cast(n);
					}
				}				
			}
		}
		return null;		
	}
	
	

}

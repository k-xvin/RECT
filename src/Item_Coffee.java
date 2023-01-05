import javafx.scene.image.Image;

public class Item_Coffee extends Item {
	
	/**
	 * default constructor
	 */
	public Item_Coffee() {
		setName("Coffee");
		setDesc("SPEED");
		setImage(new Image(getClass().getClassLoader().getResource("resources/coffee.png").toString()));
	}

	@Override
	public void use() {
		Player p = getWorld().getPlayer(); 		
		p.increaseFireRate(100);
		p.increaseMoveSpeed(2);
		p.increaseShotSpeed(2);
	}

}

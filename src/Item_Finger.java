import javafx.scene.image.Image;

public class Item_Finger extends Item {
	
	/**
	 * default constructor
	 */
	public Item_Finger() {
		setName("Itchy Finger");
		setDesc("Faster shooting.");
		setImage(new Image(getClass().getClassLoader().getResource("resources/finger.png").toString()));
	}

	@Override
	public void use() {
		Player p = getWorld().getPlayer(); 		
		p.increaseFireRate(250);
		
	}

}

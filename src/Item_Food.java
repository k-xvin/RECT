import javafx.scene.image.Image;

public class Item_Food extends Item{
	
	/**
	 * default constructor
	 */
	public Item_Food() {
		setName("");
		setDesc("");
		setImage(new Image(getClass().getClassLoader().getResource("resources/heart.png").toString()));
	}

	@Override
	public void use() {
		Player p = getWorld().getPlayer(); 
		p.setCurrentHealth(p.getCurrentHealth() + 1); 

	}
	
	
}

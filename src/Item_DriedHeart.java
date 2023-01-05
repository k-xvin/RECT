import javafx.scene.image.Image;

public class Item_DriedHeart extends Item{
	
	/**
	 * default constructor
	 */
	public Item_DriedHeart() {
		setName("Dried Heart");
		setDesc("Max Health + 1, Full health.");
		setImage(new Image(getClass().getClassLoader().getResource("resources/driedHeart.png").toString()));
	}

	@Override
	public void use() {
		Player p = getWorld().getPlayer(); 
		p.setMaxHealth(p.getMaxHealth() + 1);
		p.setCurrentHealth(p.getMaxHealth());
	}

}

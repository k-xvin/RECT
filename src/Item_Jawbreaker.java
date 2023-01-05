import javafx.scene.image.Image;

public class Item_Jawbreaker extends Item{
	
	/**
	 * default constructor
	 */
	public Item_Jawbreaker() {
		setName("Jawbreaker");
		setDesc("It hurts.");
		setImage(new Image(getClass().getClassLoader().getResource("resources/jawbreakerItem.png").toString()));
	}

	@Override
	public void use() {
		getWorld().getPlayer().increaseDamage(20);
		getWorld().getPlayer().increaseFireRate(-250);
	}

}

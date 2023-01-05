
import javafx.scene.image.Image;

public class Item_PartyHat extends Item{

	/**
	 * default constructor
	 */
	public Item_PartyHat() {
		setName("Party Hat");
		setDesc("Bouncy shots!");
		setImage(new Image(getClass().getClassLoader().getResource("resources/partyhat.png").toString()));
	}

	@Override
	public void use() {
		//modifies projectile behavior to bounce
		//added inside projectile
		Player p = getWorld().getPlayer(); 		
		p.increaseRange(100);
	}
}

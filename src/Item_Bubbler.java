import javafx.scene.image.Image;

public class Item_Bubbler extends Item {

	/**
	 * default constructor
	 */
	public Item_Bubbler() {
		setName("Bubbler");
		setDesc("Firerate+++, Damage---");
		setImage(new Image(getClass().getClassLoader().getResource("resources/bubbler.png").toString()));
	}
	
	@Override
	public void use() {
		Player p = getWorld().getPlayer(); 		
		p.increaseFireRate(1000);
		//changes shot pattern inside projectile class
	}

}

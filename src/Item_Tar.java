import javafx.scene.image.Image;

public class Item_Tar extends Item {
	
	/**
	 * default constructor
	 */
	public Item_Tar() {
		setName("Tar");
		setDesc("Max Health + 2, Speed-");
		setImage(new Image(getClass().getClassLoader().getResource("resources/tar.png").toString()));
	}

	@Override
	public void use() {
		
		Player p = getWorld().getPlayer(); 
		p.setMaxHealth(p.getMaxHealth() + 2);
		p.increaseMoveSpeed(-1);

	}

}

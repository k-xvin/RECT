import javafx.scene.image.Image;

public class Item_Popsicle extends Item {

	/**
	 * default constructor
	 */
	public Item_Popsicle(){
		setName("Popsicle");
		setDesc("All stats up!");
		setImage(new Image(getClass().getClassLoader().getResource("resources/popsicle.png").toString()));
	}
	
	@Override
	public void use() {
		Player p = getWorld().getPlayer(); 		
		p.increaseFireRate(100);
		p.increaseMoveSpeed(1);
		p.increaseShotSpeed(1);
		p.increaseDamage(2);
		p.increaseRange(100);
		p.setMaxHealth(p.getMaxHealth()+1);
		p.setCurrentHealth(p.getCurrentHealth()+1);
	}

}

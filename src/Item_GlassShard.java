import javafx.scene.image.Image;

public class Item_GlassShard extends Item {
	
	/**
	 * default constructor
	 */
	public Item_GlassShard() {
		setName("Glass Shard");
		setDesc("Ow.");
		setImage(new Image(getClass().getClassLoader().getResource("resources/glassShardV2.png").toString()));
	}

	@Override
	public void use() {
		//chance to shoot a glass projectile that deals x3 dmg
	}

}

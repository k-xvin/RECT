import javafx.scene.image.Image;

public class Item_MetalPlating extends Item{
	
	/**
	 * default constructor
	 */
	public Item_MetalPlating() {
		setName("Metal Plates");
		setDesc("Chance to ignore damage.");
		setImage(new Image(getClass().getClassLoader().getResource("resources/metalPlatingV2.png").toString()));
	}

	@Override
	public void use() {
		
	}

}

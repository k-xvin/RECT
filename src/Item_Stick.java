import javafx.scene.image.Image;

public class Item_Stick extends Item{
	
	/**
	 * default constructor
	 */
	public Item_Stick() {
		setName("Pointy Stick");
		setDesc("Shots pierce enemies.");
		setImage(new Image(getClass().getClassLoader().getResource("resources/stick.png").toString()));
	}

	@Override
	public void use() {
		
	}

}

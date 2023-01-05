import javafx.scene.image.Image;

public class Item_CrossbeamFocus extends Item {

	/**
	 * default constructor
	 */
	public Item_CrossbeamFocus() {
		setName("Crossbeam Focus");
		setDesc("+ Shot");
		setImage(new Image(getClass().getClassLoader().getResource("resources/crossBeamFocus.png").toString()));
	}

	@Override
	public void use() {
		// modifies player shooting to be a +
		// added inside player

	}

}

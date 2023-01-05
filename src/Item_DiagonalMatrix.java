import javafx.scene.image.Image;

public class Item_DiagonalMatrix extends Item{
	
	/**
	 * default constructor
	 */
	public Item_DiagonalMatrix() {
		setName("Diagonal Matrix");
		setDesc("X Shot");
		setImage(new Image(getClass().getClassLoader().getResource("resources/diagonalMatrix.png").toString()));
	}

	@Override
	public void use() {
		//modifies player shooting to be diagonal
		//added inside player
	}

}

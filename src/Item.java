
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public abstract class Item extends Actor {

	/**
	 * String of the name of the item
	 */
	private String name;
	/**
	 * String of the description of the item
	 */
	private String desc;

	/**
	 * default constructor for the item
	 */
	public Item() {

	}

	@Override
	public void act(long now) {
		if (getOneIntersectingObject(Player.class) != null) {
			displayPickup();
			use();

			// item is added into the player's inventory (unless it's food)
			if (this.getClass() != Item_Food.class) {
				getWorld().getPlayer().addInv(this);
			}
			else {
				getWorld().getPlayer().setItemAdded(true);
				getWorld().getPlayer().setItem(this); 
			}

			// setX(getWorld().getPlayer().getX());
			// setY(getWorld().getPlayer().getY() + 40);

			getWorld().getPlayer().displayItems();
			getWorld().remove(this); // remove item from world
		}

	}

	/**
	 * method that displays the name and description of the item on the screen
	 */
	public void displayPickup() {
		Text name1 = new Text(name);
		name1.setFont(new Font(50));

		Text desc1 = new Text(desc);
		desc1.setFont(new Font(20));

		getWorld().getChildren().addAll(name1, desc1);

		TranslateTransition nameSlideIn = new TranslateTransition(Duration.millis(500), name1);
		nameSlideIn.setFromY(200);
		nameSlideIn.setFromX(-300);
		nameSlideIn.setToX(350 - (name1.getBoundsInParent().getWidth() / 2));
		// nameSlideIn.play();

		TranslateTransition descSlideIn = new TranslateTransition(Duration.millis(500), desc1);
		descSlideIn.setFromY(200 + name1.getBoundsInParent().getHeight() / 2);
		descSlideIn.setFromX(900);
		descSlideIn.setToX(350 - (desc1.getBoundsInParent().getWidth() / 2));
		// descSlideIn.play();

		PauseTransition pause = new PauseTransition(Duration.millis(1000));

		TranslateTransition nameSlideOut = new TranslateTransition(Duration.millis(500), name1);
		nameSlideOut.setFromY(200);
		nameSlideOut.setFromX(350 - (name1.getBoundsInParent().getWidth() / 2));
		nameSlideOut.setToX(900);
		// nameSlideIn.play();

		TranslateTransition descSlideOut = new TranslateTransition(Duration.millis(500), desc1);
		descSlideOut.setFromY(200 + name1.getBoundsInParent().getHeight() / 2);
		descSlideOut.setFromX(350 - (desc1.getBoundsInParent().getWidth() / 2));
		descSlideOut.setToX(-300);
		// descSlideIn.play();

		SequentialTransition display = new SequentialTransition(nameSlideIn, descSlideIn, pause, nameSlideOut,
				descSlideOut);
		display.play();
	}

	/**
	 * method that sets the description
	 * 
	 * @param String
	 *            of the description of the item
	 */
	public void setDesc(String d) {
		desc = d;
	}

	/**
	 * method that sets the name
	 * 
	 * @param String
	 *            of the name of the item
	 */
	public void setName(String n) {
		name = n;
	}

	/**
	 * method that gives the name of the item
	 * 
	 * @return String of the name of the item
	 */
	public String getName() {
		return name;
	}

	/**
	 * method implemented by subclasses of item that allow the player to have
	 * certain abilities and upgrades (statistical upgrade, bouncy shots, etc.)
	 */
	public abstract void use();

}

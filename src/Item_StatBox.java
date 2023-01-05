import java.util.Random;

import javafx.scene.image.Image;

public class Item_StatBox extends Item {
	
	// 0 is damage
	// 1 is fireRate
	// 2 is moveSpeed
	// 3 is range
	// 4 is shotSpeed
	private int stat1;
	private int stat2;

	/**
	 * default constructor
	 */
	public Item_StatBox() {
		setName("Stat Box");
		setImage(new Image(getClass().getClassLoader().getResource("resources/box.png").toString()));
		
		// determine which two stats to increase (two different stats)
		Random ran = new Random();
		stat1 = ran.nextInt(4);
		stat2 = ran.nextInt(4);
		while (stat2 == stat1)
			stat2 = ran.nextInt(4);
		
		setDesc(statType(stat1) + " " + statType(stat2));
	}

	@Override
	public void use() {

		Player p = getWorld().getPlayer();

		// increases the stats depending on which numbers were generated
		if (stat1 == 0 || stat2 == 0) {
			//System.out.println("dmg");
			p.increaseDamage(2);
		}
		if (stat1 == 1 || stat2 == 1) {
			//System.out.println("fire");
			p.increaseFireRate(50);
		}
		if (stat1 == 2 || stat2 == 2) {
			//System.out.println("speed");
			p.increaseMoveSpeed(0.5);
		}
		if (stat1 == 3 || stat2 == 3) {
			//System.out.println("range");
			p.increaseRange(25);
		}
		if (stat1 == 4 || stat2 == 4) {
			//System.out.println("shotSpeed");
			p.increaseShotSpeed(2);
		}

	}
	
	/**
	 * method that returns what the stat box does
	 * @param num
	 * @return string of what the stat box does
	 */
	private String statType(int num){
		if (num == 0) {
			return "Damage+";
		}
		if (num == 1) {
			return "Firerate+";
		}
		if (num == 2) {
			return "Speed+";
		}
		if (num == 3) {
			return "Range+";
		}
		if (num == 4) {
			return "Shotspeed+";
		}
		return "";
	}

}

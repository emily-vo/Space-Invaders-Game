
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tank extends GameObj {
	public static final String img_file = "TankImage.png";
	public static final int SIZE = 20;
	//initial position
	public static final int INIT_X = 0;
	public static final int INIT_Y = 0;
	//initial velocities
	public static final int INIT_VEL_X = 0;
	public static final int INIT_VEL_Y = 0;
	//player health
	private int health;
	private static BufferedImage img;

	public Tank(int courtWidth, int courtHeight) {
		super(INIT_VEL_X, INIT_VEL_Y, courtWidth / 2, 
				courtHeight, SIZE, SIZE, courtWidth,
				courtHeight);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
		health = 10;
	}
	/**
	 * Allows game court to control the player health
	 * @return
	 */
	public int getHealth() {
		return health;
	}
	public void decHealth() {
		health--;
	}
	public void resetHealth() {
		health = 10;
	}
	@Override
	public void draw(Graphics g) {
		g.drawImage(img, pos_x, pos_y, width, height, null);
	}

}

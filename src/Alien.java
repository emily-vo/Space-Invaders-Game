
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A basic game object displayed as a yellow circle, starting in the upper left
 * corner of the game court.
 * 
 */
public class Alien extends GameObj {
	public static final int SIZE = 20; //Size of alien
	public static int width; //alien width
	public static int height;//alien height
	public static int VEL_MAG = 5; //magnitude of alien velocity
	public static final int INIT_VEL_X = VEL_MAG; //initial vel-x
	public static final int INIT_VEL_Y = 0; // initial vel-y
	private static final int DESCENT_LENGTH = 5; //vertical distance traveled
	private int delta = 0; //vertical steps taken each bounce
	private int lastX = 0; //of previous x-vel
	private int descentNum; //num of descents, used for row creation
	private int HEALTH; //Health of the alien
	private boolean DIVE = false;
	//Used for animation
	public static int ALIEN_TICK = 0; //increments with each tick
	
	public Alien(int x, int y, int courtWidth, int courtHeight) {
		super(INIT_VEL_X, INIT_VEL_Y, x, y, SIZE, SIZE,
				courtWidth, courtHeight);
		
		width = super.width;
		height = super.height;
		descentNum = 0;
		HEALTH = 2;
	}
	/** Update the velocity of the object in response to hitting
	 *  an obstacle in the given direction. If the direction is
	 *  null, this method has no effect on the object. */
	//will change only if alien is not in dive mode
	public void bounce(Direction d) {
		if (d == null) return;
		
		switch (d) {
		case UP:    v_y = 0;  v_x = -lastX; break;  
		case DOWN:  v_y = 0; v_x = -lastX; break;
		case LEFT:  lastX = v_x; v_x = 0; v_y = VEL_MAG; break;
		case RIGHT: lastX = v_x; v_x = 0; v_y = VEL_MAG; break;
		}
	}
	//Keeps track of whether or not alien will dive
	public boolean getDive() {
		return DIVE;
		
	}
	public void setDive(boolean set) {
		DIVE = set;
	}
	public void move() {
		super.move();
		//Keeps track of vertical movement if vy is non zero
		if(v_y != 0) { delta++;}
		if(delta > DESCENT_LENGTH) {
			delta = 0;
			descentNum++;
			bounce(Direction.UP);
		}
	}
	public int getNumDescents () {
		return descentNum;
	}
	//Allows a user to affect the health of an alien
	public int getHealth () {
		return this.HEALTH;
	}
	public void decHealth () {
		this.HEALTH--;
	}
	@Override
	public void draw(Graphics g) {
		//Alien appearance changes based on health
		String img_file;
		BufferedImage img = null;
		if(this.HEALTH == 2) {
			if(ALIEN_TICK < 10) {
				img_file = "alien.gif";
			}
			else if (ALIEN_TICK < 20){
				img_file = "alienAN.png";
				//System.out.println("Tick cleared");
			}
			else {
				ALIEN_TICK = 0;
				img_file = "alien.gif";
			}
			
		}
		else {
			img_file = "alien2.png";
		}
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
		g.drawImage(img, pos_x, pos_y, width, height, null);
	}

}
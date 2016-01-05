import java.awt.Color;
import java.awt.Graphics;

public class Bullet extends GameObj {
	public static final int SIZE = 5;
	//initial position
	public static final int INIT_X = 130;
	public static final int INIT_Y = 130;
	//initial velocity
	public static final int INIT_VEL_X = 0;
	public static final int INIT_VEL_Y = -5;

	public Bullet(int x, int y, int courtWidth, int courtHeight) {
		super(INIT_VEL_X, INIT_VEL_Y, x, y, SIZE, SIZE, courtWidth,
				courtHeight);
	}

	@Override
	public void draw(Graphics g) {
		//Random colored bullet
		int r = (int) (Math.random() * 255);
		int gr = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
		g.setColor(new Color(r, gr, b));
		g.fillRect(super.pos_x, super.pos_y, SIZE, SIZE);
	}
}

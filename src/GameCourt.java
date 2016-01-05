import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 * 
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	// GAME STATE
	private Tank player; //The tank that the player controls
	public static boolean MULTIPLAYER = false;
	private Tank player2;
	private LinkedList<GameObj> aliens = 
			new LinkedList<GameObj>(); //List of aliens in play
	private LinkedList<GameObj> bullets = 
			new LinkedList<GameObj>(); //bullets fired
	public boolean playing = false; // whether the game is running
	private JLabel status; // Current status text (i.e. Running...)
	public JLabel SCORE_DISPLAY = new JLabel("Score: 0");
	public JLabel HEALTH_DISPLAY = new JLabel("Health");
	public static int NUM_LIVES = 3; // Current number of lives
	public static int ALIENS_CREATED = 0; 	//aliens created so far
	public static int ALIENS_KILLED = 0;   //number of conquered aliens
	public static int TANK_HEALTH = 4; //Current tank health
	public static int BULLETS_LEFT; //finite number of bullets to shoot
	public static int SCORE1 = 0;
	public static int SCORE2 = 0;
	// Game constants
	public static final int COURT_WIDTH = 800;
	public static final int COURT_HEIGHT = 600;
	public static final int player_VELOCITY = 4;
	public static final int player2_VELOCITY = 4;
	public static final int LIVES_INIT = 3; // Number of lives at beginning
	public static final int ALIENS_PER_ROW = 15;  //Number of aliens per row
	public static final int ALIENS_TO_KILL = 100; //aliens to be killed/round
	public static final int TANK_HEALTH_INIT = 4;
	// Update interval for timer, in milliseconds
	public static final int INTERVAL = 35;
	public static boolean END = false;
	public static String COURT_NAME = "";
	
	public GameCourt(JLabel status) {
		BULLETS_LEFT = (int) (ALIENS_TO_KILL * 1.25);
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// The timer is an object which triggers an action periodically
		// with the given INTERVAL. One registers an ActionListener with
		// this timer, whose actionPerformed() method will be called
		// each time the timer triggers. We define a helper method
		// called tick() that actually does everything that should
		// be done in a single timestep.
		Timer timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer.start(); // MAKE SURE TO START THE TIMER!

		// Enable keyboard focus on the court area.
		// When this component has the keyboard focus, key
		// events will be handled by its key listener.
		setFocusable(true);

		//KEY LISTENER HERE
		// This key listener allows the player to move as long
		// as an arrow key is pressed, by changing the player's
		// velocity accordingly. (The tick method below actually
		// moves the player.)
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					player.v_x = -player_VELOCITY;
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					player.v_x = player_VELOCITY;
				//Sends a bullet from the location of the player if
				//space is pressed
				else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					bullets.add(new Bullet(player.pos_x + 
							player.width / 2, 
							player.pos_y, 
							COURT_WIDTH, COURT_HEIGHT)); 
				}
				else if (e.getKeyCode() == KeyEvent.VK_P)
					playing = !playing;
				if(MULTIPLAYER) {
					if (e.getKeyCode() == KeyEvent.VK_A)
						player2.v_x = -player2_VELOCITY;
					else if (e.getKeyCode() == KeyEvent.VK_D)
						player2.v_x = player2_VELOCITY;
					//Sends a bullet from the location of the player if
					//space is pressed
					else if (e.getKeyCode() == KeyEvent.VK_W) {
						bullets.add(new Bullet(player2.pos_x + 
								player2.width / 2, 
								player2.pos_y, 
								COURT_WIDTH, COURT_HEIGHT)); 
					}
				}
				
					
			}
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_LEFT 
						|| e.getKeyCode() == KeyEvent.VK_RIGHT) player.v_x = 0;
				if(MULTIPLAYER) {
					if(e.getKeyCode() == KeyEvent.VK_A
							|| e.getKeyCode() == KeyEvent.VK_D)
						player2.v_x = 0;
				}
			}
		});

		this.status = status;
		
	}

	/**
	 * (Re-)set the game to its initial state.
	 */
	public void reset() {
			player = new Tank(COURT_WIDTH, COURT_HEIGHT);
		if(MULTIPLAYER) {
			player = new Tank(COURT_WIDTH - 2 * player.width, COURT_HEIGHT);
			player2 = new Tank(COURT_WIDTH + 2 * player.width, COURT_HEIGHT);
		}
		aliens = new LinkedList<GameObj>();
		bullets = new LinkedList<GameObj>();
		
		ALIENS_CREATED = 0;
		ALIENS_KILLED = 0;
        NUM_LIVES = LIVES_INIT;
        TANK_HEALTH = TANK_HEALTH_INIT;
        SCORE1 = 0;
        SCORE2 = 0;
        BULLETS_LEFT = (int) (1.25 * ALIENS_TO_KILL);
		playing = false;
		END = false;
		
		//Displays current number of lives and aliens left to kill
		status.setText("Lives left: " + NUM_LIVES
				+ "\n" + 
				"Aliens left: " + (ALIENS_TO_KILL - ALIENS_KILLED));

		requestFocusInWindow();
		

	}

	/**
	 * This method is called every time the timer defined in the constructor
	 * triggers.
	 */
	void tick() {
		//Makes sure that game does not continue after loss/win
		if (playing) {
			if(END) {
				reset();
			}
		
		//Changes alien speed randomly
		//boolean speedChange = (int) (Math.random () * 100) < 5;
		//if(speedChange) Alien.VEL_MAG = (int) (Math.random() * 3);
			
		//***********************************************************//	
		//THIS SECTION DEALS ALIEN ROW CREATION
		//***********************************************************//
			//Creates aliens in rows, keeps track of number of aliens
			//Makes sure that the number of aliens created over round is capped
			if(!aliens.isEmpty()) {
				//Checks if the aliens have descended before new row is created
				if(((Alien) aliens.getLast()).getNumDescents () == 2) {
					for(int y = 0; y < ALIENS_PER_ROW; y++) {
						if(ALIENS_CREATED < ALIENS_TO_KILL) {
							aliens.add(new Alien(y * 2 * Alien.width, 
									0, COURT_WIDTH, COURT_HEIGHT));
							ALIENS_CREATED++;
						}
					}
				}
			}
			else {
				for(int y = 0; y < ALIENS_PER_ROW; y++) {
					if(ALIENS_CREATED < ALIENS_TO_KILL) {
						aliens.add(new Alien(y * 2 * Alien.width, 
								0, COURT_WIDTH, COURT_HEIGHT));
						ALIENS_CREATED++;
					}
				}
			}
			// advance the player 
			player.move();
			if(MULTIPLAYER) player2.move();
			
			// displays state at bottom of the window
			status.setText("Lives left: " + NUM_LIVES
					+ "\n" + 
					"Aliens left: " + (ALIENS_TO_KILL - ALIENS_KILLED)
					+ "\n" + "\nBullets left: " + BULLETS_LEFT );
		//***********************************************************//	
		//THIS SECTION DEALS WITH ALIEN-BULLETS INTERACTION
		//***********************************************************//
		int x_loc = 0;
		int y_loc = 0;
		if(aliens != null) {
			if(((Alien) aliens.getLast()).getNumDescents () % 2 == 1) {
				//Five percent change that an alien will shoot
				boolean shoot = (int) (Math.random () * 100) < 5;
				//random alien in list will shoot
				int index = (int) (Math.random() * aliens.size() - 1);
				if(index < 0) 
					index = 0;
				
				x_loc = aliens.get(index).pos_x;
				y_loc = aliens.get(index).pos_y;
				if(shoot) {
					Bullet bulletShot = new Bullet(x_loc, 
							y_loc, 
							COURT_WIDTH, COURT_HEIGHT); 
					bulletShot.v_y = (int) (.01 * (player.pos_y - 
							aliens.get(index).pos_y));
					bulletShot.v_x = (int) (.01 * (player.pos_x - 
							aliens.get(index).pos_x));
					if(MULTIPLAYER) {
						boolean player2hit = (int) (Math.random() * 100) < 50;
						if(player2hit) {
							bulletShot.v_y = (int) 
									(.01 * (player2.pos_y - 
											aliens.get(index).pos_y));
							bulletShot.v_x = (int) 
									(.01 * (player2.pos_x - 
											aliens.get(index).pos_x));
						}
					}
					bullets.add(bulletShot);
				}
			}
		}
		
		//***********************************************************//	
		//THIS SECTION DEALS WITH ALIEN AND PLAYER-BULLET INTERACTION
		//***********************************************************//
			//List to keep track of bullets to be removed
			LinkedList<GameObj> bulletsUsed = new LinkedList<GameObj>();
			//List to keep track of aliens to remove
			LinkedList<GameObj> deadAliens = new LinkedList<GameObj>();
			
			
			//Moves each alien, if alien is hit with bullet from 
			//Spaceship, alien is destroyed
			Iterator<GameObj> i = aliens.iterator();
			while(i.hasNext()) {
				Alien thisAlien = (Alien) i.next();
				thisAlien.move();
				//Turns alien if they hit the wall
				thisAlien.bounce(thisAlien.hitWall());
				
				//Alien collision causes player to lose life
				//Player loses game if number of lives is 0
				if(thisAlien.willIntersect(player)) {
					NUM_LIVES--;
					player = new Tank(COURT_WIDTH, COURT_HEIGHT);
					deadAliens.add(thisAlien);
					
					if(NUM_LIVES <= 0) {
						status.setText("You lose!");
					    END = true;
						playing = false;
						
					}
				}
				if(MULTIPLAYER) {
					if(thisAlien.willIntersect(player2)) {
						NUM_LIVES--;
						player2 = new Tank(COURT_WIDTH, COURT_HEIGHT);
						deadAliens.add(thisAlien);
						
						if(NUM_LIVES <= 0) {
							status.setText("You lose!");
						    END = true;
							playing = false;
							
						}
					}
				}
				//checks all existing bullets against this alien
				//to see if they collide
				Iterator<GameObj> it2 = bullets.iterator();
				while(it2.hasNext()) {
					Bullet thisBullet = (Bullet) it2.next();
					//Kills alien if bullet is traveling upwards
					//indicating that bullet is from player
					if(thisAlien.willIntersect(thisBullet) 
							&& thisBullet.v_y < 0) {
						if(MULTIPLAYER) {
							if(thisAlien.getHealth() == 2) {
								thisAlien.decHealth();
							}
							else {
								deadAliens.add(thisAlien);
								ALIENS_KILLED++;
								SCORE1++;
							}
						}
						else {
							if(!deadAliens.contains(thisAlien)) {
								deadAliens.add(thisAlien);
								ALIENS_KILLED++;
								SCORE1++;
							}
						}
						
						//Bullet is used if alien is hit
						bulletsUsed.add(thisBullet);
					}
				}
			}

			Iterator<GameObj> i2 = bullets.iterator();

			//if bullet hits the wall, then bullet is to be removed
			while(i2.hasNext()) {
				Bullet thisBullet = (Bullet) i2.next();
				thisBullet.move();
				if(thisBullet.hitWall() != null) {
					bulletsUsed.add(thisBullet);
				}
				if(thisBullet.willIntersect(player)) {
					if(player.getHealth() > 1) {
						player.decHealth();
						bulletsUsed.add(thisBullet);
					}
					else {
						NUM_LIVES--;
						player.resetHealth();
						bulletsUsed.add(thisBullet);
					}
				}
				if(MULTIPLAYER) {
					if(thisBullet.willIntersect(player2)) {
						if(player2.getHealth() > 1) {
							player2.decHealth();
							bulletsUsed.add(thisBullet);
						}
						else {
							NUM_LIVES--;
							player2.resetHealth();
							bulletsUsed.add(thisBullet);
						}
					}
				}
				
			}
		
			// Removes dead alien and used bullets
			removeList(deadAliens, aliens);
			removeList(bulletsUsed, bullets);
			
			
			// Wins game if all aliens are killed
			if(ALIENS_KILLED == ALIENS_TO_KILL) {
				status.setText("You won!");
				SCORE1 += 5 * BULLETS_LEFT;
				END = true;
				playing = false;
			}
			//Loses game if bullets are used up
			if(BULLETS_LEFT <= 0 && aliens.size() != 0) {
				status.setText("You lost!");
				END = true;
				playing = false;
			}

			repaint();
			
		}
		SCORE_DISPLAY.setText("Score: " + SCORE1);	
		HEALTH_DISPLAY.setText("  Health: " + player.getHealth());
		if(MULTIPLAYER) {
			HEALTH_DISPLAY.setText("  Health p1: " + player.getHealth() + 
					"   Health p2: " + player2.getHealth());
		}
		repaint();
		Alien.ALIEN_TICK++;
		
	}
	@Override
	/**
	 * Repaints all things in field
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Paints game piece if in game
		if(playing) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, COURT_WIDTH, COURT_HEIGHT);
			paintList(g, aliens);
			paintList(g, bullets);
			
			player.draw(g);
			if(MULTIPLAYER) player2.draw(g);
		} 
		//Displays title screen 
		else {
			String img_file = "title.jpg";
			BufferedImage img = null;
			try {
				if (img == null) {
					img = ImageIO.read(new File(img_file));
				}
			} catch (IOException e) {
				System.out.println("Internal Error:" + e.getMessage());
			}
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, COURT_WIDTH, COURT_HEIGHT);
			g.drawImage(img, 75, 175, img.getWidth()/3, img.getHeight()/3, null);
			g.setColor(Color.WHITE);
			g.drawString("Space Invaders", COURT_WIDTH/2, COURT_HEIGHT/2);
			
			if(END) {
				g.drawString("Press P to reset", 0, COURT_HEIGHT - 25);
			}
			else {
				g.drawString("  Press P to play", 0, COURT_HEIGHT - 25);
			}
		}
		
	}
	/**
	 * Paints all components in a list
	 * @param g
	 * @param objs
	 */
	public void paintList (Graphics g, LinkedList<GameObj> objs) {
		Iterator<GameObj> i = objs.iterator();
		while(i.hasNext()) {
			i.next().draw(g);
		}
	}
	/**
	 * Removes all used/dead objects from field
	 * @param deadObjs
	 * @param objs
	 */
	public void removeList 
	(LinkedList<GameObj> deadObjs, LinkedList<GameObj> objs) {
		Iterator<GameObj> i = deadObjs.iterator();
		while(i.hasNext()) {
			Object thisObject = i.next();
			objs.remove(thisObject);
			if(thisObject instanceof Bullet) BULLETS_LEFT--;
		}
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}
}

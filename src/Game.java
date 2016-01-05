
// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	public static SortedMap<Integer, String> highscoresList 
	= new TreeMap<Integer, String>();
	public void run() {
		// NOTE : recall that the 'final' keyword notes inmutability
		// even for local variables.

		// Top-level frame in which game components live
		// Be sure to change "TOP LEVEL FRAME" to the name of your game
		final JFrame frame = new JFrame("SPACE INVADERS");
		frame.setLocation(300, 300);

		// Status panel
		final JPanel status_panel = new JPanel();
		frame.add(status_panel, BorderLayout.SOUTH);
		final JLabel status = new JLabel("Running...");
		status_panel.add(status);

		// Main playing area
		final GameCourt court = new GameCourt(status);
		frame.add(court, BorderLayout.CENTER);

		//control panel
		final JPanel control_panel = new JPanel();
		frame.add(control_panel, BorderLayout.NORTH);


		final JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.reset();
			}
		});
		final JCheckBox multiplayer = new JCheckBox("multiplayer", false);
		multiplayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameCourt.MULTIPLAYER = !GameCourt.MULTIPLAYER;
				court.reset();
			}
		});
		
		final JButton instructions = new JButton("Instructions");
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.reset();
				JOptionPane.showMessageDialog
				(frame, "Welcome to Space Invaders! \n"
				+ "\nOne player: Use LEFT and RIGHT to control tank. "
				+ "\nPress SPACE to shoot. "
				+ "\nYou have limited health, lives, and bullets, so be careful! "
				+ "\nYou will lose the game when you lose all lives and bullets. "
				+ "\nDestroy all aliens but obviously be careful; when an alien"
				+ "\nbombards you, you lose a life. Aliens use newtonian "
				+ "\nmechanics ( ;) ) to aim shoot bullets at you. Bullets will "
				+ "\ndetract from your health. When you run out of health, you"
				+ "\nlose a life."
				+ "\nTwo player: Use A and D to control movement, and W to shoot."
				+ "\nYou will be playing co-op, but watch out, you share lives and bullets!"
				+ "\nAliens now have lives and will display their damage by being green."
				+ "\nCOOL FEATURES: 1) Multiplayer 2) animation 3) AI (bullet tracks) 4) physics"
				);
				court.reset();
			}
		});
		
		//Adds all buttons created
		control_panel.add(reset);
		control_panel.add(instructions);
		control_panel.add(court.SCORE_DISPLAY);
		control_panel.add(court.HEALTH_DISPLAY);
		control_panel.add(multiplayer);
		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Start game
		court.reset();
	}

	/*
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it IMPORTANT: Do NOT delete! You MUST include
	 * this in the final submission of your game.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}

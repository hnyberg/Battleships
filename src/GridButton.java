import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;

/**
 * Object class that extends the JButton and implements action listener. The main class Battleships creates
 * a grid of GridButtons before placing ships. Each GridButton can have a ship as owner, but can also be without one.
 * When clicked on, that action listener changes the button's color and returns data to the main program and the possible
 * owner ship.
 * 
 * @author Hannes Nyberg
 *
 */
public class GridButton extends JButton implements ActionListener{
	
	//	Constants
	private final Color MISS_COLOR = new Color(0.7f,0.1f,0.1f);
	private final Color SHIP_COLOR = new Color(0.1f,0.7f,0.1f);
	
	//	Fields
	private Battleships battleShipProgram;
	private boolean hasOwnerShip;
	private Random random;
	private Ship ownerShip;

	/**
	 * Creates a GridButton with boolean for owner ship (false as default), a default color and an action listener.
	 * The constructor needs the main class Battleships as an argument to be able to send data if there is a hit or miss
	 * when action listener is called.
	 * 
	 * @param paramBattleShip The main class, Battleships. Necessary for some communication hits and misses.
	 */
	//	Constructor
	public GridButton(Battleships paramBattleShip){
		hasOwnerShip = false;
		battleShipProgram = paramBattleShip;
		random = new Random();
		setDefaultBackground();
		addActionListener(this);
	}
	
	//	Getters
	
	/**
	 * Returns true if GridButton has an owner ship.
	 * 
	 * @return Returns true if GridButton has an owner ship.
	 */
	public boolean hasOwnerShip(){
		return hasOwnerShip;
	}
	
	//	Setters
	
	/**
	 * Sets an owner ship to the GridButton.
	 * 
	 * @param paramShip The Ship to be the GridButtons new owner.
	 */
	public void setOwnerShip(Ship paramShip){
		ownerShip = paramShip;
		hasOwnerShip = true;
	}
	/**
	 * Includes actions that occur when the object is pressed. Disables button function.
	 * If the object has owner ship, adds "hits" to owner ship and to main program, and changes
	 * objects color to a "hit" color. If no owner ship, adds "misses" to main program and changes 
	 * button color to a "miss" color.
	 */
	private void pressGridButton(){
		setEnabled(false);
		if (hasOwnerShip){
			ownerShip.hitShip(); //	includes checkIfDestroyed()
			battleShipProgram.addHits();
			setBackground(SHIP_COLOR);
		}
		else {
			battleShipProgram.addMisses();
			setBackground(MISS_COLOR);
		}
	}
	/**
	 * Changes the GridButton's color to default ("water") color.
	 */
	public void setDefaultBackground(){
		setBackground(new Color(0.05f, 0.05f, (0.3f + random.nextFloat()*0.5f)));
	}
	/**
	 * Changes the GridButton's color to a shade of white/grey, for text/picture purposes.
	 */
	public void setBackgroundToTextColor(){
		float randomFloat = random.nextFloat();
		setBackground(new Color(
				0.7f + randomFloat*0.2f,
				0.7f + randomFloat*0.2f,
				0.7f + randomFloat*0.2f)
		);
	}
	/**
	 * Changes the GridButton's color to a "hit" color
	 */
	public void setShipBackground(){
		setBackground(SHIP_COLOR);
	}
	/**
	 * Changes the GridButton's color to a hinting turquoise color tone.
	 */
	public void setCheatBackground(){
		setBackground(new Color(
				0.2f, 
				0.4f + random.nextFloat()*0.2f, 
				0.2f + random.nextFloat()*0.2f));
	}
	/**
	 * Resets the GridButton's data. Works similar as the constructor.
	 */
	public void resetButton(){
		hasOwnerShip = false;
		setEnabled(true);
		setDefaultBackground();
	}
	/**
	 * Action when GridButton is pressed. Calls the ResetButton() method.
	 */
	public void actionPerformed(ActionEvent event){
		pressGridButton();
	}
}
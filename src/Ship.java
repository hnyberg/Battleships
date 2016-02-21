import java.util.Random;
import java.util.Vector;

/**
 * Object class Ship. Has a length, an orientation, ID and a start position.
 * 
 * @author Hannes Nyberg
 *
 */
public class Ship {
	
	//	Fields
	private Battleships battleShipProgram;
	private Random random;
	private boolean isHorizontal;
	private int numberOfHits;
	private int shipID;
	private int shipLength;
	private int startX;
	private int startY;
	private int xFactor;
	private int yFactor;
	
	//	Constructor
	public Ship(int paramID, int paramLength, Battleships paramBattleShip){
		shipID = paramID;	//	used for placement order identification; optional
		shipLength = paramLength;
		battleShipProgram = paramBattleShip;
		random = new Random();
		isHorizontal = random.nextBoolean();
		xFactor = isHorizontal ? 1 : 0;
		yFactor = isHorizontal ? 0 : 1;
		startX = 0;
		startY = 0;
		numberOfHits = 0;
	}
	
	//	Getters
	
	private void checkIfDestroyed(){
		if (numberOfHits == shipLength){
			battleShipProgram.addDestroyed();
		}
	}
	/**
	 * Returns Ship ID.
	 * 
	 * @return Returns Ship ID.
	 */
	public int getID(){
		return shipID;
	}
	/**
	 * Returns Ship length.
	 * 
	 * @return Returns Ship length.
	 */
	public int getShipLength(){
		return shipLength;
	}
	
	//	Setters
	
	/**
	 * Adds 1 to numberOfHits and checks if destroyed.
	 */
	public void hitShip(){
		numberOfHits++;
		checkIfDestroyed();
	}
	/**
	 * Returns if ship is horizontal
	 * 
	 * @return Boolean true if ship is horizontal.
	 */
	public boolean isShipHorizontal(){
		return isHorizontal;
	}
	/**
	 * Resets the Ship's data. Similar as constructor.
	 */
	public void resetShip(){
		isHorizontal = random.nextBoolean();
		xFactor = isHorizontal ? 1 : 0;
		yFactor = isHorizontal ? 0 : 1;
		numberOfHits = 0;
	}
	/**
	 * Removes the Ship's current trace on it's GridButtons.
	 * 
	 * @param paramGrid Needs a vector of GridButtons to reset.
	 */
	public void resetShipTrace(Vector<Vector<GridButton>> paramGrid){
		for (int i = 0; i < shipLength; i++){
			paramGrid.get(startY + i*yFactor).get(startX + i*xFactor).resetButton();
		}
	}
	/**
	 * Sets start position for Ship.
	 * 
	 * @param paramX X-position.
	 * @param paramY Y-position.
	 */
	public void setStartPosition(int paramX, int paramY){
		startX = paramX;
		startY = paramY;
	}
}

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * GUI-game Battleships lets the user generate a battlefield, place ships randomly, and then try to sink them all
 * 
 * The user can, via text fields, choose how large the battle grid should be, and how many of ships of 
 * specific lengths there should be. The text fields already have default values, but are editable. Once grid size 
 * and number of ships are chosen and the player presses a "RESET"-button, the program first creates a battle grid, then
 * starts to iterate through possible ship-placements by placing them one by one, then go back and re-do if a specific 
 * number of failed placements per ship are reached. 
 * 
 * Once the grid (made of vector of vectors of the object class GridButton, which inherits JButton) is made and ships 
 * are placed, the user can then click on each GridButton to find out if it's a hit or miss, and if a ship is fully destroyed, 
 * via a hit-, a miss-, and a destroyed-label which tracks the scores. The GUI also includes a small message field that tells
 * the user if ship-placement was successful, if the number of ships need to be reduced before placement, and if all ships 
 * have been destroyed. 
 * 
 * The ship placement is done through recursion, which can be tricky to fully understand and unfortunately brings 
 * with it a number of possible errors that can be hard to figure out.
 * 
 * @author Hannes Nyberg
 * @version 15.10.26
 */
public class Battleships extends JFrame implements ActionListener, DocumentListener{
	
	//	Color constants
	private final Color BUTTON_UNPRESSED_COLOR = new Color(0.6f,0.2f,0.2f);
	private final Color BUTTON_PRESSED_COLOR = new Color(0.2f,0.6f,0.2f);
	private final Color GRID_BACKGROUND_COLOR = new Color(0.15f,0.15f,0.35f);
	private final Color MAIN_BACKGROUND_COLOR = new Color(0.2f,0.2f,0.3f);
	private final Color TEXT_COLOR = Color.WHITE;
	
	//	Number constants
	private final int BORDER_SHIP_RATIO = 5;
	private final int FONT_SIZE = 15;
	private final int NUMBER_OF_SQUARES = 10;
	private final int MAX_SHIP_SIZE = 6;
	private final int MIN_SHIP_SIZE = 2;
	private final int MIN_NUMBER_OF_SQUARES = 10;
	private final int MAX_NUMBER_OF_SQUARES = 30;
	private final int MAX_NUMBER_OF_TRIES_PER_RECURSION = 10;
	private final int MAX_NUMBER_OF_TOTAL_TRIES = 50000;
	private final int NUMBER_OF_2 = 4;
	private final int NUMBER_OF_3 = 3;
	private final int NUMBER_OF_4 = 2;
	private final int NUMBER_OF_5 = 0;
	private final int NUMBER_OF_6 = 1;
	private final int NUMBER_OF_SHIP_TYPES = 5;
	
	//	Dimension constants (pixels)
	private final int GRID_DIMENSION = 400;
	private final int GRID_SPACE = 1;
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	
	//	String constants
	private final String STRING_ALL_PLACED = "Ships placed; enjoy!";
	private final String STRING_BUTTON_RESET_GRID = "RESET GRID";
	private final String STRING_BUTTON_SHOW_SHIPS = "SHOW SHIPS";
	private final String STRING_BUTTON_SHOW_TITLE = "SHOW TITLE";
	private final String STRING_HIDING_SHIPS = "HISING SHIPS";
	private final String STRING_NOT_PLACED = "Too much ship; please reduce!";
	private final String STRING_SHOWING_SHIPS = "SHOWING SHIPS";
	private final String STRING_WELCOME = "Choose ships; press RESET GRID";
	private final String STRING_WIN = "All dead; happy now?";
	
	//	Fields
	private boolean allInputsValid;
	private boolean allShipsPlaced;
	private boolean allZeroes;
	private boolean canPlaceShipMethodBoolean;
	private boolean showShipsButtonClicked;
	private boolean lastShipPlacementOK;
	private Font font;
	private int borderShipsLimit;
	private int checkValue;
	private int currentShipLength;
	private int destroyed; 
	private int hits;
	private int missed;
	private int numberOfSquares;
	private int shipCounter; 
	private int totalNumberOfShips; 
	private int totalNumberOfTries;
	private int[] defaultShipNumbers;
	private int[] shipNumbers;
	private GridBagConstraints constraints;
	private JButton resetGridButton;
	private JButton showShipsButton;
	private JButton showTitleButton;
	private JLabel[] shipChoiceLabels;
	private JLabel destroyedLabel;
	private JLabel hitLabel;
	private JLabel missLabel;
	private JLabel squareLabel;
	private JTextField[] shipChoiceFields;
	private JTextField destroyedField;
	private JTextField hitField;
	private JTextField messageField; 
	private JTextField missField;
	private JTextField squareField;
	private JPanel gridPanel;
	private JPanel leftPanel;
	private JPanel mainPanel;
	private JPanel messagePanel;
	private JPanel midPanel;
	private JPanel rightPanel;
	private Random random;
	private Ship[] ships;
	private Ship currentShip;
	private Vector<Vector<GridButton>> grid;
	
	//	Main
	/**
	 * Runs main program. Calls a constructor, which calls an initializer, which then creates the GUI.
	 * 
	 * @param args String arguments
	 */
	public static void main(String[] args) {
		new Battleships();
	}
	
	//	Constructor
	private Battleships(){
		initBattleships();
	}
	
	//	Initializer
	private void initBattleships(){
		
		//	Set window
		setTitle("Battleships");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setFocusable(true);
		
		//	Create font
		font = new Font(Font.MONOSPACED, Font.BOLD, FONT_SIZE);
		
		//	Set randomizer
		random = new Random();
		
		//	Create panels
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		midPanel = new JPanel();
		rightPanel = new JPanel();
		gridPanel = new JPanel();
		messagePanel = new JPanel();
		
		//	Set main panel
		mainPanel.setLayout(new GridBagLayout());
		add(mainPanel);
		constraints = new GridBagConstraints();
		mainPanel.setBackground(MAIN_BACKGROUND_COLOR);
		
		//	Set left panel
		constraints.gridx = 0;
		constraints.weightx = 0.1;
		mainPanel.add(leftPanel, constraints);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBackground(MAIN_BACKGROUND_COLOR);
		
		//	Set mid panel
		constraints.gridx = 1;
		mainPanel.add(midPanel, constraints);
		midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
		
		//	Set right panel
		constraints.gridx = 2;
		mainPanel.add(rightPanel, constraints);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBackground(MAIN_BACKGROUND_COLOR);
		
		//	Set grid panel
		numberOfSquares = NUMBER_OF_SQUARES;
		gridPanel.setLayout(new GridLayout(
				numberOfSquares, 
				numberOfSquares, 
				GRID_SPACE, 
				GRID_SPACE));
		gridPanel.setPreferredSize(new Dimension(GRID_DIMENSION, GRID_DIMENSION));
		gridPanel.setBackground(GRID_BACKGROUND_COLOR);
		midPanel.add(gridPanel);
		
		//	Set message panel
		messagePanel.setLayout(new GridLayout(1, 1));
		midPanel.add(messagePanel);
		
		//	SET LEFT PANEL COMPONENTS
		
		//	Set ship numbers
		defaultShipNumbers = new int[NUMBER_OF_SHIP_TYPES];
		defaultShipNumbers[0] = NUMBER_OF_2;
		defaultShipNumbers[1] = NUMBER_OF_3;
		defaultShipNumbers[2] = NUMBER_OF_4;
		defaultShipNumbers[3] = NUMBER_OF_5;
		defaultShipNumbers[4] = NUMBER_OF_6;
		
		//	Set label and field for grid-configuration
		//	and add listener
		squareLabel = new JLabel(
				"Grid("+MIN_NUMBER_OF_SQUARES+"-"+MAX_NUMBER_OF_SQUARES+")");
		squareLabel.setFont(font);
		squareLabel.setForeground(TEXT_COLOR);
		leftPanel.add(squareLabel);
		numberOfSquares = NUMBER_OF_SQUARES;
		squareField = new JTextField("" + numberOfSquares);
		squareField.setHorizontalAlignment(JTextField.CENTER);
		squareField.setFont(font);
		squareField.getDocument().addDocumentListener(this);
		leftPanel.add(squareField);
		
		//	Set label and field for ship-configuration
		//	and add listener
		shipChoiceLabels = new JLabel[NUMBER_OF_SHIP_TYPES];
		shipChoiceFields = new JTextField[NUMBER_OF_SHIP_TYPES];
		for (int i = 0; i < NUMBER_OF_SHIP_TYPES; i++){
			
			//	Text labels
			shipChoiceLabels[i] = new JLabel(
					"  "+(MIN_SHIP_SIZE+i)+"-ships");
			shipChoiceLabels[i].setFont(font);
			shipChoiceLabels[i].setForeground(TEXT_COLOR);
			
			//	Text fields
			shipChoiceFields[i] = new JTextField(Integer.toString(defaultShipNumbers[i]));
			shipChoiceFields[i].setHorizontalAlignment(JTextField.CENTER);
			shipChoiceFields[i].setFont(font);
			shipChoiceFields[i].getDocument().addDocumentListener(this);
			
			//	Add
			leftPanel.add(shipChoiceLabels[i]);
			leftPanel.add(shipChoiceFields[i]);
		}
		
		//	Set and add RESET GRID button
		resetGridButton = new JButton(STRING_BUTTON_RESET_GRID);
		resetGridButton.setBackground(BUTTON_UNPRESSED_COLOR);
		resetGridButton.setForeground(TEXT_COLOR);
		resetGridButton.setFont(font);
		resetGridButton.addActionListener(this);
		leftPanel.add(resetGridButton);
		
		//	Set and add SHOW SHIPS button
		showShipsButtonClicked = false;
		showShipsButton = new JButton(STRING_BUTTON_SHOW_SHIPS);
		showShipsButton.setBackground(BUTTON_UNPRESSED_COLOR);
		showShipsButton.setForeground(TEXT_COLOR);
		showShipsButton.setFont(font);
		showShipsButton.addActionListener(this);
		leftPanel.add(showShipsButton);
		
		//	Set and add SHOW TITLE button
		showTitleButton = new JButton(STRING_BUTTON_SHOW_TITLE);
		showTitleButton.setBackground(BUTTON_UNPRESSED_COLOR);
		showTitleButton.setForeground(TEXT_COLOR);
		showTitleButton.setFont(font);
		showTitleButton.addActionListener(this);
		leftPanel.add(showTitleButton);
		
		//	SET GRID PANEL COMPONENTS
		
		//	Set grid buttons
		grid = new Vector<Vector<GridButton>>();
		resetGrid(grid, gridPanel);
		
		//	Set message field
		messageField = new JTextField(STRING_WELCOME);
		messageField.setEditable(false);
		messageField.setHorizontalAlignment(JTextField.CENTER);
		messageField.setFont(font);
		messagePanel.add(messageField);
		
		//	SET RIGHT PANEL COMPONENTS
		
		//	Hit label and field
		hitLabel = new JLabel("   Hits");
		hitLabel.setFont(font);
		hitLabel.setForeground(TEXT_COLOR);
		rightPanel.add(hitLabel);
		hits = 0;
		hitField = new JTextField("" + hits);
		hitField.setEditable(false);
		hitField.setHorizontalAlignment(JTextField.CENTER);
		hitField.setFont(font);
		rightPanel.add(hitField);
		
		//	Miss label and field
		missLabel = new JLabel("  Missed");
		missLabel.setFont(font);
		missLabel.setForeground(TEXT_COLOR);
		rightPanel.add(missLabel);
		missed = 0;
		missField = new JTextField("" + missed);
		missField.setEditable(false);
		missField.setHorizontalAlignment(JTextField.CENTER);
		missField.setFont(font);
		rightPanel.add(missField);
		
		//	Destroyed label and field
		destroyedLabel = new JLabel("Destroyed");
		destroyedLabel.setFont(font);
		destroyedLabel.setForeground(TEXT_COLOR);
		rightPanel.add(destroyedLabel);
		destroyed = 0;
		destroyedField = new JTextField("" + destroyed);
		destroyedField.setEditable(false);
		destroyedField.setHorizontalAlignment(JTextField.CENTER);
		destroyedField.setFont(font);
		rightPanel.add(destroyedField);
		
		setWelcomeGrid(grid, gridPanel);
		
		setVisible(true);
	}
	
	//	Setters
	
	/**
	 * Starts the process of creating grid and placing ships. Checks users input in text-fields for size of battle grid 
	 * and number of ships, creates the grid, then calls a recursion-method to place the ships. After all recursions are 
	 * done, or cancelled due to reaching maximum allowed number of recursions (to avoid long processing time), the user 
	 * is told through the message field if ships have been placed or are needed/recommended to be reduced before trying 
	 * again.
	 */
	private void placeShips(){
		//	Before placing, get ships' information.
		//	How many of each ship? store in array.
		shipNumbers = new int[NUMBER_OF_SHIP_TYPES];
		totalNumberOfShips = 0;
		for (int i = 0; i < NUMBER_OF_SHIP_TYPES; i++){
			shipNumbers[i] = Integer.parseInt(shipChoiceFields[i].getText());
			totalNumberOfShips += shipNumbers[i];
		}
		//	Create ships and store in array.
		ships = new Ship[totalNumberOfShips];
		shipCounter = 0;
		currentShipLength = MAX_SHIP_SIZE;
		for (int i = NUMBER_OF_SHIP_TYPES-1; i >= 0; i--){
			for (int j = 0; j < shipNumbers[i]; j++){
				ships[shipCounter] = new Ship(shipCounter, currentShipLength, this);
				shipCounter++;
			}
			currentShipLength--;
		}
		//	Also, calculate number of border ships.
		borderShipsLimit = totalNumberOfShips / BORDER_SHIP_RATIO;
		allShipsPlaced = false;
		lastShipPlacementOK = false;
		shipCounter = 0;
		totalNumberOfTries = 0;
		tryPlacingCurrentShip();
		if (!allShipsPlaced){
			resetGrid(grid, gridPanel);
			writeMessage(STRING_NOT_PLACED);
		}
		System.out.println("Total tries: " + totalNumberOfTries);
	}
	/**
	 * Recursion method that tries to place each ship a maximum number of tries before giving up.
	 * The maximum number of allowed tries should in theory be (numberOfTriesPerShip)^(numberOfShips).
	 * There is possible a coding error in the method which makes it run more times than planned.
	 * This is a risk with recursion methods that have not been thought through. 
	 */
	private void tryPlacingCurrentShip(){
		//	Recursion method
		//	For each ship, initialize variables.
		int currentX = 0;
		int currentY = 0;
		int shipStartX = 0;
		int shipStartY = 0;
		int xFactor = 0;
		int yFactor = 0;
		int numberOfTriesPerRecursion = 0;
		boolean tryingToPlaceCurrentShip = true;
		boolean okToPlaceShip = true;
		while (!allShipsPlaced && tryingToPlaceCurrentShip){
			
			//	Get current ship info.
			currentShip = ships[shipCounter];
			currentShipLength = currentShip.getShipLength();
			xFactor = currentShip.isShipHorizontal() ? 1 : 0;
			yFactor = currentShip.isShipHorizontal() ? 0 : 1;
			
			//	Place ship (within grid)
			shipStartX = random.nextInt(numberOfSquares - currentShipLength*xFactor);
			shipStartY = random.nextInt(numberOfSquares - currentShipLength*yFactor);
			
			//	Correct placement if border ship
			if (shipCounter <= borderShipsLimit){
				if (currentShip.isShipHorizontal()){
					shipStartY = random.nextInt(2)*(numberOfSquares - 1);
				}
				else {
					shipStartX = random.nextInt(2)*(numberOfSquares - 1);
				}
			}
			
			//	If OK to place ship, do so.
			if (isShipPlacable(shipStartX, shipStartY, currentShipLength, xFactor, yFactor)){
				
				//	note that ship placement was OK
				lastShipPlacementOK = true;
				
				//	Save start position
				ships[shipCounter].setStartPosition(shipStartX, shipStartY);
				
				//	Place ship
				for (int i = 0; i < currentShipLength; i++){
					currentY = shipStartY + i*yFactor;
					currentX = shipStartX + i*xFactor;
					grid.get(currentY).get(currentX).setOwnerShip(currentShip);
				}
				
				//	Go to next ship; repeat till last ship is placed
				shipCounter++;
				if (shipCounter != totalNumberOfShips){
					tryPlacingCurrentShip();
				}
				else {
					allShipsPlaced = true;
					writeMessage(STRING_ALL_PLACED);
				}
				//	this is where we end up after recursion
			}
			else {
				//	note that placement was NOT OK
				lastShipPlacementOK = false;
				//	inform that ship placement failed
			}
			//	whether last placement worked or not, count it.
			numberOfTriesPerRecursion++;
			totalNumberOfTries++;
			
			//	If too many total tries, give up placing ships
			if (totalNumberOfTries >= MAX_NUMBER_OF_TOTAL_TRIES){
				tryingToPlaceCurrentShip = false;
			}
			//	If too many recursions and last placement not OK, stop placing current ship
			if (!lastShipPlacementOK && numberOfTriesPerRecursion == MAX_NUMBER_OF_TRIES_PER_RECURSION){
				//	Go back to last ship and start over
				//	When out of ships (shipCounter == 0), stop going back
				if (shipCounter > 0){
					shipCounter--;
					ships[shipCounter].resetShipTrace(grid);
					ships[shipCounter].resetShip();
				}
				
				tryingToPlaceCurrentShip = false;
			}
		}
	}
	/**
	 * Removes all current GridButtons from the grid-vector, clears the panel that holds the GridButtons, 
	 * then creates and places a new grid.
	 * 
	 * @param inputGrid The grid (vector of vectors of GridButton) that is to be cleared.
	 * @param paramPanel The Panel that shows the GridButtons. Needs a grid layout to show the GridButtons correctly.
	 */
	private void resetGrid(Vector<Vector<GridButton>> inputGrid, JPanel paramPanel){
		//	Clear panel.
		paramPanel.removeAll();
		paramPanel.revalidate();
		//	Get chosen number of squares.
		numberOfSquares = Integer.parseInt(squareField.getText());
		//	Set new panel size.
		int gridSpace = (GRID_DIMENSION / numberOfSquares) / 10;
		if (gridSpace < 1){
			gridSpace = 1;
		}
		paramPanel.setLayout(new GridLayout(
				numberOfSquares, 
				numberOfSquares, 
				gridSpace, 
				gridSpace));
		inputGrid.clear();
		for (int y = 0; y < numberOfSquares; y++){
			//	Create new row
			inputGrid.add(new Vector<GridButton>());
			for (int x = 0; x < numberOfSquares; x++){
				//	Create new grid buttons and add to panel.
				inputGrid.get(y).add(new GridButton(this));
				paramPanel.add(inputGrid.get(y).get(x));
			}
		}
		//	After clearing and adding new elements, update panel.
		paramPanel.repaint();
	}
	/**
	 * Resets the text fields to '0'.
	 */
	private void resetFields(){
		hits = 0;
		hitField.setText("" + hits);
		missed = 0;
		missField.setText("" + missed);
		destroyed = 0;
		destroyedField.setText("" + destroyed);
	}
	/**
	 * Used by a document listener. Checks each text field that it holds a correct integer value within allowed bounds
	 */
	private void checkText(){
		allInputsValid = true;
		checkValue = 0;
		allZeroes = true;
		//	Check ship fields if at least 1 ship and values are integers over 0.
		for (int i = 0;  i < NUMBER_OF_SHIP_TYPES; i++){
			try{
				checkValue = Integer.parseInt(shipChoiceFields[i].getText());
				if (checkValue < 0){
					allInputsValid = false;
				}
				if (checkValue != 0){
					allZeroes = false;
				}
			}
			catch(NumberFormatException e){
				allInputsValid = false;
			}
		}
			
		//	Check grid configuration field if value is integer and over 0.
		try{
			checkValue = Integer.parseInt(squareField.getText());
			if (checkValue < MIN_NUMBER_OF_SQUARES || checkValue > MAX_NUMBER_OF_SQUARES){
				allInputsValid = false;
				}
			}
		catch(NumberFormatException e){
			allInputsValid = false;
			}
		//	Were all fields zeroes? If so, no go.
		if (allZeroes) {
			allInputsValid = false;
		}
		//	If any field has invalid value, disable RESET button.
		if (allInputsValid){
			resetGridButton.setEnabled(true);
			}
		else resetGridButton.setEnabled(false);
	}
	/**
	 * Adds 1 to the total number of hits, and displays on hit-label
	 */
	public void addHits(){
		hits++;
		hitField.setText("" + hits);
	}
	/**
	 * Adds 1 to the total number of misses, and displays on miss-label
	 */
	public void addMisses(){
		missed++;
		missField.setText("" + missed);
	}
	/**
	 * Adds 1 to the total number of destroyed ships, and displays on destroyed-label
	 */
	public void addDestroyed(){
		destroyed++;
		destroyedField.setText("" + destroyed);
		if (destroyed == totalNumberOfShips){
			writeMessage(STRING_WIN);
		}
	}
	/**
	 * Prints out a string on the message field (JTextField) in the GUI
	 * 
	 * @param paramString Text string to be displayed in message field
	 */
	private void writeMessage(String paramString){
		messageField.setText(paramString);
	}
	/**
	 * Checks if a certain ship is OK to be placed at its thought location. The check starts at a location (paramX, paramY),
	 * then iterates through each new GridButton until it has gone through the whole ship length (paramShipLength).
	 * Depending on if the ship is horizontal or vertical (paramXFactor = 1 or paramYFactor = 1 respectively), only the 
	 * x- or y-coordinates increase through the iteration.
	 * 
	 * @param paramX Start x-coordinate for ship placement
	 * @param paramY Start y-coordinate for ship placement
	 * @param paramShipLength Length of ship
	 * @param paramXFactor Multiplication factor (0 or 1) to be multiplied with x-coordinate increment
	 * @param paramYFactor Multiplication factor (0 or 1) to be multiplied with y-coordinate increment
	 * @return Returns a boolean; if current ship is OKToPlace (true or false).
	 */
	private boolean isShipPlacable(int paramX, int paramY, int paramShipLength, int paramXFactor, int paramYFactor){
		
		//	Go through ship; check 1 square in each direction
		//	The method controlledInt() makes sure to never go outside grid
		canPlaceShipMethodBoolean = true;
		for (int i = 0; i < paramShipLength; i++){
			
			if (grid.get(controlledInt(paramY + i*paramYFactor + 1)).get(controlledInt(paramX + i*paramXFactor)).hasOwnerShip()
					|| grid.get(controlledInt(paramY + i*paramYFactor - 1)).get(controlledInt(paramX + i*paramXFactor)).hasOwnerShip()
					|| grid.get(controlledInt(paramY + i*paramYFactor)).get(controlledInt(paramX + i*paramXFactor + 1)).hasOwnerShip()
					|| grid.get(controlledInt(paramY + i*paramYFactor)).get(controlledInt(paramX + i*paramXFactor - 1)).hasOwnerShip()){
				canPlaceShipMethodBoolean = false;
			}
		}
		return canPlaceShipMethodBoolean;
	}
	/**
	 * Checks that the input integer is within the grid boundaries, and corrects it if not.
	 * 
	 * @param paramInt Integer value to be checked if within grid boundaries.
	 * @return Returns the input integer, corrected if needed.
	 */
	private int controlledInt(int paramInt){
		if (paramInt < 0){
			paramInt = 0;
		}
		if (paramInt >= numberOfSquares){
			paramInt = numberOfSquares - 1;
		}
		return paramInt;
	}
	/**
	 * Action when the reset button is pressed. Resets grid, text fields, and replaces ships
	 */
	public void actionPerformed(ActionEvent e){
		//	Is RESET GRID button clicked?
		if (e.getSource() == resetGridButton){
			//	Reset grid.
			resetGrid(grid, gridPanel);
			//	Reset points.
			resetFields();
			//	Reset SHOW SHIPS button
			showShipsButton.setBackground(BUTTON_UNPRESSED_COLOR);
			showShipsButtonClicked = false;
			//	Place new ships.
			placeShips();
		}
		//	If not, is SHOW SHIPS button clicked?
		else if (e.getSource() == showShipsButton){
			if(!showShipsButtonClicked){
				System.out.println(STRING_SHOWING_SHIPS);
				showShipsButton.setBackground(BUTTON_PRESSED_COLOR);
				showShipsButtonClicked = true;
				for (int y = 0; y < numberOfSquares; y++){
					for (int x = 0; x < numberOfSquares; x++){
						if (grid.get(y).get(x).hasOwnerShip() && grid.get(y).get(x).isEnabled()){
							grid.get(y).get(x).setCheatBackground();
						}
					}
				}
			}
			else {
				System.out.println(STRING_HIDING_SHIPS);
				showShipsButton.setBackground(BUTTON_UNPRESSED_COLOR);
				showShipsButtonClicked = false;
				for (int y = 0; y < numberOfSquares; y++){
					for (int x = 0; x < numberOfSquares; x++){
						if (grid.get(y).get(x).hasOwnerShip() && grid.get(y).get(x).isEnabled()){
							grid.get(y).get(x).setDefaultBackground();
						}
					}
				}
			}
		}
		//	If not, SHOW TITLE buttons is clicked
		else {
			setWelcomeGrid(grid, gridPanel);
			//	Reset SHOW SHIPS button
				showShipsButton.setBackground(BUTTON_UNPRESSED_COLOR);
				showShipsButtonClicked = false;
		}
	}
	//	Document listener events
	public void changedUpdate(DocumentEvent event){
		checkText();
	}
	public void insertUpdate(DocumentEvent event){
		checkText();
	}
	public void removeUpdate(DocumentEvent event){
		checkText();
	}
	/**
	 * changes the grid to a welcome grid temporarily
	 * 
	 * @param inputGrid Grid to be changed
	 * @param paramPanel Panel that holds grid
	 */
	private void setWelcomeGrid(Vector<Vector<GridButton>> paramGrid, JPanel paramPanel){
		//	Clear panel.
		paramPanel.removeAll();
		paramPanel.revalidate();
		//	Set chosen number of squares.
		numberOfSquares = 29;
		//	Set new panel size.
		int gridSpace = 1;
		paramPanel.setLayout(new GridLayout(
				numberOfSquares, 
				numberOfSquares, 
				gridSpace, 
				gridSpace));
		paramGrid.clear();
		for (int y = 0; y < numberOfSquares; y++){
			//	Create new row
			paramGrid.add(new Vector<GridButton>());
			for (int x = 0; x < numberOfSquares; x++){
				//	Create new grid buttons and add to panel.
				paramGrid.get(y).add(new GridButton(this));
				paramPanel.add(paramGrid.get(y).get(x));
			}
		}
		//	Set individual squares to text color
		int textX = 3;
		int textY = 5;
		int textXStep = 4;
		writeB(grid, textY, textX);
		textY -= 3;
		textX += textXStep;
		writeA(grid, textY, textX);
		textY -= 1;
		textX += textXStep;
		writeT(grid, textY, textX);
		textX += textXStep;
		writeT(grid, textY, textX);
		textY += 1;
		textX += textXStep;
		writeL(grid, textY, textX);
		textY += 3;
		textX += textXStep;
		writeE(grid, textY, textX);
		//	New row
		textY = 23;
		textX = 5;
		writeS(grid, textY, textX);
		textY += 1;
		textX += textXStep;
		writeH(grid, textY, textX);
		textX += textXStep;
		writeI(grid, textY, textX);
		textX += textXStep;
		writeP(grid, textY, textX);
		textY -= 1;
		textX += textXStep;
		writeS(grid, textY, textX);
		//	Add skull
		writeSkull(grid, 7, 8);
		
		//	After clearing and adding new elements, update panel.
		paramPanel.repaint();
	}
	/**
	 * Writes the letter A on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeA(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+1).setBackgroundToTextColor();
		
		paramGrid.get(paramY+1).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX+2).setBackgroundToTextColor();
	}
	/**
	 * Writes the letter B on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeB(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX+1).setBackgroundToTextColor();
		
		paramGrid.get(paramY+1).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX+2).setBackgroundToTextColor();
	}
	/**
	 * Writes the letter E on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeE(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX+1).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX+2).setBackgroundToTextColor();
	}
	/**
	 * Writes the letter H on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeH(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		
		paramGrid.get(paramY+2).get(paramX+1).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+1).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX+2).setBackgroundToTextColor();
	}
	/**
	 * Writes the letter I on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeI(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+1).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX+1).setBackgroundToTextColor();
	}
	/**
	 * Writes the letter L on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeL(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		
		paramGrid.get(paramY+4).get(paramX+1).setBackgroundToTextColor();
		
		paramGrid.get(paramY+4).get(paramX+2).setBackgroundToTextColor();
	}
	/**
	 * Writes the letter P on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeP(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+1).setBackgroundToTextColor();
		
		paramGrid.get(paramY+1).get(paramX+2).setBackgroundToTextColor();
	}
	/**
	 * Writes the letter S on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeS(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX+1).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+2).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX+2).setBackgroundToTextColor();
	}
	/**
	 * Writes the letter T on a 5*3 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeT(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY).get(paramX).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+1).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+2).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX+1).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX+1).setBackgroundToTextColor();
		
		paramGrid.get(paramY).get(paramX+2).setBackgroundToTextColor();
	}
	/**
	 * Writes a skull painting on a 15*13 GridButton area.
	 * 
	 * @param paramGrid	Grid that holds GridButtons
	 * @param paramY Y-position (outer vector) on grid
	 * @param paramX X-position (inner vector) on grid.
	 */
	private void writeSkull(Vector<Vector<GridButton>> paramGrid, int paramY, int paramX){
		paramGrid.get(paramY+10).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+12).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+8).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+9).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+13).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+14).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+2).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+5).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+9).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+13).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+6).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+10).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+12).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+0).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+6).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+10).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+12).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+0).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackground(Color.RED);
		paramGrid.get(paramY+4).get(paramX).setBackground(Color.RED);
		paramGrid.get(paramY+7).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+8).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+11).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+0).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+8).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+11).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+0).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackground(Color.RED);
		paramGrid.get(paramY+4).get(paramX).setBackground(Color.RED);
		paramGrid.get(paramY+7).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+8).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+11).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+0).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+6).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+10).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+12).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+1).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+6).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+10).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+12).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+2).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+3).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+4).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+5).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+9).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+13).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+8).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+9).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+13).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+14).get(paramX).setBackgroundToTextColor();
		paramX += 1;
		paramGrid.get(paramY+10).get(paramX).setBackgroundToTextColor();
		paramGrid.get(paramY+12).get(paramX).setBackgroundToTextColor();
	}
}
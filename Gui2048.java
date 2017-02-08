//-------------------------------//
//  Author: Bilguun Bulgan 
//  File: Gui2048.java 
//
//  This file represents the 2048 game GUI. This file contains methods 
//  for manipulating the GUI depending on user input. The methods take 
//  key input, updates the board and then updates the GUI depending on
//  the updated board. Lets the user define the size of the board, load 
//  a board, save a board to a file, rotate the board. The GUI is also
//  resizable. This class also contains inner classes Tile (which helps
//  to keep track of the numbers on the tile and the color of the tile)
//  and myKeyHandler (which handles key input).
//------------------------------------------------------------------//
import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;

public class Gui2048 extends Application
{
	private String outputBoard; // The filename for where to save the Board
	private Board board; // The 2048 Game Board

	// the static variables
	private static final int TILE_WIDTH = 106;

	private static final int TEXT_SIZE_LOW = 50; // Low value tiles (2,4,8,etc)
	private static final int TEXT_SIZE_MID = 40; // Mid value tiles 
	//(128, 256, 512)
	private static final int TEXT_SIZE_HIGH = 30; // High value tiles 
	//(1024, 2048, Higher)

	// Fill colors for each of the Tile values
	private static final Color COLOR_EMPTY = Color.rgb(238, 228, 218, 0.35);
	private static final Color COLOR_2 = Color.rgb(238, 228, 218);
	private static final Color COLOR_4 = Color.rgb(237, 224, 200);
	private static final Color COLOR_8 = Color.rgb(242, 177, 121);
	private static final Color COLOR_16 = Color.rgb(245, 149, 99);
	private static final Color COLOR_32 = Color.rgb(246, 124, 95);
	private static final Color COLOR_64 = Color.rgb(246, 94, 59);
	private static final Color COLOR_128 = Color.rgb(237, 207, 114);
	private static final Color COLOR_256 = Color.rgb(237, 204, 97);
	private static final Color COLOR_512 = Color.rgb(237, 200, 80);
	private static final Color COLOR_1024 = Color.rgb(237, 197, 63);
	private static final Color COLOR_2048 = Color.rgb(237, 194, 46);
	private static final Color COLOR_OTHER = Color.BLACK;
	private static final Color COLOR_GAME_OVER = Color.rgb(238, 228, 218, 0.73);

	private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242); 
	// For tiles >= 8

	private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101); 
	// For tiles < 8

	private GridPane pane;


	private Tile[][] tileArray;  //2D array of Tiles that keep the color and 
	//the number on a tile
	private Text scoreText;      //contains the score
	private Scene scene;         
	private StackPane stack;     //the bottommost pane
	private boolean isAlreadyGameOver; //keeps track of if showGameOver() is
	//used only once

	/** 
	 * Sets the groundwork of starting the game  
	 */
	@Override
		public void start(Stage primaryStage)
		{
			// Process Arguments and Initialize the Game Board
			processArgs(getParameters().getRaw().toArray(new String[0]));

			// Create the pane that will hold all of the visual objects
			pane = new GridPane();
			pane.setAlignment(Pos.CENTER);
			pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
			pane.setStyle("-fx-background-color: rgb(187, 173, 160)");

			// Set the spacing between the Tiles
			pane.setHgap(8); 
			pane.setVgap(8);


			stack = new StackPane();
			stack.getChildren().add(pane);

			//setting scene and stage
			scene = new Scene(stack, 550, 580);
			primaryStage.setTitle("Gui2048");
			primaryStage.setScene(scene);
			primaryStage.show();

			//initializing a tile array from the current state of the board
			//and showing it (adds to the pane)
			tileArray = initTileArr(board);
			showTileArr(tileArray);

			//setting 2048 title on the upper left corner
			Text title = new Text();
			title.setText("2048");
			title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
			GridPane.setHalignment(title, HPos.CENTER);
			pane.add(title, 0,0,2,1);

			//adding score indicator on the top right of the game
			scoreText = new Text();
			scoreText.setText("Score: " +board.getScore());
			scoreText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
			pane.add(scoreText, board.GRID_SIZE-2,0,2,1);
			GridPane.setHalignment(scoreText, HPos.CENTER);

			//registers the handler to the scene
			scene.setOnKeyPressed(new myKeyHandler());


		}


	/** 
	 * Initializes a 2D tile array according to the values (numbers)
	 *          on each square of the board
	 * 
	 * @param Board board : the board from which to take the values from
	 * @return Tile[][] - fully initialized 2D tile array according to the 
	 *                     parameter board
	 */
	public Tile[][] initTileArr(Board board) {
		int[][] boardValues = board.getGrid();
		//making a new tile array to bind number and the square
		Tile[][] tileArr = new Tile[boardValues.length][boardValues.length];

		//initializing the tiles in the array
		for (int row = 0; row < boardValues.length; row++) {
			for (int column = 0; column < boardValues.length; column++) {
				int tileVal = boardValues[row][column];

				//creates a new tile object, sets the width and height of the tile
				//and assigns it to the 2D array
				Tile tile = new Tile (tileVal);
				tile.getSquare().setWidth(TILE_WIDTH);
				tile.getSquare().setHeight(TILE_WIDTH);
				tileArr[row][column] = tile;
			}
		}
		return tileArr;
	} // ends initTileArr()

	/** 
	 * Shows (adds to pane) every tile of the parameter 2D tile array,
	 *         which should be fully initialized with the initTileArr method above
	 * 
	 * @param Tile[][] tileArr : the tile array to show on pane 
	 */   
	public void showTileArr (Tile[][] tileArr) {

		//looping through the parameter tile array 
		for (int row = 0; row < tileArr.length; row ++) {
			for (int column = 0; column < tileArr.length; column++) {

				Tile tile = tileArr[row][column];

				//binds the width and height of the square with the
				//width and height of the scene, so if the user changes the
				//window size, the squares will change size accordingly
				tile.getSquare().widthProperty().bind(scene.
						widthProperty().divide(board.GRID_SIZE+1));
				tile.getSquare().heightProperty().bind(scene.
						heightProperty().divide(board.GRID_SIZE+1));

				//adds the square and the number on it to the pane
				pane.add(tile.getSquare(), column, row + 1);
				pane.add(tile.getText(), column, row + 1);
			}
		}

	} //ends showTileArr

	/** 
	 * Updates the player's score on the GUI after the user's move
	 * 
	 * @param int score : the score value that must be shown on the GUI 
	 */   
	public void updateScore(int score) {
		scoreText.setText("Score: " + score);
	}

	/** 
	 * Updates the tileArray (which will be shown on the GUI) 
	 * according to the passed in board object that contains the 
	 * number values of each tile
	 * 
	 * @param Board board : the board object from which to update the tile 
	 *                           array
	 * @param Tile[][] tileArr : the tile array to be update  
	 */   
	public void updateGUI(Board board, Tile[][] tileArr) {

		int[][] grid = board.getGrid();

		//looping through the grid and updating each tile in the tile array
		//according to their new values

		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid.length; column++) {
				tileArr[row][column].updateTile(grid[row][column]);
			}
		}

	} //ends updateGUI

	/** 
	 * Puts a semi-transparent layer on the game board with the 
	 *          message "Game Over!" if there are no more possible moves
	 * 
	 * @param boolean isGameOver : the method will only do something if this 
	 * value is true  
	 */   
	public void showGameOver(boolean isGameOver) {
		//aborts method if game over screen has already been displayed
		if (isAlreadyGameOver) {
			return;
		}

		if (isGameOver) {

			//creating the transparent overlay
			Rectangle gameOverScreen = new Rectangle(scene.getWidth(), 
					scene.getHeight());
			gameOverScreen.setFill(COLOR_GAME_OVER);

			//creating the "Game Over!" text
			Text gameOverText = new Text();
			gameOverText.setText("Game Over!");
			gameOverText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 
						50));
			gameOverText.setFill(COLOR_VALUE_DARK);

			//shows the overlay and the text on the GUI
			stack.getChildren().addAll(gameOverScreen, gameOverText);

			//centers everything
			GridPane.setHalignment(gameOverScreen, HPos.CENTER);
			GridPane.setHalignment(gameOverText, HPos.CENTER);
			GridPane.setValignment(gameOverText, VPos.CENTER); 

			//sets this variable true so this method won't proceed again
			isAlreadyGameOver = true;
		}
	}

	/** 
	 * Name: Tile (the inner class)
	 * Purpose: This class is defined so that the number value of the game tile 
	 *          the color of the tile are conveniently tied together for easier 
	 *          access to these data. Contains methods for changing the colors
	 *          and numbers of each tile after each move depending on the tile 
	 *          number value.
	 */
	private class Tile {
		//instance variables
		private Text text = new Text("");
		private Rectangle square;

		//constructors

		//this constructor takes a number which is the tile value, puts the number 
		//on the tile ina text form and colors the tile accordingly
		private Tile(int numberOnTile) {
			text = new Text();
			this.text.setFill(COLOR_VALUE_DARK);
			square = new Rectangle();
			this.updateTile(numberOnTile); 
		}

		//getters and setters

		//returns the reference to the text instance variable, which is the number
		//displayed on the game tile
		public Text getText() {
			return this.text;
		}

		//returns the reference to the rectangle instance variable, which defines 
		//the color of the game tile
		public Rectangle getSquare() {
			return this.square;
		}

		//this setter takes an int (not a string) as the text on the tile 
		//will always be a number
		public void setText(int numberOnTile) {
			this.text.setText("" + numberOnTile);
		}

		//methods

		/** 
		 * Sets the color of the tile
		 * @param Color c : the color that the tile should be painted with 
		 */
		private void setColor(Color c) {
			this.square.setFill(c);
		}

		/** 
		 * Updates the color and the number on the game tile according
		 *          to the new number value of the tile after each move
		 * @param int numberOnTile  
		 */
		private void updateTile(int numberOnTile) {
			//setting number on tile except 0s
			if (numberOnTile != 0) {
				this.setText(numberOnTile); }

			else {
				this.getText().setText("");
			}

			//sets the number in the middle of the grid/tile
			GridPane.setHalignment(this.getText(), HPos.CENTER);

			//setting font size of tile depending on the value on the tile
			if (numberOnTile < 128) {
				this.getText().setFont(Font.font("Times New Roman", 
						FontWeight.BOLD, TEXT_SIZE_LOW));
			}
			else if (numberOnTile < 1024 && numberOnTile > 128) {
				this.getText().setFont(Font.font("Times New Roman", 
						FontWeight.BOLD, TEXT_SIZE_MID));
			}
			else {
				this.getText().setFont(Font.font("Times New Roman", 
						FontWeight.BOLD, TEXT_SIZE_HIGH));
			}

			//setting the text color of the tile depending on value 
			if (numberOnTile < 8) {
				this.text.setFill(COLOR_VALUE_DARK);
			}

			else if (numberOnTile >= 8) {
				this.getText().setFill(COLOR_VALUE_LIGHT);
			} 


			//setting the tile color
			switch (numberOnTile) {
				case 0: this.setColor(COLOR_EMPTY); break;
				case 2: this.setColor(COLOR_2); break; 
				case 4: this.setColor(COLOR_4); break; 
				case 8: this.setColor(COLOR_8); break; 
				case 16: this.setColor(COLOR_16); break; 
				case 32: this.setColor(COLOR_32); break; 
				case 64: this.setColor(COLOR_64); break; 
				case 128: this.setColor(COLOR_128); break;
				case 256: this.setColor(COLOR_256); break;  
				case 512: this.setColor(COLOR_512); break; 
				case 1024: this.setColor(COLOR_1024); break; 
				case 2048: this.setColor(COLOR_2048); break; 
				default: this.setColor(COLOR_OTHER); break;
			}

		} //ends updateTile 
	} // ends tile class


	/** 
	 * Name: myKeyHandler (class)
	 * Purpose: handles user's key input
	 */
	private class myKeyHandler implements EventHandler<KeyEvent> {

		@Override
			public void handle (KeyEvent e) {
				switch (e.getCode()) {

					//when user presses up arrow key
					case UP: 

						//if the board has no possible moves, 
						//displayes the game over overlay
						showGameOver(board.isGameOver());
						if (board.canMove(Direction.UP)) {
							//moves and adds a random tile on the board
							board.move(Direction.UP);
							board.addRandomTile();

							//updates the score and the gameboard on GUI
							updateGUI(board, tileArray);
							updateScore(board.getScore());

							//prints on the consile
							System.out.println("Moving up");
						}
						//checks again after moving if there are no possible 
						//moves left, if not, displays the game over overlay
						showGameOver(board.isGameOver());
						break;



					case DOWN: 

						//if the board has no possible moves, 
						//displayes the game over overlay
						showGameOver(board.isGameOver());
						if (board.canMove(Direction.DOWN)) {
							//moves and adds a random tile on the board 
							board.move(Direction.DOWN);
							board.addRandomTile();

							//updates the score and the gameboard on GUI
							updateGUI(board, tileArray);
							updateScore(board.getScore());

							//prints on the consile
							System.out.println("Moving down");
						}
						//checks again after moving if there are no 
						//possible moves left, if not, displays the game over overlay
						showGameOver(board.isGameOver());
						break;



					case LEFT: 
						//if the board has no possible moves, 
						//displayes the game over overlay 
						showGameOver(board.isGameOver());
						if (board.canMove(Direction.LEFT)) {
							//moves and adds a random tile on the board 
							board.move(Direction.LEFT);
							board.addRandomTile();

							//updates the score and the gameboard on GUI
							updateGUI(board, tileArray);
							updateScore(board.getScore());

							//prints on the consile
							System.out.println("Moving left");
						}
						//checks again after moving if there are no possible moves left, if not,
						//displays the game over overlay
						showGameOver(board.isGameOver());
						break;



					case RIGHT: 
						//if the board has no possible moves, 
						//displayes the game over overlay 
						showGameOver(board.isGameOver());
						if (board.canMove(Direction.RIGHT)) {
							//moves and adds a random tile on the board 
							board.move(Direction.RIGHT);
							board.addRandomTile();

							//updates the score and the gameboard on GUI
							updateGUI(board, tileArray);
							updateScore(board.getScore());

							//prints on the consile
							System.out.println("Moving right");
						}
						//checks again after moving if there are no possible 
						//moves left, if not, displays the game over overlay
						showGameOver(board.isGameOver());
						break;

						//rotates the board clockwise if the user presses "r" 
					case R: 
						board.rotate(true);
						updateGUI(board, tileArray);
						updateScore(board.getScore());
						break;

						//saves the board of the user presses "s" 
					case S:
						try {
							board.saveBoard(outputBoard); 
						}
						catch (Exception ex) {
							System.out.println("SaveBoard threw an exception");
						}
						System.out.println("Saving board to " + outputBoard);
				}   
			} // ends handle method

	} //ends myKeyHandler class



	// The method used to process the command line arguments
	private void processArgs(String[] args)
	{
		String inputBoard = null;   // The filename for where to load the Board
		int boardSize = 0;          // The Size of the Board

		// Arguments must come in pairs
		if((args.length % 2) != 0)
		{
			printUsage();
			System.exit(-1);
		}

		// Process all the arguments 
		for(int i = 0; i < args.length; i += 2)
		{
			if(args[i].equals("-i"))
			{   //processing the argument that specifies
				//the input file to be used to set the board
				inputBoard = args[i + 1];
			}
			else if(args[i].equals("-o"))
			{   //processing the argument that specifies
				//the output file to be used to save the board
				outputBoard = args[i + 1];
			}
			else if(args[i].equals("-s"))
			{   //processing the argument that specifies
				//the size of the Board
				boardSize = Integer.parseInt(args[i + 1]);
			}
			else
			{   // Incorrect Argument 
				printUsage();
				System.exit(-1);
			}
		}

		// Set the default output file if none specified
		if(outputBoard == null)
			outputBoard = "2048.board";
		// Set the default Board size if none specified or less than 2
		if(boardSize < 2)
			boardSize = 4;

		// Initialize the Game Board
		try{
			if(inputBoard != null)
				board = new Board(inputBoard, new Random());
			else
				board = new Board(boardSize, new Random());
		}
		catch (Exception e)
		{
			System.out.println(e.getClass().getName() + 
					" was thrown while creating a " +
					"Board from file " + inputBoard);
			System.out.println("Either your Board(String, Random) " +
					"Constructor is broken or the file isn't " +
					"formated correctly");
			System.exit(-1);
		}
	}

	// Print the Usage Message 
	private static void printUsage()
	{
		System.out.println("Gui2048");
		System.out.println("Usage:  Gui2048 [-i|o file ...]");
		System.out.println();
		System.out.println("  Command line arguments come in pairs of the "+ 
				"form: <command> <argument>");
		System.out.println();
		System.out.println("  -i [file]  -> Specifies a 2048 board that " + 
				"should be loaded");
		System.out.println();
		System.out.println("  -o [file]  -> Specifies a file that should be " + 
				"used to save the 2048 board");
		System.out.println("                If none specified then the " + 
				"default \"2048.board\" file will be used");  
		System.out.println("  -s [size]  -> Specifies the size of the 2048" + 
				"board if an input file hasn't been"); 
		System.out.println("                specified.  If both -s and -i" + 
				"are used, then the size of the board"); 
		System.out.println("                will be determined by the input" +
				" file. The default size is 4.");
	}
}

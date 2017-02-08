//package G2048;

//------------------------------------------------------------------//
//  Name: Bilguun Bulgan  
//  File: Board.java 
//
//  This file represents the 2048 game board. This file contains methods 
//  for manipulating the game board. The methods generate a new board, 
//  load an existing game board, generate new tiles at available spots,
//  rotate the board clockwise and counterclockwise, check if moves by 
//  the user are possible, move tiles and check if the 
//  input board file is not tampered with.
//------------------------------------------------------------------//
/**
 * Sample Board
 * <p/>
 *     0   1   2   3
 * 0   -   -   -   -
 * 1   -   -   -   -
 * 2   -   -   -   -
 * 3   -   -   -   -
 * <p/>
 * The sample board shows the index values for the columns and rows
 */

import java.util.ArrayList;
import java.util.*;
import java.io.*;

/** 
 * Class name: Board 
 * Purpose: Represents the 2048 game board, contains methods for
 * manipulating the board.
 */
public class Board {
  public final int NUM_START_TILES = 2;
  public final int TWO_PROBABILITY = 90;
  public final int GRID_SIZE;
  
  
  private final Random random;
  private int[][] grid;
  private int score;
  private int[][] prevGrid;
  private int prevScore = -1;
  
  /** Constructs a game board with the specified size
   * @param int boardSize: the size of the game board
   * @param Random random: random generator  
   */
  public Board(int boardSize, Random random) {
    
    this.random = random;
    GRID_SIZE = boardSize;
    this.grid = new int[GRID_SIZE][GRID_SIZE];
    this.prevGrid = new int[GRID_SIZE][GRID_SIZE];
    this.prevScore = -1;
    this.score = 0;
    for (int i = 0; i < NUM_START_TILES; i++) {
      this.addRandomTile();
      
    }
  }
  /** 
   * Constructs a game board from an input file
   * @param String inputBoard: the name of the input file
   * @param Random random: random generator  
   */
  public Board(String inputBoard, Random random) throws IOException {
    this.random = random;
    File inputBoardFile = new File (inputBoard);
    Scanner scanner = new Scanner(inputBoardFile);
    GRID_SIZE = scanner.nextInt();
    this.grid = new int[GRID_SIZE][GRID_SIZE];
    this.prevGrid = new int[GRID_SIZE][GRID_SIZE];
    this.prevScore = -1;
    this.score = scanner.nextInt();
    
    //nested for loops for assigning tile values
    for (int row = 0; row < GRID_SIZE; row++) {
      for (int column = 0; column < GRID_SIZE; column++) {
        grid[row][column] = scanner.nextInt();
      }
    }
    scanner.close();
  }
  
  /** 
   * Getter method for the instance variable prevScore
   * @return int prevScore
   */
  public int getPrevScore() {
    return this.prevScore;
  }
  
  /** 
   * Saves the current board into a file
   * @param String outputBoard: name of the file where to save the board
   */
  public void saveBoard(String outputBoard) throws IOException {
    File outputFile = new File(outputBoard);
    PrintWriter writer = new PrintWriter(outputFile);
    writer.println(this.GRID_SIZE);
    writer.print(this.score);
    
    
    //nested for loops for saving tile values
    for(int row = 0; row < this.GRID_SIZE; row++) {
      writer.println();
      for (int column = 0; column < this.GRID_SIZE; column++) {
        writer.print(this.grid[row][column] + " "); 
      }
    }
    writer.println();
    writer.close();
  }
  
  /** 
   * Adds a random tile (of value 2 or 4) to a random empty space
   * on the board
   */  
  
  public void addRandomTile() {
    int count = 0;
    //looping through all tiles to find number of empty spots
    for (int row = 0; row < this.GRID_SIZE; row++) {
      for (int column = 0; column < this.GRID_SIZE; column++) {
        if (this.grid[row][column]== 0) count++;
      }
    }
    
    //generates random numbers for location of the new tile and the
    //probability of getting a certain value for the tile
    int location = this.random.nextInt(count);
    int value = this.random.nextInt(100);
    int count2 = -1;
    
    //looping though all tiles to place the new tile, aborts after placing tile
    for (int row = 0; row < this.GRID_SIZE; row++) {
      for (int column = 0; column < this.GRID_SIZE; column++) {
        if (this.grid[row][column]== 0) count2++;
        if (count2 == location) {
          if (value < TWO_PROBABILITY) { grid[row][column] = 2; }
          else { grid[row][column] = 4; }
          return;
        }
      }
    }
  }
  
  /** 
   * Rotates the board by 90 degrees clockwise or 90 degrees 
   * counter-clockwise. 
   * @param boolean rotateClockwise - decides which direction should 
   * the board be rotated
   */
  
  public void rotate(boolean rotateClockwise) {
    
    int[][] copy = new int[this.GRID_SIZE][this.GRID_SIZE];
    
    //for loop for copying the calling object
    for (int row = 0; row < this.GRID_SIZE; row++) {
      for (int column = 0; column < this.GRID_SIZE; column++) {
        copy[row][column] = this.grid[row][column];
      }
    }
    
    //for rotating clockwise
    if (rotateClockwise) {    
      //for loops for copying from the copy to the calling object
      for (int sourceColumn = 0, targetRow = 0; sourceColumn < this.GRID_SIZE;
           sourceColumn++, targetRow++) {
             for (int sourceRow = 0, targetColumn = this.GRID_SIZE - 1;
                  sourceRow < this.GRID_SIZE; sourceRow++, targetColumn--) {
                    this.grid[targetRow][targetColumn] = copy[sourceRow][sourceColumn];
                  }
           }
    }
    
    //for rotating counterclockwise
    else if (rotateClockwise == false) {
      //for loops for copying from the copy to the calling object
      for (int sourceRow = 0, targetColumn = 0; sourceRow < this.GRID_SIZE;
           sourceRow++, targetColumn++) {
             for (int sourceColumn = 0, targetRow = this.GRID_SIZE - 1;
                  sourceColumn < this.GRID_SIZE; sourceColumn++, targetRow--) {
                    this.grid[targetRow][targetColumn] = copy[sourceRow][sourceColumn];
                  }
           }
    }
  }
  
  /** 
   * Determines if the input file is in correct format 
   * @param String inputFile - name of the input file
   * @return boolean. True if the file is in correct format, false if it isn't.
   */
  
  public static boolean isInputFileCorrectFormat(String inputFile) {
    //The try and catch block are used to handle any exceptions
    
    try {
      //checks if the file exists
      File inputFileObject = new File(inputFile);
      Scanner scanner = new Scanner(inputFileObject);
      
      //checks if the grid size is a valid number
      int boardSize = scanner.nextInt();
      if (boardSize > 2) {
        System.out.println("Board size number valid");
      }
      
      else {
        System.out.println("Board size number not valid");
        return false;}
      
      //checks if the score is a valid number (and is actually a number)
      if (scanner.hasNextInt()) {
        int score = scanner.nextInt();
        if (score >= 0 && score % 2 == 0) {
          System.out.println("Score valid");
        }
        else { 
          System.out.println("Score not valid");
          return false;
        }
      }
      else { 
        System.out.println("Score not valid");
        return false;
      }
      
      //checks if everything in the board is valid
      for (int i = 0; i < boardSize*boardSize; i++) {
        if (scanner.hasNextInt()) { 
          int boardElement = scanner.nextInt();
          if (boardElement >= 0 && ((boardElement - 1) & boardElement) == 0) {
            System.out.println("Tile number " + (i + 1) + " is valid");
          }
          else { 
            System.out.println("One of the tiles have invalid value");
            return false;
          } 
        }
        else {
          System.out.println("One of the tiles have invalid value");
          return false;}
      }
      
      //checks if there are anything extra in the file
      if (scanner.hasNext()) {
        System.out.println("Error: There are extra content in the file");
        return false;
      }
      
      scanner.close();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  /** 
   * Moves tiles and increments in the game according passed in direction
   * 
   * @param Direction direction - direction to which the move must be made
   * @return boolean - true if the move is possible, false if not
   */
  public boolean move(Direction direction) {
    if (this.canMove(direction) == false) {
      System.out.println("Can't move to that direction, pick another move.");
      return false;
    }
    //saves the board and score before moving
    this.prevScore = this.score;
    for (int row = 0; row < this.GRID_SIZE; row++) {
      for (int column = 0; column < this.GRID_SIZE; column++) {
        this.prevGrid[row][column] = this.grid[row][column];
      }
    }
    
    //invokes helper methods according to direction parameter
    if (direction == Direction.LEFT) {
      this.moveLeft();
    }
    if (direction == Direction.RIGHT) {
      this.moveRight();
    }
    if (direction == Direction.UP) {
      this.moveUp();  
    }
    if (direction == Direction.DOWN) {
      this.moveDown();
    }
    return true;
  }
  
  /** 
   * Moves tiles left and increments score accordingly
   */
  private void moveLeft() {
    
    int rightNumber, leftNumber = 0;
    
    //looping through the board
    for(int row = 0; row < this.GRID_SIZE; row++) {
      //ArrayList for storing a row of the board
      ArrayList<Integer> arrList = new ArrayList<Integer>();
      for (int column = 0; column < this.GRID_SIZE; column++) {
        
        //removing 0s and copying rest to an arrayList
        if (this.grid[row][column] != 0) {
          arrList.add(this.grid[row][column]);
        }
      }
      //checking if adjacent numbers are of the same value, 
      //adds them if they are and increments score
      for (int i = 0; i < arrList.size()-1; i++) { 
        leftNumber = arrList.get(i);
        rightNumber = arrList.get(i+1);
        if (leftNumber == rightNumber) {
          int sum = leftNumber + rightNumber;
          arrList.set(i, sum);
          arrList.remove(i+1);
          this.score += sum;
        }
      }
      //adding missing 0s
      for (int i = arrList.size(); i < this.GRID_SIZE; i++) {
        arrList.add(0);
      }
      //copying arraylist to board
      for (int column2 = 0; column2 < this.GRID_SIZE; column2++) {
        this.grid[row][column2] = arrList.get(column2);
      }  
    }
  }
  
  /** 
   * Moves tiles right and increments score accordingly
   */
  private void moveRight() {
    
    int rightNumber, leftNumber = 0;
    
    //looping through the board (from up to down and from right to left)
    for(int row = 0; row < this.GRID_SIZE; row++) {
      //ArrayList for storing a row of the board
      ArrayList<Integer> arrList = new ArrayList<Integer>();
      for (int column = this.GRID_SIZE-1; column >= 0 ; column--) {
        
        //removing 0s and copying rest to an arrayList
        if (this.grid[row][column] != 0) {
          arrList.add(this.grid[row][column]);
        }
      }
      //checking if adjacent numbers are of the same value, adds them if they are
      //and increments score
      for (int i = 0; i < arrList.size()-1; i++) { 
        leftNumber = arrList.get(i);
        rightNumber = arrList.get(i+1);
        if (leftNumber == rightNumber) {
          int sum = leftNumber + rightNumber;
          arrList.set(i, sum);
          arrList.remove(i+1);
          this.score += sum;
        }
      }
      //adding missing 0s
      for (int i = arrList.size(); i < this.GRID_SIZE; i++) {
        arrList.add(0);
      }
      //copying arraylist to board
      for (int column2 = 0, column3 = this.GRID_SIZE-1; column2 < this.GRID_SIZE; 
           column2++, column3--) {
             this.grid[row][column2] = arrList.get(column3);
           }  
    }
  }
  
  
  /** Moves tiles up and increments score accordingly
   */
  private void moveUp() {
    
    int upperNumber, lowerNumber = 0;
    
    //looping through the board
    for(int column = 0; column < this.GRID_SIZE; column++) {
      //ArrayList for storing a column of the board
      ArrayList<Integer> arrList = new ArrayList<Integer>();
      for (int row = 0; row < this.GRID_SIZE; row++) {
        
        //removing 0s and copying rest to an arrayList
        if (this.grid[row][column] != 0) {
          arrList.add(this.grid[row][column]);
        }
      }
      //checking if adjacent numbers in the same column are of the same value, 
      //adds them if they are and increments score
      for (int i = 0; i < arrList.size()-1; i++) { 
        upperNumber = arrList.get(i);
        lowerNumber = arrList.get(i+1);
        if (upperNumber == lowerNumber) {
          int sum = upperNumber + lowerNumber;
          arrList.set(i, sum);
          arrList.remove(i+1);
          this.score += sum;
        }
      }
      //adding missing 0s
      for (int i = arrList.size(); i < this.GRID_SIZE; i++) {
        arrList.add(0);
      }
      //copying arraylist to board
      for (int row2 = 0; row2 < this.GRID_SIZE; row2++) {
        this.grid[row2][column] = arrList.get(row2);
      }  
    }
  }
  
  /** 
   * Moves tiles down and increments score accordingly
   */
  private void moveDown() {
    
    int upperNumber, lowerNumber = 0;
    
    //looping through the board from down to up and from left to right
    for(int column = 0; column < this.GRID_SIZE; column++) {
      //ArrayList for storing a column of a board
      ArrayList<Integer> arrList = new ArrayList<Integer>();
      for (int row = this.GRID_SIZE-1; row >= 0 ; row--) {
        
        //removing 0s and copying rest to an arrayList
        if (this.grid[row][column] != 0) {
          arrList.add(this.grid[row][column]);
        }
      }
      //checking if adjacent numbers in the same column are of the same value, 
      //adds them if they are and increments score
      for (int i = 0; i < arrList.size()-1; i++) { 
        lowerNumber = arrList.get(i);
        upperNumber = arrList.get(i+1);
        if (lowerNumber == upperNumber) {
          int sum = lowerNumber + upperNumber;
          arrList.set(i, sum);
          arrList.remove(i+1);
          this.score += sum;
        }
      }
      //adding missing 0s
      for (int i = arrList.size(); i < this.GRID_SIZE; i++) {
        arrList.add(0);
      }
      //copying arraylist to board
      for (int row2 = 0, row3 = this.GRID_SIZE-1; row2 < this.GRID_SIZE; 
           row2++, row3--) {
             this.grid[row2][column] = arrList.get(row3);
           }  
    }
  }
  
  /** 
   * Checks if there are no more possible moves, i.e if the game is over
   * @return boolean - true if there are no possible moves, false if there are
   */
  public boolean isGameOver() {
    if (this.canMoveRight() || this.canMoveLeft() || this.canMoveUp() ||
        this.canMoveDown()) {
      return false; }
    return true;
  }
  
  /** 
   * Checks if there are no more possible moves, i.e if the game is over
   * @return boolean - true if there are no possible moves, false if there are
   */
  private boolean canMoveRight() {
    //for loop for looping through the board
    for (int row = 0; row < this.GRID_SIZE; row++) {
      for (int column = 0; column < this.GRID_SIZE-1; column++) {
        
        int leftTile = this.grid[row][column];
        int rightTile = this.grid[row][column+1];
        
        //checks if there is an empty space to the right of the tile
        if (leftTile != 0 && rightTile == 0) {
          return true; }
        if (leftTile != 0 && rightTile != 0 && leftTile == rightTile) {
          return true; }
      }
    }
    return false;
  }
  
  /** 
   * Checks if there are no more possible moves, i.e if the game is over
   * @return boolean - true if there are no possible moves, false if there are
   */
  private boolean canMoveLeft() {
    //for loop for looping through the board
    for(int row = 0; row < this.GRID_SIZE; row++) {
      for (int column = 0; column < this.GRID_SIZE-1; column++) {
        
        int leftTile = this.grid[row][column];
        int rightTile = this.grid[row][column+1];
        
        //checks if there is any empty space to the left of the tile
        if (leftTile == 0 && rightTile != 0) {
          return true;
        }
        //checks if adjacent tiles have equal value
        if (leftTile != 0 && rightTile != 0 && leftTile == rightTile) {
          return true;
        }
      } 
    }
    return false;
  } 
  
  /** 
   * Checks if there are no more possible moves, i.e if the game is over
   * @return boolean - true if there are no possible moves, false if there are
   */
  private boolean canMoveUp() {
    // for loop for looping through the board
    for (int column = 0; column < this.GRID_SIZE; column++) {
      for (int row = 0; row < this.GRID_SIZE - 1; row++) {
        
        int upperTile = this.grid[row][column];
        int lowerTile = this.grid[row+1][column];
        //checks if there is any empty space above the tile
        if (upperTile == 0 && lowerTile != 0 ) {
          return true;
        }
        //checks if adjacent tiles in one column are equal
        if (lowerTile != 0 && upperTile != 0 && upperTile == lowerTile) {
          return true;
        }
      } 
    }
    return false;
  }
  
  /** 
   * Checks if there are no more possible moves, i.e if the game is over
   * @return boolean - true if there are no possible moves, false if there are
   */
  private boolean canMoveDown() {
    // for loop for looping through the board
    for (int column = 0; column < this.GRID_SIZE; column++) {
      for (int row = 0; row < this.GRID_SIZE - 1; row++) {
        
        int upperTile = this.grid[row][column];
        int lowerTile = this.grid[row+1][column];
        //System.out.println("lower" + lowerTile + "upper" + upperTile);
        //checks if there is any empty space below the tile
        if (upperTile != 0 && lowerTile == 0 ) {
          return true;
        }
        //checks if adjacent tiles in one column are equal
        if (upperTile != 0 && lowerTile != 0 && upperTile == lowerTile) {
          return true;
        }
      } 
    }
    return false;
  }
  
  /** 
   * Checks if a move to a specific direction is possible
   * @param Direction direction - the direction to be checked
   * @return boolean - true if the move is possible, false if not
   */
  public boolean canMove(Direction direction) {
    
    //invokes helper methods according to direction paramenter  
    if (direction.equals(Direction.UP)) {
      return this.canMoveUp();
    }
    if (direction.equals(Direction.DOWN)) {
      return this.canMoveDown();
    }
    if (direction.equals(Direction.RIGHT)) {
      return this.canMoveRight();
    }
    if (direction.equals(Direction.LEFT)) {
      boolean toReturn = this.canMoveLeft();
      return toReturn;
    }
    
    return false;
  }
  
// Return the reference to the 2048 Grid
  public int[][] getGrid() {
    return grid;
  }
  
// Return the score
  public int getScore() {
    return score;
  }
  
  @Override
  public String toString() {
    StringBuilder outputString = new StringBuilder();
    outputString.append(String.format("Score: %d\n", score));
    for (int row = 0; row < GRID_SIZE; row++) {
      for (int column = 0; column < GRID_SIZE; column++)
        outputString.append(grid[row][column] == 0 ? "    -" :
                              String.format("%5d", grid[row][column]));
      
      outputString.append("\n");
    }
    return outputString.toString();
  }
  
  /* 
   * Reverts one move back
   */
  public void undo() {
    
    //copying previous board and score onto the current board and score
    this.score = this.prevScore;
    for (int row = 0; row < this.GRID_SIZE; row++) {
      for (int column = 0; column < this.GRID_SIZE; column++) {
        this.grid[row][column] = this.prevGrid[row][column];
      }
    }
    System.out.println(this.toString());
  }
}

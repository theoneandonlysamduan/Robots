import java.util.Random;

/**
 * EfficientRobot is a subclass of Robot.
 * 
 * EfficientRobot attempts to generate a efficient through Q learning. 
 * 
 * General rule: 
 * 1. Every time the robot moves its fitness reduce by 1. 
 * 2. When the robot goes onto the wall, fitness = fitness - 100, end iteration. 
 * 3. When the robot goes to the exit, fitness = fitness + 500, end iteration. 
 * 
 * 
 * @author sam
 */
public class EfficientRobot extends Robot
{
	double[][][] qGrid; 		//Replica of maze grid with Q values. 
	int[][] rewardGrid; 		//Replica of maze grid with reward values. 
	int fitness; 
	double trainRate; 
	double futureWeight; 
	
	double epsilon; 			//Used in the epsilon greedy algorithm, 
								//represents the probability for the robot 
								//to go explore. Reduce by 1% every iteration. 
								//When actually running the program, it is set
								//to 0. 
	
	static final int ACTIONS = 4; 
	
    /**
     * Creates a new EfficientRobot with the given maze
     * @param theMaze the maze to traverse
     * @pre theMaze is not null
     */
    public EfficientRobot(Maze theMaze){
    	//Initialize everything. 
        super(theMaze);
        boolean[][] maze = theMaze.getMazeGrid(); 
        qGrid = new double [maze.length][maze[0].length][ACTIONS];
        rewardGrid = new int [maze.length][maze[0].length];
        epsilon = 1; 
        trainRate = 0.2; 
        futureWeight = 0.5; 
        
        Location exit = theMaze.getMazeExit(); 
        Location entrance = theMaze.getMazeEntrance(); 
        
        //qGrid is initialized to 0, fitnessGrid need further processing. 
        //All blocks that is not part of the wall gets set to -1, walls are -100, exit is 500. 
        for (int i = 0; i < maze.length; i ++) {
        	for (int j = 0; j < maze[i].length; j ++) {
        		if (maze[i][j]) {
        			rewardGrid[i][j] = -1; 
        		}else {
        			rewardGrid[i][j] = -100; 
        		}
        	}
        }
        int exitX = exit.getXCoordinate(); 
        int exitY = exit.getYCoordinate();
        rewardGrid[exitX][exitY] = 500; 
        
        //Set the location of the robot to the entrance. 
        super.setCurrentLocation(entrance);
    }
    
    /**
     * Moves the robot one step.
     * 
     * Wall-hitting and finishing the maze is handled in ifEndOfIteration(), pulled up by train(). 
     */
    public void move(){
    	Location currentLoc = super.getCurrentLocation(); 
    	int currentX = currentLoc.getXCoordinate(); 
    	int currentY = currentLoc.getYCoordinate(); 
    	//Target X and Y initialized to 0, but should be overwritten anyway. 
    	int targetX = 0; 
    	int targetY = 0; 
    	
    	boolean[][] maze = super.getMaze().getMazeGrid(); 
    	
    	//Generate the target coordinates accordingly. 
    	switch (this.getFacingDirection()){
    	case NORTH: 
    		targetX = currentX - 1; 
    		break; 
    	case SOUTH: 
    		targetX = currentX + 1;
    		break; 
    	case EAST: 
    		targetY = currentY + 1; 
    		break; 
    	case WEST: 
    		targetY = currentY - 1; 
    		break; 
    	}
    	
    	//Check for OOB. If OOB, not move at all. 
    	if (targetX < 0) {
    		targetX = 0; 
    	}
    	if (targetX > maze.length) {
    		targetX = maze.length; 
    	}
    	if (targetY < 0) {
    		targetY = 0; 
    	}
    	if (targetY > maze[0].length) {
    		targetY = maze[0].length; 
    	}
    	
    	//Initialize new location. 
    	Location target = new Location (targetX, targetY); 
    	//Set the location of the robot. 
    	super.setCurrentLocation(target);
    	
    	//Work on fitness TODO: Might not need this. 
    	//this.fitness += this.rewardGrid[targetX][targetY]; 
    }
    
    /**
     * Trains the robot. 
     */
    private void train() {
    	//Three steps: 
    	//0. Decide whether to explore or exploit (which sets facing direction). 
    	//1. Move 
    	//2. Update Q table. 
    	//3. Update epsilon. 
    	//4. Check if end-of-iteration. 
    }
    
    private boolean ifEndOfIteration() {
    	Maze mazeMaze = super.getMaze(); 
    	boolean[][] maze = mazeMaze.getMazeGrid(); 
    	Location currentLoc = super.getCurrentLocation(); 
    	int x = currentLoc.getXCoordinate(); 
    	int y = currentLoc.getYCoordinate(); 
    	
    	//Check to see if move legal. 
    	if (!maze[x][y]) {
    		return true; 
    	}
    	
    	//Check to see if robot finished. 
    	if (currentLoc.equals(mazeMaze.getMazeExit())) {
    		return true; 
    	}
    	
    	return false; 
    }
    
    /**
     * End-of-Iteration subroutine. 
     * End of iteration entered under following circumstances: 
     * 1. The robot hits the wall. 
     * 2. The robot finishes the maze. 
     * 
     * What to do in endOfIteration: 
     * 1. Reset robot location. 
     * 
     */
    private void endofIteration() {
    	Maze mazeMaze  = super.getMaze(); 
    	Location reset = mazeMaze.getMazeEntrance(); 
    	
    	super.setCurrentLocation(reset);
    }
    /**
     * Used in epsilon greedy algorithm. 
     * Explore: Set facing direction to a random direction. 
     */
    private void explore() {
    	Random rand = new Random(); 
    	
    	int opt = rand.nextInt() % 4; 
    	
    	switch (opt) {
    	case 0: 
    		super.setFacingDirection(Direction.NORTH);
    		break; 
    	case 1: 
    		super.setFacingDirection(Direction.SOUTH);
    		break; 
    	case 2: 
    		super.setFacingDirection(Direction.EAST);
    		break; 
    	case 3: 
    		super.setFacingDirection(Direction.WEST);
    		break; 
    	}
    }
    
    /**
     * Used in epsilon greedy algorithm. 
     * Exploit: Set facing direction to the direction with greatest Q value. 
     */
    private void exploit() {
    	int action = this.getMaxNextQAction(); 
    	
    	switch (action) {
    	case 0: 
    		super.setFacingDirection(Direction.NORTH);
    		break; 
    	case 1: 
    		super.setFacingDirection(Direction.SOUTH);
    		break; 
    	case 2: 
    		super.setFacingDirection(Direction.EAST);
    		break; 
    	case 3: 
    		super.setFacingDirection(Direction.WEST);
    		break; 
    	}
    }
    
    /**
     * Refreshes the Q-value at a given state. 
     * Q values should be refreshed after moving. 
     */
    private void refreshQ() {
    	Location currentLoc = super.getCurrentLocation(); 
    	int currentX = currentLoc.getXCoordinate(); 
    	int currentY = currentLoc.getYCoordinate(); 
    	Direction currentDir = super.getFacingDirection(); 	//currentDir is translated to an int as currentState. 
    	int currentAction = 0; 
    	double updatedQ = 0; 		//Stores updated Q. Gets put into the qGrid. 
    	
    	switch (currentDir) {
    	case NORTH: 
    		currentAction = 0; 
    		break; 
    	case SOUTH: 
    		currentAction = 1; 
    		break; 
    	case EAST: 
    		currentAction = 2; 
    		break; 
    	case WEST: 
    		currentAction = 3; 
    		break; 
    	}
    	
    	//Bellman Equation part (OOB done in getMaxNextQ and peekReward): 
    	int nextReward = this.peekReward(); 
    	double currentQ = qGrid[currentX][currentY][currentAction]; 
    	double maxNextQ = this.getMaxNextQ();
    	updatedQ = currentQ + trainRate * (nextReward + futureWeight * maxNextQ - currentQ); 
    }
    
    /**
     * Peeks into next location, but does not set the location of the robot to that location. 
     * 
     * @return The reward at the location the robot is facing. 
     */
    private int peekReward(){
    	Location currentLoc = super.getCurrentLocation(); 
    	int currentX = currentLoc.getXCoordinate(); 
    	int currentY = currentLoc.getYCoordinate(); 
    	//Target X and Y initialized to 0, but should be overwritten anyway. 
    	int targetX = 0; 
    	int targetY = 0; 
    	
    	boolean[][] maze = super.getMaze().getMazeGrid(); 
    	
    	//Generate the target coordinates accordingly. 
    	switch (this.getFacingDirection()){
    	case NORTH: 
    		targetX = currentX - 1; 
    		break; 
    	case SOUTH: 
    		targetX = currentX + 1;
    		break; 
    	case EAST: 
    		targetY = currentY + 1; 
    		break; 
    	case WEST: 
    		targetY = currentY - 1; 
    		break; 
    	}
    	
    	//Check for OOB.
    	if (targetX < 0) {
    		targetX = 0; 
    	}
    	if (targetX > maze.length) {
    		targetX = maze.length; 
    	}
    	if (targetY < 0) {
    		targetY = 0; 
    	}
    	if (targetY > maze[0].length) {
    		targetY = maze[0].length; 
    	}
    	//Return the reward value of the target location. 
    	return rewardGrid[targetX][targetY]; 
    }
    
    /**
     * Looks for the maximum Q value for the block the robot is facing. 
     * @return the maximum Q value. 
     */
    private double getMaxNextQ() {
    	double maxQ; 
    	Location currentLoc = super.getCurrentLocation(); 
    	int currentX = currentLoc.getXCoordinate(); 
    	int currentY = currentLoc.getYCoordinate(); 
    	int targetX = currentX; 
    	int targetY = currentY; 
    	Maze mazeMaze = super.getMaze(); 
    	boolean[][] maze = mazeMaze.getMazeGrid(); 
    	Direction facingDir = super.getFacingDirection(); 
    	//Generate target location. 
    	switch (facingDir) { 
    	case NORTH: 
    		targetX = currentX - 1; 
    		break; 
    	case SOUTH: 
    		targetX = currentX + 1;
    		break; 
    	case EAST: 
    		targetY = currentY + 1; 
    		break; 
    	case WEST: 
    		targetY = currentY - 1; 
    		break; 
    	}
    	//Check for OOB. 
    	if (targetX < 0) {
    		targetX = 0; 
    	}
    	if (targetX > maze.length) {
    		targetX = maze.length; 
    	}
    	if (targetY < 0) {
    		targetY = 0; 
    	}
    	if (targetY > maze[0].length) {
    		targetY = maze[0].length; 
    	}
    	
    	//At this point target coordinates is determined. Use a for loop to go through the possible actions
    	// to get the maximum Q. 
    	maxQ = qGrid[targetX][targetY][0]; 
    	for (int i = 0; i < ACTIONS; i ++) {
    		if (qGrid[targetX][targetY][i] > maxQ) {
    			maxQ = qGrid[targetX][targetY][i]; 
    		}
    	}
    	
	return maxQ; 
    }
    
    /**
     * Looks for the maximum Q value and gets the action needed to get there. 
     * @return the action (a number between 0 - 3) needed to get to maxQ. 
     * 
     * TODO: Wrong algorithm. Need to check the Q value of all surrounding blocks. 
     */
    private int getMaxNextQAction() {
    	double maxQ; 
    	int maxQIndex = 0; 
    	Location currentLoc = super.getCurrentLocation(); 
    	int currentX = currentLoc.getXCoordinate(); 
    	int currentY = currentLoc.getYCoordinate(); 
    	int targetX = currentX; 
    	int targetY = currentY; 
    	Maze mazeMaze = super.getMaze(); 
    	boolean[][] maze = mazeMaze.getMazeGrid(); 
    	
    	//TODO: Add correct algorithm here. 
    	
    	//Check for OOB. 
    	if (targetX < 0) {
    		targetX = 0; 
    	}
    	if (targetX > maze.length) {
    		targetX = maze.length; 
    	}
    	if (targetY < 0) {
    		targetY = 0; 
    	}
    	if (targetY > maze[0].length) {
    		targetY = maze[0].length; 
    	}
    	
    	//At this point target coordinates is determined. Use a for loop to go through the possible actions
    	// to get the maximum Q. 
    	maxQ = qGrid[targetX][targetY][0]; 
    	for (int i = 0; i < ACTIONS; i ++) {
    		if (qGrid[targetX][targetY][i] > maxQ) {
    			maxQ = qGrid[targetX][targetY][i]; 
    			maxQIndex = i; 
    		}
    	}
    	
    	return maxQIndex; 
    }
    
    
    
}
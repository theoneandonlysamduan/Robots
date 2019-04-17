import java.util.Date;
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
	boolean endFlag; 
	
	double[][][] qGrid; 		//Replica of maze grid with Q values. 
	int[][] rewardGrid; 		//Replica of maze grid with reward values. 
	int fitness; 
	double trainRate; 
	double futureWeight; 
	
	double epsilon; 			//Used in the epsilon greedy algorithm, 
								//represents the probability for the robot 
								//to go explore. Rate of reduction set by 
	 							//EPSILON_REDUCTION_RATE. 
	
	static final int ACTIONS = 4; 
	static final double END_OF_TRAINING_EPSILON_THRESHOLD = 0.00001;//epsilon will be reduced each iteration. This is 
																	//the threshold at which the training is considered
																	//to be finished. 
	static final double EPSILON_REDUCTION_RATE = 0.95; 				//The rate of reduction of epsilon. It is multiplied
																	//to epsilon at each end of iteration. 
	
    /**
     * Creates a new EfficientRobot with the given maze
     * @param theMaze the maze to traverse
     * @pre theMaze is not null
     */
    public EfficientRobot(Maze theMaze){
    	//Initialize everything. 
        super(theMaze);
        
        endFlag = false; 
        
        boolean[][] maze = theMaze.getMazeGrid(); 
        qGrid = new double [maze.length][maze[0].length][ACTIONS];
        rewardGrid = new int [maze.length][maze[0].length];
        epsilon = 1; 
        trainRate = 0.2; 
        futureWeight = 0.5; 
        
        Location exit = theMaze.getMazeExit(); 
        Location entrance = theMaze.getMazeEntrance(); 
        
        //qGrid is initialized to 0, fitnessGrid need further processing. 
        //The reward on all blocks that is not part of the wall gets set to -1, walls are -100, exit is 500. 
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
     * Due to the limitations of the RobotRunner class, move method here is used as a runner for the robot
     * instead of what it's supposed to do. The intended function move() is implemented by moveRobot. 
     * 
     * Things it does:  
     * 1. Invoke train(), 
     * 2. After training (determined by the epsilon number), run the robot again but only exploits. 
     * 3. Print the maze with the robot. 
     * 4. Set the endFlag to true, which is then returned by isAtEnd. 
     */
    public void move() {
    	//train() ends when end-of-training conditions are satisfied. 
    	this.train(); 
    	
    	//Run the robot again but only exploit this time. 
    	//Reset the robot to the entrance. Epsilon does not need to be set to 0 because we can just invoke exploit() and
    	//set the direction no problem. 
    	Location entrance = super.getMaze().getMazeEntrance(); 
    	super.setCurrentLocation(entrance);
    	final double WAIT_TIME = 0.25; 
    	
    	//ifEndOfIteration checks to see whether the robot has hit a wall (which, after training, is impossible due to 
    	//changed Q values) or finished the maze, hence it can be used as a flag for finishing up the maze .
    	while (!ifEndOfIteration()) {
    		exploit(); 
    		moveRobot();
    		//printing of the robot has to be handled here because the RobotRunner does not work properly.
    		//clear screen, hard coded here. 
    		for (int i = 0; i < 50; i ++) {
    			System.out.println("\n"); 
    		}
    		System.out.println(this);
    		pause(WAIT_TIME); 
    	}
    	//After end of the final iteration, set the endFlag to true, which is to be returned by isAtEnd method to end 
    	//the runner loop. 
    	endFlag = true; 
    }
    /**
     * pause method pulled from RobotRunner. 
     * pauses the printing of the robot. 
     * @param seconds
     */
    private static void pause (double seconds) {
        Date start = new Date();
        Date end = new Date();
        while (end.getTime() - start.getTime() < seconds * 1000) {
            end = new Date();
        }
    }
    
    /**
     * Returns endFlag, a boolean value that determines whether the robot has finished everything (training and display
     * of training). Overwrites the isAtEnd method in Robot class for greater level of control. 
     * 
     * This is involved in the flow control in RobotRunner class. 
     * 
     * @return whether the roobt has finished everything. 
     * 
     */
    public boolean isAtEnd() {
    	return endFlag; 
    }
    
    /**
     * Moves the robot one step.
     * 
     * Wall-hitting and finishing the maze is handled in ifEndOfIteration(), pulled up by train(). 
     */
    private void moveRobot(){
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
    	Random rand = new Random (); 
    	double stratKey = 0; 
    	
    	while (true) {
    		
    		stratKey = rand.nextDouble (); 	//stratKey(used in Epsilon greedy algorithm) is used to decide wether to
    										//explore or exploit. 
    		//Pre-processing of stratKey because epsilon goes from 1 to 0. 
    		while (stratKey > 1) {
    			stratKey = stratKey / 10; 
    		}
    		//Epsilon is the possibility for exploration, which is gradually decreased each generation. 
    		if (stratKey < epsilon) {
    			explore(); 
    		}else {
    			exploit(); 
    		}
    		//Update Q after direction is determined. 
    		this.refreshQ();
    		//Explore and exploit sets the facing direction, so move needs to be invoked. 
    		this.moveRobot(); 
    		//Check to see if it is end of iteration. 
    		if (ifEndOfIteration()) {
    			//If end of iteration, invoke the handler. 
    			this.endofIteration();
    		}
    		
    		//If epsilon is small enough (< END_OF_TRAINING_EPSILON_THRESHOLD), end training. 
    		if (this.epsilon < END_OF_TRAINING_EPSILON_THRESHOLD) {
    			break; 
    		}
    	}
    }
    
    /**
     * Checks to see if the current iteration of maze-venturing has ended. 
     * @return whether the maze-venturing has ended. 
     */
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
     * 2. Update epsilon. 
     */
    private void endofIteration() {
    	Maze mazeMaze  = super.getMaze(); 
    	Location reset = mazeMaze.getMazeEntrance(); 
    	
    	super.setCurrentLocation(reset);
    	this.epsilon *= EPSILON_REDUCTION_RATE; 
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
     * Exploit: Folllow the direction with the greatest Q value.  
     */
    private void exploit() {
    	int action = this.getMaxQAction(); 
    	
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
     * Gets the action that has the max Q value. 
     * The Q value at the current block represents all possible rewards that the robot can get in the future
     * by taking that action, hence no need to inspect the Q values on adjacent blocks. 
     * @return an int from 0 to 3, organized in "NORTH, SOUTH, EAST, WEST."
     */
    private int getMaxQAction() {
    	Location currentLoc = super.getCurrentLocation(); 
    	int currentX = currentLoc.getXCoordinate(); 
    	int currentY = currentLoc.getYCoordinate(); 
    	
    	double maxQ   = qGrid[currentX][currentY][0]; 
    	int maxQIndex = 0; 
    	
    	for (int i = 0; i < qGrid[currentX][currentY].length; i ++) {
    		if (qGrid [currentX][currentY][i] > maxQ) {
    			maxQ = qGrid[currentX][currentY][i]; 
    			maxQIndex = i; 
    		}
    	}
    	
    	return maxQIndex; 
    }
    /**
     * Looks for the maximum Q value in among the adjacent blocks and returns the action the robot needs to
     * get there. 
     * 
     * @retun	An int from 0 - 3, ordered in "NORTH, SOUTH, EAST, WEST". 
     * 
     */
    /**
    private int getMaxNextQAction() {
    	double maxQ; 
    	int maxQIndex = 0; 
    	Location currentLoc = super.getCurrentLocation(); 
    	int currentX = currentLoc.getXCoordinate(); 
    	int currentY = currentLoc.getYCoordinate();  
    	Maze mazeMaze = super.getMaze(); 
    	boolean[][] maze = mazeMaze.getMazeGrid(); 
    	
    	Direction[] dirArr = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}; 
    	Location[] adjacentLocs = new Location[] {currentLoc.getAdjacentLocationTowards(Direction.NORTH), 
    											  currentLoc.getAdjacentLocationTowards(Direction.SOUTH), 
    											  currentLoc.getAdjacentLocationTowards(Direction.EAST), 
    											  currentLoc.getAdjacentLocationTowards(Direction.WEST)};
    	//Find out which location has the maximum Q value. 
    	//TODO: Fully implement the finding of the maxQ. 
    	int dirIndex = 0; 
    	for (int i = 0; i < adjacentLocs.length; i ++) {
    		
    		//If OOB, invalidate the location(pass the loop.)
    		if (adjacentLocs[i].getXCoordinate() < 0) {
    			continue; 
    		}
    		if (adjacentLocs[i].getXCoordinate() > maze.length) {
    			continue; 
    		}
    		if (adjacentLocs[i].getYCoordinate() < 0) {
    			continue; 
    		}
    		if (adjacentLocs[i].getYCoordinate() > maze[0].length) {
    			continue; 
    		}
    		
    		
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
    
    */
    
}
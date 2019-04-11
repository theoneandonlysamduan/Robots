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
	int[][] qGrid; 				//Replica of maze grid with Q values. 
	int[][] fitnessGrid; 		//Replica of maze grid with fitness values. 
	
	double epsilon; 			//Used in the epsilon greedy algorithm, represents the probability for the robot to go explore. 
	
    /**
     * Creates a new EfficientRobot with the given maze
     * @param theMaze the maze to traverse
     * @pre theMaze is not null
     */
    public EfficientRobot(Maze theMaze){
    	//Initialize everything. 
        super(theMaze);
        boolean[][] maze = theMaze.getMazeGrid(); 
        qGrid = new int [maze.length][maze[0].length];
        fitnessGrid = new int [maze.length][maze[0].length];
        epsilon = 1; 
        
        Location exit = theMaze.getMazeExit(); 
        Location entrance = theMaze.getMazeEntrance(); 
        
        //qGrid is initialized to 0, fitnessGrid need further processing. 
        //All blocks that is not part of the wall gets set to -1, walls are -100, exit is 500. 
        for (int i = 0; i < maze.length; i ++) {
        	for (int j = 0; j < maze[i].length; j ++) {
        		if (maze[i][j]) {
        			fitnessGrid[i][j] = -1; 
        		}else {
        			fitnessGrid[i][j] = -100; 
        		}
        	}
        }
        int exitX = exit.getXCoordinate(); 
        int exitY = exit.getYCoordinate();
        fitnessGrid[exitX][exitY] = 500; 
        
        //Set the location of the robot to the entrance. 
        super.setCurrentLocation(entrance);
    }
    
    /**
     * Moves the robot one step.
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
    	
    	//initialize new location. 
    	Location target = new Location (targetX, targetY); 
    	//TODO: Check to see if move legal. 
    	if (!maze[targetX][targetY]) {
    		this.EndofIteration(SituationID.HIT_WALL);
    	}else {
    		super.setCurrentLocation(target);
    	}
    }
    
    /**
     * Trains the robot. 
     */
    private void train() {
    	//Three steps: 
    	//1. Move about. 
    	//2. Determine status. 
    	//3. If end iteration, update Q table. 
    }
    
    
    /**
     * End-of-Iteration subroutine. 
     * End of iteration entered under following circumstances: 
     * 1. The robot hits the wall. 
     * 2. The robot finishes the maze. 
     */
    private void EndofIteration(SituationID id) {
    	
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
    	
    }
    
    /**
     * Refreshes the Q-table. 
     */
    private void refreshQTable() {
    	
    }
}
/**
 * RandomRobot is a subclass of Robot.
 * @author
 */

import java.util.Random;

public class RandomRobot extends Robot
{
    // instance fields here
	Direction dir_list[] = new Direction[] 
			{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	
	private Location currentLocation;
	private Maze wall;
    /**
     * Creates a new RandomRobot with the given maze.
     * @param theMaze the maze to traverse 
     * @pre theMaze is not null
     */
    public RandomRobot(Maze theMaze)
    {
        super(theMaze);
    }
      
    /**
     * Moves the robot one step.
     */
    public void move()
    {
        int digit;
        Random rand = new Random();
        digit = rand.nextInt(4);
        super.setFacingDirection(dir_list[digit]);
        if (!wall.isWall(currentLocation.getAdjacentLocationTowards(dir_list[digit]))) {
        	super.setCurrentLocation(currentLocation.getAdjacentLocationTowards(dir_list[digit]));
        }
    }
}
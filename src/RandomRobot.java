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
    }
}
/**
 * RightHandRuleRobot is a subclass of Robot.
 * @author
 */
public class RightHandRuleRobot extends Robot
{
    // instance fields here

    /**
     * Creates a new RightHandRobot with the given maze
     * @param theMaze the maze to traverse
     * @pre theMaze is not null
     */
    public RightHandRuleRobot(Maze theMaze)
    {
        super(theMaze);
    }

    /**
     * Moves the robot one step.
     */
    public void move()
    {
        this.getFacingDirection();
        switch(this.getFacingDirection()){
        case NORTH: 
        case SOUTH: 
        case EAST: 
        case WEST: 
        default: System.out.println("Something is wrong.");
        }
    }
}
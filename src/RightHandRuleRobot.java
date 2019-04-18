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
        case NORTH:	if(super.getMaze().isWall(this.getCurrentLocation().getAdjacentLocationTowards(Direction.EAST))){
        				this.setFacingDirection(Direction.WEST);
        			}else{
        				this.setFacingDirection(Direction.EAST);
        				this.setCurrentLocation(this.getCurrentLocation().getAdjacentLocationTowards(Direction.EAST));
        			}break;
        
        case SOUTH:	if(super.getMaze().isWall(this.getCurrentLocation().getAdjacentLocationTowards(Direction.WEST))){
        				this.setFacingDirection(Direction.EAST);
					}else{
						this.setFacingDirection(Direction.WEST);
						this.setCurrentLocation(this.getCurrentLocation().getAdjacentLocationTowards(Direction.WEST));
					}break;
        
        case EAST:	if(super.getMaze().isWall(this.getCurrentLocation().getAdjacentLocationTowards(Direction.SOUTH))){
        				this.setFacingDirection(Direction.NORTH);
        			}else{
        				this.setFacingDirection(Direction.SOUTH);
        				this.setCurrentLocation(this.getCurrentLocation().getAdjacentLocationTowards(Direction.SOUTH));
        			}break;
        
        case WEST:	if(super.getMaze().isWall(this.getCurrentLocation().getAdjacentLocationTowards(Direction.NORTH))){
						this.setFacingDirection(Direction.SOUTH);
					}else{
						this.setFacingDirection(Direction.NORTH);
						this.setCurrentLocation(this.getCurrentLocation().getAdjacentLocationTowards(Direction.NORTH));
					}break;
        
        default: System.out.println("Something is wrong.");
        }
    }
}
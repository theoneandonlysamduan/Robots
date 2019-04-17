/**
 * Robot is an abstract robot class that represents the robot which
 * traverses a given maze.
 * This class is meant to be subclassed into different robots, with
 * the move() method overridden.
 */
public abstract class Robot
{
    /* Instance Fields */
    private Maze maze;
    private Location currentLocation;
    private Direction facingDirection;

    /**
     * Creates a new robot with the given maze.
     * @param theMaze the maze to traverse 
     * @pre maze is not null
     */
    public Robot(Maze theMaze)
    {
        if (theMaze == null)
        {
            throw new IllegalArgumentException("The maze provided cannot be null!");
        }
        else
        {
            maze = theMaze;
            currentLocation = theMaze.getMazeEntrance();
            facingDirection = Direction.SOUTH;
        }
    }

    /**
     * Returns the robot's maze.
     * @return the maze the robot is in
     */
    public final Maze getMaze()
    {
        return maze;
    }

    /**
     * Sets the robot's current location.
     * @param location the new location
     */
    public final void setCurrentLocation(Location loc)
    {
        currentLocation = loc;
    }

    /**
     * Returns the robot's current location.
     * @return the current location
     */
    public final Location getCurrentLocation()
    {
        return currentLocation;
    }

    /**
     * Sets the direction the robot is facing.
     * @param dir the direction to face
     */
    public final void setFacingDirection(Direction dir)
    {
        facingDirection = dir;
    }

    /**
     * Returns the direction the robot is facing.
     * @return the direction
     */
    public final Direction getFacingDirection()
    {
        return facingDirection;
    }

    /**
     * Returns whether the robot has finished traversing the maze.
     * @return whether the robot is at the maze exit
     */
    public boolean isAtEnd()
    {
        return currentLocation.equals(maze.getMazeExit());
    }

    /**
     * Converts the robot and the maze to a string.
     * @return the robot and the maze as a string
     */
    public final String toString()
    {
        String description = "";
        for (int row = 0; row < maze.getSize(); row++)
        {
            for (int col = 0; col < maze.getSize(); col++)
            {
                Location spot = new Location(col, row);
                if (currentLocation.equals(spot))
                {
                    switch (facingDirection)
                    {
                        case NORTH:
                        description += "^";
                        break;
                        case EAST:
                        description += ">";
                        break;
                        case SOUTH:
                        description += "v";
                        break;
                        case WEST:
                        description += "<";
                        break;
                        default:
                        description += "x";
                        break;
                    }
                } 
                else if (spot.equals(maze.getMazeEntrance()))
                {
                    description += "N";
                }
                else if (spot.equals(maze.getMazeExit()))
                {
                    description += "X";
                }
                else
                {
                    description += maze.isWall(spot) ? "*" : " ";
                }               
                if (col < maze.getSize())
                {
                    description += " ";
                }
            }
            if (row < maze.getSize())
            {
                description += "\n";
            }
        }
        return description;
    }

    /**
     * Moves the robot one step in a certain direction (method must be subclassed).
     * The move is made by calling the setCurrentLocation and setFacingDirection methods.
     */
    public abstract void move();

}
/**
 * Location is a simple container for x- and y-coordinates with added
 * logic for adjacent spaces.
 * Location coordinates work as follows:
 * the origin (0, 0) is in the upper-left corner,
 * right (east) is the direction of increasing x,
 * down (south) is the direction of increasing y.
 * Used in the Maze and Robot classes to determine spaces in the maze
 * and navigation logic.
 * @author Itai Ferber
 * @version 2/15/11
 */
public class Location
{

    /* Instance Fields */
    private int xCoordinate;
    private int yCoordinate;

    /**
     * Creates a new location with the given coordinates.
     * @param x the initial x-coordinate
     * @param y the initial y-coordinate.
     */
    public Location(int x, int y)
    {
        xCoordinate = x;
        yCoordinate = y;
    }

    /**
     * Returns the adjacent location in the given direction.
     * @param dir the direction to go towards
     */
    public Location getAdjacentLocationTowards(Direction dir)
    {
        switch (dir)
        {
            case NORTH:
            return new Location(xCoordinate, yCoordinate - 1);
            case EAST:
            return new Location(xCoordinate + 1, yCoordinate);
            case SOUTH:
            return new Location(xCoordinate, yCoordinate + 1);
            case WEST:
            return new Location(xCoordinate - 1, yCoordinate);
            default:
            return null;
        }
    }

    /**
     * Returns the location's x-coordinate.
     * @return the x-coordinate
     */
    public int getXCoordinate()
    {
        return xCoordinate;
    }

    /**
     * Returns the location's y-coordinate.
     * @return the y-coordinate
     */
    public int getYCoordinate()
    {
        return yCoordinate;
    }

    /**
     * Returns whether two locations are equal.
     * @param other the other location to check 
     * (precondition: other is a Location object)
     */
    public boolean equals(Object other)
    {
        Location otherLocation = (Location) other;
        return otherLocation.getXCoordinate() == xCoordinate
            && otherLocation.getYCoordinate() == yCoordinate;
    }
}
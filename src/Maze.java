import java.io.*;
import java.util.Scanner;
import java.util.Random;

/**
 * Maze is a class that represents a maze, containing an n x n
 * array of booleans (wall / no wall) that direct movement.
 * Used in the Robot class.
 * @author Itai Ferber
 * @version 2/15/11
 */
public class Maze {

    /* Instance Fields */
    private boolean board[][];
    private Location mazeEntrance;
    private Location mazeExit;
    private int size;

    /**
     * Creates a new maze from the given file.
     * @param mazeFile the maze database to read from
     * @pre the maze database file uses * to denote walls
     *      N for the maze entrance
     *      X for the maze entrance
     *      mazes must be square
     */
    public Maze(File mazeFile) throws IOException
    {
        // Attempt to load file. Failure will throw an IOException.
        String mazeRow = null;
        this.size = 0;
        FileReader reader = null;
        Scanner in = null;
        try
        {
            // file input
            reader = new FileReader(mazeFile);
            in = new Scanner(reader);
            mazeRow = in.nextLine();
            int row = 0;
            // maze is assumed to be square
            size = mazeRow.length();
            board = new boolean[size][size];
            while (row < size)
            {
                // scan the current row
                for (int col = 0; col < size; col++)
                {
                    char ch = mazeRow.charAt(col);
                    switch (ch)
                    {
                        case '*':
                        board[row][col] = true;
                        break;
                        case 'N':
                        mazeEntrance = new Location(col, row);
                        break;
                        case 'X':
                        mazeExit = new Location(col, row);
                    }
                }
                // avoid error at last row of maze
                if (in.hasNextLine())
                {
                    mazeRow = in.nextLine();
                }
                row++;
            }
        }
        finally
        {
            in.close();
        }
    }

    /**
     * Returns whether the given location is valid for the maze.
     * @param loc the location to check
     */
    public boolean isValid(Location loc)
    {
        return loc.getXCoordinate() >= 0 
        && loc.getXCoordinate() < this.size
        && loc.getYCoordinate() >= 0 
        && loc.getYCoordinate() < this.size;
    }

    /**
     * Returns whether there is a wall at the given location.
     * @param loc the location to check 
     * @pre the location is valid
     */
    public boolean isWall(Location loc)
    {
        if (!isValid(loc))
        {
            throw new IllegalArgumentException("The location you provided is invalid!");
        }
        else
        {
            return board[loc.getYCoordinate()][loc.getXCoordinate()];
        }
    }

    /**
     * Returns the size of one side of the n x n maze.
     * @return the maze size
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Returns the location of the entrance to the maze.
     * @return the maze entrance
     */
    public Location getMazeEntrance()
    {
        return mazeEntrance;
    }

    /**
     * Returns the location of the exit to the maze.
     * @return the maze exit
     */
    public Location getMazeExit()
    {
        return mazeExit;
    }

    /**
     * Returns a boolean array representing the walls of the maze.
     * EDITED 3/17/15 for shallow copy problem
     * @return the maze
     */
    public boolean[][] getMazeGrid()
    {
        boolean[][] grid = new boolean[size][size];
        for (int row = 0; row < board.length; row++)
        {
            for (int col = 0; col < board[row].length; col++)
            {
                grid[row][col] = board[row][col];
            }
        }
        return grid;
    }
}

/**
 * RobotRunner
 * @author Itai Ferber / Mr Young
 * @version 2/15/14
 */

/**
 * Class Description: the class which interfaces with the user and runs the mazes.
 * Uses all other classes (which must not be changed!). 
 */
import java.io.*;
import java.util.Date;
import java.util.Scanner;

public class RobotRunner {

    private static final double WAIT_TIME = .25;
    private static final String MAZE_FILE = "~/Projects/Robots/maze9e.txt";
    //private static final String MAZE_FILE = "maze15a.txt";
    private static double factor;
    
    /**
     * The main method.
     * @param args the arguments passed to the program
     */
    public static void main (String[] args) {
        Scanner in = new Scanner(System.in);
        // Run the program loop.
        int chosenOption = 0;
        while (chosenOption != 4) {
            chosenOption = displayMenu(in);
            switch (chosenOption) {
                case 1:
                    // User chose option 1. Run a new RandomRobot.
                    factor = .5;
                    run(new RandomRobot(loadMaze()));
                    break;
                case 2:
                    // User chose option 2. Run a new RightHandRuleRobot.
                    factor = 1;
                    run(new RightHandRuleRobot(loadMaze()));
                    break;
                case 3:
                    // User chose option 3. Run a new EfficientRobot.
                    factor = 1;
                    run(new EfficientRobot(loadMaze()));
                    break;
                default:
                    // User chose option 4. Exit program.
                    break;
            }
        }
    }

    /**
     * Effectively clears the screen by printing 50 blank lines.
     */
    private static void clearScreen () {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * Uses the system clock to pause for the given number of seconds
     * @param seconds the number of seconds to pause
     */
    private static void pause (double seconds) {
        Date start = new Date();
        Date end = new Date();
        while (end.getTime() - start.getTime() < seconds * 1000) {
            end = new Date();
        }
    }

    /**
     * Displays the option menu to the user and returns their choice.
     * @return the choice (1-4) that the user has made
     */
    private static int displayMenu (Scanner in) {
        clearScreen();
        System.out.println("Please choose an option:");
        System.out.println("1. Run RandomRobot through a maze.");
        System.out.println("2. Run RightHandRuleRobot through a maze.");
        System.out.println("3. Run EfficientRobot through a maze.");
        System.out.println("4. Exit.");

        System.out.println("\nEnter your choice: ");
        int response = in.nextInt();
        while (response < 1 || response > 4)
        {
            System.out.println("Sorry, that's not a valid option!");
            System.out.println("\nEnter your choice: ");
            response = in.nextInt();
        }
        return response;
    }

    /**
     * Loads a maze from the 'mazes.dat' file provided with the program.
     * @return a new maze
     */
    private static Maze loadMaze () {
        // Attempt to load file. Failure ends in an error message.
        try {
            return new Maze(new File(MAZE_FILE));
        } catch (IOException e) {
            System.out.print("Could not load mazes file! Are you sure it's in the right place?");
            System.exit(1);
            return null;
        }
    }

    /**
     * Runs a given robot with a certain behavior.
     */
    private static void run (Robot robot) {
        // Start a run loop. While the robot is still running, continue.
        while (!robot.isAtEnd()) {
            // Print the interface.
            clearScreen();
            System.out.println("Running robot:");
            System.out.println(robot);
            pause(WAIT_TIME * factor);
            robot.move();
        }

        // Print interface.
        clearScreen();
        System.out.println("Robot escaped:");
        System.out.println(robot);
        
        pause(WAIT_TIME * 5);
        return;
    }
}
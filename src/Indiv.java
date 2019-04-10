import java.util.ArrayList;
import java.util.Random; 
/**
 * Individuals used in the training of the robot. 
 * 
 * @author sam
 *
 */
public class Indiv {
	int fitness; 						//Fitness for a given iteration. 
	ArrayList<Direction> dirSeq; 		//Sequence of directions generated. 
	Random rand; 
	
	Indiv(){
		fitness = Integer.MIN_VALUE; 
		
		rand = new Random(); 
		
		
	}
}

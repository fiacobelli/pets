import java.io.*;
import java.util.Scanner;

/**
 * This class is intended to provide basic unit testing. I believe it is 
 * quicker and simpler to use than jUnit and it is aimed at teachers.
 * 
 * It is based on what I have had to grade when teaching 
 * introductory level programming courses. I find it saves tons of time.
 * 
 * This class provides the methods necessary to test a range of equalities.
 * testEquals is the main method here. You can test whether two arrays are equal,
 * whether two Objects are equal, whether two ints or doubles are equal, etc.
 * On top of that, each test can have points associated to it.
 * The testTrue Method is also helpful in that it can test any condition and
 * assign points for a correct answer.
 * 
 * At the beginning of your test program you call UnitTesting.init() to reset
 * the points. At the end you call printScore() and it automatically knows the 
 * score to print and gives you details about it.
 * 
 * There are other intermediate methods to set the minimum score possible for a 
 * question, as well as getters for the current score and total score. 
 * 
 * There is also another handy method called deleteFile(String filename) 
 * to delete a file if the assignment you grade write to a file of some kind. 
 * 
 * Then there are a couple of file methods:
 * getLineCount(String filename) and getFileSize(String filename) for verification of files.
 *
 * Lastly, if you want to capture the screen output of a main method you can preceed the call
 * to main() with beginCapturingOutput() and end with String out = endCapturingOutput(); Then
 * you can use the test methods to verify that output.
 * 
 * To use this class you can put it in the same directory as the assignments you'll be grading,
 * or you can add it to your classpath or Eclipse project if that's what you use. 
 * Extend it or use its static methods.
 * 
 * Lastly, the cleanUpMethod
 * 
 * Author: Francisco Iacobelli
 */
public class UnitTesting{


	private static int minPoints = 1;
	private static int totalPoints = 0;
	private static int currentPoints=0;
	
	private static ByteArrayOutputStream bos;
	private static PrinStream stdout;
	
	public static void init(){
		currentPoints = 0;
	}
	
	public int getCurrentPoints(){
		return currentPoints;
	}
	
	public int getTotalPoints(){
		return totalPoints;
	}
	
	public static void setMinPoints(int i){
		minPoints = i;
	}
	
	public static int getMinPoints(){
		return minPoints;
	}
	
	public static int testEquals(String expected, String actual, int points){
		totalPoints+=points;
		System.out.print("Testing expected:"+expected+"; actual="+actual);
		return testTrue(expected.equalsIgnoreCase(actual), points);
	}
	
	public static int testEquals(int expected, int actual, int points){
		totalPoints+=points;
		System.out.print("Testing expected:"+expected+"; actual="+actual);
		return testTrue(expected==actual,points);
	}
	
	public static int testEquals(long expected, long actual, int points){
		totalPoints+=points;
		System.out.print("Testing expected:"+expected+"; actual="+actual);
		return testTrue(expected==actual,points);
	}
	
	public static int testEquals(double expected, double actual, int points){
		totalPoints+=points;
		System.out.print("Testing expected:"+expected+"; actual="+actual);
		return testTrue(expected==actual,points);
	}
	
	public static int testEquals(boolean expected, boolean actual, int points){
		totalPoints+=points;
		System.out.print("Testing expected:"+expected+"; actual="+actual);
		return testTrue(expected==actual,points);
	}
	
	
	public static int testEquals(Object[] expected, Object[] actual, int points){
		totalPoints+=points;
		System.out.print("Testing expected:"+arrayToString(expected)+"; actual="+arrayToString(actual));
		return testTrue(expected.equals(actual), points);
	}
	
	public static int testEquals(Object expected, Object actual, int points){
		totalPoints+=points;
		System.out.print("Testing expected:"+expected+"; actual="+actual);
		return testTrue(expected.equals(actual), points);
	}
	
	public static String arrayToString(Object[] a){
		StringBuffer sb = new StringBuffer();
		for(Object o : a)
			sb.append("["+o.toString()+"]");
		sb.append("\n");
		return sb.toString();
	}
	
	public static int testTrue(boolean cond, int points){
		if(cond){
			System.out.println(" PASSED: "+points+" pts.");
			currentPoints += points;
			return points;
		}
		else {
			System.out.println(" FAILED: "+points+" pts.");
			currentPoints += minPoints;
			return minPoints;
		}
	}
	
	public static String scoreToString(){
		return scoreToString(currentPoints);
	}
	
	public static String scoreToString(int pts){
		StringBuilder sb = new StringBuilder();
		double percent = (double)pts/(double)totalPoints;
		sb.append("Total Score: "+pts+" / "+totalPoints+"\n");
		sb.append("Percentage wise:"+percent*100+"\n");
		return sb.toString();
	}
	
	public static void printScore(int pts){
		System.out.println(scoreToString(pts));
	}
	
	public static void printScore(){
		printScore(currentPoints);
	}
	
	public static void beginCapturingOutput(){
		bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintOutputStream(bos);
		stdout = System.out;
		System.setOut(ps);
	}
	
	public static String endCapturingOutput(){
		System.setOut(stdout);
		return bos.toString();
	}
	
	public static long getFileSize(String filename){
		try{
			File f = new File(filename);
			return f.length();
		}
		catch(Exception e){
			e.printStackTrace();
			return -1L;
		}
	}
	
	public static int getLineCount(String filename){
		int count = 0;
		try{
			Scanner s = new Scanner(new File(filename));
			while(s.hasNextLine()){
				s.nextLine();
				count++;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			count = -1;
		}
		return count;
	}
	
	
	public static void deleteFile(String filename){
		try{
			File f = new File(filename);
			f.delete();
			System.out.println(filename + " Deleted");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void cleanUp(String[] files){
		for(String f : files){
			deleteFile(f);
		}
	}
	
}

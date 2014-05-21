/*
 * Say, the goal is to test a class with a setGrades and gradeAverage functions,
 * a toString method and a sayHi method.
 * the toString method displays the grades separated by a comma, the sayHi outputs
 * "Hello" on the screen. and the gradeAverage also writes to a file named "grades.txt"
 *
 * The class under testing is SampleHomework (all your students should name it the same).
 * You can use UnitTesting as a class with static methods or you can extend it.
 *
 */
public class UseUnitTestingDemo extends UnitTesting{
	public static void main(String[] args){
		SampleHomework sh = new SampleHomework();
		double[] grades = {3.4,4.0};
		// Let the tests begin....
		init(); 
		// the default minimum points per question is 1. I will make it 0.
		setMinPoints(0);
		
		sh.setGrades(grades); //use the hw class and test.
		testEquals(grades,sh.grades,10); //this test that grades are set and gives 10 pts.
		testEquals("3.4,4.0",sh.toString(),15); //this tests toString and assigns 15 pts.
		
		// Test whether sayHi prints the desired output on the screen. This will be worth 2pts.
		beginCapturingOutput();
		sh.sayHi();
		String out = endCapturingOutput();
		testEquals("Hello\n",out,2); // if the homework does System.out.println, you must append \n at the end of the expected String.
		// One could also try: 
		testTrue(out.matches("Hello\\n?"),2); // this will account for hw with print and println too.
		
		testTrue(sh.gradeAverage() > 3.4,5); // that the average may make sense 5pts.
		testTrue(sh.gradeAverage() < 4.0,5,"Average < 4");// you can also pass a message to testTrue
		testEquals(3.7,sh.gradeAverage(),5);  // that the average matches your test. 5pts
		
		// Let's take care of the file created.
		testEquals(getLineCount("grades.txt"),1,2); // check there is just one line in the file. 2pts
		testEquals(getFileSize("grades.txt"),4,10); // instead of checking the contents, just check that the size is Ok. 10pts.
		
		// I am done, cleanup files and print the score
		String[] files = {"grades.txt"}; //you could add the java files to remove so you can put another HW in its place.
		cleanUp(files);
		printScore();
		
	}
}
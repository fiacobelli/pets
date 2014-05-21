import java.io.PrintWriter;
public class SampleHomework {

	double[] grades;
	
	public static void main(String[] arg){
		// This is supposed to test the program... student's code.
		// I usually tell my students that they should put all of the
		// user input here. Not on the methods.
		// However, beginners may just have everything here at first.
		// With the UnitTesting you can capture the output of main (or any method) 
		// and check it.
		
	}
	
	public void setGrades1(double[] grades){
		this.grades = grades;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<grades.length;i++)
			sb.append(","+grades[i]);
		return sb.toString().substring(1);
	}
	
	public double gradeAverage(){
		double sum=0,avg;
		for(int i=0;i<grades.length;i++)
			sum += grades[i];
		avg = sum/grades.length;
		writeToFile(avg);
		return avg;
	}
	
	public void writeToFile(double num){
		try{
			PrintWriter out = new PrintWriter("grades.txt");
			out.println(num);
			out.flush();
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void sayHi(){
		System.out.println("Hello");
	}
}

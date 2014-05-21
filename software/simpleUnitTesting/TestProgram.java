import java.io.*;

public class TestProgram {

  private static void printLines(String name, InputStream ins) throws Exception {
    String line = null;
    BufferedReader in = new BufferedReader(
        new InputStreamReader(ins));
    while ((line = in.readLine()) != null) {
        System.out.println(name + " " + line);
    }
  }

  private static int runProcess(String command) throws Exception {
    Process pro = Runtime.getRuntime().exec(command);
    printLines(command + " stdout:", pro.getInputStream());
    printLines(command + " stderr:", pro.getErrorStream());
    pro.waitFor();
    System.out.println(command + " exitValue() " + pro.exitValue());
    return pro.exitValue();
  }

  public static void main(String[] args) {
    try {
        String javaFiles = args[0];
        String mainFileName = args[1];
        if (!javaFiles.endsWith("/"))
            javaFiles += "/";
        javaFiles = javaFiles.replace(" ","\\ ");
        int base = runProcess("javac -cp "+javaFiles+" "+javaFiles+mainFileName);
        int methods = runProcess("javac -cp .:"+javaFiles+" UseUnitTestingDemo.java");
        int process = runProcess("java -cp .:"+ javaFiles + " UseUnitTestingDemo");
        if (base==1)
            throw new RuntimeException("0. Program does not compile");
        if (methods==1)
            throw new Exception("-10. Program does not adhere to standards set");
        if (process==1)
            throw new Exception("END OF TESTING: There was some exception during the tests");
    } catch(RuntimeException e) {
        e.printStackTrace();
        System.exit(1);
    } 
    catch (Exception e) {
        e.printStackTrace();
        System.exit(2);
    }
  }
}

public class Homeworks{
    public static void main(String[] args){
        // The args will be the filenames to compare....
        CompareFiles cf = new CompareFiles();
        for(String fname : args){
            try{
                cf.loadFile(fname);
            }
            catch(Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        cf.compareFiles();
    }
}

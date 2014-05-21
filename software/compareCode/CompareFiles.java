import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class CompareFiles {
    public HashMap<String,String> files;
    
    public CompareFiles(){
        files = new HashMap<String,String>();
    
    }
    
    public void loadFile(String fname) throws FileNotFoundException{
        Scanner f = new Scanner(new File(fname));
        StringBuffer sb = new StringBuffer(); // will have the source code.
        while(f.hasNextLine()){
            sb.append(f.nextLine());
        }
        this.files.put(fname,sb.toString());
        f.close();
    }
    
    public void compareFiles(){
        for(Entry<String,String> src : this.files.entrySet()){
            for(Entry<String,String> target : this.files.entrySet()){
                System.out.println(src.getKey()+", "+ target.getKey()+" = " +StringUtilities.histogramMatch(src.getValue(),target.getValue()));
            } 
        }
    }
}

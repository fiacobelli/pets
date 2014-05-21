/**
 * This class is called from the command line with a directory where files are.
 * It produces a file with topics and their words, a file with files and their topic proportions,
 */
 import java.io.Writer;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.io.FileOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.ObjectOutput;
 import java.io.ObjectOutputStream;
 import java.io.BufferedOutputStream;
 import java.io.InputStream;
 import java.io.ObjectInput;
 import java.io.ObjectInputStream;
 import java.io.BufferedInputStream;
 import java.io.FileInputStream;
 import java.util.ArrayList;
 import java.util.Arrays;

 
public class LdaAnalysis{
    static int fSLEEP_INTERVAL = 100;
    
    public static String buildMessage(String[] vocabulary,int[] document){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<document.length;i++)
            sb.append(vocabulary[document[i]]+" ");
        return sb.toString();
    }
    
    public static int maxIndex(double[] numbers){
        int maxIdx=0;
        for (int i=0;i<numbers.length;i++){
            if(numbers[i]>numbers[maxIdx])
                maxIdx = i;
        }
        return maxIdx;
    }
    
    
    public static LdaGibbs readLdaModel(String filename){
        LdaGibbs c = null;
        try{
              //use buffering
              InputStream file = new FileInputStream( filename );
              InputStream buffer = new BufferedInputStream( file );
              ObjectInput input = new ObjectInputStream ( buffer );
              try{
                //deserialize the List
                c = (LdaGibbs) input.readObject();
              }
              finally{
                input.close();
              }
        }
        catch(ClassNotFoundException ex){
              throw new RuntimeException(ex.getMessage() + "\nCannot perform input. Class not found.");
        }
        catch(IOException ex){
              throw new RuntimeException(ex.getMessage() + "\nCannot perform input.");
        }
        return c;
    }

    
    public static void saveThetas(String filename,LdaGibbs ldag,Corpus c){
        try{
            Writer out = new OutputStreamWriter(new FileOutputStream(filename));
            for (int m = 0; m < ldag.thetaStats.length; m++){
                StringBuilder sb = new StringBuilder();
                sb.append(c.filenames[m]);
                //sb.append(buildMessage(","+c.types,c.documents[m]));
                for(int k=0;k<ldag.thetaStats[m].length;k++)
                        sb.append(","+ldag.thetaStats[m][k]);    
                int topic = maxIndex(ldag.thetaStats[m]);
                sb.append(","+topic+"\n");
                out.write(sb.toString());
            }
            out.close();
        }
        catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Error writing the document by topic file\n"+e.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }
    
    
    public static void savePhis(String filename, LdaGibbs ldag, Corpus c){
        try{
            Writer out = new OutputStreamWriter(new FileOutputStream(filename));
            for (int w = 0; w < ldag.phiStats[0].length; w++){
                StringBuilder sb = new StringBuilder();
                sb.append(c.types[w]);
                for(int k=0;k<ldag.phiStats.length;k++)
                    sb.append(","+ldag.phiStats[k][w]);    
                out.write(sb.toString()+"\n");
            }
            out.close();
        }
        catch (IOException e){
                e.printStackTrace();
                throw new RuntimeException("Error writing the word topic proportions file\n"+e.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    
    
    public static void savePhisInverse(String filename, LdaGibbs ldag, Corpus c){
        ArrayList<WordTopic[]> phis = new ArrayList<WordTopic[]>();
        for(int k=0;k<ldag.phiStats.length;k++){
            WordTopic[] topicPhis = new WordTopic[ldag.phiStats[0].length];
            for (int w = 0; w < ldag.phiStats[0].length; w++)
                topicPhis[w] = new WordTopic(w,ldag.phiStats[k][w]);
            Arrays.sort(topicPhis);
            phis.add(topicPhis);
        }
        
        try{
            Writer out = new OutputStreamWriter(new FileOutputStream(filename));
            for (int k = 0; k < ldag.phiStats.length; k++){
                StringBuilder sb = new StringBuilder();
                sb.append(k);
                for(int w=0;w<ldag.phiStats[0].length;w++)
                    sb.append(","+c.types[phis.get(k)[w].wordId]);    
                out.write(sb.toString()+"\n");
            }
            out.close();
        }
        catch (IOException e){
                e.printStackTrace();
                throw new RuntimeException("Error writing the topic by words file\n"+e.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    
    private static void collectGarbage() {
        try {
          System.gc();
          Thread.currentThread().sleep(fSLEEP_INTERVAL);
          System.runFinalization();
          Thread.currentThread().sleep(fSLEEP_INTERVAL);
        }
        catch (InterruptedException ex){
          ex.printStackTrace();
        }
  }
  
    public static void main(String[] args){
        if (args.length<5 || args.length>6){
            String msg = "You need to run the program with 4-5 parameters:\n";
            msg += "\t1. The directory where the text files are to build the corpus. If this is a file, it will be read as a serialized corpus previously built.\n";
            msg += "\t2. The file where to save the document-topic assignment (theta)\n";
            msg += "\t3. The file where to save the word-topic assignemt (phi)\n";
            msg += "\t4. The file where to save the topic-word assignment \n";
            msg += "\t5. The number of topics\n";
            msg += "\t6. Optionally, a file where to save the serialized version of the corpus\n";
            System.out.println(msg);
            System.exit(0);
        }
        long time1 = System.currentTimeMillis();
        long totalMemory1 = Runtime.getRuntime().totalMemory();
        System.out.println("Initial Memory:"+totalMemory1);
        System.out.print("\nCreating Corpus from :"+args[0]);
        File source = new File(args[0]);
        Corpus c;
        if (source.isDirectory()){
            c = new Corpus(new String(args[0]));
            System.out.println("Loading Corpus from "+args[0]);
            Corpus.DummyDocumentFilter ddf = new Corpus.DummyDocumentFilter("Dummy");
            Corpus.StopwordFilter swf = new Corpus.StopwordFilter("Stopwords");
            //c.loadRawText();
            c.loadTextOptimize(swf,ddf);
        }
        else{
            c = new Corpus("");
            c.readModel(new String(args[0]));
        }
        long time2 = System.currentTimeMillis();
        System.out.println(" Took "+(time2-time1)+"ms");
        collectGarbage(); // Memory efficient?
        // Potential memory usage
        long totalMemory2 = Runtime.getRuntime().totalMemory();
        System.out.println("Corpus uses:"+(totalMemory2-totalMemory1));
        
        c.printCorpusStats();
        System.out.print("Started Gibbs Sampling:");
        int nTopics = Integer.valueOf(args[4]);
        LdaGibbs ldag = new LdaGibbs(c.documents,c.types.length,nTopics);
        ldag.setBeta(0.01);
        ldag.setAlpha(0.01);//50.0/(double)nTopics);
        ldag.gibbsSampling();
        System.out.println(System.currentTimeMillis()-time2);
        time2 = System.currentTimeMillis();
        // Saving the LDA model just in case.
        System.out.println("The LDA model will be saved in ./lda-model.dat just in case");
        try{
            ldag.serializeObject("lda-model.dat");
        }
        catch(Exception e){
            System.out.println("Could not save lda model");
            e.printStackTrace();
        }
        // Maybe run garbage collection to optimize memory?
        collectGarbage();
        // save messages with topic assignment to a csv.
        System.out.print("Writing the document-topic assignments to "+args[1]+": ");
        saveThetas(args[1],ldag,c);
        System.out.println(System.currentTimeMillis()-time2);
        time2 = System.currentTimeMillis();
        
        System.out.print("Writing the word-topic assignments to "+args[2]+": ");
        // Save word-topic assignment to a csv.
        savePhis(args[2],ldag,c);
        System.out.println(System.currentTimeMillis()-time2);
        time2 = System.currentTimeMillis();
        
        System.out.print("Writing the topic-word assignments to "+args[3]+": ");
        savePhisInverse(args[3],ldag,c);
        System.out.println(System.currentTimeMillis()-time2);
        time2 = System.currentTimeMillis();
        
        if (args.length==6){
            System.out.print("Serializing The Corpus :");
            c.serializeObject(args[4]);
            System.out.println(System.currentTimeMillis()-time2);
            time2 = System.currentTimeMillis();
        }
        
        System.out.println("Total Time :"+(time2-time1)+" ms");
    }
    
    /**
     * What follows are auxiliary classes I may separate soon, but I
     * don't want to have a download of a lot of irrelevant classes or 
     * external libraries yet.
     */
     static class WordTopic implements Comparable<WordTopic>{
        public int wordId;
        public double prob; //prob that wordId is in a topic array (WordTopic[])
        
        public WordTopic(int wId,double p){wordId = wId; prob = p;}
        public boolean equals(Object c){
            if (c instanceof WordTopic)
                return (prob==((WordTopic)c).prob);
            else
                return false;
        }
        public int compareTo(WordTopic c) {
            if(this.prob-c.prob<0.0)
                return -1;
            else if (this.prob-c.prob==0.0)
                return 0;
            else
                return 1;
        }
     }
}

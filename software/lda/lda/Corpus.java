/**
 * This class reads a directory of text files,
 * or texts from a file where each line is one text
 * and creates a corpus consisting of:
 * 1. A vocabulary (an array of words (types) where the index of each word is its id);
 * 2. A representation of the documents in the corpus as an int[][] where each row is a document
 * and the columns contain, sequentially, the ids of the words in the document.
 */
 import java.util.HashMap;
 import java.util.Stack;
 import java.util.HashSet;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.io.FileFilter;
 import java.io.File;
 import java.util.Scanner;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.ObjectOutput;
 import java.io.ObjectOutputStream;
 import java.io.BufferedOutputStream;
 import java.io.FileOutputStream;
 import java.io.InputStream;
 import java.io.ObjectInput;
 import java.io.ObjectInputStream;
 import java.io.BufferedInputStream;
 import java.io.FileInputStream;
 import java.io.Serializable;
 
 public class Corpus implements Serializable{
    int[][] documents;
    String[] filenames;
    String[] types; //types, not tokens.
    long numTokens;
    String sourceDir;
    
    public static class LexiconFilter{
        
        String description = "Generic Lexicon Filter";
        public LexiconFilter(String desc){
            this.description = desc;
        }
        
        public boolean includeWord(String word,int wordFreq, int docFreq, int totalDocs){
            return (docFreq>20);
        }
        
        public String toString(){
            return description;
        }
    }
    
    public static class StopwordFilter extends LexiconFilter{
        
        HashSet<String> sw = new HashSet<String>(Arrays.asList(new String[] { "este", "esta", "hay", "las", "por", "eso", "los", "como", "con", "nos", "una", "he", "de", "mas", "el", "sin", "en", "es", "ser", "sea", "al", "no", "lo", "me", "mi", "pues", "sobre", "le", "la", "un", "para", "tu", "te", "su", "se", "si", "sus", "que", "porque", "muy", "del", "ya", "yo","tema", "esto", "cuenta", "usted", "puede", "todo", "tener", "mucho", "abrazo", "favor", "estoy", "espero", "estos", "solo", "bogota", "hacer", "saber", "tiene", "ha", "dr", "seria", "doctor", "soy", "son", "donde", "ciudad", "pero", "personas", "estamos", "alcaldia", "candidato", "hace", "desde", "gracias", "gustaria", "dia", "david", "buenas", "luna", "fin", "nuestra", "tambien" }));
        
        public StopwordFilter(String desc){
            super(desc);
        }
        public void setSW(String filename){
            
        }
        
        public boolean includeWord(String word, int wordFreq, int docFreq, int totalDocs){
            //if((wordFreq/docFreq)<2 && !sw.contains(word.toLowerCase()))
            //    System.out.println("Including:"+word);
            return word.length()>4;// && !sw.contains(word.toLowerCase());
        }
        
        public String toString(){
            return description;
        }
    }
    
    public static class DummyDocumentFilter extends DocumentFilter{
        public DummyDocumentFilter(String desc){
            super(desc);
        }
        public String[] filterDocument(String[] doc){
            return doc;
        }
    }
    
    
    public static class DocumentFilter{
        HashMap<String,Integer> documentTokens;
        int minFreq = 1; //minimum frequency in the document
        String description = "Generic document filter";
        
        public DocumentFilter(String desc){
            this.description = desc;
        }
        
        public String[] filterDocument(String[] doc){
            documentTokens = new HashMap<String,Integer>();
            for(String w:doc){
                if (documentTokens.containsKey(w))
                    documentTokens.put(w,documentTokens.get(w)+1);
                else
                    documentTokens.put(w,1);
            }
            ArrayList<String> al = new ArrayList<String>();
            for(String wo:documentTokens.keySet()){
                if (documentTokens.get(wo)>minFreq)
                    al.add(wo);
            }
            // back to an array
            String[] result = new String[al.size()];
            al.toArray(result);
            return result;
        }
        
    }

 
    public Corpus(String dirname){
        this.sourceDir = dirname;
    }
    
    public void loadTextOptimize(Corpus.LexiconFilter filter, Corpus.DocumentFilter docFilter){
        // first, for each document read
        filenames =getFilenames(this.sourceDir);
        documents = new int[filenames.length][];
        System.out.println("Processing " + filenames.length+ " documents");
        int step = filenames.length>10?filenames.length/10:1;
        System.out.println("Building lexicon and filtering with "+filter.toString());
        HashMap<String,Integer> words = buildLexicon(filenames,filter,docFilter);
        System.out.println("Building documents representation ");
        int docIndex = 0;
        this.numTokens = 0;
        for(int i=0;i<filenames.length;i++){
            String[] tokens = StringUtilities.simpleCleanString(readDocument(new File(filenames[i]))).split("\\s");
            ArrayList<Integer> docTokens = new ArrayList<Integer>();
            for(String token:tokens){
                if(words.containsKey(token)){
                    docTokens.add(words.get(token));
                    numTokens++;
                }
            }
            int[] document = new int[docTokens.size()];
            for(int j=0;j<docTokens.size();j++)
                document[j]=docTokens.get(j);
            documents[docIndex] = document;
            docIndex++;
            if (docIndex%step==0){
                System.gc();
                System.out.print("->"+docIndex+" ");
            }
        }
        // create vocabulary
        types = new String[words.size()];
        for (String w:words.keySet())
            types[words.get(w)]=w;
        
    }
    
    public HashMap<String,Integer> buildLexicon(String[] filenames,LexiconFilter afterFilter, DocumentFilter perDocFilter){
        // words will be associated to a term freq and a doc freq.
        HashMap<String,Integer> wordsFreq = new HashMap<String,Integer>(); // store types and their Ids.
        HashMap<String,HashSet<Integer>> docFreq = new HashMap<String,HashSet<Integer>>(); // store documents and their token ids.
        int step = filenames.length>10?filenames.length/10:1;
        int totalWords = 0;
        int lastDoc = 0;
        System.out.print("Types:documents ");
        // For all files, filter the document and then store their representations.
        for (int i=0;i<filenames.length;i++){
            String[] tokens = perDocFilter.filterDocument(StringUtilities.simpleCleanString(readDocument(new File(filenames[i]))).split("\\s"));
            for(int j=0;j<tokens.length;j++){
                if (wordsFreq.containsKey(tokens[j]))
                    wordsFreq.put(new String(tokens[j]), wordsFreq.get(tokens[j])+1);
                else
                    wordsFreq.put(new String(tokens[j]),1);
                if (docFreq.containsKey(tokens[j]))
                    docFreq.get(tokens[j]).add(i);
                else{
                    docFreq.put(new String(tokens[j]),new HashSet<Integer>());
                    docFreq.get(tokens[j]).add(i);
                }
            }
            if (i%step==0){
                System.gc();
                System.out.print("->"+wordsFreq.size()+":"+i+" ");
            }
        }
        
        // Now, filter appropriate words in the lexicon. 
        // wordIdx is a new HashMap with only the filtered words.
        HashMap<String,Integer> wordIdx = new HashMap<String,Integer>();
        int idx = 0;
        for(String word: wordsFreq.keySet()){
            if(afterFilter.includeWord(word,wordsFreq.get(word),docFreq.get(word).size(),filenames.length))
                wordIdx.put(new String(word),idx++);
            // free some memory right away
            docFreq.remove(word);
            //wordsFreq.remove(word);
        }
        return wordIdx;
    }
    
    public void loadRawText(){
        HashMap<String,Integer> words = new HashMap<String,Integer>(); // store types and their Ids.
        // first, for each document read
        filenames =getFilenames(this.sourceDir);
        System.out.println("Processing " + filenames.length+ " documents");
        int step = filenames.length>10?filenames.length/10:1;
        documents = new int[filenames.length][];
        int typeIndex = 0;
        int docIndex = 0;
        this.numTokens = 0;
        for(int i=0;i<filenames.length;i++){
            String[] tokens = StringUtilities.simpleCleanString(readDocument(new File(filenames[i]))).split("\\s");
            //String[] tokens = StringUtilities.splitAndCleanText(readDocument(new File(filenames[i])));
            int[] document = new int[tokens.length];
            int tokenIndex = 0; // the position of the next word in the document
            for(String token:tokens){
                if(!words.containsKey(token)){
                    words.put(new String(token),typeIndex);
                    typeIndex++;
                }
                document[tokenIndex] = words.get(token);
                tokenIndex++;
                numTokens++;
            }
            documents[docIndex]=document;
            docIndex++;
            if (docIndex%step==0){
                System.gc();
                System.out.print("->"+docIndex+" ");
            }
        }
        // create vocabulary
        types = new String[words.size()];
        for (String w:words.keySet())
            types[words.get(w)]=w;
            
    }
    public String[] getFilenames(String sourceDir){
        File dir = new File(sourceDir);
        FileFilter filter = new FileFilter() {
                    public boolean accept(File file) {
                         return file.isFile();
                     }
                 };
                 
        File[] children = dir.listFiles(filter);
        if (children==null){
            throw new RuntimeException("Specified Directory has no files:"+sourceDir);
        }

        String[] files = new String[children.length];
        for(int i=0;i<children.length;i++){
            //System.out.println("Child:"+child);
            files[i]= children[i].toString();
        }
        // I know this does a cumbersome File->String->File transformation, but String[] is cheaper to store than File[]
        return files;
    }
    
    public String readDocument(File fname){
        try{
            Scanner s = new Scanner(fname);
            StringBuilder sb = new StringBuilder();
            while(s.hasNextLine()){
                sb.append(s.nextLine()+" ");
            }
            s.close();
            return sb.toString();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public void serializeObject(String filename){
        try{
            //use buffering
            OutputStream file = new FileOutputStream( filename );
            OutputStream buffer = new BufferedOutputStream( file );
            ObjectOutput output = new ObjectOutputStream( buffer );
            try{
                output.writeObject(this);
            }
            finally{
                output.close();
            }
        }  
        catch(IOException ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage()+"\nCannot serialize this object");
        }
    }
    
    public void readModel(String filename){
        try{
              //use buffering
              InputStream file = new FileInputStream( filename );
              InputStream buffer = new BufferedInputStream( file );
              ObjectInput input = new ObjectInputStream ( buffer );
              try{
                //deserialize the List
                Corpus c = (Corpus) input.readObject();
                //display its data
                this.types = c.types;
                this.documents = c.documents;
                this.sourceDir = c.sourceDir;
                this.filenames = c.filenames;
                this.numTokens = c.numTokens;
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
    }
    
    public void printInfo(){
        System.out.println("Documents\n--------------");
        for (int i=0;i<documents.length;i++){
            for (int j=0;j<documents[i].length;j++)
                System.out.print(documents[i][j]+",");
            System.out.print("\n");
        }
        
        System.out.println("Vocabulary\n---------");
        for(int i=0;i<types.length;i++)
            System.out.println(i+":"+types[i]);
    }
    
    public double averageWordLength(){
        long sumWlen = 0;
        for (int i=0;i<types.length;i++)
            sumWlen += types[i].length();
        return sumWlen/(double)types.length;
    }
    
    public void printCorpusStats(){
        String msg = "Corpus Stats:\n";
        msg += documents.length+" Documents\n";
        msg += numTokens + " Tokens\n";
        msg += (double)numTokens/(double)documents.length + " tokens per document on average\n";
        msg += types.length + " Types\n";
        msg += averageWordLength() + " characters per word on average\n";
        System.out.println(msg);
    }
    
    public static void main(String[] args){
        if(args.length<2){
            System.out.println("java [options] Corpus <dir. of text files> <name of target corpus file> [vpf] \n Use the p(rint) option with care. It prints EVERYTHING!. The v(erify) option is just to check that the saved file is ok. the f(ilter) option uses a default filter on words. May be slower and more memory intensive.");
            System.exit(0);
        }
        //Corpus c = new Corpus("./texts/");
        //Corpus c = new Corpus("/home/fid/research/corpora/senate/all/treated/senate_stemmed/");
        Corpus c = new Corpus(args[0]);
        //String[] s = c.getFilenames("./texts/");
        if (args.length>2 && args[2].contains("f")){
            Corpus.LexiconFilter lf = new LexiconFilter("Words that appear in more than 20 documents");
            Corpus.DocumentFilter df = new DocumentFilter("Words that appear at least twice in the doc.");
            c.loadTextOptimize(lf,df);
        }
        else
            c.loadRawText();
            
        //c.serializeObject("test_corpus.dat");
        c.serializeObject(args[1]);

        if(args.length>2 && args[2].contains("v")){
            c = new Corpus("");
            c.readModel(args[1]);
            System.out.println("Read from file!");
        }
        if(args.length > 2 && args[2].contains("p"))
            c.printInfo();
    }
 }

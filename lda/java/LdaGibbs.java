/**
 *  This is a basic class to perform LDA for topic modeling.
 * It implements the same algorithm as LdaGibbsSampler (found in http://www.arbylon.net/publications/text-est.pdf), 
 * but it is not GPL.
 * 
 * This implementation is tested against LdaGibbsSampler too.
 * 
 * By: Francisco Iacobelli
 */
import java.text.DecimalFormat;
import java.text.NumberFormat;

// Classes necessary only for serialization
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
 
 
public class LdaGibbs implements Serializable{
    // vars used for gibbs sampling
    int M = 0; //number of documents.
    int V = 0;
    int K = 0;
    int[][] Nmz;
    int[][] Nzt;
    int[][] Zmn;
    int[][] documents;
    int[] Nmsum;
    int[] Nzsum;
    
    // aux vars for statistics. 
    // This will keep a regularly updated phis and thetas
    double[][] phiStats;
    double[][] thetaStats;
    
    int NUM_ITERATIONS = 1000;
    int BURNOUT = 200;
    double ALPHA = 2;
    double BETA = 0.5;
    int SAMPLE_LAG = 100;
    boolean DEBUG = true;
    
    
    // initialization
    public LdaGibbs(int[][] documents, int vocabularySize, int numTopics){
        this.documents = documents;
        this.V = vocabularySize;
        this.M = documents.length;
        this.K = numTopics;
        this.Nmz = new int[M][K]; // documents by topics
        this.Nzt = new int[K][V]; // topics by terms
        this.Zmn = new int[M][]; // topic assignments (one assignment for each token of each document)
        this.Nmsum = new int[M];
        this.Nzsum = new int[K];
        
        this.phiStats = new double[K][V]; 
        this.thetaStats = new double[M][K];
    }
    
    // Some setters
    public void setDebug(boolean d){
        this.DEBUG = d;
    } 
    
    public void setAlpha(double a){
        this.ALPHA = a;
    }
    
    public void setBeta(double b){
        this.BETA = b;
    }
    
    public void setIterations(int it){
        this.NUM_ITERATIONS = it;
    }
    
    public void setBurnout(int b){
        this.BURNOUT = b;
    }
    
    public void setSampleLag(int sl){
        this.SAMPLE_LAG = sl;
    }
    
    public void initialize(int[][] documents){
        for (int m=0;m<M;m++){
            Zmn[m] = new int[documents[m].length]; // prepare topic assignments for the words in document m.
            for (int w=0;w<documents[m].length;w++){
                // sample topic index (and assign it). Implemented as random topic assignment to word w (token) in document m.
                int k = (int) (Math.random()*K);
                //System.out.println("K="+k+": m="+m+": w="+w); 
                Zmn[m][w] = k;
                // increment document-topic count & document-topic sum.
                Nmz[m][k]++;
                Nmsum[m]++;
                
                // increment topic-term count and topic term-sum
                Nzt[k][documents[m][w]]++;
                Nzsum[k]++;
            }
        }
    }
    
    public void gibbsSampling(){
        initialize(this.documents);
        
        int statsSample = 0;
        long t1 = System.currentTimeMillis(); // Just to time iterations.
        long t2 = System.currentTimeMillis();
        if (DEBUG) System.out.print("\nStarted Iterations: Burnout Period ");
        while (NUM_ITERATIONS>0){ // using a while just to conform to Figure 8. I know, it could be a for loop.
            for(int m=0;m<M;m++){
                for(int w=0;w<documents[m].length;w++){ // Now I don't really need the docs. topics assignments are enough.
                    // for the current assignment of k to a term for word w decrement counts and sums
                    int topic = Zmn[m][w]; //current assignment
                    Nmz[m][topic]--;
                    Nmsum[m]--;
                    Nzt[topic][documents[m][w]]--;
                    Nzsum[topic]--;

                    //multinomial sampling
                    int newTopic =  multinomialSampling(m,w);
                    Zmn[m][w] = newTopic;

                    // use new assignment to increment counts.
                    Nmz[m][newTopic]++;
                    Nmsum[m]++;
                    Nzt[newTopic][documents[m][w]]++;
                    Nzsum[newTopic]++;
                }
            }
            // Simple output to track progress
            if(DEBUG){
                t2 = System.currentTimeMillis();
                long iterTime = t2-t1;
                t1 = t2;
                System.out.print("\r "+NUM_ITERATIONS +" iter. left. ET left:"+(iterTime*NUM_ITERATIONS/1000)+"s. Time per iter.:"+(iterTime/1000)+". Burnout?"+(BURNOUT>0)+"    ");
            }
            
            BURNOUT--; // num of iterations is a simple naive convergence check.. one could do something more elaborate.

            // sample lag indicates when to take the average of phis and thetas.
            if (BURNOUT<0 && (NUM_ITERATIONS % SAMPLE_LAG==0) ){
                // check convergence and read out parameters
                updatePhiAndTheta();
                statsSample++;
            }
            NUM_ITERATIONS--;
        }
        
        // one last update of the parameters and average them
        updatePhiAndTheta();
        averagePhiAndTheta(statsSample+1);
        if(DEBUG) System.out.println("\nDone");
    }
    
    public int multinomialSampling(int m, int w){
        double[] p = new double[K]; // P(z=k|Z,w) cond probabilities. Each one is computed according to eq. 79
        // for all counts I need to not count the current assignment i. This is taken care of by subtracting one before sampling.
        // Also, remember that the assumption here is that alpha and beta are constant for all terms and topics.
        double totalP = 0.0;
        for (int k=0;k<K;k++){
            int nkt=Nzt[k][documents[m][w]]; 
            p[k]=(nkt+BETA)/(Nzsum[k]+ V*BETA)*(Nmz[m][k]+ALPHA)/(Nmsum[m]+K*ALPHA); //eq 79. (has a -1 next to alpha, but I am not sure that's right. c.f. http://psiexp.ss.uci.edu/research/papers/SteyversGriffithsLSABookFormatted.pdf)
            totalP += p[k];
            //System.out.println("Is negative Num?"+((Nmsum[m]+K*ALPHA)<0));
        }
        
        /*sample from distribution (which is multinomial)
         * To sample from a multinomial distribution we use an auxiliary variable X from the uniform distribution (0,1).
         * Read more in Wikipedia (binomial dist).
         */
        double x = Math.random()*totalP;
        int topic = 0;
        double maxP = p[topic];
        while (x>maxP){
            topic++;
            maxP += p[topic];
            //System.out.println(maxP+">"+x);
        }
        return topic;
    }
    
    /**
     * Accumulate the sum of current phis and thetas. They will be averaged every so often
     * depending on the sample lag
     */
    public void updatePhiAndTheta(){
        for(int k=0;k<K;k++)
            for(int t=0;t<V;t++)
                phiStats[k][t] += (Nzt[k][t]+BETA)/(Nzsum[k]+V*BETA);
        for (int m=0;m<M;m++)
            for (int k=0;k<K;k++)
                thetaStats[m][k] += (Nmz[m][k]+ALPHA)/(Nmsum[m]+K*ALPHA);
    }
    
    public void averagePhiAndTheta(int numIterations){
        for(int k=0;k<K;k++)
            for(int t=0;t<V;t++)
                phiStats[k][t] = phiStats[k][t]/(double)numIterations;
        for (int m=0;m<M;m++)
            for (int k=0;k<K;k++)
                thetaStats[m][k] = thetaStats[m][k]/(double)numIterations;
    }
    
    
    
    // Auxiliary classes to serialize and read objects for reutilization
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
    
    public static LdaGibbs readModel(String filename){
        try{
              //use buffering
              InputStream file = new FileInputStream( filename );
              InputStream buffer = new BufferedInputStream( file );
              ObjectInput input = new ObjectInputStream ( buffer );
              try{
                //deserialize the List
                LdaGibbs c = (LdaGibbs) input.readObject();
                //display its data
                return c;
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


    public static void main(String[] args){
        System.out.println("About to test this using the exact same test as LdaGibbsSampler.java");
        int[][] documents = { {1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 6},
            {2, 2, 4, 2, 4, 2, 2, 2, 2, 4, 2, 2},
            {1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 0},
            {5, 6, 6, 2, 3, 3, 6, 5, 6, 2, 2, 6, 5, 6, 6, 6, 0},
            {2, 2, 4, 4, 4, 4, 1, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 0},
            {5, 4, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2}};
        LdaGibbs ldag = new LdaGibbs(documents,7,2);
        ldag.gibbsSampling();
        // DOne. now print stats.
        System.out.println();
        System.out.println();
        System.out.println("Document--Topic Associations, Theta[d][k] (alpha="
            + ldag.ALPHA + ")");
        System.out.print("d\\k\t");
        for (int m = 0; m < ldag.thetaStats[0].length; m++) {
            System.out.print("   " + m + "    ");
        }
        System.out.println();
        for (int m = 0; m < ldag.thetaStats.length; m++) {
            System.out.print(m + "\t");
            for (int k = 0; k < ldag.thetaStats[m].length; k++) {
                System.out.print(ldag.thetaStats[m][k] + " ");
                System.out.print(shadeDouble(ldag.thetaStats[m][k], 1) + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Topic--Term Associations, Phi[k][w] (beta=" + ldag.BETA + ")");

        System.out.print("k\\w\t");
        for (int w = 0; w < ldag.phiStats[0].length; w++) {
            System.out.print("   " + w % 10 + "    ");
        }
        System.out.println();
        for (int k = 0; k < ldag.phiStats.length; k++) {
            System.out.print(k + "\t");
            for (int w = 0; w < ldag.phiStats[k].length; w++) {
                //System.out.print(ldag.phiStats[k][w] + " ");
                System.out.print(shadeDouble(ldag.phiStats[k][w], 1) + " ");
            }
            System.out.println();
        }
        
        // Save the serialized model.
        ldag.serializeObject("lda-model.dat");
    }
    
    static String[] shades = {"     ", ".    ", ":    ", ":.   ", "::   ",
        "::.  ", ":::  ", ":::. ", ":::: ", "::::.", ":::::"};

    static NumberFormat lnf = new DecimalFormat("00E0");
    /**
     * create a string representation whose gray value appears as an indicator
     * of magnitude, cf. Hinton diagrams in statistics.
     * 
     * @param d
     *            value
     * @param max
     *            maximum value
     * @return
     */
    public static String shadeDouble(double d, double max) {
        int a = (int) Math.floor(d * 10 / max + 0.5);
        if (a > 10 || a < 0) {
            String x = lnf.format(d);
            a = 5 - x.length();
            for (int i = 0; i < a; i++) {
                x += " ";
            }
            return "<" + x + ">";
        }
        return "[" + shades[a] + "]";
    }    
}

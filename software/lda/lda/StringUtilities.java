/**
 * Contiene un String match por histograma de letras
 */
import java.util.HashMap;


class StringUtilities
{
    public Double histogramMatch(String text1, String text2)
    {
        int neg = 0;
        int pos = 0;
        
        HashMap<Character,Integer> hist = new HashMap<Character,Integer>();
        int idx1 = 0;
        int idx2 = 0;
        
        for(int i=0;i<text1.length();i++)
        {
            Character letra = new Character(text1.charAt(i));
            if (hist.containsKey(letra))
                hist.put(letra, new Integer(hist.get(letra).intValue()+1));
            else
                hist.put(letra,new Integer(1));
        }
        for(int i=0;i<text2.length();i++)
        {
            Character letra = new Character(text2.charAt(i));
            if (hist.containsKey(letra))
                hist.put(letra, new Integer(hist.get(letra).intValue()-1));
            else
                hist.put(letra,new Integer(-1));
        }
        for (Character key:hist.keySet())
        {
            Integer val = hist.get(key);
            if (val.intValue()>0)
                pos = pos+val.intValue();
            else
                neg = neg + Math.abs(val.intValue());
        }
        
        int longest = Math.max(text1.length(),text2.length());
        return new Double(1-((double)(pos+neg)/(double)longest));
            
    }
    
    public static String[] splitAndCleanText(String text){
        String words[] = text.toLowerCase().replaceAll("\\.|,|-|;|:|\\?|!|\\]|\\[|'|\\(|\\)|\\{|\\}","").split("\\s");
        String[] result = new String[words.length];
        for(int i=0;i<words.length;i++){
            result[i] = words[i].trim();
        }
        return result;
    }

    public static String simpleCleanString(String text){
        String toEliminate = ".,-;:?[]{}'()";
        StringBuilder sb = new StringBuilder();
        for (char c:text.toCharArray()){
            if(toEliminate.indexOf(c)<0)
                sb.append(c);
        }
        return sb.toString().toLowerCase();
    }
    
    public String[] text2Ngrams(String text,int n)
    {
    	String[] words = text.split(" ");
        String[] ngrams = new String[words.length-n+1];
    	for (int i=0;i<ngrams.length;i++)
    	{
            StringBuffer sb = new StringBuffer();
    		for(int p=0;p<n;p++)
    		{ 
                //System.out.println("Adding p:"+p+" i:"+i+" "+words[i+p]);
    			sb.append(words[i+p]+" ");
    		}
            ngrams[i]=sb.toString().trim();
    	}
    	return ngrams;
    }
    
    
    public static void main(String[] args)
    {
        StringUtilities su = new StringUtilities();
        System.out.println(su.histogramMatch("abadia","abdicar"));
        
    }
}

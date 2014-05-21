/**
 * Contiene un String match por histograma de letras.
 * Tambien un metodo calza regexps. Lo uso pa probar
 */
import java.util.HashMap;
import java.util.regex.*;


class StringUtilities
{
    public static Double histogramMatch(String text1, String text2)
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
    
    public static String matchStr(String s, String regex){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(s);
		StringBuilder sb = new StringBuilder("Matches:");
		m.find();
		for(int i=0;i<=m.groupCount();i++){
			sb.append("Group "+ i +" = "+m.group(i));
		}
		return sb.toString();
	}
    
    
    
    public static void main(String[] args)
    {
        StringUtilities su = new StringUtilities();
        System.out.println(su.histogramMatch("abadia","abdicar"));
        
        
        // test regexps
        String a = "winnetka 60093 il";
        String b = "Oficina en cra 57 # 145 bogota";
        String c = "Oficina cerca a cll 38 n 72-35a sur en bogota";
        String[] m1 = {"winnetka, il 60093",
						"oficina en calle 73 n 9-93 sur",
						"oficina en calle 75 n 9-93 sur",
						"Cajero calle 175 con 20 bogota",
					   "Cajero Calle 73 con 9",
						"Cajero Carrera 73 con 9"
						};
						
        String r = "([0-9]{5})";
        String p = "([0-9][0-9]?[0-9]?),(.+)";
        //for(String s : m1)
			//System.out.println(su.matchStr(s,r));
        String a2 = "5 de abr,abr";
        String r2 = "(\\d\\d?) de (\\w+)";
        System.out.println("Queda:" + a2.replaceAll(r2,"$1"));
        System.out.println("can you send me the trailer".matches(".*?`\\w+`.*?"));
        
    }
  
}

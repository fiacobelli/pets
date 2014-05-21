import java.util.ArrayList;
import java.util.HashMap;


public class Argparse{
    
    private HashMap<String, Argument> argsMap;
    private int numMandatory;
    private ArrayList<String> unknown;
    
    private class Argument{
        String name;
        String def; //default values
        String type;
        String help;
        String value;
        boolean mandatory;
        
        public Argument(String name){
            this.name = name;
        }
    }
    
    
    public static Argparse newParser(){
        Argparse p = new Argparse();
        p.argsMap = new HashMap<String,Argument>();
        p.numMandatory = 0;
        return p;
    }
    
    public Argparse addArgument(String name,String def,String help, String type, boolean required){
        Argument a = new Argument(name);
        a.def = def;
        a.help = help;
        a.type = type;
        a.mandatory = required;
        if (required)
            numMandatory++;
        argsMap.put(name,a);
        
        return this;
    }
    
    public Argparse parse(String[] args){
        unknown = new ArrayList<String>();
        int passedRequired = 0;
        
        for(int i=0;i<args.length;i++){
            // if the arg[i] is an argument, read it and the value.
            Argument a = argsMap.get(args[i]);
            if(a!=null){
                i = setArgVal(a,i,args); //sets the value of a and increments i if necessary
                passedRequired += a.mandatory?1:0;
            } else {
                unknown.add(args[i]);
            }
        }
        
        if(unknown.size()>0){
            throw new RuntimeException("Unknown arguments:"+ arrayListToString(unknown));
        }
        if (passedRequired < numMandatory)
            throw new RuntimeException("Missing Arguments. You need all these:"+requiredToString());
        return this;
    }
    
    public String getArgumentString(String name){
        return argsMap.get(name).value;
    }
    
    // private methods
    private int setArgVal(Argument a,int pos,String[] arguments){
        if (argsMap.containsKey(arguments[pos+1])) // then the argument a is a flag.
            a.value = a.def;
        else{
            pos++;
            a.value = arguments[pos];
        }
        
        return pos;
    }
    
    // Utility
    public String arrayListToString(ArrayList<String> a){
        StringBuilder sb = new StringBuilder("");
        for(String o:a)
            sb.append(o.toString()+",");
        return sb.substring(0,sb.lastIndexOf(","));
    }
    
    public String requiredToString(){
        return null;
    }
    
    //TEST
    public static void main(String args[]){
        Argparse parser = Argparse.newParser();
        parser.addArgument("-f",null,"Filename","string",true);
        parser.addArgument("-g","kalua","some value","string",true);
        parser.addArgument("-t","raton","another arg","integer",false);
        parser.parse(args);
        
        System.out.println(parser.getArgumentString("-f")+" - " +
                            parser.getArgumentString("-g"));
    }
}

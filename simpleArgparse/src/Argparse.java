
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Very simple Argparse. It has an inner class Argument that keeps track of
 * basic properties of each command line argument.
 * Check the main method for usage.
 * @author fdiacobe
 *
 */
public class Argparse{
	public static final int STRING_TYPE=0;
	public static final int INTEGER_TYPE=1;
    
	private HashMap<String, Argument> argsMap;
    private int numMandatory;
    private ArrayList<String> unknown;
    private String progName;
    
    //PRIVATE CLASS TO REPRESENT AN ARGUMENT
    /**
     * This class represents an argument.
     * @author fdiacobe
     *
     */
    private class Argument{
    	
        String name;
        String def; //default values
        int type=0;
        String help;
        String value;
        boolean mandatory;
        
        public Argument(String name){
            this.name = name;
        }

        
        public String shortString(){
        	if(mandatory)
        		return " "+name+" "+"<some_"+getType()+">";
        	else
        		return "["+name+" "+"<some_"+getType()+">]";
        }
        
        public String toString(){
        	return "\n"+name+": "+getType()+": "+(mandatory?"optional":"required")+
        			": default="+def+"\n\t"+help;
        }
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the def
		 */
		public String getDef() {
			return def;
		}

		/**
		 * @param def the def to set
		 */
		public void setDef(String def) {
			this.def = def;
		}

		/**
		 * @return the type
		 */
		public String getType() {
			String type="";
			switch(this.type){
			case Argparse.STRING_TYPE: type="String";
				break;
			case Argparse.INTEGER_TYPE: type="Integer";
			}
			return type;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(int type) {
			if(type>0)
				this.type = type;
		}

		/**
		 * @return the help
		 */
		public String getHelp() {
			return help;
		}

		/**
		 * @param help the help to set
		 */
		public void setHelp(String help) {
			this.help = help;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			String message="";
			if(type==Argparse.STRING_TYPE)
				this.value = value;
			else if (type==Argparse.INTEGER_TYPE && value.matches("\\d+"))
				this.value = value;
			else
				throw new RuntimeException("Argument:"+name+" with value:"+value+" is of the wrong type."+toString());
		}

		/**
		 * @return true if this argument is mandatory (required)
		 */
		public boolean isMandatory() {
			return mandatory;
		}

		/**
		 * @param mandatory true if this argument is mandatory
		 */
		public void setMandatory(boolean mandatory) {
			this.mandatory = mandatory;
		}
    }
    // END OF THE PUBLIC CLASS.
    
    /**
     * Factory method to return an Argparse object.
     * @param progName is the name of the program (Class).
     * @return
     */
    public static Argparse newParser(String progName){
        Argparse p = new Argparse();
        p.progName = progName;
        p.argsMap = new HashMap<String,Argument>();
        p.numMandatory = 0;
        return p;
    }
    
    /**
     * Adds an argument to the list of possible arguments.
     * @param name the name of the argument. Can be prefixed with - or -- or nothing.
     * @param def the default value for that argument. If null, there is no default
     * @param help the help for the argument
     * @param type the type: string, integer (not yet implemented, but file is a possiblity)
     * @param required whether this argument is required (mandatory) or not.
     * @return this parser.
     */
    public Argparse addArgument(String name,String def,String help, int type, boolean required){
        Argument a = new Argument(name);
        a.setDef(def);
        a.setHelp(help);
        a.setType(type);
        a.setMandatory(required);
        if (required)
            numMandatory++;
        argsMap.put(name,a);
        
        return this;
    }
    
    /**
     * This is the method that will process the command line arguments
     * @param args
     * @return this parser.
     */
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
            throw new RuntimeException("Unknown arguments:"+ arrayListToString(unknown) +"\n"+getUsage());
        }
        if (passedRequired < numMandatory)
            throw new RuntimeException("Missing Arguments. You need all these:"+requiredToString()+"\n"+getUsage());
        return this;
    }
    
    /**
     * Gets an argument as a String.
     * @param name of the argument
     * @return that argument as a String.
     */
    public String getArgumentString(String name){
        return argsMap.get(name).value;
    }
    
    /**
     * Gets an argument as an Integer.
     * @param name of the argument
     * @return that argument as a String.
     */
    public Integer getArgumentInteger(String name){
        return Integer.valueOf(argsMap.get(name).value);
    }
    
    public String getUsage(){
    	StringBuilder sb=new StringBuilder("Usage:\n\tjava "+progName+" ");
    	StringBuilder sbLong = new StringBuilder("Details:");
    	for(String key:argsMap.keySet()){
    		sb.append(argsMap.get(key).shortString());
    		sbLong.append(argsMap.get(key).toString());
    	}
    	return sb+"\n"+sbLong;
    	
    }
    
    /**
     * Prints both arguments passed and usage.
     */
    public String toString(){
    	StringBuilder sb = new StringBuilder("");
    	for(String key:argsMap.keySet()){
    		sb.append(argsMap.get(key).name+"="+argsMap.get(key).value+"\n");
    	}
    	sb.append(getUsage());
    	return sb.toString();
    }
    // private methods
    /**
     * Method used to check an argument, see if it is in the 
     * HashMap of arguments and continue visiting the array to 
     * see if there's a value for it. 
     * If there's a value, then set it. If not, set the default
     * value as the value.
     * @param a the {@link Argument}
     * @param pos the position in {@code arguments}.
     * @param arguments the {@code String[] args}.
     * @return the position before the next argument.
     */
    private int setArgVal(Argument a,int pos,String[] arguments){
        if (argsMap.containsKey(arguments[pos+1])) // then the argument a is a flag.
            if(a.getDef()!=null)
            	a.setValue(a.getDef());
            else
            	throw new RuntimeException(a.name+" requires a value."+getUsage());
        else{
            pos++;
            a.setValue(arguments[pos]);
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
        StringBuilder sb = new StringBuilder("");
        for(String key:argsMap.keySet()){
        	if(argsMap.get(key).mandatory)
        		sb.append(", "+argsMap.get(key).name);
        }
        return sb.toString();
    }
    
    //TEST
    public static void main(String args[]){
        Argparse parser = Argparse.newParser("Test");
        parser.addArgument("-n1",null,"The first Integer",Argparse.INTEGER_TYPE,true);
        parser.addArgument("-n2","0","The second integer",Argparse.INTEGER_TYPE,true);
        parser.addArgument("-m","=","A message to display before the result",Argparse.STRING_TYPE,false);
        
        String[] args1={"-n1","34","-n2","56","-m","Result"};
        parser.parse(args1);
        
        int a = parser.getArgumentInteger("-n1");
        int b = parser.getArgumentInteger("-n2");
        String message = parser.getArgumentString("-m");
        System.out.println(a+"+"+b+message+" "+(a+b));
        
        // for convenience:
        System.out.println(parser.toString());
    }
}

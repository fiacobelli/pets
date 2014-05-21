The src directory contains the ONE class that comprises this project.

The idea is to include this simple class and use it to quickly parse arguments. It is very limited in what it can do,
but it should be enough for most argument parsing.

Here's an example of how to use it:

    public static void main(String args[]){
        // create a parser with a name
        Argparse parser = Argparse.newParser("Test");
        
        // add arguments to be parsed: 
        //    name,default value, help text, type, and whether it is required or not
        parser.addArgument("-n1",null,"The first Integer",Argparse.INTEGER_TYPE,true);
        parser.addArgument("-n2","0","The second integer",Argparse.INTEGER_TYPE,true);
        parser.addArgument("-m","default msg","A message to display before the result",Argparse.STRING_TYPE,false);
        
        // parse the arguments
        parser.parse(args);
        
        // access the argument's values with their name
        // and a getter according to their type
        int a = parser.getArgumentInteger("-n1");
        int b = parser.getArgumentInteger("-n2");
        String message = parser.getArgumentString("-m");
        System.out.println(a+"+"+b+message+" "+(a+b));
        
        // for convenience:
        System.out.println(parser.toString());
    }


What this does is it takes two required arguments: -n1 and -n2 which are numbers to be added. -m is an optional message.

****
Feel free to add to it, but the idea is to keep the usage simple and --although contrary to some OOP paradigms-- keep it in ONE class, so people can use it without hassle.

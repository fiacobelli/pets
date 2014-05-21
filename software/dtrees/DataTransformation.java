/**
 * This is meant to be a standalone utility class with methods that transform data.
 * The range of data transformations can vary. For example, training of a model transforms 
 * text files into a classifier that can later be read by a filter. 
 * A simpler transformation can be to convert a CSV file to a SQLlite db.
 * 
 * Because it is meant as a utility file, it would be useful if the main method is
 * updated as this class gets updated, giving command line options for the different
 * transformations.
 * 
 * @author fid
 *
 */
import java.io.File;
import java.io.IOException;

/*import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
*/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

//import weka.core.Instances;
//import weka.core.converters.ArffSaver;
//import weka.core.converters.CSVLoader;


public class DataTransformation {
	/**
	 * Turns a text file into a database in sql.
	 * @param src the source text file
	 * @param db destination database file "<some path>/somename.db"
	 * @param table the name of the new table
	 * @param sep field delimiter in the text file
	 * @throws Exception 
	 */
	/*public static void csvToTable(String src, String db, String table, String sep) throws Exception{
		Scanner s = new Scanner(new File(src));
		System.out.println("Working on table "+table+" from file "+src+" separator="+sep);
		String[] header = s.nextLine().split(sep);
		String dbName = db.substring(db.lastIndexOf('/')+1,db.lastIndexOf('.')); // too simple?
		String tableName = table;
		// create a table based on the filename and varchar fields based on the header fields.
		Class.forName("org.sqlite.JDBC");
	    Connection conn = DriverManager.getConnection("jdbc:sqlite:"+db);
	    Statement stat = conn.createStatement();
	    stat.executeUpdate("DROP TABLE IF EXISTS "+tableName+";");
	    System.out.println("CREATE TABLE "+tableName+" ("+arrayToCsv(header)+");");
	    stat.executeUpdate("CREATE TABLE "+tableName+" ("+arrayToCsv(header)+");");
	    
	    // Now, loop throught the file and insert values
	    String l=null; // the line in the file
	    String sqlStr=null; // the current SQL command
	    int records=0;
	    int track=100; //track interval (will printout a line saying how many records inserted so far) 
	    while(s.hasNextLine()){
	    	try{
	    		l = s.nextLine();
				String[] line = l.split(sep);
				sqlStr = "INSERT INTO "+tableName+" ("+arrayToCsv(header)+") VALUES("+repeatStringCsv("?",',',header.length)+")";
				PreparedStatement prep = conn.prepareStatement(sqlStr);
				for(int i=0;i<line.length;i++)
					prep.setString(i+1, line[i]);
				records++;
				if (records%track==0)
					System.out.print("\r"+records+"       ");
				prep.execute();
				prep.close();
	    	}
	    	catch(Exception e){
	    		String msg = "LINE: "+l+"\nSQL: "+sqlStr+"\n";
	    		throw new Exception(msg,e);
	    	}
		}
	    stat.close();
	    conn.close();
	}
	
	private static String arrayToCsv(String[] a){
		StringBuilder sb = new StringBuilder();
		for(String field:a){
			sb.append(","+field.replace(" ", "_").toLowerCase());
		}
		return sb.toString().substring(1); 
	}

	private static String repeatStringCsv(String text, char sep, int repeat){
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<repeat;i++)
			sb.append(sep+text);
		return sb.toString().substring(1);
	}
	*/
	
	public static TrainingData Csv2TrainingData(String filename, int targetAttrib)
		throws Exception{
		Scanner s = new Scanner(new File(filename));
		String firstRow = s.next();
		HashMap<String,Integer> attributeNames = getAttribNames(firstRow); //just attrib names
		ArrayList<int[]> data = new ArrayList<int[]>(); //data
		HashMap<Integer,ArrayList<String>> attributes = new HashMap<Integer, ArrayList<String>>(); //attribute details 
		for(Entry<String,Integer> e: attributeNames.entrySet())
			attributes.put(e.getValue(),new ArrayList<String>());
		if (targetAttrib<0)
			targetAttrib=attributeNames.size()-1; //if negative, then last attribute is assumed
		int count=0;

		while(s.hasNext()){
			String[] fields = s.next().split(",",-1);
			int[] values = new int[fields.length];
			for(int i=0;i<fields.length;i++){
				int value;
				// empty values are don't care in training.
				String field = fields[i].length()==0?TrainingData.DONT_CARE:new String(fields[i]);
				if (field.equals(TrainingData.DONT_CARE))
				    value = TrainingData.DONT_CARE_VAL;
				else if(attributes.get(i).contains(field))
					value = attributes.get(i).indexOf(field);
				else{
					attributes.get(i).add(field);
					value = attributes.get(i).size()-1;
				}
				values[i] = value;
			}
			data.add(values);
			count++;
		}
		
		TrainingData td = new TrainingData();
		HashMap<Integer, String[]> lazyAttribs = new HashMap<Integer, String[]>();
		for(Entry<Integer, ArrayList<String>> entry:attributes.entrySet()){
			String[] attnames = new String[entry.getValue().size()];
			entry.getValue().toArray(attnames);
			lazyAttribs.put(entry.getKey(), attnames);
		}
		td.setAttributeNames(attributeNames);
		td.setAttributes(lazyAttribs);
		td.setExamples(data);
		td.setTargetAttribute(targetAttrib);
		return td;
	}
	
	
	private static HashMap<String, Integer> getAttribNames(String headerLine){
		String[] fields = headerLine.split(",");
		//System.out.println(headerLine);
		HashMap<String,Integer> res = new HashMap<String, Integer>();
		for(int i=0;i<fields.length;i++)
			res.put(fields[i], i);
		return res;
	}
	
	
	/*public void csv2Arff(String source, String target){
	    try{
    	    CSVLoader loader = new CSVLoader();
    	    loader.setSource(new File(source));
    	    Instances data = loader.getDataSet();
    	 
    	    // save ARFF
    	    ArffSaver saver = new ArffSaver();
    	    saver.setInstances(data);
    	    saver.setFile(new File(target));
    	    saver.writeBatch();
	    }
	    catch(IOException e){
	        throw new RuntimeException("Exception converting CSV to ARFF\n"+e);
	    }
	}
	*/
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			//String dir = "/home/fid/consulting/bluemessaging/bmi/data/cinemex/";
			//String[] files = {"Cartelera.csv",  "catnatprems.txt",  "catweekprems.txt",  "sinopsis.txt","catcentros2.txt","catgeneros.txt"};
			//String[] tables = {"MOVIES_CATALOG","ESTRENOS_NACIONALES_CATALOG","ESTRENOS_SEMANALES_CATALOG","SUMMARIES_CATALOG", "THEATERS_CATALOG", "GENRES_CATALOG"};
			//String[] seps = {"\\|","\t","\t","\\|","\t","\t"};
		    String dir = "/home/fid/consulting/bluemessaging/pruebas/";
		    String[] files = {"locationsGM.csv"};
		    String[] tables={"locations"};
		    String[] seps={"\\|"};
		    
			for (int i=0;i<files.length;i++){
				System.out.println("Working on "+tables[i]+ " with "+files[i]);
				//DataTransformation.csvToTable(dir+files[i], dir+"guiamais.db", tables[i], seps[i]);
			}
			//String dir2 = "/home/fid/consulting/bluemessaging/pruebas/reglas_cinemax.csv";
			//TrainingData td = DataTransformation.Csv2TrainingData(dir2, -1);
			//System.out.println(td.toString());
		}
		catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

}

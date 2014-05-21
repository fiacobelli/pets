import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * A simple wrapper for a set of examples to use as
 * training data for non-textual classifiers using integer values only.
 * This is good enough for a discrete decision tree.
 * @author fid
 *
 */
public class TrainingData {
	HashMap<Integer,String[]> attributes;
	ArrayList<int[]> examples;
	HashMap<String,Integer> attributeNames; //used mainly for reference.
	int targetAttribute;
	public static final String DONT_CARE = "?";
	public static final int DONT_CARE_VAL = -1;
	
	public TrainingData(){
		super();
	}

	public String toString(){
		StringBuilder sb=new StringBuilder();
		int i=0,j=0;
		
		try{
			sb.append("Target Attribute:"+targetAttribute+"\n");
			for(Entry<String,Integer> att:attributeNames.entrySet()){
				sb.append(att.getValue()+": "+att.getKey());
				if(att.getValue()==targetAttribute)
					sb.append("*");
				sb.append(" {");
				if(attributes==null)
					System.out.println("CACACACACACACA");
				for (String value:attributes.get(att.getValue())){
					sb.append(value+",");
				}
				sb.append("}\n");
			}
			for(i=0;i<examples.size();i++){
				sb.append("\n"+i+"\t");
				for(int val:examples.get(i))
					sb.append(val+",");
			}
			sb.append("\nAnd in the original form:\n");
			for(i=0;i<examples.size();i++){
				sb.append("\n"+i+"\t");
				int[] values = examples.get(i);
				for(j=0;j<values.length;j++)
				    if(values[j]==TrainingData.DONT_CARE_VAL)
				        sb.append(TrainingData.DONT_CARE + ",");
				    else
				        sb.append(attributes.get(j)[values[j]]+",");
			}
			sb.append("\n");
		}
		catch(NullPointerException e){
			e.printStackTrace();
			System.out.println(sb.toString());
			throw new RuntimeException(e);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
		    e.printStackTrace();
		    System.out.println("Trying to access something weird:");
		    System.out.println("Example:"+i+" - Value["+j+"]="+examples.get(i)[j]+" - Attribute "+j+" has "+attributes.get(j).length+" elements");
		    throw new RuntimeException(e);
		}
		return sb.toString();
	}
	/**
	 * @return the attributes
	 */
	public HashMap<Integer, String[]> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(HashMap<Integer, String[]> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the examples
	 */
	public ArrayList<int[]> getExamples() {
		return examples;
	}

	/**
	 * @param examples the examples to set
	 */
	public void setExamples(ArrayList<int[]> examples) {
		//cleanExamples(examples);
		this.examples = examples;
	}

	/**
	 * This method puts the "DONT_CARE" markers as the last value for each attribute
	 * and updates the representation of the examples accordingly.
	 * @param examples2
	 * @return
	 */
	/*private void cleanExamples(ArrayList<int[]> examples2) {
		for(Entry<Integer,String[]> attrib:this.attributes.entrySet()){
			if(!attrib.getValue()[attrib.getValue().length-1].equals(DONT_CARE)){
				int oldVal = attrib.getValue().length-1;
				int newVal = findDontCareIdx(attrib.getValue());
				if (oldVal!=newVal){
					for(int[] example:examples2){
						if(example[attrib.getKey()]==oldVal)
							example[attrib.getKey()]=newVal;
					}
					attrib.getValue()[newVal]=attrib.getValue()[oldVal];
					attrib.getValue()[oldVal]=DONT_CARE;
				}
			}
		}
	}

	private int findDontCareIdx(String[] values) {
		for(int i=0;i<values.length;i++)
			if (values[i].equals(DONT_CARE))
				return i;
		return values.length-1;
	}
    */
	/**
	 * @return the attributeNames
	 */
	public HashMap<String, Integer> getAttributeNames() {
		return attributeNames;
	}

	/**
	 * @param attributeNames the attributeNames to set
	 */
	public void setAttributeNames(HashMap<String, Integer> attributeNames) {
		this.attributeNames = attributeNames;
	}

	/**
	 * @return the targetAttribute
	 */
	public int getTargetAttribute() {
		return targetAttribute;
	}

	/**
	 * @param targetAttribute the targetAttribute to set
	 */
	public void setTargetAttribute(int targetAttribute) {
		this.targetAttribute = targetAttribute;
	}
	
	/**
	 * @param targetAttribute the name of the targetAttribute to set
	 */
	public void setTargetAttribute(String targetAttribute) {
		this.targetAttribute = attributeNames.get(targetAttribute);
	}
	
		// TREAT TRAINING DATA (EXPAND DON'T CARES)
	/**
	 * This is quite inefficient, but does the job for small datasets
	 * @param td the training data processed by DataTransformation.java
	 */
	public void expandDontCare(){
	    // for each attribute
	    for(int i=0;i<this.getAttributes().size();i++){
	        ArrayList<int[]> extraExamples = new ArrayList<int[]>();
	        // that is not the class attrib
	        if (i!=this.getTargetAttribute()){
    	        // go through each example
    	        for(int j=0;j<this.getExamples().size();j++){
    	            // if a don't care is found,
    	            if (this.getExamples().get(j)[i]==TrainingData.DONT_CARE_VAL &&
    	                    this.getAttributes().get(i).length>0){
    	               //System.out.println("Expanding ? to "+this.getAttributes().get(i)[0]);
                	   this.getExamples().get(j)[i] = 0;
                	   int[] example = this.getExamples().get(j);
                	   // for each value of the attribute
                	   int k=0;
                	   for(k = 1; k<this.getAttributes().get(i).length;k++){
                	       // copy the example over.
                	       int[] newEx = example.clone();
                	       newEx[i] = k;
                	       extraExamples.add(newEx);
                	   }
                	  // System.out.println("Expanded "+k+" examples for att:"+j);
    	            }
    	        }
	        }
	        this.getExamples().addAll(extraExamples);
	    }
	}
	
	public void testSetup(){
		HashMap<String,Integer> attN = new HashMap<String,Integer>();
		attN.put("sky",0);
		attN.put("temp",1);
		attN.put("wind",2);
		attN.put("coat?",3);
		
		HashMap<Integer,String[]> atts = new HashMap<Integer,String[]>();
		String[] a0 = {"sunny","rainy","cloudy"};
		String[] a1 = {"<80",">80"};
		String[] a2 = {"Strong","Weak"};
		String[] a3 = {"no","yes"};
		atts.put(0,a0);
		atts.put(1,a1);
		atts.put(2,a2);
		atts.put(3,a3);
		
		ArrayList<int[]> e = new ArrayList<int[]>();
		int[] e1 = {0,0,1,1}; //sunny, <80,weak,yes
		int[] e2 = {0,1,1,0}; //sunny,>80,weak,no
		int[] e3 = {1,-1,-1,1};//rainy,?,?,yes
		int[] e4 = {2,0,-1,1}; //cloudy,<80,?,yes
		int[] e5 = {2,1,-1,0}; //cloudy,>80,?,no
		e.add(e1);
		e.add(e2);
		e.add(e3);
		e.add(e4);
		e.add(e5);
		
		this.setAttributeNames(attN);
		this.setAttributes(atts);
		this.setTargetAttribute(3);
		this.setExamples(e);
	
	}
	
	
	public static void main(String[] args){
		
		TrainingData td = new TrainingData();
		td.testSetup();
		System.out.println(td.toString());
		td.expandDontCare();
		System.out.println(td.toString());
		
	}
	
}

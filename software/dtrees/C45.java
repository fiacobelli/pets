import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

//import com.bm.bmi.util.data.DataTransformation;
//import com.bm.bmi.util.data.TrainingData;

public class C45 {	
	public boolean DEBUG = false;
	public static String DONT_CARE = TrainingData.DONT_CARE;
	public static class DTree{
		public int attribute;
		public int freqValue;
		public DTree[] children;
		
		public DTree(){
			super();
		}
		
		public DTree(int value,int numChildren){
			this.attribute = value;
			this.children = new DTree[numChildren];
		}
		
		public DTree(int value,int numChildren, int mostFrequentVal){
			this.attribute=value;
			this.children = new DTree[numChildren];
			this.freqValue = mostFrequentVal;
		}
		
		public DTree(int attribute){
			this.attribute = attribute;
		}
		
		public boolean isLeaf(){
			return (children==null);
		}
		
		public String toString(){
			return this.toString(0);
		}
		
		public String toString(int depth){
			StringBuilder sb = new StringBuilder("\n");
			for (int i=0;i<depth;i++)
				sb.append("|  ");
			sb.append("+--"+this.attribute);
			if(this.children!=null && this.children.length>0){
				for(DTree node:children)
					sb.append(node.toString(depth+1));
			}
			else
				sb.append("*"); //leaf node.
			return sb.toString();
		}

		/**
		 * @return the value
		 */
		public int getAttribute() {
			return attribute;
		}

		public int getMostFreqValue(){
			return freqValue;
		}
		
		/**
		 * @param value the value to set
		 */
		public void setAttribute(int value) {
			this.attribute = value;
		}
		
		/**
		 * @param the default value to use if in conflict.
		 */
		public void setMostFreqValue(int value) {
			this.freqValue = value;
		}

		/**
		 * @return the children
		 */
		public DTree[] getChildren() {
			return children;
		}

		/**
		 * @param children the children to set
		 */
		public void setChildren(DTree[] children) {
			this.children = children;
		}
	}

	//TRAINING
	/**
	 * Given a set of attributes with their values, a class attribute with its possible values
	 * and training data, this function returns an ID3 Tree. I will modify it to resemble C4.5.
	 * @param attributes HashMap with an attribute id and its possible values. The indices of the values 
	 * are the ids of those values.
	 * @param classes
	 */
	public DTree trainC45(HashMap<Integer,String[]> attributes, int targetAttrib, ArrayList<int[]> examples){
		DTree root;
		if (examples==null || examples.size()==0)
			return null;
		if (areExamplesOneClass(examples,targetAttrib))
			return new DTree(examples.get(0)[targetAttrib]);
		if (attributes.size()==1) //only the target attribute
			return new DTree(mostCommonClass(examples,targetAttrib,attributes));
		int A = bestClassifier(attributes,examples,targetAttrib);
		if (DEBUG) System.out.println("Best class attrib:"+A);
		root = new DTree(A,attributes.get(A).length,getMostCommonValue(examples,A));
		HashMap<Integer,String[]> newatts = (HashMap<Integer,String[]>)attributes.clone();
		newatts.remove(A);
		for (int i=0;i<root.children.length;i++){
			ArrayList<int[]> exSubset = subsetExamples(A,i,examples);
			//printExamples(exSubset);
			if (exSubset.size()==0)
				root.children[i]=new DTree(mostCommonClass(examples,targetAttrib,newatts));
			else
				root.children[i] = trainC45(newatts,targetAttrib,exSubset);
		}
		return root;
	}

	/**
	 * Returns the possible number of values for an attribute.
	 * If the training put "don't cares" there, they are 
	 * substracted from the total number of vals.
	 * @param strings
	 * @return
	 */
	private int getNumValues(String[] values) {
		//if (values.length>0]) // some values only have don't cares. This is wrong!
		//	int minusDontCare = values[values.length-1].equals(DONT_CARE)?-1:0;
		return values.length;//-minusDontCare;
	}

	public int getMostCommonValue(ArrayList<int[]> examples, int targetAttrib){
		HashMap<Integer,Integer> counts = new HashMap<Integer,Integer>();
		for(int[] example:examples){
			if(counts.containsKey(example[targetAttrib]))
				counts.get(example[targetAttrib]);
			else
				counts.put(example[targetAttrib],1);
		}
		
		return getMaxVal(counts);
		
	}
	
	public int getMaxVal(HashMap<Integer,Integer> counts){
		int maxVal = -1;
		int value = -1;
		for(Entry<Integer,Integer> e:counts.entrySet())
			if(e.getValue() > maxVal){
				maxVal=e.getValue();
				value = e.getKey();
			}
		return value;
	}
	
	
	public boolean areExamplesOneClass(ArrayList<int[]> examples, int targetAttrib){
		int lastClass=examples.get(0)[targetAttrib];
		for(int[] example: examples)
			if(example[targetAttrib]!=lastClass)
				return false;
		return true;
	}
	
	public int mostCommonClass(ArrayList<int[]> examples, int targetAttrib, HashMap<Integer,String[]> attributes){
		int[] classCount = new int[attributes.get(targetAttrib).length];
		for(int[] example:examples)
			classCount[example[targetAttrib]]++;
		return maxIndex(classCount);
	}
		
	public ArrayList<int[]> subsetExamples(int attribute,int value,ArrayList<int[]> examples){
		ArrayList<int[]> res= new ArrayList<int[]>();
		for(int[] example:examples){
			if(example[attribute]==value)
				res.add(example);
		}
		return res;
	}
	
	public int bestClassifier(HashMap<Integer, String[]> attributes, ArrayList<int[]> example, int targetAttrib){
		int bestAtt = -1;
		double bestGain = Integer.MIN_VALUE;
		for(Entry<Integer,String[]> attribute:attributes.entrySet()){
			double G = attribute.getKey()==targetAttrib?Integer.MIN_VALUE:gain(example,attribute.getKey(),attributes,targetAttrib);
			System.out.println("G("+attribute.getKey()+")="+G);
			if(G>bestGain){
				bestAtt = attribute.getKey();
				bestGain = G;
			}
		}
		System.out.println("Best gain with attribute: G("+bestAtt+")="+bestGain);
		if (DEBUG) System.out.println("G("+bestAtt+")="+bestGain);
		return bestAtt;
	}
	
	// Helper math functions
	public double log2(double i){
		return Math.log(i)/Math.log(2);
	}
	
	public double entropy(ArrayList<int[]> examples, int attribute,HashMap<Integer,String[]> attributes){
		double res=0.0;
		for(int j=0;j<attributes.get(attribute).length;j++){
			double P = prob(j,attribute,examples);
			if (P!=0.0){ //when P=0 it means the value is not in the set. 
				res += P*log2(P);
			}
		}
		return -res;
	}
	
	public ArrayList<int[]> subset(ArrayList<int[]> examples, int attribute, int value){
		ArrayList<int[]> sub = new ArrayList<int[]>();
		for(int[] example:examples){
			if (example[attribute]==value){
				sub.add(example);
			}
		}
		return sub;
	}
	
	public double prob(int value, int attribute, ArrayList<int[]> examples){
		int instance=0;
		for(int[] example:examples)
			if(example[attribute]==value) instance++;
		//System.out.println("Prob("+value+", at="+attribute+"="+(double)instance/(double)examples.size());
		return (double)instance/(double)examples.size();
	}
	
	public double gain(ArrayList<int[]> example, int attribute, HashMap<Integer,String[]> attributes, int targetAttribute){
		double sum = 0.0;
		if (attributes.get(attribute).length<1) // it only has missing or don't cares
			return Integer.MIN_VALUE;
		for(int i=0;i<attributes.get(attribute).length;i++){
				double P = prob(i,attribute,example);
				sum += P==0.0?0:P*entropy(subset(example, attribute, i),targetAttribute,attributes);
		}
		return entropy(example,targetAttribute,attributes)-sum;
	}
	
	// Prediction
	/**
	 * Predicts based on an instance, which is an array of ints. 
	 * The elements of the array have to be in the same order as
	 * the training data.
	 */
	public int predict(int[] instance, DTree model){
		DTree node = model;
		while(!node.isLeaf()){ //while not a leaf node
			int attrib = node.getAttribute();
			int val=instance[attrib];
			if (val>=node.getChildren().length || val<0){
				System.out.println("PROBLEM! Value:"+val+" is not an alternative for "+ attrib);
				System.out.println("Choosing the first branch (0)");
				val=node.getMostFreqValue();
			}
			node=node.getChildren()[val];
		}
		return node.getAttribute(); //which is a value of the class attrib on leaf nodes.
	}
	
	// Helper random functions.
	
	/**
	 * returns the index of the largest number in the array.
	 */
	public int maxIndex(int[] array){
		int idx=0;
		for(int i=0;i<array.length;i++)
			if(array[i]>array[idx])
				idx=i;
		return idx;
	}

	public void printExamples(ArrayList<int[]> ex){
		for(int[] example:ex)
			if (DEBUG) System.out.println(example2String(example));
	}
	
	public String example2String(int[] example){
		StringBuilder sb=new StringBuilder();
		for(int value:example)
			sb.append(value+",");
		return sb.toString();
	}
	/**
	 * 
	 * @param examples
	 * @param attribute
	 * @return the value of the attribute in a random entry of the hashmap.
	 */
	public int peekValue(HashMap<Integer,int[]> examples,int attribute){
		ArrayList<Integer> keys = new ArrayList<Integer>(examples.keySet());
		if (keys.size()>0)
			return examples.get(keys.get(0))[attribute];
		else
			return -1;
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			TrainingData td = DataTransformation.Csv2TrainingData("/media/sf_fac-staff/teaching/datasets/diabetes.csv", -1);
			TrainingData test = DataTransformation.Csv2TrainingData("/media/sf_fac-staff/teaching/datasets/diabetes.csv", -1);
			//TrainingData test = DataTransformation.Csv2TrainingData("/home/fid/consulting/codeTests/play_tennis.csv", -1);
			//TrainingData td = new TrainingData();
			//TrainingData test = new TrainingData();
			//td.testSetup();
			//test.testSetup();
			//test.expandDontCare();
			System.out.println(td.toString());
			C45 id3=new C45();
			td.expandDontCare();
			System.out.println(td.toString());
			C45.DTree model = id3.trainC45(td.getAttributes(), td.getTargetAttribute(), td.getExamples());
			System.out.println(model.toString());
			// Evaluate the model
			// now, td should now be a TestSet, but I am just evaluating on the training data.
			ArrayList<int[]> testSet = test.getExamples();
			HashMap<Integer,Double[]> precByClass = new HashMap<Integer,Double[]>();
			int tp=0,fp=0; //true positives, true negatives
			for(int i=0;i<testSet.size();i++){
				int e=testSet.get(i)[td.getTargetAttribute()];
				int p=id3.predict(testSet.get(i),model);
				if (e==p)
					tp++;
				else
					fp++;
				System.out.println("Expected/Predicted:"+e+"/"+p+" OR "+td.getAttributes().get(td.getTargetAttribute())[e]+"/"+td.getAttributes().get(td.getTargetAttribute())[p]+" TP:"+tp+" - FP:"+fp);
			}
			System.out.println("Accuracy:" + ((double)tp/((double)tp+(double)fp)));
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

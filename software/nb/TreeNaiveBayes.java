package com.rc.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.rc.tree.ArrayTree;
import com.rc.tree.Tree;
import com.rc.util.MultiArray;


public class TreeNaiveBayes implements Serializable {
    
    //~ CONSTANT(S) ----------------------------------------------------------

    private static final long serialVersionUID = -6523267264254686755L;
    
    // This index means that we do not care about the value
    public static final int UNSEEN = -1;
    
    // This index means that we have a missing value
    public static final int MISSING = -2;
    
    // Wether we should check that sums are valid
    // private static final boolean CHECK_SUM = true;

    
    //~ ATTRIBUTE(S) ---------------------------------------------------------

    private boolean ignoreUnseen = false;
    private boolean complement = true;
    private double smoothingWeight = 0.00;
    private double countNormalizer = 0.00;
    private double countInitializer = 0.00;
    private Tree<Info> tree = new ArrayTree<Info>();
    

    //~ CONSTRUCTOR(S) -------------------------------------------------------

    public TreeNaiveBayes() { }
    

    //~ METHOD(S) ------------------------------------------------------------

    public void addClass(String name) { addClass(name, null); }
    public void addClass(String name, Object[] states) { 
        Tree.Node<Info> node = tree.add(null, new Info(name, true));
        if (states != null) setStates(node.getData(), states);
    }
    
    public String getClassName() { return getRoot().name; }

    public Object[] getClassStates() { 
        ArrayList<Object> states = getRoot().states;
        boolean allStrings = true;
        for (Object st : states) {
            if (!(st instanceof String)) { allStrings = false; break; }
        }
        return allStrings? states.toArray(new String[states.size()]) : states.toArray();
    }
    
    public void setClassStates(Object[] states) { if (states != null) setStates(getRoot(), states); }

    public void addAttribute(String name) { addAttribute(name, null); }
    public void addAttribute(String name, Object[] states) { 
        Tree.Node<Info> node = tree.add(tree.getRoot(), new Info(name, false)); 
        if (states != null) setStates(node.getData(), states);
    }

    public String[] getAttributeNames() { 
        Tree.Node<Info>[] children = tree.getRoot().getChildren();
        String[] names = new String[children.length];
        for (int i = 0; i < children.length; i++) names[i] = children[i].getData().name;
        return names;
    }
    
    public Object[] getAttributeStates(String name) { 
        ArrayList<Object> states = getInfo(name).states;
        boolean allStrings = true;
        for (Object st : states) {
            if (!(st instanceof String)) { allStrings = false; break; }
        }
        return allStrings? states.toArray(new String[states.size()]) : states.toArray();
    }
    
    public void setAttributeStates(String name, Object[] states) { 
        if (states != null) setStates(getInfo(name), states);
    }
    
    public Info getRoot() {
        return tree.getRoot().getData();
    }
    
    public Info getInfo(int i) {
        return tree.getRoot().get(i).getData();
    }
    
    public Info getInfo(String name) {
        for (Iterator<Tree.Node<Info>> it = tree.iterator(); it.hasNext(); ) {
            Tree.Node<Info> node = it.next();
            Info info = node.getData();
            if (name == null? info.name == null : name.equals(info.name)) return node.getData();
        }
        return null;
    }
    
    public double getSmoothingWeight() { return smoothingWeight; }
    public boolean hasSmoothingWeight() { return smoothingWeight != 0.0; }
    public void setSmoothingWeight(double sw) { smoothingWeight = sw; }
    
    public double getCountNormalizer() { return countNormalizer; }
    public boolean hasCountNormalizer() { return countNormalizer != 0.0; }
    public void setCountNormalizer(double cn) { countNormalizer = cn; }
    
    public double getCountInitializer() { return countInitializer; }
    public void setCountInitializer(double ci) { 
        double oldCountInitializer = countInitializer;
        countInitializer = ci; 
        double diff = (countInitializer - oldCountInitializer);
        
        Tree.Node<Info> root = tree.getRoot();
        if (root == null) return;
        Info info = root.getData();
        info.countTable.plusEquals(diff);
        // info.sums[0] = info.states.size() * diff;
        for (Iterator<Tree.Node<Info>> it = root.iterator(); it.hasNext();) {
            info = it.next().getData();
            info.countTable.plusEquals(diff);
            // VectorUtils.plusEquals(info.sums, info.states.size() * diff);
        }
    }

    public boolean isIgnoringUnseen() { return ignoreUnseen; }
    public void setIgnoreUnseen(boolean iu) { ignoreUnseen = iu; }
    
    public boolean isComplement() { return complement; }
    public void setComplement(boolean c) { complement = c; }
    
    public int size() { return tree.getRoot().childCount(); }

    public String toString() { return toString(tree.getRoot()); }

    private String toString (Tree.Node<Info> node) {
        StringBuffer sb = new StringBuffer();        
        sb.append(node.getData().name);
        sb.append(node.getData().states.toString());
        if (node.childCount() > 0) {
            sb.append(" [");
            for (Iterator<Tree.Node<Info>> it = node.iterator(); it.hasNext();) {
                sb.append(toString(it.next()));
                if (it.hasNext()) sb.append (", ");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    
    //~ TRAINING METHOD(S) ---------------------------------------------------

    public void addExample(String targetName, Object targetState, Map<String, Object> childStates) { 
        Tree.Node<Info> root = tree.getRoot();
        if (root == null || !root.getData().name.equals(targetName)) {
            tree.clear();
            addClass(targetName, null);
        }
        for (String name : childStates.keySet()) {
            if (getInfo(name) == null) addAttribute(name, null);
        }
        addExample(targetState, getCategoricalChildStates(childStates));
    }
    
    public void addExample(Object targetState, Object[] childStates) {
        // If the target example is missing, ignore training sample completely
        if (isValueEmpty(targetState)) return;
        
        // Add one to examples and ensure the root value is accounted for
        int rootState = ensureValue(getRoot(), targetState);
        Info rootInfo = getRoot();
        double observations = rootInfo.countTable.get(rootState) + 1;
        rootInfo.countTable.set(rootState, observations);            
        
        for (int i = 0; i < childStates.length; i++) {
            // Make sure the value is not empty, otherwise ignore it
            if (isValueEmpty(childStates[i])) continue;
            
            // Now add the value
            Tree.Node<Info> node = tree.getRoot().get(i);
            int state = ensureValue(node.getData(), childStates[i]);
            Info nodeInfo = node.getData();
            observations = nodeInfo.countTable.get(rootState, state) + 1;
            nodeInfo.countTable.set(rootState, state, observations);
        }
    }

    public void setClassCountTable(double[] value) {
    }
    
    /**
     * Slow method, use with care
     */
    public void setAttributeCountTable(String name, double[][] value) {
    }
    
    

    //~ CLASSIFICATION METHOD(S) ---------------------------------------------

    public Object classify(Map<String, Object> childStates) { 
        return classify(getCategoricalChildStates(childStates)); 
    }
    
    public Object classify(Object[] childStates) { 
        int rootState = classify(getOrdinalChildStates(childStates)); 
        return getRoot().states.get(rootState);
    }
    
    public int classify(int[] childStates) {
        int maxIndex = -1;
        double maxValue = -1;
        for (int i = 0, l = getRoot().states.size(); i < l; i++) {
            double cv = classificationValue(i, childStates);
            if (cv > maxValue) { maxIndex = i; maxValue = cv; }
        }
        return maxIndex;
    }
    
    public double[] classificationValues(Map<String, Object> childStates) { return classificationValues(getCategoricalChildStates(childStates)); }
    public double[] classificationValues(Object[] childStates) { return classificationValues(getOrdinalChildStates(childStates)); }
    protected double[] classificationValues(int[] childStates) {
        int rootStateCount = getRoot().states.size();
        double[] values = new double[rootStateCount];
        for (int i = 0; i < rootStateCount; i++) values[i] = classificationValue(i, childStates);
        return values;
    }

    /**
     * The classification value is the numerator of the probability term.  Mitchell (in 
     * http://www.cs.cmu.edu/~tom/mlbook/NBayesLogReg.pdf) describes it as:
     * 
     *           __
     * P(Y=y_k)  ||i P(X_i|Y=y_k)
     * 
     * 
     * @param rootState
     * @param childStates
     * @return
     */
    public double classificationValue(Object rootState, Map<String, Object> childStates) { return classificationValue(rootState, getCategoricalChildStates(childStates)); }
    public double classificationValue(Object rootState, Object[] childStates) { return classificationValue(getRoot().states.indexOf(rootState), getOrdinalChildStates(childStates)); }
    protected double classificationValue(int rootState, int[] childStates) {
        double prob = getMarginalProbability(tree.getRoot(), rootState);
        double value = prob;
        
        // If we want marginal probabilities (not conditioned on anything)
        if (childStates == null) return value;
        
        for (int i = 0; i < childStates.length; i++) {
            // Ignore unseen data only if mandated
            if (ignoreUnseen && childStates[i] == UNSEEN) continue;
            
            // Always ignore missing data
            if (childStates[i] == MISSING) continue;
            
            Tree.Node<Info> node = tree.getRoot().get(i);
            value *= getConditionalProbability(node, rootState, childStates[i]);
        }
        return value;
    }
    
    /**
     * Probabilities are the classification values normalized by the sum of all
     * the values.
     * 
     * @param childStates
     * @return Returns an array of probabilities.  Each probabilty values has to be
     *         matched against the list of possible states.
     */
    public double[] getProbabilities() { return getProbabilities((int[])null); }
    public double[] getProbabilities(Map<String, Object> childStates) { return getProbabilities(getCategoricalChildStates(childStates)); }
    public double[] getProbabilities(Object[] childStates) { return getProbabilities(getOrdinalChildStates(childStates)); }
    protected double[] getProbabilities(int[] childStates) {
        double[] values = classificationValues(childStates);
        double sum = 0.0;
        for (int i = 0; i < values.length; i++) sum += values[i];
        if (sum == 0.0) return values;
        for (int i = 0; i < values.length; i++) values[i] /= sum;
        return values;
    }

    /**
     * Calculate individual probability.  If what is really wanted is to calculate the
     * probabilities for all states, the funcion <code>getProbabilities</code> should
     * be used, since this function is really <b>innefficient</b> since it has to compute
     * the sum of classification values over and over.
     * 
     * @param rootState
     * @param childStates
     * @return
     */
    public double getProbability(Object rootState) { return getProbability(rootState, (Object[])null); }
    public double getProbability(Object rootState, Map<String, Object> childStates) { return getProbability(rootState, getCategoricalChildStates(childStates)); }
    public double getProbability(Object rootState, Object[] childStates) { return getProbability(getRoot().states.indexOf(rootState), getOrdinalChildStates(childStates)); }
    protected double getProbability(int rootState, int[] childStates) { 
        double value = classificationValue(rootState, childStates);
        
        // Calculate sum
        double sum = 0;
        for (int i = 0, l = getRoot().states.size(); i < l; i++) sum += classificationValue(i, childStates);
        
        // Return probability
        return value / sum;
    }

    public double getMarginalProbability(Tree.Node<Info> node, Object rootState) { 
        return getMarginalProbability(node, node.getData().states.indexOf(rootState)); 
    }
    
    protected double getMarginalProbability(Tree.Node<Info> node, int rootState) {
        Info info = node.getData();
        if (node.getParent() != null) throw new IllegalArgumentException("Node '"+info.name+"' cannot be used here, use child equivalent.");
        
        // Variable 'sum' is the sum of all probabilities for the current state(s)
        // double sum = info.sums[0];
        // if (CHECK_SUM && sum != info.getSum()) throw new RuntimeException("Expected '"+info.getSum()+"' count, but got '"+sum+"'");
        double sum = info.getSum();
        
        // If the state does not exist, then the count is zero
        double count = rootState == UNSEEN? 0.0 : info.countTable.get(rootState);

        // P(w) = C(w) + s / N + s * V
        if (countNormalizer == 0.0) return (count + smoothingWeight) / (sum + info.states.size() * smoothingWeight);
        else return ((countNormalizer*count/sum) + smoothingWeight) / (countNormalizer + info.states.size() * smoothingWeight);
    }
    
    public double getConditionalProbability(Tree.Node<Info> node, Object rootState, Object state) { 
        return getConditionalProbability(node, getRoot().states.indexOf(rootState), node.getData().states.indexOf(state)); 
    }
    
    protected double getConditionalProbability(Tree.Node<Info> node, int rootState, int state) {
        Info info = node.getData();
        if (node.getParent() == null) throw new IllegalArgumentException("Node '"+info.name+"' cannot be used here, use root equivalent.");

        // Variable 'sum' is the sum of all probabilities for the current state(s)
        // double sum = info.sums[rootState];
        // if (CHECK_SUM && sum != info.getSum(rootState)) throw new RuntimeException("Expected '"+info.getSum(rootState)+"' count, but got '"+sum+"'");
        double sum = info.getSum(rootState);
        
        // If the sum is zero, so will the final result
        // if (sum == 0) return 0.0;
        
        // If the state does not exist, then the count is zero
        double count = rootState == UNSEEN || (state == UNSEEN || state == MISSING)? 0.0 : info.countTable.get(rootState, state);

        // P(w) = C(w) + s / N + s * V
        if (countNormalizer == 0.0) return (count + smoothingWeight) / (sum + info.states.size() * smoothingWeight);
        else return ((countNormalizer*count/sum) + smoothingWeight) / (countNormalizer + info.states.size() * smoothingWeight);
    }
    
    
    //~ HELPER(S) ------------------------------------------------------------

    private boolean isValueEmpty(Object value) {
        return value == null || (value instanceof String && ((String)value).length() == 0);
    }

    private Object[] getCategoricalChildStates(Map<String, Object> mapChildStates) {
        if (mapChildStates == null) return null;
        Object[] catChildStates = new Object[tree.getRoot().childCount()];
        int i = 0;
        for (Iterator<Tree.Node<Info>> it = tree.getRoot().iterator(); it.hasNext();) {
            Tree.Node<Info> node = it.next();
            Object state = mapChildStates.get(node.getData().name);
            catChildStates[i++] = state;
        }
        return catChildStates;
    }

    private int[] getOrdinalChildStates(Object[] catChildStates) {
        if (catChildStates == null) return null;
        int[] ordChildStates = new int[tree.getRoot().childCount()];
        int i = 0;
        for (Iterator<Tree.Node<Info>> it = tree.getRoot().iterator(); it.hasNext();) {
            Tree.Node<Info> node = it.next();
            ordChildStates[i] = node.getData().states.indexOf(catChildStates[i]);
            i++;
        }
        return ordChildStates;
    }

    private int ensureValue(Info info, Object value) {
        // Check that the value is not is null
        if (value == null) throw new IllegalArgumentException("State value cannot be null.");

        int index = info.states.indexOf(value);
        if (index != -1) return index;
        if (info == getRoot()) {
            // Add state to root node
            info.states.add(value);
            info.countTable.resize(new int[] { info.states.size() }, countInitializer);

            // Ensure sums is well defined
            // info.sums[0] += countInitializer;
            
            // Iterate resizing lower nodes
            for (Iterator<Tree.Node<Info>> it = tree.getRoot().iterator(); it.hasNext();) {
                Tree.Node<Info> childNode = it.next();
                Info childInfo = childNode.getData();
                childNode.getData().countTable.resize(new int[] { info.states.size(), childInfo.states.size() }, countInitializer);
                // childInfo.sums = ArrayUtils.add(childInfo.sums, countInitializer);
            }
        }
        else {
            info.states.add(value);
            info.countTable.resize(new int[] { getRoot().states.size(), info.states.size() }, countInitializer);
        }
        
        return info.states.size() - 1;
    }

    private void setStates(Info info, Object[] states) { 
        info.states.clear();
        for (Object state : states) if (state == null) throw new IllegalArgumentException("State array "+Arrays.asList(states)+" cannot contain nulls.");
        for (Object state : states) ensureValue(info, state);
    }
    
    
    //~ INNER CLASS(ES) ------------------------------------------------------
    
    public class Info implements Serializable {
        
        //~ ATTRIBUTE(S) -----------------------------------------------------
        
        private static final long serialVersionUID = -2708261083948922291L;

        /**
         * Variable name
         */
        protected String name;
        
        /**
         * All the possible states that a node can be in
         */
        protected ArrayList<Object> states = new ArrayList<Object>();
        
        /**
         * Count Table for the node.  In the case of a root node this is a uni-dimensional
         * matrix, in the case of a child node, this a multidimensional matrix.
         */
        protected MultiArray        countTable;
        
        /**
         * Variable <code>sums</code> contains the sum of the counts in the table
         * in such a way that they serve as a cache.  In the case of a root node, this
         * is an array with only one element in it
         */
        // protected double[]         sums;
        
        
        //~ CONSTRUCTOR(S) -------------------------------------------------------
        
        protected Info(String nm, boolean root) { 
            name = nm; 
            countTable = new MultiArray(root? new int[] { 0 } : new int[] { 0, 0 });
            // sums = new double[root? 1 : getRoot().states.size() ];
            // for (int i = 0; i < sums.length; i++) sums[i] = countInitializer;
        }
        
        
        //~ METHOD(S) ------------------------------------------------------------

        // protected double getCountTableValue(int rootState) { return countTable.get(rootState); }
        
        // protected double getCountTableValue(int rootState, int state) { return countTable.get(rootState, state); }

        /*
        protected double[] getCountTable() {
            double probs[] = new double[states.size()];
            for (int i = 0; i < probs.length; i++) probs[i] = countTable.get(i);
            return probs;
        }

        protected double[] getCountTable(int rootState) {
            double probs[] = new double[states.size()];
            for (int i = 0; i < probs.length; i++) probs[i] = countTable.get(rootState, i);
            return probs;
        }
        */

        public String getName() { return name; }
        
        /*
        protected double setCountTableValue(int rootState, double value) {
            sums[0] += (value - countTable.get(rootState));
            return countTable.set(rootState, value);
        }
        
        protected double setCountTableValue(int rootState, int state, double value) {
            sums[rootState] += (value - countTable.get(rootState, state));
            return countTable.set(rootState, state, value);
        }
        */

        @Override
        public String toString() { return name+" "+states.toString(); }
        
        
        //~ HELPER(S) ------------------------------------------------------------
        
        protected double getSum() {
            int sc = countTable.getSize(0);
            double sum = 0.0;
            for (int i = 0; i < sc; i++) sum += countTable.get(i);
            return sum;
        }
        
        protected double getSum(int rootState) {
            int sc = countTable.getSize(1);
            double sum = 0.0;
            for (int i = 0; i < sc; i++) sum += countTable.get(rootState, i);
            return sum;
        }
    }
}

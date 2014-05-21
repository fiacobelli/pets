package com.rc.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestSuite;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rc.ai.ListNaiveBayes.Info;


public class NaiveBayesTest {
    
    //~ CONSTANT(S) ----------------------------------------------------------
    
    public static double DELTA = Math.pow(10.0, -4.0);
    private static final int TARGET = 5;
    private static final int[] FEATURES = {1, 2, 3, 4};
    
    
    //~ ATTRIBUTES ------------------------------------------------------------

    protected ListNaiveBayes playTennisNet;
    protected Info PlayTennis, Outlook, Temperature, Humidity, Wind;
    protected String[] playTennisNames;
    protected List<String[]> playTennisData;
    protected String[] playTennisInstance = {"Sunny", "Cool", "High", "Strong"};
    protected String[] PlayTennisStates   = {"Yes", "No"};
    protected String[] OutlookStates      = {"Sunny", "Overcast", "Rain"};
    protected String[] TemperatureStates  = {"Hot", "Mild", "Cool"};
    protected String[] HumidityStates     = {"High", "Normal"};
    protected String[] WindStates         = {"Weak", "Strong"};
    int YES = 0, NO = 1;

    private String[] values = new String[FEATURES.length];


    //~ CONSTRUCTOR -----------------------------------------------------------


    //~ SETUP -----------------------------------------------------------------

    @Before
    public void setUp() {
        playTennisNet = new ListNaiveBayes();
        playTennisData = DataGenerator.playTennis();
        playTennisNames = playTennisData.remove(0);
        
        playTennisNet.addClass("PlayTennis", new String[] { "Yes", "No" });
        PlayTennis = playTennisNet.getInfo("PlayTennis");
        playTennisNet.addAttribute("Outlook", new String[] { "Sunny", "Overcast", "Rain" });
        Outlook = playTennisNet.getInfo("Outlook");
        playTennisNet.addAttribute("Temperature", new String[] { "Hot", "Mild", "Cool" });
        Temperature = playTennisNet.getInfo("Temperature");
        playTennisNet.addAttribute("Humidity", new String[] { "High", "Normal" });
        Humidity = playTennisNet.getInfo("Humidity");
        playTennisNet.addAttribute("Wind", new String[] { "Weak", "Strong" });
        Wind = playTennisNet.getInfo("Wind");
    }


    //~ METHODS ---------------------------------------------------------------

    @Test
    public void testGetState() {
        // System.out.println("states: "+ArrayUtils.toString(playTennis.getInfo("PlayTennis").getStates()));
        Assert.assertArrayEquals(PlayTennisStates, playTennisNet.getAttributeStates("PlayTennis"));
    }

    
    //~ TRAINING METHOD(S) ---------------------------------------------------

    // Tests that adding examples, one by one, yields the desired result
    @Test
    public void testAddExample() {
        // Starter table
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Wind, NO), DELTA);
        
        // Add example
        playTennisNet.addExample(selectTarget(0), selectFeatures(0));
        
        // Tables after example
        Assert.assertArrayEquals(new double[] {0, 1},    getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0, 0}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0, 0}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0},    getCountTable(Wind, NO), DELTA);
        
        // Add example
        playTennisNet.addExample(selectTarget(1), selectFeatures(1));
        
        // Tables after example
        Assert.assertArrayEquals(new double[] {0, 2},    getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 0, 0}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 0}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 0, 0}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 0},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(Wind, NO), DELTA);
        
        // Add example
        playTennisNet.addExample(selectTarget(2), selectFeatures(2));
        
        // Tables after example
        Assert.assertArrayEquals(new double[] {1, 2},    getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {0, 1, 0}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 0, 0}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0, 0}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 0, 0}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 0},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(Wind, NO), DELTA);
        
        // Add rest of the examples
        for (int i = 3; i < playTennisData.size(); i++) playTennisNet.addExample(selectTarget(i), selectFeatures(i));

        // Numbers should be aligned with Mitchell
        Assert.assertArrayEquals(new double[] {9, 5},    getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {2, 4, 3}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 0, 2}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {2, 4, 3}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 2, 1}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {3, 6},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {4, 1},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {6, 3},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 3},    getCountTable(Wind, NO), DELTA);
    }

    // Test adds a node after some values have been inserted.  The first line in the test
    // adds the dataset, and then it tries adding a dummy node.
    @Test
    public void testAddLateNode() {
        // Add all the examples
        for (int i = 0; i < playTennisData.size(); i++) playTennisNet.addExample(selectTarget(i), selectFeatures(i));
        
        // Add dummy node
        playTennisNet.addAttribute("Dummy", null);
        Info Dummy = playTennisNet.getInfo("Dummy");

        // Add 'foo' example
        playTennisNet.addExample("No", new String[] {"Rain", "Hot", "High", "Weak", "Foo"});

        // Adding one to all the elements of the example
        Assert.assertArrayEquals(new double[] {9, 6},    getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {2, 4, 3}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 0, 3}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {2, 4, 3}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 2, 1}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {3, 6},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {5, 1},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {6, 3},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 3},    getCountTable(Wind, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0},       getCountTable(Dummy, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1},       getCountTable(Dummy, NO), DELTA);

        // Add 'bar' example (introducing a new target state)
        playTennisNet.addExample("Maybe", new String[] {"Rain", "Hot", "High", "Weak", "Bar"});
        int MAYBE = PlayTennis.states.indexOf("Maybe");
        
        // Adding one to new target state
        Assert.assertArrayEquals(new double[] {9, 6, 1}, getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {2, 4, 3}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 0, 3}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0, 1}, getCountTable(Outlook, MAYBE), DELTA);
        Assert.assertArrayEquals(new double[] {2, 4, 3}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 2, 1}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0, 0}, getCountTable(Temperature, MAYBE), DELTA);
        Assert.assertArrayEquals(new double[] {3, 6},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {5, 1},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0},    getCountTable(Humidity, MAYBE), DELTA);
        Assert.assertArrayEquals(new double[] {6, 3},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 3},    getCountTable(Wind, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0},    getCountTable(Wind, MAYBE), DELTA);
        Assert.assertArrayEquals(new double[] {0, 0},    getCountTable(Dummy, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 0},    getCountTable(Dummy, NO), DELTA);
        Assert.assertArrayEquals(new double[] {0, 1},    getCountTable(Dummy, MAYBE), DELTA);
    }

    @Test
    public void testCountInitializer() {
        playTennisNet.setCountInitializer(1);
        
        // Starter table
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1, 1}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1, 1}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1, 1}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1, 1}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(Wind, NO), DELTA);
        
        // Add example
        playTennisNet.addExample(selectTarget(0), selectFeatures(0));
        
        // Tables after example
        Assert.assertArrayEquals(new double[] {1, 2},    getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1, 1}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 1, 1}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1, 1}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 1, 1}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 1},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {1, 1},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {2, 1},    getCountTable(Wind, NO), DELTA);
        
        // Add rest of the examples
        for (int i = 1; i < playTennisData.size(); i++) playTennisNet.addExample(selectTarget(i), selectFeatures(i));

        // Numbers should be aligned with Mitchell
        Assert.assertArrayEquals(new double[] {10, 6},   getCountTable(PlayTennis), DELTA);
        Assert.assertArrayEquals(new double[] {3, 5, 4}, getCountTable(Outlook, YES), DELTA);
        Assert.assertArrayEquals(new double[] {4, 1, 3}, getCountTable(Outlook, NO), DELTA);
        Assert.assertArrayEquals(new double[] {3, 5, 4}, getCountTable(Temperature, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 3, 2}, getCountTable(Temperature, NO), DELTA);
        Assert.assertArrayEquals(new double[] {4, 7},    getCountTable(Humidity, YES), DELTA);
        Assert.assertArrayEquals(new double[] {5, 2},    getCountTable(Humidity, NO), DELTA);
        Assert.assertArrayEquals(new double[] {7, 4},    getCountTable(Wind, YES), DELTA);
        Assert.assertArrayEquals(new double[] {3, 4},    getCountTable(Wind, NO), DELTA);
    }        
    
    @Test
    public void testClassificationValue() {
        // Add examples
        for (int i = 0; i < playTennisData.size(); i++) playTennisNet.addExample(selectTarget(i), selectFeatures(i));

        // Try classifying
        Assert.assertEquals(0.0053, playTennisNet.classificationValue("Yes", playTennisInstance), DELTA);
        Assert.assertEquals(0.0206, playTennisNet.classificationValue("No", playTennisInstance), DELTA);
    }
    
    @Test
    public void testProbabilities() {
        // Add examples
        for (int i = 0; i < playTennisData.size(); i++) playTennisNet.addExample(selectTarget(i), selectFeatures(i));

        // The sum of probabilities for all states, given any combination should be 1.0
        double yesProb = playTennisNet.getProbability("Yes", (Object[])null);
        double noProb = playTennisNet.getProbability("No", (Object[])null);
        Assert.assertEquals(1.0000, yesProb + noProb, DELTA);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDynamicNet() {
        ListNaiveBayes dn = new ListNaiveBayes();
        Map fm = new HashMap();
        
        // Add examples
        String[] columnNames = selectColumnNames();
        for (int i = 0; i < playTennisData.size(); i++) {
            String t = selectTarget(i);
            String[] fa = selectFeatures(i);
            for (int j = 0; j < fa.length; j++) fm.put(columnNames[j], fa[j]);
            playTennisNet.addExample(t, fa);
            dn.addExample("PlayTennis", t, fm);
        }

        Info r1 = playTennisNet.getRoot(), r2 = dn.getRoot();
        
        // Same states
        Assert.assertEquals(r1.states.size(), r2.states.size());
        Assert.assertEquals(new HashSet(r1.states), new HashSet(r2.states));

        // Root counts are the same
        Object[] rootStates = r1.states.toArray();
        for (int i = 0; i < rootStates.length; i++) {
            int orv1 = r1.states.indexOf(rootStates[i]);
            int orv2 = r2.states.indexOf(rootStates[i]);
            Assert.assertEquals((int)r1.countTable.get(orv1), (int)r2.countTable.get(orv2));
        }

        // Same number of children
        Assert.assertEquals(playTennisNet.size(), dn.size());
        
        // Each child has the same table
        for (int i = 0; i < playTennisNet.size(); i++) {
            Info i1 = playTennisNet.getInfo(i);
            Info i2 = dn.getInfo(i1.name);
            Assert.assertEquals(i1.name, i2.name);
            
            // Same states
            Assert.assertEquals(i1.states.size(), i2.states.size());
            Assert.assertEquals(new HashSet(i1.states), new HashSet(i2.states));

            // count tables are the same
            Object[] states = i1.states.toArray();
            for (int j = 0; j < rootStates.length; j++) {
                for (int k = 0; k < states.length; k++) {
                    int orv1 = r1.states.indexOf(rootStates[j]);
                    int orv2 = r2.states.indexOf(rootStates[j]);
                    int ov1 = i1.states.indexOf(states[k]);
                    int ov2 = i2.states.indexOf(states[k]);
                    
                    int c1 = (int)i1.countTable.get(orv1, ov1);
                    int c2 = (int)i2.countTable.get(orv2, ov2);
                    Assert.assertEquals(c1, c2);
                }
            }
        }
    }

    /*
    public void testSerialization() {
        // Add all the examples
        for (int i = 0; i < playTennisData.size(); i++) playTennisNet.addExample(selectTarget(i), selectFeatures(i));

        // Serialize/Serialize/Test
        byte[] bytes = SerializationUtils.serialize(playTennisNet);
        NewNaiveBayes deserializedPlayTennis = (NewNaiveBayes)SerializationUtils.deserialize(bytes);
        assertEquals(playTennisNet, deserializedPlayTennis);
        assertNotSame(playTennisNet, deserializedPlayTennis);
    }
    */

    
    //~ HELPER(S) ------------------------------------------------------------

    private String selectTarget(int row) { return playTennisData.get(row)[TARGET]; }
    private String[] selectFeatures(int rn) { return (String[])selectIndexes(playTennisData.get(rn), FEATURES, values); }
    private String[] selectColumnNames() { return selectColumnNames(playTennisNames, FEATURES, new String[FEATURES.length]); }

    public static String[] selectColumnNames(String[] names, int[] indexes, String[] array) {
        for (int i = 0, l = indexes.length; i < l; i++) array[i] = names[indexes[i]];
        return array;
    }

    public static Object[] selectColumnNames(String[] names, int from, int to, String[] array) {
        for (int i = from; i < to; i++) array[i] = names[i];
        return array;
    }

    public static Object[] selectIndexes(String[] row, int[] indexes, Object[] array) {
        for (int i = 0, l = indexes.length; i < l; i++) array[i] = row[indexes[i]];
        return array;
    }

    public static Object[] selectIndexes(String[] row, int from, int to, Object[] array) {
        for (int i = from; i < to; i++) array[i] = row[i];
        return array;
    }

    public static Object[] selectButIndex(String[] row, int index, Object[] array) {
        for (int i = 0, l = array.length; i < l; i++) {
            if (i == index) continue;
            array[i] = row[i < index? i : i + 1];
        }
        return array;
    }

    public static double[] getCountTable(Info info) {
        double probs[] = new double[info.states.size()];
        for (int i = 0; i < probs.length; i++) probs[i] = info.countTable.get(i);
        return probs;
    }

    public static double[] getCountTable(Info info, int rootState) {
        double probs[] = new double[info.states.size()];
        for (int i = 0; i < probs.length; i++) probs[i] = info.countTable.get(rootState, i);
        return probs;
    }
    

    //~ MAIN~ ------------------------------------------------------------------

    public static void main(String args[]) { 
        junit.textui.TestRunner.run(new TestSuite(NaiveBayesTest.class)); 
    }

}

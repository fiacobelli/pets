package com.rc.ai;

import java.util.ArrayList;
import java.util.List;


public class DataGenerator {

    //~ CONSTANT(S) ----------------------------------------------------------

    
    //~ STATIC ATTRIBUTE(S) --------------------------------------------------
    
    // private static final Random rnd = new Random(System.currentTimeMillis() });

    
    //~ METHOD(S) ------------------------------------------------------------
    
    public static List<String[]> playTennis() {
        List<String[]> table = new ArrayList<String[]>();

        // Columns
        table.add(new String[] { "Day", "Outlook", "Temperature", "Humidity", "Wind", "PlayTennis" });

        table.add(new String[] { "D1",  "Sunny",    "Hot",  "High",   "Weak",   "No" });
        table.add(new String[] { "D2",  "Sunny",    "Hot",  "High",   "Strong", "No" });
        table.add(new String[] { "D3",  "Overcast", "Hot",  "High",   "Weak",   "Yes" });
        table.add(new String[] { "D4",  "Rain",     "Mild", "High",   "Weak",   "Yes" });
        table.add(new String[] { "D5",  "Rain",     "Cool", "Normal", "Weak",   "Yes" });
        table.add(new String[] { "D6",  "Rain",     "Cool", "Normal", "Strong", "No" });
        table.add(new String[] { "D7",  "Overcast", "Cool", "Normal", "Strong", "Yes" });
        table.add(new String[] { "D8",  "Sunny",    "Mild", "High",   "Weak",   "No" });
        table.add(new String[] { "D9",  "Sunny",    "Cool", "Normal", "Weak",   "Yes" });
        table.add(new String[] { "D10", "Rain",     "Mild", "Normal", "Weak",   "Yes" });
        table.add(new String[] { "D11", "Sunny",    "Mild", "Normal", "Strong", "Yes" });
        table.add(new String[] { "D12", "Overcast", "Mild", "High",   "Strong", "Yes" });
        table.add(new String[] { "D13", "Overcast", "Hot",  "Normal", "Weak",   "Yes" });
        table.add(new String[] { "D14", "Rain",     "Mild", "High",   "Strong", "No" });
        
        return table;
    }
    
    public static List<String[]> adultSubset() {
        List<String[]> table = new ArrayList<String[]>();

        // Columns
        table.add(new String[] { "age", "workclass", "fnlwgt", "education", "education-num", "marital-status", "occupation", "relationship", "race", "sex", "capital-gain", "capital-loss", "hours-per-week", "native-country", "income" });

        table.add(new String[] { "39", "State-gov", "77516", "Bachelors", "13", "Never-married", "Adm-clerical", "Not-in-family", "White", "Male", "2174", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "50", "Self-emp-not-inc", "83311", "Bachelors", "13", "Married-civ-spouse", "Exec-managerial", "Husband", "White", "Male", "0", "0", "13", "United-States", "<=50K" });
        table.add(new String[] { "38", "Private", "215646", "HS-grad", "9", "Divorced", "Handlers-cleaners", "Not-in-family", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "53", "Private", "234721", "11th", "7", "Married-civ-spouse", "Handlers-cleaners", "Husband", "Black", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "28", "Private", "338409", "Bachelors", "13", "Married-civ-spouse", "Prof-specialty", "Wife", "Black", "Female", "0", "0", "40", "Cuba", "<=50K" });
        table.add(new String[] { "37", "Private", "284582", "Masters", "14", "Married-civ-spouse", "Exec-managerial", "Wife", "White", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "49", "Private", "160187", "9th", "5", "Married-spouse-absent", "Other-service", "Not-in-family", "Black", "Female", "0", "0", "16", "Jamaica", "<=50K" });
        table.add(new String[] { "52", "Self-emp-not-inc", "209642", "HS-grad", "9", "Married-civ-spouse", "Exec-managerial", "Husband", "White", "Male", "0", "0", "45", "United-States", ">50K" });
        table.add(new String[] { "31", "Private", "45781", "Masters", "14", "Never-married", "Prof-specialty", "Not-in-family", "White", "Female", "14084", "0", "50", "United-States", ">50K" });
        table.add(new String[] { "42", "Private", "159449", "Bachelors", "13", "Married-civ-spouse", "Exec-managerial", "Husband", "White", "Male", "5178", "0", "40", "United-States", ">50K" });
        table.add(new String[] { "37", "Private", "280464", "Some-college", "10", "Married-civ-spouse", "Exec-managerial", "Husband", "Black", "Male", "0", "0", "80", "United-States", ">50K" });
        table.add(new String[] { "30", "State-gov", "141297", "Bachelors", "13", "Married-civ-spouse", "Prof-specialty", "Husband", "Asian-Pac-Islander", "Male", "0", "0", "40", "India", ">50K" });
        table.add(new String[] { "23", "Private", "122272", "Bachelors", "13", "Never-married", "Adm-clerical", "Own-child", "White", "Female", "0", "0", "30", "United-States", "<=50K" });
        table.add(new String[] { "32", "Private", "205019", "Assoc-acdm", "12", "Never-married", "Sales", "Not-in-family", "Black", "Male", "0", "0", "50", "United-States", "<=50K" });
        table.add(new String[] { "40", "Private", "121772", "Assoc-voc", "11", "Married-civ-spouse", "Craft-repair", "Husband", "Asian-Pac-Islander", "Male", "0", "0", "40", "?", ">50K" });
        table.add(new String[] { "34", "Private", "245487", "7th-8th", "4", "Married-civ-spouse", "Transport-moving", "Husband", "Amer-Indian-Eskimo", "Male", "0", "0", "45", "Mexico", "<=50K" });
        table.add(new String[] { "25", "Self-emp-not-inc", "176756", "HS-grad", "9", "Never-married", "Farming-fishing", "Own-child", "White", "Male", "0", "0", "35", "United-States", "<=50K" });
        table.add(new String[] { "32", "Private", "186824", "HS-grad", "9", "Never-married", "Machine-op-inspct", "Unmarried", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "38", "Private", "28887", "11th", "7", "Married-civ-spouse", "Sales", "Husband", "White", "Male", "0", "0", "50", "United-States", "<=50K" });
        table.add(new String[] { "43", "Self-emp-not-inc", "292175", "Masters", "14", "Divorced", "Exec-managerial", "Unmarried", "White", "Female", "0", "0", "45", "United-States", ">50K" });
        table.add(new String[] { "40", "Private", "193524", "Doctorate", "16", "Married-civ-spouse", "Prof-specialty", "Husband", "White", "Male", "0", "0", "60", "United-States", ">50K" });
        table.add(new String[] { "54", "Private", "302146", "HS-grad", "9", "Separated", "Other-service", "Unmarried", "Black", "Female", "0", "0", "20", "United-States", "<=50K" });
        table.add(new String[] { "35", "Federal-gov", "76845", "9th", "5", "Married-civ-spouse", "Farming-fishing", "Husband", "Black", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "43", "Private", "117037", "11th", "7", "Married-civ-spouse", "Transport-moving", "Husband", "White", "Male", "0", "2042", "40", "United-States", "<=50K" });
        table.add(new String[] { "59", "Private", "109015", "HS-grad", "9", "Divorced", "Tech-support", "Unmarried", "White", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "56", "Local-gov", "216851", "Bachelors", "13", "Married-civ-spouse", "Tech-support", "Husband", "White", "Male", "0", "0", "40", "United-States", ">50K" });
        table.add(new String[] { "19", "Private", "168294", "HS-grad", "9", "Never-married", "Craft-repair", "Own-child", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "54", "?", "180211", "Some-college", "10", "Married-civ-spouse", "?", "Husband", "Asian-Pac-Islander", "Male", "0", "0", "60", "South", ">50K" });
        table.add(new String[] { "39", "Private", "367260", "HS-grad", "9", "Divorced", "Exec-managerial", "Not-in-family", "White", "Male", "0", "0", "80", "United-States", "<=50K" });
        table.add(new String[] { "49", "Private", "193366", "HS-grad", "9", "Married-civ-spouse", "Craft-repair", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "23", "Local-gov", "190709", "Assoc-acdm", "12", "Never-married", "Protective-serv", "Not-in-family", "White", "Male", "0", "0", "52", "United-States", "<=50K" });
        table.add(new String[] { "20", "Private", "266015", "Some-college", "10", "Never-married", "Sales", "Own-child", "Black", "Male", "0", "0", "44", "United-States", "<=50K" });
        table.add(new String[] { "45", "Private", "386940", "Bachelors", "13", "Divorced", "Exec-managerial", "Own-child", "White", "Male", "0", "1408", "40", "United-States", "<=50K" });
        table.add(new String[] { "30", "Federal-gov", "59951", "Some-college", "10", "Married-civ-spouse", "Adm-clerical", "Own-child", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "22", "State-gov", "311512", "Some-college", "10", "Married-civ-spouse", "Other-service", "Husband", "Black", "Male", "0", "0", "15", "United-States", "<=50K" });
        table.add(new String[] { "48", "Private", "242406", "11th", "7", "Never-married", "Machine-op-inspct", "Unmarried", "White", "Male", "0", "0", "40", "Puerto-Rico", "<=50K" });
        table.add(new String[] { "21", "Private", "197200", "Some-college", "10", "Never-married", "Machine-op-inspct", "Own-child", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "19", "Private", "544091", "HS-grad", "9", "Married-AF-spouse", "Adm-clerical", "Wife", "White", "Female", "0", "0", "25", "United-States", "<=50K" });
        table.add(new String[] { "31", "Private", "84154", "Some-college", "10", "Married-civ-spouse", "Sales", "Husband", "White", "Male", "0", "0", "38", "?", ">50K" });
        table.add(new String[] { "48", "Self-emp-not-inc", "265477", "Assoc-acdm", "12", "Married-civ-spouse", "Prof-specialty", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "31", "Private", "507875", "9th", "5", "Married-civ-spouse", "Machine-op-inspct", "Husband", "White", "Male", "0", "0", "43", "United-States", "<=50K" });
        table.add(new String[] { "53", "Self-emp-not-inc", "88506", "Bachelors", "13", "Married-civ-spouse", "Prof-specialty", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "24", "Private", "172987", "Bachelors", "13", "Married-civ-spouse", "Tech-support", "Husband", "White", "Male", "0", "0", "50", "United-States", "<=50K" });
        table.add(new String[] { "49", "Private", "94638", "HS-grad", "9", "Separated", "Adm-clerical", "Unmarried", "White", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "25", "Private", "289980", "HS-grad", "9", "Never-married", "Handlers-cleaners", "Not-in-family", "White", "Male", "0", "0", "35", "United-States", "<=50K" });
        table.add(new String[] { "57", "Federal-gov", "337895", "Bachelors", "13", "Married-civ-spouse", "Prof-specialty", "Husband", "Black", "Male", "0", "0", "40", "United-States", ">50K" });
        table.add(new String[] { "53", "Private", "144361", "HS-grad", "9", "Married-civ-spouse", "Machine-op-inspct", "Husband", "White", "Male", "0", "0", "38", "United-States", "<=50K" });
        table.add(new String[] { "44", "Private", "128354", "Masters", "14", "Divorced", "Exec-managerial", "Unmarried", "White", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "41", "State-gov", "101603", "Assoc-voc", "11", "Married-civ-spouse", "Craft-repair", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "29", "Private", "271466", "Assoc-voc", "11", "Never-married", "Prof-specialty", "Not-in-family", "White", "Male", "0", "0", "43", "United-States", "<=50K" });
        table.add(new String[] { "25", "Private", "32275", "Some-college", "10", "Married-civ-spouse", "Exec-managerial", "Wife", "Other", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "18", "Private", "226956", "HS-grad", "9", "Never-married", "Other-service", "Own-child", "White", "Female", "0", "0", "30", "?", "<=50K" });
        table.add(new String[] { "47", "Private", "51835", "Prof-school", "15", "Married-civ-spouse", "Prof-specialty", "Wife", "White", "Female", "0", "1902", "60", "Honduras", ">50K" });
        table.add(new String[] { "50", "Federal-gov", "251585", "Bachelors", "13", "Divorced", "Exec-managerial", "Not-in-family", "White", "Male", "0", "0", "55", "United-States", ">50K" });
        table.add(new String[] { "47", "Self-emp-inc", "109832", "HS-grad", "9", "Divorced", "Exec-managerial", "Not-in-family", "White", "Male", "0", "0", "60", "United-States", "<=50K" });
        table.add(new String[] { "43", "Private", "237993", "Some-college", "10", "Married-civ-spouse", "Tech-support", "Husband", "White", "Male", "0", "0", "40", "United-States", ">50K" });
        table.add(new String[] { "46", "Private", "216666", "5th-6th", "3", "Married-civ-spouse", "Machine-op-inspct", "Husband", "White", "Male", "0", "0", "40", "Mexico", "<=50K" });
        table.add(new String[] { "35", "Private", "56352", "Assoc-voc", "11", "Married-civ-spouse", "Other-service", "Husband", "White", "Male", "0", "0", "40", "Puerto-Rico", "<=50K" });
        table.add(new String[] { "41", "Private", "147372", "HS-grad", "9", "Married-civ-spouse", "Adm-clerical", "Husband", "White", "Male", "0", "0", "48", "United-States", "<=50K" });
        table.add(new String[] { "30", "Private", "188146", "HS-grad", "9", "Married-civ-spouse", "Machine-op-inspct", "Husband", "White", "Male", "5013", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "30", "Private", "59496", "Bachelors", "13", "Married-civ-spouse", "Sales", "Husband", "White", "Male", "2407", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "32", "?", "293936", "7th-8th", "4", "Married-spouse-absent", "?", "Not-in-family", "White", "Male", "0", "0", "40", "?", "<=50K" });
        table.add(new String[] { "48", "Private", "149640", "HS-grad", "9", "Married-civ-spouse", "Transport-moving", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "42", "Private", "116632", "Doctorate", "16", "Married-civ-spouse", "Prof-specialty", "Husband", "White", "Male", "0", "0", "45", "United-States", ">50K" });
        table.add(new String[] { "29", "Private", "105598", "Some-college", "10", "Divorced", "Tech-support", "Not-in-family", "White", "Male", "0", "0", "58", "United-States", "<=50K" });
        table.add(new String[] { "36", "Private", "155537", "HS-grad", "9", "Married-civ-spouse", "Craft-repair", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "28", "Private", "183175", "Some-college", "10", "Divorced", "Adm-clerical", "Not-in-family", "White", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "53", "Private", "169846", "HS-grad", "9", "Married-civ-spouse", "Adm-clerical", "Wife", "White", "Female", "0", "0", "40", "United-States", ">50K" });
        table.add(new String[] { "49", "Self-emp-inc", "191681", "Some-college", "10", "Married-civ-spouse", "Exec-managerial", "Husband", "White", "Male", "0", "0", "50", "United-States", ">50K" });
        table.add(new String[] { "25", "?", "200681", "Some-college", "10", "Never-married", "?", "Own-child", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "19", "Private", "101509", "Some-college", "10", "Never-married", "Prof-specialty", "Own-child", "White", "Male", "0", "0", "32", "United-States", "<=50K" });
        table.add(new String[] { "31", "Private", "309974", "Bachelors", "13", "Separated", "Sales", "Own-child", "Black", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "29", "Self-emp-not-inc", "162298", "Bachelors", "13", "Married-civ-spouse", "Sales", "Husband", "White", "Male", "0", "0", "70", "United-States", ">50K" });
        table.add(new String[] { "23", "Private", "211678", "Some-college", "10", "Never-married", "Machine-op-inspct", "Not-in-family", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "79", "Private", "124744", "Some-college", "10", "Married-civ-spouse", "Prof-specialty", "Other-relative", "White", "Male", "0", "0", "20", "United-States", "<=50K" });
        table.add(new String[] { "27", "Private", "213921", "HS-grad", "9", "Never-married", "Other-service", "Own-child", "White", "Male", "0", "0", "40", "Mexico", "<=50K" });
        table.add(new String[] { "40", "Private", "32214", "Assoc-acdm", "12", "Married-civ-spouse", "Adm-clerical", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "67", "?", "212759", "10th", "6", "Married-civ-spouse", "?", "Husband", "White", "Male", "0", "0", "2", "United-States", "<=50K" });
        table.add(new String[] { "18", "Private", "309634", "11th", "7", "Never-married", "Other-service", "Own-child", "White", "Female", "0", "0", "22", "United-States", "<=50K" });
        table.add(new String[] { "31", "Local-gov", "125927", "7th-8th", "4", "Married-civ-spouse", "Farming-fishing", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "18", "Private", "446839", "HS-grad", "9", "Never-married", "Sales", "Not-in-family", "White", "Male", "0", "0", "30", "United-States", "<=50K" });
        table.add(new String[] { "52", "Private", "276515", "Bachelors", "13", "Married-civ-spouse", "Other-service", "Husband", "White", "Male", "0", "0", "40", "Cuba", "<=50K" });
        table.add(new String[] { "46", "Private", "51618", "HS-grad", "9", "Married-civ-spouse", "Other-service", "Wife", "White", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "59", "Private", "159937", "HS-grad", "9", "Married-civ-spouse", "Sales", "Husband", "White", "Male", "0", "0", "48", "United-States", "<=50K" });
        table.add(new String[] { "44", "Private", "343591", "HS-grad", "9", "Divorced", "Craft-repair", "Not-in-family", "White", "Female", "14344", "0", "40", "United-States", ">50K" });
        table.add(new String[] { "53", "Private", "346253", "HS-grad", "9", "Divorced", "Sales", "Own-child", "White", "Female", "0", "0", "35", "United-States", "<=50K" });
        table.add(new String[] { "49", "Local-gov", "268234", "HS-grad", "9", "Married-civ-spouse", "Protective-serv", "Husband", "White", "Male", "0", "0", "40", "United-States", ">50K" });
        table.add(new String[] { "33", "Private", "202051", "Masters", "14", "Married-civ-spouse", "Prof-specialty", "Husband", "White", "Male", "0", "0", "50", "United-States", "<=50K" });
        table.add(new String[] { "30", "Private", "54334", "9th", "5", "Never-married", "Sales", "Not-in-family", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "43", "Federal-gov", "410867", "Doctorate", "16", "Never-married", "Prof-specialty", "Not-in-family", "White", "Female", "0", "0", "50", "United-States", ">50K" });
        table.add(new String[] { "57", "Private", "249977", "Assoc-voc", "11", "Married-civ-spouse", "Prof-specialty", "Husband", "White", "Male", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "37", "Private", "286730", "Some-college", "10", "Divorced", "Craft-repair", "Unmarried", "White", "Female", "0", "0", "40", "United-States", "<=50K" });
        table.add(new String[] { "28", "Private", "212563", "Some-college", "10", "Divorced", "Machine-op-inspct", "Unmarried", "Black", "Female", "0", "0", "25", "United-States", "<=50K" });
        table.add(new String[] { "30", "Private", "117747", "HS-grad", "9", "Married-civ-spouse", "Sales", "Wife", "Asian-Pac-Islander", "Female", "0", "1573", "35", "?", "<=50K" });
        table.add(new String[] { "34", "Local-gov", "226296", "Bachelors", "13", "Married-civ-spouse", "Protective-serv", "Husband", "White", "Male", "0", "0", "40", "United-States", ">50K" });
        table.add(new String[] { "29", "Local-gov", "115585", "Some-college", "10", "Never-married", "Handlers-cleaners", "Not-in-family", "White", "Male", "0", "0", "50", "United-States", "<=50K" });
        table.add(new String[] { "48", "Self-emp-not-inc", "191277", "Doctorate", "16", "Married-civ-spouse", "Prof-specialty", "Husband", "White", "Male", "0", "1902", "60", "United-States", ">50K" });
        table.add(new String[] { "37", "Private", "202683", "Some-college", "10", "Married-civ-spouse", "Sales", "Husband", "White", "Male", "0", "0", "48", "United-States", ">50K" });
        table.add(new String[] { "48", "Private", "171095", "Assoc-acdm", "12", "Divorced", "Exec-managerial", "Unmarried", "White", "Female", "0", "0", "40", "England", "<=50K" });
        table.add(new String[] { "32", "Federal-gov", "249409", "HS-grad", "9", "Never-married", "Other-service", "Own-child", "Black", "Male", "0", "0", "40", "United-States", "<=50K" });
        
        return table;
    }
}

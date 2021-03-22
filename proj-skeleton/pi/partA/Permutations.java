import java.util.*;

class Permutations{

    private HashMap<String, ArrayList<String>> graphMap;
    private HashMap<String, Integer> usesMap;
    private final int T_SUPPORT;
    private final double T_CONFIDENCE;

    public Permutations(HashMap<String, ArrayList<String>> graph, HashMap<String, Integer> uses, int support, double confidence) {
        graphMap = graph;
        usesMap = uses;
        T_SUPPORT = support;
        T_CONFIDENCE = confidence;
    }

    /**
     * Method to calculate the support of a pair of functions
     * @param func1
     * @param func2
     * @return int support
     */
    public int calculateSupport(String func1, String func2) {
        int count = 0;
        Iterator iterator = graphMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry scope = (Map.Entry)iterator.next();
            ArrayList<String> calls = (ArrayList<String>) scope.getValue();
            if (calls.contains(func1) && calls.contains(func2))
                count++;
        }
        return count;
    }

    /**
     * Method to calculate support of a single function
     * @param func
     * @return int support
     */
    public int countOccurences(String func) {
        return usesMap.get(func) - 1;
    }

    /**
     * Method to calculate the confidence of 2 sets of functions
     * Allows for up to 2 functions per set
     * TODO: return error message if set size > 2
     * @param set1
     * @param set2
     * @return double confidence
     */
    public double calculateConfidence(ArrayList<String> set1, ArrayList<String> set2) {
        //set 1 support
        int set1Support = 0;
        if (set1.size() == 1) set1Support = countOccurences(set1.get(0));
        else if (set1.size() == 2) set1Support = calculateSupport(set1.get(0), set1.get(1));

        //set 2 support
        int set2Support = 0;
        if (set2.size() == 1) set2Support = countOccurences(set2.get(0));
        else if (set2.size() == 2) set2Support = calculateSupport(set2.get(0), set2.get(1));

        if(set2Support == 0){
            return 0.0;
        }
        double confidence = (double)set1Support / (double)set2Support;

        return confidence;
    }

    /**
     * Methods to locate potential bugs in the program,
     * by calculating the support and confidence for all pairs
     * of functions and comparing them to the threshold values
     *
     * POTENTIAL TODO: compare with both functions' individual supports
     */
    public void permute() {
        //build arraylist of all functions for easier iteration
        ArrayList<String> functions = new ArrayList();
        for (String funcName : usesMap.keySet()) {
            functions.add(funcName);
        }

        //find all permutations of function pairs
        for (int i = 0; i < functions.size(); i++) {
            for (int j = i + 1; j < functions.size(); j++) {
                if (i == j) continue;
                //create sets, one pair and one individual
                ArrayList<String> set1 = new ArrayList<>(List.of(functions.get(i), functions.get(j)));
                ArrayList<String> set2 = new ArrayList<>(List.of(functions.get(i)));
                //calculate confidence
                identifyBugs(set1, set2);
                set2 = new ArrayList<>(List.of(functions.get(j)));
                identifyBugs(set1, set2);
            }
        }
    }

    public void identifyBugs(ArrayList<String> set1, ArrayList<String> set2) {
        double confidence = calculateConfidence(set1, set2);
        if (confidence == 0.0) continue;
        if (confidence >= T_CONFIDENCE) {
            //potential bug, calculate support
            int support = calculateSupport(set1.get(0), set1.get(1));
            if (support >= T_SUPPORT) {
                //bug found, send data to findBugs()
                System.out.println("findBugs inputs: " + set1.toString() + " " + support + " " + confidence);
                findBugs(set1, support, confidence);
            }
        }
    }

    /**
     * Method to locate and print all of the bugs found by permute()
     * Bug is defined as when an individual function is called where
     * it is typically called with a pair
     * @param set1
     * @param support
     * @param confidence
     */
    public void findBugs(ArrayList<String> set1, int support, double confidence) {
        //find out where one function in the set is being called without the other
        Iterator iterator = graphMap.entrySet().iterator();
        while (iterator.hasNext()) {
            //find which scope contains the bug
            Map.Entry scope = (Map.Entry)iterator.next();
            ArrayList<String> calls = (ArrayList<String>) scope.getValue();
            //checking first function in pair
            if (calls.contains(set1.get(0)) && !calls.contains(set1.get(1))) {
                System.out.println("bug: " + set1.get(0) + " in " + scope.getKey()
                        + ", pair: " + set1.toString() + ", support: " + support
                        + ", confidence: " + confidence*100 + "%\n");
            }
            //checking second function in pair
//            else if (!calls.contains(set1.get(0)) && calls.contains(set1.get(1))) {
//                System.out.println("bug: " + set1.get(1) + " in " + scope.getKey()
//                        + ", pair: " + set1.toString() + ", support: " + support
//                        + ", confidence: " + confidence*100 + "%\n");
//            }
        }
    }


}
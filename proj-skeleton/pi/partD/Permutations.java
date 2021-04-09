import java.util.*;
import java.text.DecimalFormat;

class Permutations {

    private HashMap<String, ArrayList<String>> graphMap;
    private HashMap<String, Integer> usesMap;
    private static HashMap<ArrayList<String>, Integer> permutationsMap;
    private final int T_SUPPORT;
    private final double T_CONFIDENCE;
    private static DecimalFormat df2 = new DecimalFormat("##.00");
    private static int t_distance;
    private static ArrayList<String> functionsToIgnore;

    public Permutations(HashMap<String, ArrayList<String>> graph, HashMap<String, Integer> uses, int support, double confidence, int distance, ArrayList<String> func) {
        graphMap = graph;
        usesMap = uses;
        T_SUPPORT = support;
        T_CONFIDENCE = confidence / 100.0;
        permutationsMap = new HashMap<>();
        t_distance = distance;
        functionsToIgnore = func;
    }

    /**
     * Method to calculate the support of a pair of functions
     *
     * @param func1
     * @param func2
     * @return int support
     */
    public int calculateSupport(String func1, String func2) {
        int count = 0;
        Iterator iterator = graphMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry scope = (Map.Entry) iterator.next();
            ArrayList<String> calls = (ArrayList<String>) scope.getValue();
            if (calls.contains(func1) && calls.contains(func2))
                count++;
        }
        return count;
    }

    /**
     * Method to calculate support of a single function
     *
     * @param func
     * @return int support
     */
    public int countOccurences(String func) {
        return usesMap.get(func);
    }

    /**
     * Method to calculate the confidence of 2 sets of functions
     * Allows for up to 2 functions per set
     * TODO: return error message if set size > 2
     *
     * @param set1
     * @param set2
     * @return double confidence
     */
    public double calculateConfidence(ArrayList<String> set1, ArrayList<String> set2, int set1Support) {
        int set2Support = 0;
        set2Support = countOccurences(set2.get(0));

        if (set2Support == 0) {
            return 0.0;
        }
        double confidence = (double) set1Support / (double) set2Support;
        //uncomment below for testing
        //System.out.println("------" + set1.toString() + "support: " + set1Support + ", conf: " + confidence + ", A uses: " + countOccurences(set1.get(0)) + ", B uses: " + countOccurences(set1.get(1)));
        return confidence;
    }

    //create an arraylist pair of functions that are ordered alphabetically
    public ArrayList<String> makePair(String functionA, String functionB) {
        int compareVal = functionA.compareToIgnoreCase(functionB);
        ArrayList<String> pair = new ArrayList();
        if (compareVal < 0) {
            pair.add(functionA);
            pair.add(functionB);
        } else if (compareVal > 0) {
            pair.add(functionB);
            pair.add(functionA);
        }
        return pair;
    }

    public ArrayList<String> removeDuplicates(ArrayList<String> list) {
        if (list.size() < 2) {
            return list;
        }
        ArrayList<String> listUnique = new ArrayList<>(new HashSet<>(list));
        return listUnique;
    }

    //for testing only
    public static void printPermutationsMap() {
        System.out.println("printing permutationsMap HashMap");
        for (ArrayList<String> pair : permutationsMap.keySet()) {
            String val = permutationsMap.get(pair).toString();
            System.out.println(pair.toString() + " " + val);
        }
        System.out.println("\n");
    }

    /**
     * Methods to locate potential bugs in the program,
     * by calculating the support and confidence for all pairs
     * of functions and comparing them to the threshold values
     * <p>
     * POTENTIAL TODO: compare with both functions' individual supports
     */
    public void permute() {
        for (String name : graphMap.keySet()) {
            String key = name.toString();
            ArrayList<String> val = removeDuplicates(graphMap.get(name));

            if (val.size() < 2) {
                continue;
            }

            for (int i = 0; i < val.size(); i++) {
                for (int j = i + 1; j < val.size(); j++) {
                    if(Math.abs(i - j) > t_distance){
                        continue;
                    }
                    String functionA = val.get(i);
                    String functionB = val.get(j);
                    ArrayList<String> pair = makePair(functionA, functionB);
                    if (permutationsMap.containsKey(pair)) {
                        permutationsMap.put(pair, permutationsMap.get(pair) + 1);
                    } else {
                        permutationsMap.put(pair, 1);
                    }
                }
            }
        }

        for (ArrayList<String> pair : permutationsMap.keySet()) {
            int set1Support = permutationsMap.get(pair);
            if (set1Support < T_SUPPORT) {
                continue;
            }
            ArrayList<String> set1 = pair;
            ArrayList<String> set2 = new ArrayList<>(List.of(pair.get(0)));
            identifyBugs(set1, set2, set1Support);
            ArrayList<String> set2Switch = new ArrayList<>(List.of(pair.get(1)));
            identifyBugs(set1, set2Switch, set1Support);
        }
    }

    public void identifyBugs(ArrayList<String> set1, ArrayList<String> set2, int set1Support) {
        double confidence = calculateConfidence(set1, set2, set1Support);
        if (confidence == 0.0) return;
        if (confidence >= T_CONFIDENCE) {
            //avoid locating errors with functions user has requested to ignore
            for (String func : functionsToIgnore) {
                if (set1.contains(func) || set2.contains(func)) return;
            }
            //potential bug, calculate support
            findBugs(set1, set2, set1Support, confidence);
        }
    }

    /**
     * Method to locate and print all of the bugs found by permute()
     * Bug is defined as when an individual function is called where
     * it is typically called with a pair
     *
     * @param set1
     * @param support
     * @param confidence
     */
    public void findBugs(ArrayList<String> set1, ArrayList<String> set2, int support, double confidence) {
        //find out where one function in the set is being called without the other
        Iterator iterator = graphMap.entrySet().iterator();
        while (iterator.hasNext()) {
            //find which scope contains the bug
            Map.Entry scope = (Map.Entry) iterator.next();
            ArrayList<String> calls = (ArrayList<String>) scope.getValue();

            //checking first function in pair
            if (set2.get(0) == set1.get(0)) {
                if (calls.contains(set1.get(0)) && !calls.contains(set1.get(1))) {
                    System.out.println("bug: " + set1.get(0) + " in " + scope.getKey()
                            + ", pair: (" + set1.get(0) + ", " + set1.get(1) + "), support: " + support
                            + ", confidence: " + df2.format(confidence * 100) + "%");
                }
            } else {
                if (!calls.contains(set1.get(0)) && calls.contains(set1.get(1))) {
                    System.out.println("bug: " + set1.get(1) + " in " + scope.getKey()
                            + ", pair: (" + set1.get(0) + ", " + set1.get(1) + "), support: " + support
                            + ", confidence: " + df2.format(confidence * 100) + "%");
                }
            }
        }
    }
}

import java.util.*;

class Permutations{

    Hashmap<String, ArrayList<String>> graphMap = new HashMap();
    Hashmap<String, int> usesMap = new HashMap();

    public static void main(String [] args){
        int T_SUPPORT = 3;
        double T_CONFIDENCE = 0.65;
        //with hashmap from Pi.java, calculate support and confidence for all permutations
        //return those whose values exceed the thresholds


    }

    public int calculateSupport(String func1, String func2) {
        int count = 0;
        Iterator iterator = graphMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry scope = (Map.Entry)iterator.next();
            ArrayList<String> calls = scope.getValue();
            if (calls.contains(func1) && calls.contains(func2))
                count++;
        }
        return count;
    }

    public int countOccurences(String func) {
        return usesMap.getValue(func);
    }

    public double calculateConfidence(ArrayList<String> set1, ArrayList<String> set2) {
        //set 1 support
        int set1Support = 0;
        if (set1.size() == 1) set1Support = countOccurences(set1.get(0));
        else if (set1.size() == 2) set1Support = calculateSupport(set1.get(0), set1.get(1));

        //set 2 support
        int set2Support = 0;
        if (set2.size() == 1) set2Support = countOccurences(set2.get(0));
        else if (set2.size() == 2) set2Support = calculateSupport(set2.get(0), set2.get(1));

        double confidence = set1Support / set2Support;

        return confidence;
    }

    public void findBugs() {
        Set<String> func = usesMap.keySet();
        ArrayList<String> functions = new ArrayList();
        Iterator<String> iterator = func.iterator();
        while (iterator.hasNext) {
            functions.add(iterator.next());
        }

        for (int i = 0; i <= functions.size(); i++) {
            for (int j = 0; j <= functions.size(); j++) {
                if (i == j) continue;
                ArrayList<String> set1 = {}
            }
        }
    }

}

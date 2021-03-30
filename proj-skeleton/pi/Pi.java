import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;


/*generate call graph and fill graphMap and usesMap
  graphMap: hashmap with scope1, scope2, etc. as keys and array of functions as values
  usesMap: hashmap with scope1, scope2, etc. as keys and int of # of uses as values.
  Access each map with getGraphMap() and getUsesMap() */
class Pi {
    public static HashMap<String, ArrayList<String>> graphMap = new HashMap<>();
    public static HashMap<String, Integer> usesMap = new HashMap<>();
    public static int t_support = 3;
    public static double t_confidence = 65;

    public static void main(String[] args) {

        if (args.length == 4) {
            t_support = Integer.valueOf(args[2]);
            t_confidence = Double.valueOf(args[3]);
        } else if (args.length != 2) {
            printUsageMessage();
            return;
        }

        ArrayList<String> nullFunctionList = new ArrayList();

        try {
            File graph = new File(args[0]);
            Scanner reader = new Scanner(graph);
            boolean readToScope = false;
            boolean readNullFunction = false;

            String tmpScopeName = "";
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                //uncomment below to see call graph
                //System.out.println(line);

                if (line.contains("<<null function>>")) {
                    readNullFunction = true;
                } else if (readNullFunction && !line.isEmpty()) {
                    String funcName = getScopeName(line);
                    nullFunctionList.add(funcName);
                } else {
                    readNullFunction = false;
                }

                if (isScopeHeader(line)) {
                    readToScope = true;
                    String scopeName = getScopeName(line);
                    ArrayList<String> valuesList = new ArrayList<>();
                    graphMap.put(scopeName, valuesList);
                    tmpScopeName = scopeName;

                    int uses = getFuncUses(line);
                    if (usesMap.containsKey(scopeName)) {
                        usesMap.put(scopeName, usesMap.get(scopeName) + (uses - 1));
                    } else {
                        usesMap.put(scopeName, uses - 1);
                    }

                } else if (readToScope && !line.isEmpty()) {
                    if (!line.contains("external node")) {
                        String funcName = getScopeName(line);
                        //adjust uses to not count duplicates
                        if (graphMap.get(tmpScopeName).contains(funcName)) {
                            if (usesMap.containsKey(funcName)) {
                                usesMap.put(funcName, usesMap.get(funcName) - 1);
                            } else {
                                usesMap.put(funcName, -1);
                            }

                        }
                        graphMap.get(tmpScopeName).add(funcName);
                    }
                } else {
                    readToScope = false;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


        for (String name : usesMap.keySet()) {
            String key = name.toString();
            if (!nullFunctionList.contains(key)) {
                usesMap.put(key, usesMap.get(key) + 1);
            }
        }
        //uncomment below to see each hashMap
        //printUsesMap();
        //printGraphMap();

        Permutations P = new Permutations(graphMap, usesMap, t_support, t_confidence);
        P.permute();
    }

    public static HashMap<String, Integer> getUsesMap() {
        return usesMap;
    }

    public static HashMap<String, ArrayList<String>> getGraphMap() {
        return graphMap;
    }

    public static int getTSupport() {
        return t_support;
    }

    //helper method for main()
    private static boolean isScopeHeader(String line) {
        return (line.length() > 1) && (line.substring(0, 4).equals("Call")) && (!line.contains("null function"));
    }

    private static void printUsageMessage() {
        System.out.println("Run pipair.sh with args <example.bc>, or <example.bc> <T_support> <T_confidence>");
        System.out.println("Default: T_support=3, T_confidece=65");
    }

    //for testing only
    public static void printUsesMap() {
        System.out.println("printing Uses HashMap");
        for (String name : usesMap.keySet()) {
            String key = name.toString();
            String val = usesMap.get(name).toString();
            System.out.println(key + " " + val);
        }
        System.out.println("\n");
    }

    //for testing only
    public static void printGraphMap() {
        System.out.println("printing Graph HashMap");
        for (String name : graphMap.keySet()) {
            String key = name.toString();
            String val = graphMap.get(name).toString();
            System.out.println(key + " " + val);
        }
        System.out.println("\n");
    }

    //extract name from line
    private static String getScopeName(String line) {
        String name = null;
        if (line.contains("\'")) {
            int startIndex = line.indexOf("\'");
            int endIndex = line.indexOf("\'", startIndex + 1);
            name = line.substring(startIndex + 1, endIndex);
        }
        return name;
    }

    //extract usage number from line
    private static int getFuncUses(String line) {
        int startIndex = line.indexOf("#uses=");
        String usesStr = line.substring(startIndex + 6, line.length());
        int ret = Integer.valueOf(usesStr);
        return ret;
    }
}
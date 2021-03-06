import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;


/**
 * generate call graph and fill graphMap and usesMap
 * graphMap: hashmap with scope1, scope2, etc. as keys and array of functions as values
 * usesMap: hashmap with scope1, scope2, etc. as keys and int of # of uses as values.
 * Access each map with getGraphMap() and getUsesMap()
 */

class Pi {
    private static HashMap<String, ArrayList<String>> graphMap = new HashMap<>();
    private static HashMap<String, Integer> usesMap = new HashMap<>();
    private static HashMap<String, ArrayList<String>> graphMapExpanded = new HashMap<>();
    private static int t_support = 3;
    private static double t_confidence = 65;
    private static boolean expand = false;

    /**
     * populate graphMap and useMap
     *
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 5) {
            t_support = Integer.valueOf(args[2]);
            t_confidence = Double.valueOf(args[3]);
            if (args[4].contains("expand")) {
                expand = true;
            }
        } else if (args.length == 4) {
            t_support = Integer.valueOf(args[2]);
            t_confidence = Double.valueOf(args[3]);
        } else if (args.length == 3) {
            if (args[2].contains("expand")) {
                expand = true;
            }
        } else if (args.length != 2) {
            printUsageMessage();
            return;
        }

        try {
            File graph = new File(args[0]);
            Scanner reader = new Scanner(graph);
            boolean readToScope = false;

            String tmpScopeName = "";
            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                //uncomment below to see call graph
                //System.out.println(line);

                //add scope header as a key in usesMap
                if (isScopeHeader(line)) {
                    readToScope = true;
                    String scopeName = getScopeName(line);
                    ArrayList<String> valuesList = new ArrayList<>();
                    graphMap.put(scopeName, valuesList);
                    tmpScopeName = scopeName;

                    //add function to corresponding value list in usesMap
                } else if (readToScope && !line.isEmpty()) {
                    if (!line.contains("external node")) {
                        String funcName = getScopeName(line);
                        //add funcName if it is not a duplicate
                        if(!graphMap.get(tmpScopeName).contains(funcName)) {
                            graphMap.get(tmpScopeName).add(funcName);
                        }
                    }
                } else {
                    readToScope = false;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        //uncomment below to see each hashMap
        //printUsesMap();
        //printGraphMap();
        //printGraphMapExpanded();


        if (expand) {
            fillExpandedMap();
            fillUsesMap(graphMapExpanded);
            Permutations P = new Permutations(graphMapExpanded, usesMap, t_support, t_confidence);
            P.permute();
        }
        else{
            fillUsesMap(graphMap);
            Permutations P = new Permutations(graphMap, usesMap, t_support, t_confidence);
            P.permute();
        }

    }

    /**
     * expanding and replacing all functions inside each scope in graphMapExpanded
     */
    public static void fillExpandedMap() {
        for (String name : graphMap.keySet()) {
            ArrayList<String> list = graphMap.get(name);
            ArrayList<String> listTmp = (ArrayList<String>)graphMap.get(name).clone();

            for(String func : list) {
                if (func.equals(name)) continue;
                ArrayList<String> toAdd = graphMap.get(func);
                if (toAdd.size() == 0) continue;

                listTmp.remove(func);
                listTmp.addAll(toAdd);
            }
            ArrayList<String> listUnique = new ArrayList<>(new HashSet<>(listTmp));
            graphMapExpanded.put(name, listUnique);
        }
    }

    /**
     * find the number of uses for each function in graphMap
     *
     * @param graph
     */
    public static void fillUsesMap(HashMap<String, ArrayList<String>> graph) {
        for (String name : graphMap.keySet()) {
            ArrayList<String> list = graphMap.get(name);

            for(String func : list) {
                if(usesMap.containsKey(func)){
                    usesMap.put(func, usesMap.get(func) + 1);
                }
                else{
                    usesMap.put(func, 1);
                }
            }
        }
    }

    /**
     * helper method for main()
     * checks if line is a scope header
     *
     * @param line
     * @return
     */
    private static boolean isScopeHeader(String line) {
        return (line.length() > 1) && (line.substring(0, 4).equals("Call")) && (!line.contains("null function"));
    }

    /**
     * printing usage message
     */
    private static void printUsageMessage() {
        System.out.println("Run pipair.sh with args <example.bc>, or <example.bc> <T_support> <T_confidence>");
        System.out.println("Default: T_support=3, T_confidence=65");
    }

    /**
     * print uses map
     * for testing purposes
     */
    public static void printUsesMap() {
        System.out.println("printing Uses HashMap");
        for (String name : usesMap.keySet()) {
            String key = name.toString();
            String val = usesMap.get(name).toString();
            System.out.println(key + " " + val);
        }
        System.out.println("\n");
    }

    /**
     * print graph map
     * for testing purposes
     */
    public static void printGraphMap() {
        System.out.println("printing graph HashMap");
        for (String name : graphMap.keySet()) {
            String key = name.toString();
            String val = graphMap.get(name).toString();
            System.out.println(key + " " + val);
        }
        System.out.println("\n");
    }

    /**
     * print extended graph map
     * for testing purposes
     */
    public static void printGraphMapExpanded() {
        System.out.println("printing extended graph HashMap");
        for (String name : graphMapExpanded.keySet()) {
            String key = name.toString();
            String val = graphMapExpanded.get(name).toString();
            System.out.println(key + " " + val);
        }
        System.out.println("\n");
    }

    /**
     * get scope name from line of call graph
     *
     * @param line
     * @return
     */
    private static String getScopeName(String line) {
        String name = null;
        if (line.contains("\'")) {
            int startIndex = line.indexOf("\'");
            int endIndex = line.indexOf("\'", startIndex + 1);
            name = line.substring(startIndex + 1, endIndex);
        }
        return name;
    }
}
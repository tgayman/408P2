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

    public static void main(String[] args) {
        try {
            File graph = new File(args[0]);
            Scanner reader = new Scanner(graph);
            boolean readToScope = false;
            String tmpScopeName = "";
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                //uncomment below to see call graph
                //System.out.println(line);

                if (isScopeHeader(line)) {
                    readToScope = true;
                    String scopeName = getScopeName(line);
                    ArrayList<String> valuesList = new ArrayList<>();
                    graphMap.put(scopeName, valuesList);
                    tmpScopeName = scopeName;

                    int uses = getFuncUses(line);
                    usesMap.put(scopeName, uses);

                } else if (readToScope && !line.isEmpty()) {
                    if (!line.contains("external node")) {
                        String funcName = getScopeName(line);
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
        //uncomment below to see each hashMap
        //printUsesMap();
        //printGraphMap();

    }

    public static HashMap<String, Integer> getUsesMap(){
        return usesMap;
    }

    public static HashMap<String, ArrayList<String>> getGraphMap(){
        return graphMap;
    }

    //helper method for main()
    private static boolean isScopeHeader(String line) {
        return (line.length() > 1) && (line.substring(0, 4).equals("Call")) && (!line.contains("null function"));
    }

    //for testing only
    public static void printUsesMap() {
        System.out.println("printing uses HashMap");
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

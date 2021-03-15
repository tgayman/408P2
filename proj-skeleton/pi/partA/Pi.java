import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class Pi{
	public static void main(String[] args){
		//For testing, print call graph
		try{
			File graph = new File(args[0]);
			Scanner reader = new Scanner(graph);
			while(reader.hasNextLine()){
				String s = reader.nextLine();
				System.out.println(s);
			}
			reader.close();
		}
		catch(FileNotFoundException e){
			System.out.println("File not found");
		}

		//generate call graph and map
		//hashmap with scope1, scope2, etc. as keys and array of functions as values
		//return hashmap
	}
}

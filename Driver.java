import ParseData.*;
import SetBased.SetSearch;
import Slot_Occupant.*;
import Parser.*;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


public class Driver {

    public static Random random = new Random();
    static ParseData parseData;
    static int MAX_GEN = 150000;
    private static final int RUNTIME = 68400; //seconds

    public static void main(String args[]){

        String configFile = "configFile.txt";			// soft constraint values
        String fileName;

        // Deal with command line args:
        System.out.println("Running ...");

        if(args.length != 2){
            System.out.println("Please provide the config file followed by the input file");
            System.out.println("Usage: java Driver [configFilePath] [inputFilePath]");
            System.exit(0);
        }
        configFile = args[0];
        fileName = args[1];
        //fileName = "testFile.txt";
        try {

            // parsing the input file and initializing all data types
            parseData = Parser.parse(fileName);


            //-------------------------------
            //Set Based Search here
            //-------------------------------
            SetSearch setSearch = new SetSearch(parseData, configFile);
            long endTime = System.currentTimeMillis() + (RUNTIME*1000);

            for(int i = 0 ; i < MAX_GEN && System.currentTimeMillis() < endTime; i++ ) {
                setSearch.runGeneration();
                Pair<Map<Slot_Occupant, Slot>, Double> bestSolution = setSearch.getBestSolution();
                System.out.printf("Best solution for generation %s: %s\n", i, bestSolution.getValue());
                //if(bestSolution != null) {
                  //  printSolution(bestSolution.getKey(), bestSolution.getValue());
              //  }else{
              //      System.out.println("No solution was possible for this generation");
              //  }

            }

            //final solution after all generation ran
            Pair<Map<Slot_Occupant, Slot>, Double> bestSolution = setSearch.getBestSolution();
            System.out.println("Final solution:\n");
            if(bestSolution != null) {
                printSolution(bestSolution.getKey(), bestSolution.getValue());
            }else{
                System.out.println("There was no possible solution for this problem instance");
            }

        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            System.out.println("Invalid argument: try \t java Driver [configFile] [inputFile]");
            System.exit(0);
        } catch (SetSearch.ExhaustedError exhaustedError) {
            System.out.println("All solution exhausted");
            //final solution after all generation ran and exhausted or tree
            Pair<Map<Slot_Occupant, Slot>, Double> bestSolution = exhaustedError.solution;
            System.out.println("Final solution:\n");
            if(bestSolution != null) {
                printSolution(bestSolution.getKey(), bestSolution.getValue());
            }else{
                System.out.println("There was no possible solution for this problem instance");
            }

        } catch (ParseError parseError) {
            parseError.print();
        }

    }

    private static void printSolution(Map<Slot_Occupant, Slot> mutant, double evalVal) {
        Map<Slot_Occupant, Slot> map = new TreeMap<>(new Comparator<Slot_Occupant>() {
            @Override
            public int compare(Slot_Occupant o1, Slot_Occupant o2) {
                if( o1 == o2 )
                    return 0;
                if( o1 == null )
                    return 1;
                if( o2 == null )
                    return -1;
                return o1.toString().compareTo( o2.toString());
            }
        });
        map.putAll(mutant);
        System.out.println("Eval-value: " + evalVal);
        for (Map.Entry<Slot_Occupant, Slot> entry : map.entrySet()) {
            System.out.printf("%-25s : %-10s%n", entry.getKey(), entry.getValue());
        }
    }
}

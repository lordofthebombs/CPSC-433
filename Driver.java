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

    public static void main(String args[]){

        String configFile = "configFile.txt";			// soft constraint values
        String fileName;

        // Deal with command line args:
        System.out.println("Running ...");

        /*if(args.length != 2){
            System.out.println("Please provide the config file followed by the input file");
            System.out.println("Usage: java Driver [configFilePath] [inputFilePath]");
            System.exit(0);
        }*/
        //configFile = args[0];
        //fileName = args[1];
        fileName = "testFile.txt";
        try {
            parseData = Parser.parse("SetTestFiles/setTestFile.txt");
            

            //parseData.Non_Compat.print();
            //Gen the starting states with the Partial Assignments
            //---- Check to make sure the partial assignment is valid if not post error.
            //Then start.

            //The Solutions for the OR tree here
            //-------------------------------
            //Set Based Search here
            //-------------------------------
            SetSearch setSearch = new SetSearch(parseData, configFile);
            Pair<Map<Slot_Occupant, Slot>, Double> bestSolution = setSearch.getBestSolution();
            printSolution(bestSolution.getKey(), bestSolution.getValue() );


            //Output the answers
            //-------------------------------
//            OrTreeSearch orTreeSearch = new OrTreeSearch(parseData);
//            HashSet<Map<Slot_Occupant,Slot>> unique = new HashSet<>();
//            int y = 0;
//            int x = 0;
//
//            Map<Slot_Occupant, Slot> slot_occupantSlotMap = orTreeSearch.OrTreeRecursiveSearch();		// parent
//            if(slot_occupantSlotMap != null) {
//            	System.out.println("PARENT");
//            	printSolution(slot_occupantSlotMap);
//
//
//                if(!unique.add(slot_occupantSlotMap)){
//                	System.out.println("NOT UNIQUE");
//                }
//                Map<Slot_Occupant,Slot> mutant = slot_occupantSlotMap;
//            for(x = 0 ; x < 27 ; x++) {
//
//
//                    System.out.println("MUTANT ---------------------------------------------------");
//                    mutant = orTreeSearch.mutateSearch(mutant);
//                    if (mutant == null) break;		// mutation could be null when all solutions have been exhausted
//                    System.out.println("Mutation on " + orTreeSearch.mutatedOccupant);
//
//                    printSolution(mutant);
//                    if(!unique.add(mutant)){
//                    	System.out.println("NOT UNIQUE");
//                    	break;
//                    }
//
//
//                }
//            }
//            System.out.println("Possible solutions tried = " + unique.size());

            



//            Map<Slot_Occupant, Slot> mutant = orTreeSearch.mutateParentSolution(slot_occupantSlotMap);
//            System.out.println("Parent passed for mutation ----------->");
//            printSolution(slot_occupantSlotMap);
//            System.out.println("MUTATION CREATED ---------------->");
//            if(mutant != null) {
//                printSolution(mutant, 0);
//            }else{
//                System.out.println("Mutant Result was null");
//            }



        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            System.out.println("Invalid argument: try \t java Driver [configFile] [inputFile]");
            System.exit(0);
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

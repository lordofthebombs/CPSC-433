import ParseData.*;
import Slot_Occupant.*;
import Parser.*;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


public class Driver {

    public static Random random = new Random();
    static ParseData parseData;

    public static void main(String args[]){

        String configFile;
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
            parseData = Parser.parse("medfile.txt");

            parseData.Non_Compat.print();
            //Gen the starting states with the Partial Assignments
            //---- Check to make sure the partial assignment is valid if not post error.
            //Then start.

            //The Solutions for the OR tree here
            //-------------------------------
            //Set Based Search here
            //-------------------------------


            //Output the answers
            //-------------------------------

            ConstraintChecker constraintChecker = new ConstraintChecker(parseData);
            OrTreeSearch orTreeSearch = new OrTreeSearch(parseData, constraintChecker);

            for(int x = 0 ; x < 100 ; x++) {
                Map<Slot_Occupant, Slot> slot_occupantSlotMap = orTreeSearch.OrTreeRecursiveSearch(parseData, constraintChecker);

                if(slot_occupantSlotMap != null) {
                    printSolution(slot_occupantSlotMap);
                    System.out.println("NOW MUTATING ---------------------------------------------------------------");
                    slot_occupantSlotMap = orTreeSearch.MutateRecursiveSearch(parseData,constraintChecker,slot_occupantSlotMap);
                    printSolution(slot_occupantSlotMap);
                }else{
                    System.out.println("Result was null");
                }
            }



//            Map<Slot_Occupant, Slot> mutant = orTreeSearch.mutateParentSolution(slot_occupantSlotMap);
//            System.out.println("Parent passed for mutation ----------->");
//            printSolution(slot_occupantSlotMap);
//            System.out.println("MUTATION CREATED ---------------->");
//            if(mutant != null) {
//                printSolution(mutant);
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

    private static void printSolution(Map<Slot_Occupant, Slot> mutant) {
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
        for (Map.Entry<Slot_Occupant, Slot> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }


}

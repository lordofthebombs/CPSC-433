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

        if(args.length != 2){
            System.out.println("Please provide the config file followed by the input file");
            System.out.println("Usage: java Driver [configFilePath] [inputFilePath]");
            System.exit(0);
        }
        configFile = args[0];
        fileName = args[1];

        try {
            parseData = Parser.parse(fileName);

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
            Map<Slot_Occupant, Slot> slot_occupantSlotMap = orTreeSearch.buildValidCandidateSol();

            if(slot_occupantSlotMap != null) {
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
                map.putAll(slot_occupantSlotMap);
                for (Map.Entry<Slot_Occupant, Slot> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
            }else{
                System.out.println("Result was null");
            }


        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            System.out.println("Invalid argument: try \t java Driver [configFile] [inputFile]");
            System.exit(0);
        }

    }



}

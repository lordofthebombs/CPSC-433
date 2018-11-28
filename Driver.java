import ParseData.*;
import Slot_Occupant.*;
import Parser.*;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Random;


public class Driver {

    public static Random random = new Random();
    static ParseData parseData;

    public static void main(String args[]){

        String fileName = "largefile.txt"; //should be an args same with val functions;

        try {
            ParseData p = Parser.parse(fileName);

            p.Non_Compat.print();
            //Gen the starting states with the Partial Assignments
            //---- Check to make sure the partial assignment is valid if not post error.
            //Then start.

            //The Solutions for the OR tree here
            //-------------------------------
            //Set Based Search here
            //-------------------------------


            //Output the answers
            //-------------------------------



        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
        }

        // This is just the main file, untill everything is made though, I would reccomend making your own driver (main) files to test
        // the set based search and or tree.

        String configFile;
        String filename;

        // Deal with command line args:
        System.out.println("Running ...");

        if(args.length != 2){
            System.out.println("Please provide the config file followed by the input file");
            System.out.println("Usage: java Driver [configFilePath] [inputFilePath]");
        }
        configFile = args[0];
        filename = args[1];

        try {
            parseData  = Parser.parse(filename);;

        }
        catch(FileNotFoundException e) {
            System.out.println("Invalid argument: try \t java Driver [configFile] [inputFile]");
            System.exit(0);
        }

        /*  This block if for testing the orTreeSearch */
        OrTreeSearch orTreeSearch = new OrTreeSearch(parseData);
       // orTreeSearch.createSuccessorNodes(labs.get(0));
        Map<Slot_Occupant, Slot> slot_occupantSlotMap = orTreeSearch.buildValidCandidateSol();
        if(slot_occupantSlotMap != null) {
            for (Map.Entry<Slot_Occupant, Slot> entry : slot_occupantSlotMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }else {
            System.out.println("Map was null");
        }


//        OrTreeSearch orTree2 = new OrTreeSearch(parseData);
//        System.out.println("Parent Data being passed: ");
//        Map<Slot_Occupant, Slot> parent = new LinkedHashMap<>();
//        parent.put(parseData.courses.get(0), course_slot.get(0));
//        parent.put(courses.get(1), course_slot.get(2));
//        parent.put(courses.get(2), course_slot.get(1));
//        parent.put(labs.get(0), lab_slot.get(0));
//
//        for (Map.Entry<Slot_Occupant, Slot> entry : parent.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//            }
//            System.out.println();
//        Map<Slot_Occupant, Slot> slot_occupantSlotMap1 = orTree2.mutateParentSolution(parent);
//        if(slot_occupantSlotMap1 != null) {
//            for (Map.Entry<Slot_Occupant, Slot> entry : slot_occupantSlotMap1.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//            }
//        }else {
//            System.out.println("Map was null");
//        }


    }


    public void printResult(Map<Slot_Occupant, Slot> result){

    }



}

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



        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            System.out.println("Invalid argument: try \t java Driver [configFile] [inputFile]");
            System.exit(0);
        }

    }



}

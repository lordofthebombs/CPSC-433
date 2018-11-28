import ParseData.ParseData;
import Parser.*;

import java.io.FileNotFoundException;

public class Driver {

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





    }



}

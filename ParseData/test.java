package ParseData;

import Slot_Occupant.Course;
import Slot_Occupant.Slot_Occupant;

import java.io.IOException;
import java.util.HashSet;

public class test {

    public static void main(String args[]){

        try {
            ParseData d = Parser.Parser.parse("medfile.txt");

        }catch(IOException e){
            System.out.println("File not found");
        }
    }
}

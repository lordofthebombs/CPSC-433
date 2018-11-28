package ParseData;

import Slot_Occupant.Course;
import Slot_Occupant.Slot_Occupant;

import java.io.IOException;
import java.util.HashSet;

public class test {

    public static void main(String args[]){

        try {
            ParseData d = Parser.Parser.parse("testFile.txt");

            Course c1 = new Course("CPSC",567,1);
            Course c2 = new Course("CPSC", 433,2);
            Course c3 = new Course("CPSC",567,1);
            Course c4 = new Course("CPSC", 433,2);

            HashSet< Pair<Slot_Occupant,Slot_Occupant>> e = new HashSet();

            Pair<Slot_Occupant,Slot_Occupant> p1 = new Pair(c1,c2);
            Pair<Slot_Occupant,Slot_Occupant> p2 = new Pair(c4,c3);

            System.out.println(p1.hashCode() + " " + p2.hashCode() + " " + p1.equals(p2) + " " + p2.equals(p1));
            System.out.println(e.add(p1));
            System.out.println(e.add(p2));

        }catch(IOException e){
            System.out.println("File not found");
        }
    }
}

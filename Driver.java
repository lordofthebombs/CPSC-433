import ParseData.*;
import Slot_Occupant.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Driver {

    public static void main(String args[]){


        // This is just the main file, untill everything is made though, I would reccomend making your own driver (main) files to test
        // the set based search and or tree.


        /*  This block if for testing the orTree */
        ParseData parseData = new ParseData();
        Vector<Slot_Occupant> courses = new Vector<>();
        courses.add(new Course("CPSC", 433, 01 ));

        Vector<Slot_Occupant> labs = new Vector<>();
        labs.add(new Lab("CPSC", 433, 00, 01));

        Vector<Slot> lab_slot = new Vector<>();
        lab_slot.add(new Slot(Slot.Day.Mon, 12, 1, 1));

        Vector<Slot> course_slot = new Vector<>();
        course_slot.add(new Slot(Slot.Day.Mon, 12, 1, 1));

        parseData.setCourses(courses);
        parseData.setLabs(labs);
        parseData.setCourse_Slots(course_slot);
        parseData.setLab_Slots(lab_slot);
        
        Slot courseSlot = parseData.Course_Slots.firstElement();
        int labInd = parseData.Lab_Slots.indexOf(new Slot(courseSlot.day, 12, -1, -1));
        System.out.println(labInd);
        
        /*
        parseData.Partial_Assignments.addEntry(courses.get(0), course_slot.firstElement());

        OrTree orTree = new OrTree(parseData);
        orTree.altern(labs.get(0));
        System.out.println(orTree.toString());
		*/




    }




}

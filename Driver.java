import ParseData.*;
import Slot_Occupant.*;

import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class Driver {

    public static Random random = new Random();

    public static void main(String args[]){


        // This is just the main file, untill everything is made though, I would reccomend making your own driver (main) files to test
        // the set based search and or tree.

        Random random = new Random();

        /*  This block if for testing the orTree */
        ParseData parseData = new ParseData();
        Vector<Slot_Occupant> courses = new Vector<>();
        courses.add(new Course("CPSC", 433, 01 ));
        courses.add(new Course("CPSC", 451, 01 ));
        courses.add(new Course("CPSC", 452, 01 ));

        Vector<Slot_Occupant> labs = new Vector<>();
        labs.add(new Lab("CPSC", 433, 01, 01));

        Vector<Slot> lab_slot = new Vector<>();
        lab_slot.add(new Slot(Slot.Day.Mon, 12, 1, 1));

        Vector<Slot> course_slot = new Vector<>();
        course_slot.add(new Slot(Slot.Day.Mon, 13, 1, 1));
        course_slot.add(new Slot(Slot.Day.Tues, 13, 1, 1));
        course_slot.add(new Slot(Slot.Day.Fri, 13, 1, 1));

        parseData.setCourses(courses);
        parseData.setLabs(labs);
        parseData.setCourse_Slots(course_slot);
        parseData.setLab_Slots(lab_slot);

        parseData.Partial_Assignments.addEntry(courses.firstElement(), course_slot.firstElement());

        OrTree orTree = new OrTree(parseData);
       // orTree.createSuccessorNodes(labs.get(0));
        Map<Slot_Occupant, Slot> slot_occupantSlotMap = orTree.buildValidCandidateSol();
        if(slot_occupantSlotMap != null) {
            for (Map.Entry<Slot_Occupant, Slot> entry : slot_occupantSlotMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }else {
            System.out.println("Map was null");
        }





    }




}

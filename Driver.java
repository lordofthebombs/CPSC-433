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
        courses.add(new Course("CPSC", 433, 1 ));
        courses.add(new Course("CPSC", 449, 1));
        courses.add(new Course("CPSC", 231, 9));
        courses.add(new Course("CPSC", 501, 1));
        courses.add(new Course("CPSC", 502, 1)); 
        
        Slot_Occupant clone433 = new Course("CPSC", 433, 1);

        Vector<Slot_Occupant> labs = new Vector<>();
        labs.add(new Lab("CPSC", 433, 00, 01));

        Vector<Slot> lab_slot = new Vector<>();
        lab_slot.add(new Slot(Slot.Day.Mon, 12, 1, 1));

        Vector<Slot> course_slot = new Vector<>();
        course_slot.add(new Slot(Slot.Day.Mon, 12, 1, 1));
        course_slot.add(new Slot(Slot.Day.Mon, 18, 1, 1));
        

        parseData.setCourses(courses);
        parseData.setLabs(labs);
        parseData.setCourse_Slots(course_slot);
        parseData.setLab_Slots(lab_slot);
        
       // parseData.Non_Compat.addEntry(courses.get(0), courses.get(1));
        //parseData.Unwanted.addEntry(courses.get(0), course_slot.get(0));
        
        
        Map<Slot_Occupant, Slot> m = new HashMap<>();
        m.put(courses.get(3), course_slot.get(1));
        m.put(courses.get(4), course_slot.get(0));
        m.put(courses.get(1), course_slot.get(0));
        
        m.put(labs.get(0), lab_slot.get(0));

        
        ConstraintChecker cc = new ConstraintChecker(parseData);
        System.out.println("slotMax should be false");
        System.out.println(cc.isSlotMaxValid(m));
         
        System.out.println("overlap should be false");
        System.out.println(cc.isOverlapValid(m));
        
        System.out.println("non-compat should be false");
        System.out.println(cc.isCompatibleValid(m));
        
        System.out.println("Unwanted should be false");
        System.out.println(cc.isUnwantedValid(m));
        
        System.out.println("Evening slot should be true");
        System.out.println(cc.isEveningSlotsValid(m));
        
        System.out.println("is500Course should be false");
        System.out.println(cc.is500CoursesValid(m));
        
        
        System.out.println(clone433.equals(courses.get(0)));
        
        
        
        //Vector<Slot> timeSlots = parseData.getSlots();
        //timeSlots.get(0).max = 0;
        //System.out.println(timeSlots.get(0).max);
        //System.out.println(parseData.Course_Slots_Orig.get(0).max);
       // System.out.println(labInd);
        
        /*
        parseData.Partial_Assignments.addEntry(courses.get(0), course_slot.firstElement());

        OrTree orTree = new OrTree(parseData);
        orTree.altern(labs.get(0));
        System.out.println(orTree.toString());
		*/




    }




}

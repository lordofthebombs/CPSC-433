package ParseData;

import Slot_Occupant.Slot_Occupant;

import java.util.Iterator;
import java.util.Vector;

public class ParseData {

    //Reminder: I might need to write my own (pair) class for the dataClasses so I can override the Equals method.

    public Vector<Slot_Occupant> Courses;

    public Vector<Slot_Occupant> Labs;
    public Vector<Slot> Course_Slots;
    private Vector<Slot> Course_Slots_Orig;
    public Vector<Slot> Lab_Slots;
    private Vector<Slot> Lab_Slots_Orig;

    public Non_Compatable Non_Compat;
    public Unwanted Unwanted;
    public Preferences Preferences;
    public Pairs Pairs;
    public Partial_Assignments Partial_Assignments;

    public ParseData(){
        this.Non_Compat = new Non_Compatable();
        this.Unwanted = new Unwanted();
        this.Preferences = new Preferences();
        this.Pairs = new Pairs();
        this.Partial_Assignments = new Partial_Assignments();
    }

    public void setCourses(Vector<Slot_Occupant> courses) {
    	this.Courses = courses;
    }

    public void setLabs(Vector<Slot_Occupant> labs) {
        this.Labs = labs;
    }

    public void setCourse_Slots(Vector<Slot> Course_Slots){
    	this.Course_Slots_Orig = Course_Slots;
        this.Course_Slots = cloneSlots(Course_Slots_Orig);
    }
    
    public void setLab_Slots(Vector<Slot> Lab_Slots){
    	this.Lab_Slots_Orig = Lab_Slots;
        this.Lab_Slots = cloneSlots(Lab_Slots_Orig);
    }
    
    // **ENSURE THAT THIS FUNCTION IS ONLY CALLED WHEN A NEW ORTREE IS CREATED**
    // resets the value of the maximum courses for all slots in Lab_Slots and Course_Slots
    // used because courseMax will be decremented whenever a new course is added to a slot
    public void resetCourseSlots() {
    	for(int i = 0; i < Course_Slots_Orig.size(); i++) {
    		Slot currentSlot = Course_Slots.get(i);
    		currentSlot.max = Course_Slots_Orig.get(i).max;
    	}
    	
    	for(int i = 0; i < Lab_Slots_Orig.size(); i++) {
    		Slot currentSlot = Lab_Slots.get(i);
    		currentSlot.max = Lab_Slots_Orig.get(i).max;
    	}

    }
    
    private Vector<Slot> cloneSlots(Vector<Slot> vSlot) {
    	Vector<Slot> copyVSlot = new Vector<Slot>();
    	
    	for(Slot currentSlot : vSlot) {
    		copyVSlot.add(new Slot(currentSlot.day, currentSlot.time, currentSlot.max, currentSlot.min));
    	}
    	
    	return copyVSlot;
    }
}

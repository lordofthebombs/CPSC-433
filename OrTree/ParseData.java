package ParseData;


import Slot_Occupant.*;
import java.util.Vector;

public class ParseData {

    //Reminder: I might need to write my own (pair) class for the dataClasses so I can override the Equals method.

	public Vector<Slot> AllSlots;
	public Vector<Slot_Occupant> AllOccupants;



    public Vector<Slot_Occupant> Courses;
    public Vector<Slot_Occupant> Labs;

    public Vector<Slot> Course_Slots_Orig;
    public Vector<Slot> Course_Slots;
    
    public Vector<Slot> Lab_Slots_Orig;
    public Vector<Slot> Lab_Slots;

    public Non_Compatable Non_Compat;
    public Unwanted Unwanted;
    public Preferences Preferences;
    public Pairs Pairs;
    public Partial_Assignments Partial_Assignments;

    public ParseData(){
        this.Courses = new Vector<Slot_Occupant>();
        this.Labs = new Vector<Slot_Occupant>();

        this.Course_Slots = new Vector<Slot>();
        this.Lab_Slots = new Vector<Slot>();

        this.Non_Compat = new Non_Compatable();
        this.Unwanted = new Unwanted();
        this.Preferences = new Preferences();
        this.Pairs = new Pairs();
        this.Partial_Assignments = new Partial_Assignments();
        AllSlots = null;
        AllOccupants = null;
    }

    public void setAllSlots(){
        AllSlots = getSlots();
    }
    public Vector<Slot> getAllSlots(){
        return AllSlots;
    }
    public void setAllOccupants(){
       AllOccupants = getOccupants();
    }
    public Vector<Slot_Occupant> getAllOccupants(){
        return AllOccupants;
    }

    public void setCourses(Vector<Slot_Occupant> courses) {
        this.Courses = courses;
    }

    public void setLabs(Vector<Slot_Occupant> labs) {
        this.Labs = labs;
    }
    
    // creates two copies of slots
    public void setCourse_Slots(Vector<Slot> Course_Slots){
    	this.Course_Slots_Orig = Course_Slots;
        this.Course_Slots = cloneSlots(Course_Slots_Orig);
    }
    
    // creates two copies of slots
    public void setLab_Slots(Vector<Slot> Lab_Slots){
    	this.Lab_Slots_Orig = Lab_Slots;
        this.Lab_Slots = cloneSlots(Lab_Slots_Orig);
    }

    public Vector<Slot> getSlots(){
       Vector<Slot> Time_Slots = new Vector<Slot>();
        Time_Slots.addAll(this.Course_Slots);
        Time_Slots.addAll(this.Lab_Slots);
        return Time_Slots;
    }

    public Vector<Slot_Occupant> getOccupants(){
    	Vector<Slot_Occupant> occupants = new Vector<>();
    	occupants.addAll(this.Courses);
    	occupants.addAll(this.Labs); 
    	return occupants; 
    }
    
    // **ENSURE THAT THIS FUNCTION IS ONLY CALLED WHEN A NEW CANDIDATE SOLUTION IS CREATED**
    // resets the value of the maximum courses for all slots in Lab_Slots and Course_Slots
    // used because courseMax will be decremented whenever a new course is added to a slot
    public void resetTimeSlots() {
    	// j counter accounts for the copy of time slots having different amount of slots in it
    	// since some slots will be removed due to the hard constraint
    	int j = 0;
    	for(int i = 0; i < Course_Slots_Orig.size(); i++) {
    		if (j < Course_Slots.size() && Course_Slots_Orig.get(i).equals(Course_Slots.get(j))) {
    			Slot currentSlot = Course_Slots.get(j);
    			currentSlot.max = Course_Slots_Orig.get(i).max;
    			j++;
    		}
    	}
    	
    	j = 0;
    	for(int i = 0; i < Lab_Slots_Orig.size(); i++) {
    		if (Lab_Slots_Orig.get(i).equals(Lab_Slots.get(j))) {
    			Slot currentSlot = Lab_Slots.get(j);
    			currentSlot.max = Lab_Slots_Orig.get(i).max;
    			j++;
    		}
    	}

    }
    
    // helper function for duplicating the time slots for courses and labs
    // relevant for checking hard constraints
    private Vector<Slot> cloneSlots(Vector<Slot> vSlot) {
    	Vector<Slot> copyVSlot = new Vector<Slot>();
    	
    	for(Slot currentSlot : vSlot) {
    		copyVSlot.add(new Slot(currentSlot.day, currentSlot.time, currentSlot.max, currentSlot.min));
    	}
    	
    	return copyVSlot;
    }

}

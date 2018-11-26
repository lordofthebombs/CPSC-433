import ParseData.Slot.Day;
import Slot_Occupant.Course;
import Slot_Occupant.Lab;
import Slot_Occupant.Slot_Occupant;
import java.util.*;
import ParseData.*;


/**
 *
 */

/* HARD CONSTRAINTS
1. Not more than coursemax(s) courses can be assigned to slot s.

2. Not more than labmax(s) labs can be assigned to slot s.

3. assign(ci) has to be unequal to assign(lik) for all k and i. (a course and its labs cannot be in the same slot)		

4. not-compatible(a,b) means: assign(a) cannot equal assign(b) (where a,b in Courses + Labs)

5. partassign: assign(a) must equal partassign(a) for all a in Courses + Labs with partassign(a) not equal to $		// done when orTree is initialized

6. unwanted(a,s): assign(a) cannot equal s (with a in Courses + Labs and s in Slots)								// use Unwanted's isUnwanted() function

7. All course sections with a section number starting LEC 9 are evening classes and have to be scheduled into evening slots (18:00 or later).

8. All 500-level course sections must be scheduled into different time slots.					

9. No courses can be scheduled at Tuesdays 11:00-12:30.																// class removes tues slots at 11.0f when created


	IF CPSC 313 IN COURSES:
10. CPSC 813 must be scheduled for Tuesdays/Thursdays 18:00-19:00 (note, they are scheduled into lab slots)		// done when an OrTree data is initialized
11. CPSC 813 cannot overlap with any labs/tutorials, or course sections of CPSC 313
12. CPSC 813 cannot overlap with any courses that cannot overlap with CPSC 313

	IF CPSC 413 IN COURSES:
13. CPSC 913 must be scheduled for Tuesdays/Thursdays 18:00-19:00 (note, they are scheduled into lab slots)		// done when an OrTree data is initialized
14. CPSC 913 cannot overlap with any labs/tutorials, or course sections of CPSC 413
15. CPSC 913 cannot overlap with any courses that cannot overlap with CPSC 413

16. Can we ignore these due to abstract slot representation? Probably should ask the prof
	-- If a course (course section) is put into a slot on Mondays, it has to be put into the corresponding time slots on Wednesdays and Fridays.
	-- If a course (course section) is put into a slot on Tuesdays, it has to be put into the corresponding time slots on Thursdays.
	-- If a lab/tutorial is put into a slot on Mondays, it has to be put into the corresponding time slots on Wednesdays.
	-- If a lab/tutorial is put into a slot on Tuesdays, it has to be put into the corresponding time slots on Thursdays.
*/
public class ConstraintChecker {
	private int EVENING_SECTION = 9;
	private ParseData parseData;
	// contains labs for a course
	private Map<Course, Vector<Lab>> correspondingLabs;
	// contains all courses corresponding to a lab without section #
	private Map<Lab, Vector<Course>> correspondingCourses;

	
    public ConstraintChecker(ParseData parseData){
    	this.parseData = parseData;
    	
    	// removes time slots at tuesdays 11.0f
    	// unsure about start time
        Iterator<Slot> iter = parseData.getSlots().iterator();
        while (iter.hasNext()) {
        	Slot currentSlot = iter.next();
        	if (currentSlot.day == Day.Tues && currentSlot.time == 11.0f) {
        		iter.remove();
        	}
        }
        
        // pairs courses to its corresponding labs
        for (Slot_Occupant c : parseData.Courses) {
        	Vector<Lab> correspondingL = new Vector<>();
        	// 813, 913 have no corresponding labs
        	// skip maybe
        	for (Slot_Occupant l : parseData.Labs) {
        		// adds to corresponding labs for each courses
        		if (c.id.equals(l.id) && c.courseNum == l.courseNum) {
        			correspondingL.add((Lab)l);
        		}
 
        	}
        	
        	this.correspondingLabs.put((Course)c, correspondingL);
        }
        
        // pairs labs that have no lecture sections to all its corresponding courses
        for (Slot_Occupant l : parseData.Labs) {
        	if (((Lab)l).hasLectSect()) continue;
        	
        	Vector<Course> correspondingC = new Vector<>();
        	for (Slot_Occupant c : parseData.Courses) {
        		if (c.id.equals(l.id) && c.courseNum == l.courseNum) {
        			correspondingC.add((Course)c);
        		}
        	}
        	
        	this.correspondingCourses.put((Lab)l, correspondingC);
        }
        
    }
    
    // pairs a slot occupant to a slot on the hashmap
    // decrements the amount max courses the slot can take
    // CAREFUL : doesnt care if max dips below 0 
    private void pairSlotOccupantToSlot(Map<Slot_Occupant, Slot> data, Slot_Occupant sl, Slot s) {
    	data.put(sl, s);
    	s.max--;
    }
    
    // computes hard coded stuff such as:
    // adding part-assignments
    // adding 813, 913 into lab slots
    // assumes max courses/labs are not exceeded
    public Map<Slot_Occupant, Slot> initialize() {
    	// initializes a node data with the required part assigns
    	Vector<Slot> allSlots = parseData.Course_Slots;
    	allSlots.addAll(parseData.Lab_Slots);

    	Map<Slot_Occupant, Slot> data = new LinkedHashMap<>();
        allSlots.forEach((item) -> data.put(null, item));


        HashMap<Slot_Occupant, Slot> allPartialAssignments = this.parseData.Partial_Assignments.getAllPartialAssignments();

        for(Map.Entry<Slot_Occupant, Slot> assignment : allPartialAssignments.entrySet()){
            if(data.containsKey(assignment.getKey())){
            	pairSlotOccupantToSlot(data, assignment.getKey(), assignment.getValue());
            }
        }
        
        // add 813 and 913 into lab slots
        // not sure what program will implement this
        // assumes 813 and 913 always exists
       // assumes parser has added 813 / 913 into the vector of courses
        // 2nd last should contain 813
        // last should contain 913
        
        // add 813 to lab slots
        Slot_Occupant specialC = parseData.Courses.get(parseData.Course_Slots.size()-2);
        Slot tue18 = parseData.Lab_Slots.get(parseData.Lab_Slots.indexOf(new Slot(Day.Tues, 18.0f, -1, -1)));	// lab slots only compare day and time
        pairSlotOccupantToSlot(data, specialC, tue18);
        
        // add 913 to lab slots
        specialC = parseData.Courses.get(parseData.Course_Slots.size() - 1);
        pairSlotOccupantToSlot(data, specialC, tue18);
        
        return data;
    }
    
    
    public boolean checkHardConstraints(Map<Slot_Occupant, Slot> data, Slot s, Course so) {
    	
    	
    	
    	return true;
    }
    
    public boolean checkHardConstraints(Map<Slot_Occupant, Slot> data, Slot s, Lab so) {
    	
    	
    	
    	return true;
    }
   
   // checks if the current slot still has room for a course/lab
   private boolean isSlotFull(Slot s) {
	   return s.max < 1;
   }
   
   // checks if a course overlaps with its corresponding labs
   private boolean isOverlap(Map<Slot_Occupant, Slot> data, Slot s, Course so) {
	   Slot correspondingSlot = findLabSlot(s);
	   // implies no corresponding lab slot
	   if (correspondingSlot == null) return false;

   }
    
    
    
    /*
    // assumes that our time slot is the same as the time slot of cpsc 813
    // checks if the given lab is compatible with the time slot
    private boolean isCompatibleWith813(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Lab so) {
    	return (so.id.equals("CPSC") && so.courseNum == 313);
    }
    
    // checks if the given course is compatible with cpsc 313 course
    private boolean isCompatibleWith813(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Course so) {
    	if (so.id.equals("CPSC") && so.courseNum == 313) return false;
    	// assumes cpsc 313 lecture 1 exists
    	return (parseData.Non_Compat.Compatible(so, new Course("CPSC", 313, 1)));		
    	
    }
    
    
    // checks if a time slot is the same time and day as cpsc 813
    private boolean is813Slot(Slot s) {
    	return (s.day == Slot.Day.Tues && s.time == 18.0f);
    }
    
    
    
    // labmax and courseMax unecessary since slots are separated already
    // assumes that slot and slot_occupant are from the same group (courses/labs)
    private boolean isMax(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Slot_Occupant so) {
    	Vector<Slot_Occupant> slotsVector = data.get(s);
    	return s.max > slotsVector.size();
    }
    
    // checks if a course overlaps with its respective tutorials
    private boolean isOverlap(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Course so) {
    	Slot correspondingSlot = findLabSlot(s);
    	if (correspondingSlot == null) {				// means there's no corresponding lab slot
    		return true;
    	} else {
    		Vector<Slot_Occupant> slotsVector = data.get(correspondingSlot);
    		Iterator<Slot_Occupant> vecIterator = slotsVector.iterator();
    		while(vecIterator.hasNext()) {
    			Slot_Occupant currentSO = vecIterator.next();
    			if (currentSO.id.equals(so.id) && currentSO.courseNum == so.courseNum) {
    				return false;
    			}
    		}
    		return true;
    	}
    }
    
    // checks if a lab overlaps with its respective courses
    private boolean isOverlap(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Lab so) {
    	Slot correspondingSlot = findCourseSlot(s);
    	if (correspondingSlot == null) {		// means there's no corresponding course slot
    		return true;
    	} else {
    		Vector<Slot_Occupant> slotsVector = data.get(correspondingSlot);
    		Iterator<Slot_Occupant> vecIterator = slotsVector.iterator();
    		while(vecIterator.hasNext()) {
    			Slot_Occupant currentSO = vecIterator.next();
    			if (currentSO.id.equals(so.id) && currentSO.courseNum == so.courseNum) {
    				return false;
    			}
    		}
    		return true;
    	}
    	
    }
    
    // checks if the given slot_occupant is compatible with all the slot_occupants in the given time slot
    private boolean isCompatible(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Slot_Occupant so) {
    	Vector<Slot_Occupant> slotsVector = data.get(s);
    	if (slotsVector.size() == 0) {
    		return true;
    	} else {
    		Iterator<Slot_Occupant> vecIterator = slotsVector.iterator();
    		while(vecIterator.hasNext()) {
    			if (!parseData.Non_Compat.Compatible(so, vecIterator.next())) return false;
    		}
    		return true;
    	}
    }
    
   // checks if courses with lecture section higher than or equal to 9 is scheduled into evening time slots
    private boolean isScheduledCorrect(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Slot_Occupant so) {
    	boolean output = false;
    	if ((so.lectSection < EVENING_SECTION || so.lectSection >= EVENING_SECTION && s.time >= 18.0f)) {
    		output = true;
    	}
    	return output;
    }
    
    // checks if a time slot does not already contain a 500 level course
    private boolean isSlotOpenFor500LevelCourse(Map<Slot, Vector<Slot_Occupant>> data, Slot s) {
    	Vector<Slot_Occupant> sVector = data.get(s);
    	Iterator<Slot_Occupant> vecIterator = sVector.iterator();
    	while(vecIterator.hasNext()) {
    		if (vecIterator.next().courseNum == 500) return false;
    	}
    	return true;
    }
    
    
    // checks whether the given time slot is within tuesday from 11:00 to 12:30 pm
    private boolean isTimeOff(Slot s) {
    	return (s.day == Slot.Day.Tues && s.time >= 11.0f && s.time <= 12.5f);
    }
    
    */
   
    // helper function that finds the corresponding lab_slot given the course_slot
    // used for checking if labs and courses are in the same slots
    private Slot findLabSlot(Slot courseSlot) {
    	Vector<Slot> labSlots = parseData.Lab_Slots;
    	int labInd = labSlots.indexOf(new Slot(courseSlot.day, courseSlot.time, -1, -1));
    	if (labInd == -1) {
    		return null;
    	} else {
    		return labSlots.get(labInd);
    	}
    	
    }
    
    // helper function that finds the corresponding course_slot given the lab_slot
    private Slot findCourseSlot(Slot labSlot) {
    	Vector<Slot> courseSlots = parseData.Course_Slots;
    	int courseInd = courseSlots.indexOf(new Slot(labSlot.day, labSlot.time, -1, -1));
    	if (courseInd == -1) {
    		return null;
    	} else {
    		return courseSlots.get(courseInd);
    	}
    }
	
    
}

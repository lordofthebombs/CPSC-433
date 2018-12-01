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
	private float EVENING_TIME = 18.0f;
	private ParseData parseData;
	// contains all the corresponding labs of any course, if it exists
	private Map<Slot_Occupant, Vector<Slot_Occupant>> correspondingLabs = new HashMap<>();
	
	// contains all courses corresponding to a lab without section #
	//private Map<Slot_Occupant, Vector<Slot_Occupant>> correspondingCourses = new HashMap<>();
	
	//private Vector<Slot_Occupant> allSlot_Occupants = new Vector<>();
	private Vector<Slot_Occupant> courses500 = new Vector<>();
	//private Vector<Slot_Occupant> all313Courses = new Vector<>();
	//private Vector<Slot_Occupant> all413Courses = new Vector<>();
	//private Slot_Occupant course813;
	//private Slot_Occupant course913;


	
	/**
	 * **REMOVES COURSE SLOT AT TUESDAY, 11.0**
	 * 
	 * @param parseData : data containing the slots, slot_occupants and hard-constraint information
	 */
    public ConstraintChecker(ParseData parseData){
    	this.parseData = parseData;
    	
    	// removes course time slots at tuesdays 11.0f
    	// assumes only 1 course slot at tue 11.0f exists
        int indexOfTue = parseData.Course_Slots.indexOf(new Slot(Day.Tues, 11.0f, -1, -1));			// finds tues slot, works becuause of slot equal()
        // if tues slot exists
        if (indexOfTue != -1) {
        	parseData.Course_Slots.remove(indexOfTue);

        }
        
        
        // pairs courses to its corresponding labs
        for (Slot_Occupant c : parseData.Courses) {
        	Vector<Slot_Occupant> correspondingL = new Vector<Slot_Occupant>();
        	
        	// add 500-level courses to a vector
        	if (c.courseNum >= 500 && c.courseNum < 600) courses500.add(c);
        	
        	// add 313 course sections to a vector
        	//else if (c.id.equals("CPSC") && c.courseNum == 313) all313Courses.add(c);
        	// add 413 course sections to a vector
        	//else if (c.id.equals("CPSC") && c.courseNum == 413) all413Courses.add(c);
        	
        	// save 813 pointer
        	else if (c.id.equals("CPSC") && c.courseNum == 813) {
        		//course813 = c;
        		continue;
        	}
        	// save 913 pointer
        	else if (c.id.equals("CPSC") && c.courseNum == 913) {
        		//course913 = c;
        		continue;
        	}
        	
        	
        	// 813, 913 have no corresponding labs
        	
        	for (Slot_Occupant l : parseData.Labs) {
        		
        		// adds to corresponding labs for each courses
        		if (c.id.equals(l.id) && c.courseNum == l.courseNum) {
        			// no lab section or same lab section
        			if (!((Lab)l).hasLectSect() || l.lectSection == c.lectSection) {
        				correspondingL.add(l);
        			}
        		}
 
        	}
        	// empty vector is acceptable
        	this.correspondingLabs.put(c, correspondingL);
        	
        }
        
    }
    
    
    /**
     * Compute hard-Coded stuff such as:
     * partial assignments
     * 813/913 go into corresponding lab slots 
     * does not decrement slot counter at the moment
     * @return Map<Slot_Occupant, Slot>
     */
    public Map<Slot_Occupant, Slot> initialize() {
    	// initializes a node data with the required part assigns
    	Map<Slot_Occupant, Slot> data = new LinkedHashMap<Slot_Occupant, Slot>();

    	Vector<Slot_Occupant> allSlotOccupants = parseData.getOccupants();
        allSlotOccupants.forEach((item) -> data.put(item, null));
		
        HashMap<Slot_Occupant, Slot> allPartialAssignments = this.parseData.Partial_Assignments.getAllPartialAssignments();
        // partial assignments already contain 813/913 into corresponding lab slots
        for(Map.Entry<Slot_Occupant, Slot> assignment : allPartialAssignments.entrySet()){
            if(data.containsKey(assignment.getKey())){
            	data.put(assignment.getKey(), assignment.getValue());
            }
        }
        
        return data;
    }
    
    /**
     * 
     * 
     * @param data : Map containing the assignments of Slot_Occupant to Slot
     * @return : true if all hard_constraints were passed, false otherwise
     */
    public boolean checkHardConstraints(Map<Slot_Occupant, Slot> data) {
    	Set<Slot_Occupant> keys = data.keySet();
 	   	// no keys therefore no courses/labs are assigned
 	   	if (keys.isEmpty()) return true;
    	
    	if (!isSlotMaxValid(data)){
    		return false;
    	}
    	
    	if (!isOverlapValid(data)) {
    		//System.out.println("Corresponding courses and labs were scheduled in the same slot.");
    		return false;
    	}
    	
    	if (!isCompatibleValid(data)) {
    		//System.out.println("Incompatible slot_occupants were scheduled in the same slot");
    		return false;
    	}
    	
    	if (!isUnwantedValid(data)) {
    		//System.out.println("Slot_occupant were schedule in an unwanted slot");
    		return false;
    	}
    	/*
    	if (!isEveningSlotsValid(data)) { 
    		System.out.println("Evening slots");
    		return false;
    	
    	}
    	*/
    	
    	if (!(is500CoursesValid(data))) {
    		//System.out.println("500 course/s were scheduled in the same slot");
    		return false;
    	}
    	
    	//if (!(is813CourseValid(data))) return false;
    	
    	//if (!(is913CourseValid(data))) return false;
    	
    	return true;
    }
    
    
    // checks if all the slots can be paired to their corresponding courses
    public boolean isSlotMaxValid(Map<Slot_Occupant, Slot> solution) {

        boolean output = true;

    	// iterate through keys that aren't null
    	Iterator<Slot_Occupant> soIter = solution.keySet().iterator();
    	
    	while (soIter.hasNext()) {
    		Slot currentSlot = solution.get(soIter.next());
    		if (currentSlot == null) continue;
    		
    		if (currentSlot.max > 0) {
    			currentSlot.max--;
    		} else {
    			output = false;
    			break;
    		}
    	}
    	
    	// reset counter for time slots
    	parseData.resetTimeSlots();

    	return output;
    }
    
   
   // checks if a course overlaps with its corresponding labs
   public boolean isOverlapValid(Map<Slot_Occupant, Slot> data) {
	   Iterator<Slot_Occupant> slIterator = data.keySet().iterator();
	  
	   while (slIterator.hasNext()) {
		   Slot_Occupant currentSO = slIterator.next();
		   Slot currentSO_Slot = data.get(currentSO);
		   
		   if (currentSO_Slot == null) continue;
		   
		   if (isCourseObject(currentSO)) {
			   // compare corresponding labs
			   Vector<Slot_Occupant> matchingLabs = correspondingLabs.get(currentSO);
			   Iterator<Slot_Occupant> matchingLabsIter = matchingLabs.iterator();
			   
			   while (matchingLabsIter.hasNext()) {
				   Slot currentSlot = data.get(matchingLabsIter.next());
				   if (currentSlot == null) continue;
				   
				   if (currentSO_Slot.equals(currentSlot)) return false;
				   
			   }
			   
		   }
	   }
	 
	   return true;
   }
   
   // checks if any of the assigned slot_occupants are incompatible(so, so) with other assigned slot_occupants
   public boolean isCompatibleValid(Map<Slot_Occupant, Slot> data) {
	   Iterator<Slot_Occupant> soIter = data.keySet().iterator();
	   
	   while (soIter.hasNext()) {
		   Slot_Occupant currentSO = soIter.next();
		   Slot currentSO_Slot = data.get(currentSO);
		   
		   // ensures that current slot_occupant is assigned a slot
		   if (currentSO_Slot != null) {
			   HashSet<Slot_Occupant> non_compatibles = parseData.Non_Compat.isNonCompatableWith(currentSO);
			   
			   Iterator<Slot_Occupant> non_compat_iter = non_compatibles.iterator();
			   while (non_compat_iter.hasNext()) {
				   Slot_Occupant nonCompat_currentSO = non_compat_iter.next();
				   Slot nonCompat_currentS = data.get(nonCompat_currentSO);
				   
				   if (nonCompat_currentS != null && nonCompat_currentS.equals(currentSO_Slot)) return false;
					   
			   }
			   
		   }
	   }
	   
	   return true;
	   
   }
   
   // checks if any unwanted(a, s) is in the data
   public boolean isUnwantedValid(Map<Slot_Occupant, Slot> data) {
	   Iterator<Slot_Occupant> soIter = data.keySet().iterator();
	   
	   while (soIter.hasNext()) {
		   Slot_Occupant currentSO = soIter.next();
		   Slot currentSO_Slot = data.get(currentSO);
		   
		   if (currentSO_Slot != null) {
			   HashSet<Slot_Occupant> unwanteds = parseData.Unwanted.isUnwantedWith(currentSO_Slot);
			   
			   // unsure if this works
			   if (unwanteds.contains(currentSO)) return false;
		   }
	   }
	   
	   return true;
   }
   
   // checks if all evening sections are scheduled into evening slots
   public boolean isEveningSlotsValid(Map<Slot_Occupant, Slot> data) {
	   Iterator<Slot_Occupant> soIter = data.keySet().iterator();
	   
	   while (soIter.hasNext()) {
		   Slot_Occupant currentSO = soIter.next();
		   Slot currentSO_Slot = data.get(currentSO);
		   
		   if (currentSO_Slot != null) {
			   if (currentSO.lectSection == EVENING_SECTION && currentSO_Slot.time < EVENING_TIME) return false;
		   }
	   }
	   
	   return true;
   }
   
   // checks that all 500 courses have different time slots
   public boolean is500CoursesValid(Map<Slot_Occupant, Slot> data) {
	   Iterator<Slot_Occupant> iter500 = courses500.iterator();
	   HashSet<Slot> slSet = new HashSet<>();
	   int assigned500Courses = 0;
	   
	   while (iter500.hasNext()) {
		   Slot_Occupant currentSO = iter500.next();
		   Slot currentSO_Slot = data.get(currentSO);
		   
		   if (currentSO_Slot != null) {
			   slSet.add(currentSO_Slot);
			   assigned500Courses++;
		   }
	   }
	   
	   return slSet.size() == assigned500Courses;
   }
   
   // **USELESS PROBABLY**
   /*
   public boolean is813CourseValid(Map<Slot_Occupant, Slot> data) {
	   return isSpecialCourseValid(data, course813);
   }
   
   public boolean is913CourseValid(Map<Slot_Occupant, Slot> data) {
	   return isSpecialCourseValid(data, course913);
   }
   
   
   
   // cannot overlap with 313/413 courses and labs
   // **transitivity not implemented**
   private boolean isSpecialCourseValid(Map<Slot_Occupant, Slot> data, Slot_Occupant specialCourse) {
	   Slot slotSpecialCourse = data.get(specialCourse);
	   Iterator<Slot_Occupant> iterSpecialCourseSec = null;
	   if (specialCourse.equals(course813)) iterSpecialCourseSec = all313Courses.iterator();
	   else if (specialCourse.equals(course913)) iterSpecialCourseSec = all413Courses.iterator();
	   
	   while (iterSpecialCourseSec.hasNext()) {
		   Slot_Occupant currentSC_C = iterSpecialCourseSec.next();
		   Slot currentSC_S = data.get(currentSC_C);
		   if (currentSC_S != null) {
			   // check that the isn't in the same slot
			   if (currentSC_S.equals(slotSpecialCourse)) return false;
			   
			   // check if the labs of the course isn't in the same slot
			   Iterator<Slot_Occupant> currentSC_C_Labs_Iter = correspondingLabs.get(currentSC_C).iterator();
			   while (currentSC_C_Labs_Iter.hasNext()) {
				   Slot_Occupant currentSC_C_Lab = currentSC_C_Labs_Iter.next();
				   Slot currentSC_C_Lab_S = data.get(currentSC_C_Lab);
				   
				   if (currentSC_C_Lab_S != null) {
					   if (currentSC_C_Lab_S.equals(slotSpecialCourse)) return false;
				   }
				   
			   }
		   }
	   }
	   
   }
   */
   
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
   	
   /*
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
    */
   
    private boolean isCourseObject(Slot_Occupant so) {
    	return (so instanceof Course);
    }
	
    
}

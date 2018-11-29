import ParseData.ParseData;

/**
 *
 */

/* HARD CONSTRAINTS
1. Not more than coursemax(s) courses can be assigned to slot s.

2. Not more than labmax(s) labs can be assigned to slot s.

3. assign(ci) has to be unequal to assign(lik) for all k and i. (a course and its labs cannot be in the same slot)

4. not-compatible(a,b) means: assign(a) cannot equal assign(b) (where a,b in Courses + Labs)

5. partassign: assign(a) must equal partassign(a) for all a in Courses + Labs with partassign(a) not equal to $

6. unwanted(a,s): assign(a) cannot equal s (with a in Courses + Labs and s in Slots)

7. All course sections with a section number starting LEC 9 are evening classes and have to be scheduled into evening slots (18:00 or later).

8. All 500-level course sections must be scheduled into different time slots.

9. No courses can be scheduled at Tuesdays 11:00-12:30.


	IF CPSC 313 IN COURSES:
10. CPSC 813 must be scheduled for Tuesdays/Thursdays 18:00-19:00 (note, they are scheduled into lab slots)
11. CPSC 813 cannot overlap with any labs/tutorials, or course sections of CPSC 313
12. CPSC 813 cannot overlap with any courses that cannot overlap with CPSC 313

	IF CPSC 413 IN COURSES:
13. CPSC 913 must be scheduled for Tuesdays/Thursdays 18:00-19:00 (note, they are scheduled into lab slots)
14. CPSC 913 cannot overlap with any labs/tutorials, or course sections of CPSC 413
15. CPSC 913 cannot overlap with any courses that cannot overlap with CPSC 413

16. Can we ignore these due to abstract slot representation? Probably should ask the prof
	-- If a course (course section) is put into a slot on Mondays, it has to be put into the corresponding time slots on Wednesdays and Fridays.
	-- If a course (course section) is put into a slot on Tuesdays, it has to be put into the corresponding time slots on Thursdays.
	-- If a lab/tutorial is put into a slot on Mondays, it has to be put into the corresponding time slots on Wednesdays.
	-- If a lab/tutorial is put into a slot on Tuesdays, it has to be put into the corresponding time slots on Thursdays.
*/
public class ConstraintChecker {



    public ConstraintChecker(ParseData parseData){

    }


    
}

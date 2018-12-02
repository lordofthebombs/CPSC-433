import ParseData.*;

import java.math.BigInteger;
import java.util.*;

import OrTree.*;
import Slot_Occupant.*;
import javafx.util.Pair;

public class Eval {
    private double pen_coursemin, pen_preference, pen_notpaired, pen_section;
    private Pairs pairs;
    private Preferences pref;
    private Map<String, List<Slot_Occupant>> courseSections;



    public Eval(ParseData data, double coursemin, double preference, double notpaired, double section) {
        pen_coursemin = coursemin;
        pen_preference = preference;
        pen_notpaired = notpaired;
        pen_section = section;
        pairs = data.Pairs;
        pref = data.Preferences;
        setCoursesSections(data);
    }


    /**
     *Helper method to map each course with all its sections
     * This is called only once on comstruction
     * @param data parsedData
     */
    private void setCoursesSections(ParseData data){
        Map<String, List<Slot_Occupant>> courseSections = new LinkedHashMap<>();

        for(Slot_Occupant course : data.Courses){
            String id = course.id + course.courseNum;
            if(courseSections.get(id) != null){
                List<Slot_Occupant> slot_occupants = courseSections.get(id);
                slot_occupants.add(course);
            }else{
                List<Slot_Occupant> slot_occupants = new ArrayList<>();
                slot_occupants.add(course);
                courseSections.put(id, slot_occupants);
            }
        }

        this.courseSections = courseSections;
    }


    /**
     * This is the main eval function that is called from setSearch
     *
     * @param solution solution with complete assignments
     * @return double total eval score
     */
    public double eval(Map<Slot_Occupant, Slot> solution) {

        return evalMinfilled(solution) * pen_coursemin +
                evalPref(solution) * pen_preference +
                evalPair(solution) * pen_notpaired +
                evalSecDiff(solution) * pen_section;

    }



    private int evalMinfilled(Map<Slot_Occupant, Slot> solution) {

        return 0;
    }


    /**
     * This calculates the penalty score for preferences
     * For each assignment in solution, we add up the preference-values for a course/lab
     * that refer to a different slot compared to Preferences given.
     * @param solution
     * @return double totalPreferencePenalty Score
     */
    double evalPref(Map<Slot_Occupant, Slot> solution) {

        double prefPenalty = 0;
        for (Map.Entry<Slot_Occupant, Slot> entry : solution.entrySet()) {
            if (!pref.isPreference(entry.getKey(), entry.getValue())) {
                HashSet<Slot> preferredWith = pref.getPreferredWith(entry.getKey());
                for (Slot slot : preferredWith) {
                    double val = pref.getPreferenceValue(entry.getKey(), slot);
                    prefPenalty += val;
                }
            }
        }

        return prefPenalty;

    }


    /**
     * This method calculates the number of violations for Pair list given
     * For every pair(a,b) statement, for which assign(a) is not equal to assign(b),
     * we add pen_notpaired to the Eval-value of assign.
     * @param solution
     * @return int total number of violation
     */
    int evalPair(Map<Slot_Occupant, Slot> solution) {
        int violationCounter = 0;
        HashSet<Pair<Slot_Occupant, Slot_Occupant>> pair_entries = pairs.getPair_Entries();
        for(Pair<Slot_Occupant, Slot_Occupant> pair : pair_entries){
            Slot leftCourseSlot = solution.get(pair.getKey());
            Slot rightCourseSlot = solution.get(pair.getValue());
            if(!leftCourseSlot.equals(rightCourseSlot)){
               violationCounter++;
            }
        }

        return violationCounter;
    }


    /**
     * This is the method to calculate penalty score for
     * For each pair of sections that is scheduled into the same slot,
     * we add a penalty pen_section to the Eval-value of an assignment assign.
     * @param solution
     * @return  total penalty score for all courses with different sections assigned to same slot
     */
     int evalSecDiff(Map<Slot_Occupant, Slot> solution) {

         int violationCounter = 0;

         // iterate through each course
         for (Map.Entry<String, List<Slot_Occupant>> sectionList : this.courseSections.entrySet()) {

             //course has more than one sec
             if (sectionList.getValue().size() > 1) {
                 Map<Slot, Integer> slotsForEachSec = new LinkedHashMap<>();

                 // for the lecture sections of the course
                 for (Slot_Occupant section : sectionList.getValue()) {
                     if (slotsForEachSec.get(solution.get(section)) != null) {
                         int counter = slotsForEachSec.get(solution.get(section));

                         // storing the slot and corresponding count of sections assigned to the slot
                         slotsForEachSec.put(solution.get(section), counter + 1);
                     } else {
                         slotsForEachSec.put(solution.get(section), 1);
                     }
                 }

                 //Since the doc says  For each pair of sections that is scheduled into the same slot,
                 //* we add a penalty pen_section to the Eval-value of an assignment assign.
                 //* i.e for a course with sec [1,2,3,4] and slots [s1, s2], the penalty for [(1-> S1),(2-> S1),(3-> S1),(4-> S1)]
                 //* should be 6 since the following pairs are violated [(1,2),(1,3),(1,4),(2,3),(2,4),(3,4)]
                 violationCounter+= calculateSecDiffCombinatorialPen(slotsForEachSec);
             }
         }

         return  violationCounter;
     }

    /**
     * Calculates total penalty for one course
     * Least penalty score is 0 when each slot has count of 1
     * Max penalty is the sum of nc2 of each slot count
     * @param slotsForEachSec
     * @return
     */
     private int calculateSecDiffCombinatorialPen(Map<Slot, Integer> slotsForEachSec){
        int totalPen = 0 ;
        for(int n : slotsForEachSec.values()){
            totalPen += nChoose2(n).intValue();
        }
         return totalPen;
     }

    /**
     * Using Pascal's Triangle to find n! /( K! * (n-K)!)
     * https://stackoverflow.com/questions/2201113/combinatoric-n-choose-r-in-java-math
     * @param N
     * @return ncr value in BigInteger
     */
     private  BigInteger nChoose2 (final int N) {
        BigInteger ret = BigInteger.ONE;
        for (int k = 0; k < 2; k++) {
            ret = ret.multiply(BigInteger.valueOf(N-k))
                    .divide(BigInteger.valueOf(k+1));
        }
        return ret;
    }
}

package SetBased;

import ParseData.*;

import java.math.BigInteger;
import java.util.*;

import Slot_Occupant.*;
import javafx.util.Pair;

public class Eval {
    private double pen_coursemin, pen_labmin, pen_notpaired, pen_section;
    private double w_minFilled, w_pref, w_notPaired, w_secDiff;
    private Pairs pairs;
    private Preferences pref;
    private Map<String, List<Slot_Occupant>> courseSections;



    public Eval(ParseData data,
                double minFilledWeight,
                double prefWeight,
                double notPairedWeight,
                double secDiffWeight,
                double penCourseMin,
                double penLabMin,
                double penNotPaired,
                double penSection) {
        w_minFilled = minFilledWeight;
        w_pref = prefWeight;
        w_notPaired = notPairedWeight;
        w_secDiff = secDiffWeight;
        pen_coursemin = penCourseMin;
        pen_labmin= penLabMin;
        pen_notpaired = penNotPaired;
        pen_section = penSection;
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

        return evalMinfilled(solution) * w_minFilled +
                evalPref(solution) * w_pref +
                evalPair(solution) * w_notPaired +
                evalSecDiff(solution) * w_secDiff;


    }



     // Counts the amount of times that the given solution does not meet the
  // coursemin soft constraint
  public int evalMinfilled(Map<Slot_Occupant, Slot> solution)) {
      // The penalty value that will be returned
      int pen = 0;
      // Counts the amount of courses that are in a particular slot
      int courseCounter;

      Set<Slot_Occupant> keys = solution.keySet();
      Slot_Occupant[] allOccupants = keys.toArray();

      Collection<Slot> values = solution.values();
      Slot[] allSlots = values.toArray();

    // Checks each slot and see how mnay courses occupy that slot
      for (int slot = 0; slot < allSlots.length; slot++) {
          courseCounter = 0;
          for (int occupant = 0; occupant < allOccupants.length; occupant++) {
              if (allSlots[slot].equals(solution.get(allOccupants[occupant]))) {
                  courseCounter++;
              }
          }
          if (courseCounter < allSlots[slot].min) {
              pen++;
          }
      }

      return pen;
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
     * This method calculates pen_notPaired * number of violations for Pair list given
     * For every pair(a,b) statement, for which assign(a) is not equal to assign(b),
     * we add pen_notpaired to the SetBased.Eval-value of assign.
     * @param solution
     * @return int total number of violation
     */
    double evalPair(Map<Slot_Occupant, Slot> solution) {
        int violationCounter = 0;
        HashSet<Pair<Slot_Occupant, Slot_Occupant>> pair_entries = pairs.getPair_Entries();
        for(Pair<Slot_Occupant, Slot_Occupant> pair : pair_entries){
            Slot leftCourseSlot = solution.get(pair.getKey());
            Slot rightCourseSlot = solution.get(pair.getValue());
            if(!leftCourseSlot.equals(rightCourseSlot)){
               violationCounter++;
            }
        }

        return violationCounter * pen_notpaired;
    }


    /**
     * This is the method to calculate penalty score for
     * For each pair of sections that is scheduled into the same slot,
     * we add a penalty pen_section to the SetBased.Eval-value of an assignment assign.
     * @param solution
     * @return  total penalty score for all courses with different sections assigned to same slot
     */
     double evalSecDiff(Map<Slot_Occupant, Slot> solution) {

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
                 //* we add a penalty pen_section to the SetBased.Eval-value of an assignment assign.
                 //* i.e for a course with sec [1,2,3,4] and slots [s1, s2], the penalty for [(1-> S1),(2-> S1),(3-> S1),(4-> S1)]
                 //* should be 6 since the following pairs are violated [(1,2),(1,3),(1,4),(2,3),(2,4),(3,4)]
                 violationCounter+= calculateSecDiffCombinatorialPen(slotsForEachSec);
             }
         }

         return  violationCounter * pen_section;
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

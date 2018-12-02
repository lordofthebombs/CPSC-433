package SetBased;

import ParseData.*;
import Slot_Occupant.*;
import Parser.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 *
 */


public class EvalTest {

    static ParseData parseData ;

    public void setUp(String fileName) throws FileNotFoundException {

        parseData = Parser.parse(fileName);
    }

    @Test
    public void eval_withAllSoftConstraintAndEqualWeights_shouldReturnExpected() throws FileNotFoundException {
        setUp("EvalTestFiles/allSoftConstraints.txt");

        // evalPref = 24.0
        // evalPair = 2
        //evalSecDiff = 1

        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        double expected = 27.0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));



        double result = eval.eval(sol);
        assertEquals(expected, result);
    }

    @Test
    public void eval_withAllSoftConstraintAndVaryingWeights_shouldReturnExpected() throws FileNotFoundException {
        setUp("EvalTestFiles/allSoftConstraints.txt");

        // evalPref = 24.0
        // evalPair = 2
        //evalSecDiff = 1

        int courseMin = 1;
        int prefWeight = 2;
        int notPairedWeight = 4;
        int diffSecWeight = 2;

        Eval eval = new Eval(parseData,
                courseMin,
                prefWeight,
                notPairedWeight,
                diffSecWeight);

        double expected = 58.0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));



        double result = eval.eval(sol);
        assertEquals(expected, result);
    }


    @Test
    public void eval_withAllSoftConstraintAndZeroWeights_shouldReturnExpected() throws FileNotFoundException {
        setUp("EvalTestFiles/allSoftConstraints.txt");

        // evalPref = 24.0
        // evalPair = 2
        //evalSecDiff = 1

        int courseMin = 1;
        int prefWeight = 2;
        int notPairedWeight = 4;
        int diffSecWeight = 2;

        Eval eval = new Eval(parseData,
                0,
                0,
                0,
                0);

        double expected = 0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));



        double result = eval.eval(sol);
        assertEquals(expected, result);
    }


    @Test
    public void evalPref_onlyMeetsTwoOutOf4Prefs_shouldReturnScoreOfTwoUnmet() throws FileNotFoundException {
        setUp("EvalTestFiles/2_4PrefMet.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        double expected = 20.0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
            for ( Slot_Occupant c : parseData.getOccupants()){
                sol.put(c, new Slot(Slot.Day.Mon, 8, 2, 1));
            }
            double result = eval.evalPref(sol);
           assertEquals(expected, result);
    }

    @Test
    public void evalPref_noPrefsGiven_shouldReturnScoreOfZero() throws FileNotFoundException {
        setUp("EvalTestFiles/noPrefsGiven.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        double expected = 0.0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        for ( Slot_Occupant c : parseData.getOccupants()){
            sol.put(c, new Slot(Slot.Day.Mon, 8, 2, 1));
        }
        double result = eval.evalPref(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPref_allPrefsMet_shouldReturnScoreOfZero() throws FileNotFoundException {
        setUp("EvalTestFiles/allPrefsMet.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        double expected = 0.0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        double result = eval.evalPref(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPref_PrefNotMetForASingleCourse_shouldReturnScoreOfAllPrefEntriesForThatCourse() throws FileNotFoundException {
        setUp("EvalTestFiles/multiplePrefEntriesForOne.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        double expected = 25.0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Mon, (float) 10 , 3, 2));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        double result = eval.evalPref(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPref_AllInvalidPref_shouldReturnScoreOfZero() throws FileNotFoundException {
        setUp("EvalTestFiles/allInvalidPref.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        double expected = 0.0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        double result = eval.evalPref(sol);
        assertEquals(expected, result);
    }


    @Test
    public void evalPair_allPairViolated_shouldReturnTotalSizeOfPairs() throws FileNotFoundException {
        setUp("EvalTestFiles/allPairsViolated.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 2;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Mon, (float) 9 , 3, 2));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalPair(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPair_noPairsGiven_shouldReturnZero() throws FileNotFoundException {
        setUp("EvalTestFiles/noPairs.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Mon, (float) 9 , 3, 2));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalPair(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPair_mutiplePairsForOneCourse_shouldReturnTotalViolationForThatCourse() throws FileNotFoundException {
        setUp("EvalTestFiles/multiplePairsForOne.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 3;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Mon, (float) 9 , 3, 2));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalPair(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPair_allPairsMet_shouldReturnZero() throws FileNotFoundException {
        setUp("EvalTestFiles/allPairsMet.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Mon, (float) 8 , 3, 2));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalPair(sol);
        assertEquals(expected, result);
    }


    @Test
    public void evalSecDiff_allSameSlotAssigned_returnTotalPenForEachPairOfSec() throws FileNotFoundException {
        setUp("EvalTestFiles/withOneCourseDiffSec.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 10;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(2), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(3), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(4), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalSecDiff(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalSecDiff_4outOf5SecSameSlotAssigned_returnTotalPenForEachPairNotMet() throws FileNotFoundException {
        setUp("EvalTestFiles/withOneCourseDiffSec.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 6;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(2), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(3), new Slot(Slot.Day.Tues, (float) 12.5 , 2, 1));
        sol.put(parseData.Courses.get(4), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalSecDiff(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalSecDiff_3Same2sameSlotsAssigned_returnTotalPenForEachPairNotMet() throws FileNotFoundException {
        setUp("EvalTestFiles/withOneCourseDiffSec.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 4;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(2), new Slot(Slot.Day.Tues, (float) 12.5 , 2, 1));
        sol.put(parseData.Courses.get(3), new Slot(Slot.Day.Tues, (float) 12.5 , 2, 1));
        sol.put(parseData.Courses.get(4), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalSecDiff(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalSecDiff_3Same2DiffSlotsAssigned_returnTotalPenForEachPairNotMet() throws FileNotFoundException {
        setUp("EvalTestFiles/withOneCourseDiffSec.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 3;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(2), new Slot(Slot.Day.Tues, (float) 12.5 , 2, 1));
        sol.put(parseData.Courses.get(3), new Slot(Slot.Day.Mon, (float) 8 , 2, 1));
        sol.put(parseData.Courses.get(4), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalSecDiff(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalSecDiff_3Diff2SameSlotsAssigned_returnTotalPenForEachPairNotMet() throws FileNotFoundException {
        setUp("EvalTestFiles/withOneCourseDiffSec.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 1;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(2), new Slot(Slot.Day.Tues, (float) 12.5 , 2, 1));
        sol.put(parseData.Courses.get(3), new Slot(Slot.Day.Mon, (float) 8 , 2, 1));
        sol.put(parseData.Courses.get(4), new Slot(Slot.Day.Tues, (float) 9 , 2, 2));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalSecDiff(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalSecDiff_withAllDiifSlotsAssigned_returnZero() throws FileNotFoundException {
        setUp("EvalTestFiles/withOneCourseDiffSec.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 15.5 , 5, 1));
        sol.put(parseData.Courses.get(2), new Slot(Slot.Day.Tues, (float) 12.5 , 2, 1));
        sol.put(parseData.Courses.get(3), new Slot(Slot.Day.Mon, (float) 8 , 2, 1));
        sol.put(parseData.Courses.get(4), new Slot(Slot.Day.Tues, (float) 9 , 2, 2));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalSecDiff(sol);
        assertEquals(expected, result);
    }


    @Test
    public void evalSecDiff_withAllSameSlotsForMultipleCourses_returnsTotalCombinations() throws FileNotFoundException {
        setUp("EvalTestFiles/withDiffCourseSec.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 10;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Mon, (float) 8 , 3, 2));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Mon, (float) 8 , 3, 2));
        sol.put(parseData.Courses.get(2), new Slot(Slot.Day.Mon, (float) 8 , 3, 2));
        sol.put(parseData.Courses.get(3), new Slot(Slot.Day.Tues, (float) 15.5 , 5, 1));
        sol.put(parseData.Courses.get(4), new Slot(Slot.Day.Tues, (float) 15.5 , 5, 1));
        sol.put(parseData.Courses.get(5), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(6), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(7), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(8), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));

        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalSecDiff(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalSecDiff_withAllDiffSlotsForMultipleCourses_returnsTotalCombinations() throws FileNotFoundException {
        setUp("EvalTestFiles/withDiffCourseSec.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Mon, (float) 8 , 3, 2));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Mon, (float) 9 , 3, 2));
        sol.put(parseData.Courses.get(2), new Slot(Slot.Day.Tues, (float) 15.5 , 3, 2));
        sol.put(parseData.Courses.get(3), new Slot(Slot.Day.Tues, (float) 15.5 , 5, 1));
        sol.put(parseData.Courses.get(4), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(5), new Slot(Slot.Day.Tues, (float) 14 , 5, 1));
        sol.put(parseData.Courses.get(6), new Slot(Slot.Day.Tues, (float) 15.5 , 5, 1));
        sol.put(parseData.Courses.get(7), new Slot(Slot.Day.Mon, (float) 8 , 3, 2));
        sol.put(parseData.Courses.get(8), new Slot(Slot.Day.Mon, (float) 9 , 3, 2));

        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalSecDiff(sol);
        assertEquals(expected, result);
    }




}

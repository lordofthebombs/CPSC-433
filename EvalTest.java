
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
    public void evalPref_onlyMeetsTwoOutOf4Prefs_shouldReturnScoreOfTwoUnmet() throws FileNotFoundException {
        setUp("TestFiles/2_4PrefMet.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 20;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
            for ( Slot_Occupant c : parseData.getOccupants()){
                sol.put(c, new Slot(Slot.Day.Mon, 8, 2, 1));
            }
            int result = eval.evalPref(sol);
           assertEquals(expected, result);
    }

    @Test
    public void evalPref_noPrefsGiven_shouldReturnScoreOfZero() throws FileNotFoundException {
        setUp("TestFiles/noPrefsGiven.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        for ( Slot_Occupant c : parseData.getOccupants()){
            sol.put(c, new Slot(Slot.Day.Mon, 8, 2, 1));
        }
        int result = eval.evalPref(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPref_allPrefsMet_shouldReturnScoreOfZero() throws FileNotFoundException {
        setUp("TestFiles/allPrefsMet.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalPref(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPref_PrefNotMetForASingleCourse_shouldReturnScoreOfAllPrefEntriesForThatCourse() throws FileNotFoundException {
        setUp("TestFiles/multiplePrefEntriesForOne.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 25;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Mon, (float) 10 , 3, 2));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalPref(sol);
        assertEquals(expected, result);
    }

    @Test
    public void evalPref_AllInvalidPref_shouldReturnScoreOfZero() throws FileNotFoundException {
        setUp("TestFiles/allInvalidPref.txt");
        Eval eval = new Eval(parseData, 1, 1, 1, 1);

        int expected = 0;
        Map<Slot_Occupant, Slot> sol = new LinkedHashMap<>();
        sol.put(parseData.Courses.get(0), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Courses.get(1), new Slot(Slot.Day.Tues, (float) 9.5 , 2, 1));
        sol.put(parseData.Labs.get(0), new Slot(Slot.Day.Tues, (float) 10 , 2, 1));
        sol.put(parseData.Labs.get(1), new Slot(Slot.Day.Mon, (float) 8 , 4, 2));

        int result = eval.evalPref(sol);
        assertEquals(expected, result);
    }


    @Test
    public void evalPair_allPairViolated_shouldReturnTotalSizeOfPairs() throws FileNotFoundException {
        setUp("TestFiles/allPairsViolated.txt");
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
        setUp("TestFiles/noPairs.txt");
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
        setUp("TestFiles/multiplePairsForOne.txt");
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
        setUp("TestFiles/allPairsMet.txt");
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






}

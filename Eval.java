import ParseData.*;

import java.util.*;

import OrTree.*;
import Slot_Occupant.Slot_Occupant;

public class Eval {
    private double pen_coursemin, pen_preference, pen_notpaired, pen_section;
    private Pairs pairs;
    private Preferences pref;

    public Eval(ParseData data, double coursemin, double preference, double notpaired, double section) {
        pen_coursemin = coursemin;
        pen_preference = preference;
        pen_notpaired = notpaired;
        pen_section = section;
        pairs = data.Pairs;
        pref = data.Preferences;
    }

    public double eval(Map<Slot_Occupant, Slot> solution) {

        return evalMinfilled(solution) * pen_coursemin +
                evalPref(solution) * pen_preference +
                evalPair(solution) * pen_notpaired +
                evalSecDiff(solution) * pen_section;

    }


    private int evalMinfilled(Map<Slot_Occupant, Slot> solution) {

        return 0;
    }

    public int evalPref(Map<Slot_Occupant, Slot> solution) {

        int prefPenalty = 0;
        for (Map.Entry<Slot_Occupant, Slot> entry : solution.entrySet()) {
            if (!pref.isPreference(entry.getKey(), entry.getValue())) {
                HashSet<Slot> preferredWith = pref.getPreferredWith(entry.getKey());
                for (Slot slot : preferredWith) {
                    int val = pref.getPreferenceValue(entry.getKey(), slot);
                    prefPenalty += val;
                }
            }
        }

        return prefPenalty;

    }

    private int evalPair(Map<Slot_Occupant, Slot> solution) {
        return 0;
    }

    private int evalSecDiff(Map<Slot_Occupant, Slot> solution) {
        return 0;
    }
}

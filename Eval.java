import ParseData.*;
import java.util.*;
import OrTree.*;

public class Eval{
  private double pen_coursemin, pen_preference, pen_notpaied, pen_section;
  private Pairs pairs;
  private Preferences pref;

  public Eval(ParseData data, double coursemin, double preference, double notpaired, double section){
    pen_coursemin = coursemin;
    pen_preference = preference;
    pen_notpaired = notpaired;
    pen_section = section;
    //pairs = data.Pairs;
    //pref = data.Preferences;
  }

  public double eval(Map<Slot_Occupant, Slot> solution){
      return 0;
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

  private int evalPref(Map<Slot_Occupant, Slot> solution)) {
      return 0;
  }

  private int evalPair(Map<Slot_Occupant, Slot> solution)) {
      return 0;
  }

  private int evalSecDiff(Map<Slot_Occupant, Slot> solution)) {
      return 0;
  }

}

import ParseData.*;
import java.util.*;
import OrTree.*;

public class Eval{
  private double pen_labsmin, pen_coursemin, pen_notpaired, pen_section;
  private Pairs pairs;
  private Preferences pref;

  public Eval(ParseData data, double labsmin, double coursemin, double notpaired, double section){
    pen_labsmin = labsmin;
    pen_coursemin = coursemin;
    pen_notpaired = notpaired;
    pen_section = section;
    pairs = data.Pairs;
    pref = data.Preferences;
  }

  public double eval(Map<Slot_Occupant, Slot> solution){

      return 0;

  }


  private int evalMinfilled(Map<Slot_Occupant, Slot> solution)) {

  }

  private int evalPref(Map<Slot_Occupant, Slot> solution)) {

  }

  private int evalPair(Map<Slot_Occupant, Slot> solution)) {

  }

  private int evalSecDiff(Map<Slot_Occupant, Slot> solution)) {

  }
}

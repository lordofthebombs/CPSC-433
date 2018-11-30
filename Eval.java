import ParseData.*;

public Eval{
  private double pen_labsmin, pen_coursemin, pen_notpaired, pen_section
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

  public int eval(Map<Slot_Occupant, Slot> solution){
    



  }

}

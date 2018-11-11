package Slot_Occupant;

public class Lab extends Slot_Occupant {

    public int labSect;     // The lab section i.e. 01, 02 etc.

    //Constructors
    public Lab(String id, int courseNum, int lectSection, int labSect) {
        super(id, courseNum, lectSection);
        this.labSect = labSect;
    }
}

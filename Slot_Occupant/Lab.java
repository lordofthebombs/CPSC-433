package Slot_Occupant;

import java.util.Objects;

public class Lab extends Slot_Occupant {

    public int labSect;     // The lab section i.e. 01, 02 etc.

    //Constructors
    public Lab(String id, int courseNum, int lectSection, int labSect) {
        super(id, courseNum, lectSection);
        this.labSect = labSect;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Lab)) {
            return false;
        }
        Lab otherLab = (Lab) o;
        return id.equals(otherLab.id) &&
                courseNum == otherLab.courseNum &&
                lectSection == otherLab.lectSection &&
                labSect == otherLab.labSect;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseNum, lectSection, labSect);
    }

    @Override
    public String toString(){
        return this.id + courseNum + "LEC " + lectSection + " LAB " + labSect;
    }
}

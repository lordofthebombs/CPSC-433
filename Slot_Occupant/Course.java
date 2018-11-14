package Slot_Occupant;

import java.util.Objects;

public class Course extends Slot_Occupant{

    //Essentially a dummy class
    public Course(String id, int courseNum, int lectSection){super(id, courseNum, lectSection);}


    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Course)) {
            return false;
        }
        Course otherCourse = (Course) o;
        return id.equals(otherCourse.id) &&
                courseNum == otherCourse.courseNum &&
                lectSection == otherCourse.lectSection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseNum, lectSection);
    }

    @Override
    public String toString(){
        return this.id + courseNum + "LEC " + lectSection;
    }

}

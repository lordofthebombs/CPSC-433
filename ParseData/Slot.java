package ParseData;

import java.sql.Time;
import java.util.Objects;

import Slot_Occupant.Course;



public class Slot {

    public Day day;          //The day of the week the slot starts on
    public float time;       //Time represented as a decimal 24hr. i.e 1:30pm = 13.5
    public int max;          //The max that this slot can hold for courses (remember slots are separated into two differed lists, one for courses, and one for labs)
    public int min;          //The min that this slot should hold. (Soft Constraint)

    public enum Day{
        Mon,
        Tues,
        Wed,
        Thur,
        Fri
    };
    
    
    public Slot(Day day, float time, int max, int min){
        this.day = day;
        this.time = time;
        this.max = max;
        this.min = min;
    }

    @Override
    public String toString(){
        return this.day + ", " + this.time;
    }
    
    @Override
    public boolean equals(Object o) {
    	
        if (o == this) return true;
        if (!(o instanceof Slot)) {
            return false;
        }
        Slot otherSlot = (Slot) o;
        return  this.day == otherSlot.day &&
                this.time == otherSlot.time;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(day, time, max, min);
    }
    
}

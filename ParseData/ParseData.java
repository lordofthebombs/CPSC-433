package ParseData;

import Slot_Occupant.Course;
import Slot_Occupant.Lab;

import java.util.Vector;

public class ParseData {

    //Reminder: I might need to write my own (pair) class for the dataClasses so I can override the Equals method.

    public Vector<Course> Courses;
    public Vector<Lab> Labs;
    public Vector<Slot> Course_Slots;
    public Vector<Slot> Lab_Slots;

    public Non_Compatable Non_Compat;
    public Unwanted Unwanted;
    public Preferences Preferences;
    public Pairs Pairs;
    public Partial_Assignments Partial_Assignments;

    public ParseData(){
        this.Non_Compat = new Non_Compatable();
        this.Unwanted = new Unwanted();
        this.Preferences = new Preferences();
        this.Pairs = new Pairs();
        this.Partial_Assignments = new Partial_Assignments();
    }

    public void setCourses(Vector<Course> courses) {
        this.Courses = courses;
    }

    public void setLabs(Vector<Lab> labs) {
        this.Labs = labs;
    }

    public void setCourse_Slots(Vector<Slot> Course_Slots){
        this.Course_Slots = Course_Slots;
    }

    public void setLab_Slots(Vector<Slot> Lab_Slots){
        this.Lab_Slots = Lab_Slots;
    }


}

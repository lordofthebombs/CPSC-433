package Slot_Occupant;


abstract public class Slot_Occupant {

    public int lectSection; //The int representing the section number i.e. 01 , 02
    public String id;       //The string representing the Id, i.e. SENG or CPSC
    public int courseNum;   //The int represting the course numbered i.e 433 for CPSC 433

    //All "Slot_Occcupants" Need this data, there should not be an empty constructor.
    public Slot_Occupant( String id, int courseNum, int lectSection){
        this.courseNum = courseNum;
        this.lectSection = lectSection;
        this.id = id;
    }

    @Override
    public String toString(){
        return this.id + " " + this.courseNum + " " + this.lectSection;
    }

}

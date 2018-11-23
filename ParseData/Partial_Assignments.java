package ParseData;

import Slot_Occupant.Slot_Occupant;

import java.util.HashMap;

public class Partial_Assignments {

    //This is a fairly lazy way to do this, but is effectively what a bimap is, except less space efficient.
    //This is quite a bit faster than the hashset however;
    private HashMap<Slot,Slot_Occupant> Partial_Entries_S_SO;
    private HashMap<Slot_Occupant,Slot> Partial_Entries_SO_S;

    public Partial_Assignments(){
        Partial_Entries_S_SO = new HashMap<>();
        Partial_Entries_SO_S = new HashMap<>();
    }

    //Gets the Occupant for a partial assignment, returns null if no slot occupant exists.
    public Slot_Occupant getOccupant(Slot a){
       return Partial_Entries_S_SO.get(a);
    }

    public Slot getSlot(Slot_Occupant a){
        return Partial_Entries_SO_S.get(a);
    }

    //Gets the slot for a partial assignment, returns null if no slot occupant exists.
    public boolean isAssigned(Slot_Occupant a){
       return Partial_Entries_SO_S.containsKey(a);
    }

    public boolean isAssigned(Slot a){
        return Partial_Entries_S_SO.containsKey(a);
    }

    public boolean isPartialEntry(Slot_Occupant a, Slot b){
        return (this.Partial_Entries_SO_S.get(a) == b);
    }

    public boolean addEntry(Slot_Occupant a, Slot b){

        boolean s = true;

        if(null != Partial_Entries_SO_S.put(a,b)){s = false;}
        if(null != Partial_Entries_S_SO.put(b,a)){s = false;}

        return s;
    }

    public boolean removeEntry(Slot_Occupant a, Slot b){
        boolean s = true;
        s = Partial_Entries_SO_S.remove(a,b);
        s = Partial_Entries_S_SO.remove(b,a);

        return s;
    }

    public HashMap<Slot_Occupant,Slot> getAllPartialAssignments(){
        return Partial_Entries_SO_S;
    }
}

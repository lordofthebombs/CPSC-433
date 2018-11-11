package ParseData;

import Slot_Occupant.Slot_Occupant;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Partial_Assignments {

    private HashSet<Pair<Slot_Occupant, Slot>> Partial_Entries;

    public Partial_Assignments(){
        Partial_Entries = new HashSet<>();
    }


    //Gets the Occupant for a partial assignment, returns null if no slot occupant exists.
    public Slot_Occupant getOccupant(Slot a){

        Iterator<Pair<Slot_Occupant, Slot>> iter =  Partial_Entries.iterator();

        while(iter.hasNext()){

            Pair<Slot_Occupant, Slot> temp = iter.next();

            if(a.equals(temp.getValue())){
                return temp.getKey();
            }
        }

        return null;
    }

    //Gets the slot for a partial assignment, returns null if no slot occupant exists.
    public Slot getSlot(Slot_Occupant a){

        Iterator<Pair<Slot_Occupant, Slot>> iter =  Partial_Entries.iterator();

        while(iter.hasNext()){

            Pair<Slot_Occupant, Slot> temp = iter.next();

            if(a.equals(temp.getKey())){
                return temp.getValue();
            }
        }

        return null;

    }

    //Note this won't be in any particular order, returns an array of pairs of partial assignments.
    public Pair<Slot_Occupant, Slot>[] getAll(){

        //This might need to be checked
        return (Pair<Slot_Occupant, Slot>[])Partial_Entries.toArray();

    }
    public boolean isPartialEntry(Slot_Occupant a, Slot b){
        return this.Partial_Entries.contains(newPair(a,b));
    }

    public boolean addEntry(Slot_Occupant a, Slot b){
        return this.Partial_Entries.add(newPair(a,b));
    }

    public boolean removeEntry(Slot_Occupant a, Slot b){
        return this.Partial_Entries.remove(newPair(a,b));
    }

    private Pair<Slot_Occupant,Slot> newPair(Slot_Occupant a, Slot b){
        return new Pair<Slot_Occupant, Slot>(a,b);
    }

}

package ParseData;

import Slot_Occupant.Slot_Occupant;
import javafx.util.Pair;
import java.util.HashSet;
import java.util.Iterator;


public class Unwanted {

    private HashSet<Pair<Slot_Occupant, Slot>> Unwanted_Entries;

    public Unwanted(){
        Unwanted_Entries = new HashSet<>();
    }


    //Returns a HashSet of Slot Occupant entries that are Unwanted with a given Slot a
    public HashSet<Slot_Occupant> isUnwantedWith(Slot a){

        HashSet<Slot_Occupant> UnwantedWith = new HashSet<Slot_Occupant>();

        Iterator<Pair<Slot_Occupant, Slot>> iter =  Unwanted_Entries.iterator();

        while(iter.hasNext()){

            Pair<Slot_Occupant, Slot> temp = iter.next();

            if(a.equals(temp.getValue())){
                UnwantedWith.add(temp.getKey());
            }
        }

        return UnwantedWith;
    }

    //Gets the slot for a partial assignment, returns null if no slot occupant exists.
    public HashSet<Slot> isUnwantedWith(Slot_Occupant a){

        HashSet<Slot> UnwantedWith = new HashSet<Slot>();

        Iterator<Pair<Slot_Occupant, Slot>> iter =  Unwanted_Entries.iterator();

        while(iter.hasNext()){

            Pair<Slot_Occupant, Slot> temp = iter.next();

            if(a.equals(temp.getKey())){
                UnwantedWith.add(temp.getValue());
            }
        }

        return UnwantedWith;
    }

    //Returns an array of all Uwanted Pairs
    public Pair<Slot_Occupant, Slot>[] getAll(){

        //This might need to be checked for functionality /todo
        return (Pair<Slot_Occupant, Slot>[])Unwanted_Entries.toArray();
    }

    public boolean isUnwanted(Slot_Occupant a, Slot b){
        return this.Unwanted_Entries.contains(newPair(a,b));
    }

    public boolean addEntry(Slot_Occupant a, Slot b){
        return this.Unwanted_Entries.add(newPair(a,b));
    }

    public boolean removeEntry(Slot_Occupant a, Slot b){
        return this.Unwanted_Entries.remove(newPair(a,b));
    }

    private Pair<Slot_Occupant,Slot> newPair(Slot_Occupant a, Slot b){
        return new Pair<Slot_Occupant, Slot>(a,b);
    }

}

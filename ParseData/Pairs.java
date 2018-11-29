package ParseData;

import Slot_Occupant.Slot_Occupant;

import java.util.HashSet;
import java.util.Iterator;
import javafx.util.Pair;

public class Pairs {

    private HashSet<Pair<Slot_Occupant, Slot_Occupant>> Pair_Entries;

    public Pairs(){
        Pair_Entries = new HashSet<>();
    }

    //Returns a HashSet (which is a list) of all other courses it is paired with, this probably shouldn't be used
    public HashSet<Slot_Occupant> isPairedWith(Slot_Occupant a){

        HashSet<Slot_Occupant> pairedWith = new HashSet<Slot_Occupant>();

        Iterator<Pair<Slot_Occupant, Slot_Occupant>> iter =  Pair_Entries.iterator();

        while(iter.hasNext()){

            Pair<Slot_Occupant, Slot_Occupant> temp = iter.next();

            if(a.equals(temp.getKey())){
                pairedWith.add(temp.getValue());
            }
        }

        return pairedWith;
    }

    public boolean isPaired(Slot_Occupant a, Slot_Occupant b){
        return this.Pair_Entries.contains(newPair(a,b));
    }

    public boolean addEntry(Slot_Occupant a, Slot_Occupant b){
        return this.Pair_Entries.add(newPair(a,b));
    }

    public boolean removeEntry(Slot_Occupant a, Slot_Occupant b){
        return this.Pair_Entries.remove(newPair(a,b));
    }

    private Pair<Slot_Occupant,Slot_Occupant> newPair(Slot_Occupant a, Slot_Occupant b){
        return new Pair<>(a,b);
    }

}

package ParseData;

import Slot_Occupant.Slot_Occupant;
import java.util.HashSet;
import java.util.Iterator;

/*
    Object that has simple functionality, determines ether or not Clashes between Courses, and Labs exist.
    Use of the 'Compatible(x,y)' function where x and y are of type Slot_Occupant.

 */
public class Non_Compatable {

    private HashSet<Pair<Slot_Occupant, Slot_Occupant>> Non_Compatable_Entries;

    public Non_Compatable(){
        Non_Compatable_Entries = new HashSet<>();
    }

    //Returns a HashSet (which is a list) of all other courses Not_Compatable with, this probably shouldn't be used
    public HashSet<Slot_Occupant> isNonCompatableWith(Slot_Occupant a){

        HashSet<Slot_Occupant> NonCompatableWith = new HashSet<Slot_Occupant>();

        Iterator<Pair<Slot_Occupant, Slot_Occupant>> iter =  Non_Compatable_Entries.iterator();

        while(iter.hasNext()){

            Pair<Slot_Occupant, Slot_Occupant> temp = iter.next();

            if(a.equals(temp.getLeft())){
                NonCompatableWith.add(temp.getRight());
            }
        }

        return NonCompatableWith;
    }

    public boolean Compatible(Slot_Occupant a, Slot_Occupant b){
        return this.Non_Compatable_Entries.contains(newPair(a,b));
    }

    public boolean addEntry(Slot_Occupant a, Slot_Occupant b){
        return this.Non_Compatable_Entries.add(newPair(a,b));
    }

    public boolean removeEntry(Slot_Occupant a, Slot_Occupant b){
        return this.Non_Compatable_Entries.remove(newPair(a,b));
    }

    private Pair<Slot_Occupant,Slot_Occupant> newPair(Slot_Occupant a, Slot_Occupant b){
        return new Pair<>(a,b);
    }
}


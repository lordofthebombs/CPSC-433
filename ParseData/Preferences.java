package ParseData;

import Slot_Occupant.Slot_Occupant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Preferences {

    private HashMap<Pair<Slot_Occupant, Slot>, Integer> Preference_Entries;

    public Preferences(){
        Preference_Entries = new HashMap<>();
    }

    //Get the Occupant of the slot if it exists returns null otherwise.
    public Slot_Occupant getPreference(Slot a){

        Iterator<Map.Entry<Pair<Slot_Occupant, Slot>, Integer>> iter = Preference_Entries.entrySet().iterator();

        while(iter.hasNext()){

            Map.Entry<Pair<Slot_Occupant, Slot>, Integer> temp = iter.next();
            Pair<Slot_Occupant, Slot> p = temp.getKey();

            if(a.equals(p.getRight())){
                return p.getLeft();
            }

        }

        return null;
    }

    //Get the Slot of a Slot Occupant if the given Slot
    public Slot getPreference(Slot_Occupant a){

        Iterator<Map.Entry<Pair<Slot_Occupant, Slot>, Integer>> iter = Preference_Entries.entrySet().iterator();

        while(iter.hasNext()){

            Map.Entry<Pair<Slot_Occupant, Slot>, Integer> temp = iter.next();
            Pair<Slot_Occupant, Slot> p = temp.getKey();

            if(a.equals(p.getLeft())){
                return p.getRight();
            }

        }

        return null;
    }

    //gets the entry in the table, if it does not exist 0 is returned.
    public int getPreferenceValue(Slot_Occupant a, Slot b){

        Integer pref = Preference_Entries.get(newPair(a,b));

        if(pref == null) //there is no preference
            pref = 0;

        return pref;
    }

    public boolean isPreference(Slot_Occupant a, Slot b){
        return this.Preference_Entries.containsKey(newPair(a,b));
    }

    public boolean addEntry(Slot_Occupant a, Slot b, int prefValue){
        if(null == this.Preference_Entries.put(newPair(a,b), prefValue)){ //returns null only on new entries
            return true;
        }
        return false;
    }

    public boolean removeEntry(Slot_Occupant a, Slot b){

        if(null != this.Preference_Entries.remove(newPair(a,b))){  //returns null if there was no entry
            return true;
        }
        return false;
    }

    private Pair<Slot_Occupant,Slot> newPair(Slot_Occupant a, Slot b){
        return new Pair<>(a,b);
    }

}

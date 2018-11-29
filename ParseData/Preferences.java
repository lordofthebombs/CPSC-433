package ParseData;

import Slot_Occupant.Slot_Occupant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javafx.util.Pair;

public class Preferences {

    private HashMap<Pair<Slot_Occupant, Slot>, Integer> Preference_Entries;

    public Preferences(){
        Preference_Entries = new HashMap<>();
    }

    //Get slots that are preferred with a given Slot_Occupant if none exists returns null
    public HashSet<Slot> getPreferredWith(Slot_Occupant a){

        HashSet<Slot> preferredWith = new HashSet<>();

        for(Pair<Slot_Occupant,Slot> p : Preference_Entries.keySet()){
            if(p.getKey().equals(a)){ preferredWith.add(p.getValue()); }
        }

        return preferredWith;
    }
    public HashSet<Slot_Occupant> getPreferredWith(Slot a){

        HashSet<Slot_Occupant> preferredWith = new HashSet<>();

        for(Pair<Slot_Occupant,Slot> p : Preference_Entries.keySet()){
            if(p.getValue().equals(a)){ preferredWith.add(p.getKey()); }
        }

        return preferredWith;
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

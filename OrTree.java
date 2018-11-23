import ParseData.ParseData;
import ParseData.Partial_Assignments;
import ParseData.Slot;
import Slot_Occupant.Course;
import Slot_Occupant.Slot_Occupant;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class OrTree {

    private Map<Slot_Occupant, Slot> data;
    private OrTree parent;
    private List<OrTree> children = new ArrayList<>();
    private ParseData parseData;

    public OrTree(Map<Slot_Occupant, Slot> data){
    }
    public OrTree(ParseData parseData){
    }
    void addChild(Map<Slot_Occupant, Slot> data){
    }
    public void altern(Slot_Occupant slotOccupant){
    }
    private Map<Slot_Occupant, Slot> buildValidCandidateSolution(){
    }
    private Map<Slot_Occupant, Slot> mutateParentSolution( Map<Slot_Occupant, Slot> parentData){
        return null;
    }

}

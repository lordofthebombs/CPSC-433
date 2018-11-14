import ParseData.ParseData;
import ParseData.Partial_Assignments;
import ParseData.Slot;
import Slot_Occupant.Course;
import Slot_Occupant.Slot_Occupant;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class OrTree {

    private Map<Slot_Occupant, Slot> data;
    private OrTree parent;
    private List<OrTree> children = new ArrayList<>();
    private ParseData parseData;



    public OrTree(Map<Slot_Occupant, Slot> data){
        this.data = data;

    }
    public OrTree(ParseData parseData){
        this.parseData = parseData;
        initializePr();
    }


    private void addChild(Map<Slot_Occupant, Slot> data){
        OrTree child = new OrTree(data);
        child.parent = this;
        this.children.add(child);
    }

    private void initializePr(){
        Vector<Slot_Occupant> all = parseData.Courses;
        all.addAll(parseData.Labs);

        Map<Slot_Occupant, Slot> data = all.stream()
                .collect(Collectors.toMap( item -> item , null));

        Pair<Slot_Occupant, Slot>[] partialAssignmentsAll = this.parseData.Partial_Assignments.getAll();

        for(Pair<Slot_Occupant, Slot> assignment : partialAssignmentsAll){
            data.computeIfPresent( assignment.getKey(), (k, v) -> assignment.getValue());
        }

        this.data = data;
    }

    private void altern(OrTree parent) {
    }

    private boolean isPrSolved(OrTree leaf){


        return false;
    }


    private Map<Slot_Occupant, Slot> buildValidCandidateSolution(){

        return null;
    }


    private Map<Slot_Occupant, Slot> mutateParentSolution( Map<Slot_Occupant, Slot> parentData){

        return null;
    }



}

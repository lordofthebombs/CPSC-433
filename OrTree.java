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


    /**
     * Constructor for using within this class
     * meant to use for creating a child node
     * @param data
     */
    public OrTree(Map<Slot_Occupant, Slot> data){
        this.data = data;
    }


    /**
     * Main Constructor for initializing the OrTree
     * @param parseData
     */
    public OrTree(ParseData parseData){
        this.parseData = parseData;
        initializePr();
    }


    /**
     * Method to add a child to a parent node
     * @param data
     */
    private void addChild(Map<Slot_Occupant, Slot> data){
        OrTree child = new OrTree(data);
        child.parent = this;
        this.children.add(child);
    }

    /**
     * This method will create the template for Candidate solution
     * The size of the map will be equal to Courses + Labs
     * The slot value for each course or lab will be null unless there is a partial assignment
     */
    private void initializePr(){
        Vector<Slot_Occupant> all = parseData.Courses;
        all.addAll(parseData.Labs);

        Map<Slot_Occupant, Slot> data = new LinkedHashMap<>();
        all.forEach((item) -> data.put(item, null));


        HashMap<Slot_Occupant, Slot> allPartialAssignments = this.parseData.Partial_Assignments.getAllPartialAssignments();

        for(Map.Entry<Slot_Occupant, Slot> assignment : allPartialAssignments.entrySet()){
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


    /**
     * prints the tree in breadth-first traversal
     * added this for debugging
     * @return
     */
    @Override
    public String toString(){
        Queue<OrTree> queue = new LinkedList<OrTree>();
        queue.add(this);
        StringBuilder stringBuilder = new StringBuilder();
        while (!queue.isEmpty())
        {

            // poll() removes the present head.
            OrTree tempNode = queue.poll();
            stringBuilder.append(getStringFormData(tempNode.data) + " ");

            /*Enqueue all children */
            if (!tempNode.children.isEmpty()) {
                queue.addAll(tempNode.children);
            }
        }

        return stringBuilder.toString();
    }

    String getStringFormData(Map<Slot_Occupant, Slot> data){

        StringBuilder sb = new StringBuilder();
        data.entrySet().stream().forEach( entry -> sb.append(entry.getKey() + ": " + entry.getValue() + "\n"));
        return sb.toString();
    }


}

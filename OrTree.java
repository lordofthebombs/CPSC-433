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

    //private Map<Slot_Occupant, Slot> data;
	private Map<Slot, Vector<Slot_Occupant>> data;
    private OrTree parent;
    private List<OrTree> children = new ArrayList<>();
    private ParseData parseData;


    /**
     * Constructor for using within this class
     * meant to use for creating a child node
     * @param data
     */
    public OrTree(Map<Slot, Vector<Slot_Occupant>> data){
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
    void addChild(Map<Slot, Vector<Slot_Occupant>> data){
        OrTree child = new OrTree(data);
        child.parent = this;
        this.children.add(child);
        this.data = data;
    }

    /**
     * This method will create the template for Candidate solution
     * The size of the map will be equal to Courses + Labs
     * The slot value for each course or lab will be null unless there is a partial assignment
     */
    private void initializePr(){
        //Vector<Slot_Occupant> all = parseData.Courses;
       // all.addAll(parseData.Labs);
    	Vector<Slot> allSlots = parseData.Course_Slots;
    	allSlots.addAll(parseData.Lab_Slots);

        Map<Slot, Vector<Slot_Occupant>> data = new LinkedHashMap<>();
        allSlots.forEach((item) -> data.put(item, new Vector<Slot_Occupant>()));


        HashMap<Slot_Occupant, Slot> allPartialAssignments = this.parseData.Partial_Assignments.getAllPartialAssignments();

        for(Map.Entry<Slot_Occupant, Slot> assignment : allPartialAssignments.entrySet()){
            if(data.containsKey(assignment.getValue())){
            	Vector<Slot_Occupant> dataValue = data.get(assignment.getValue());	// adds the slot occupant to the corresponding vector
            	dataValue.add(assignment.getKey());
                data.put(assignment.getValue(), dataValue);
            }
        }

        this.data = data;
    }

    /**
     * Find all possible slots, s, applicable to the Slot Occupant in the parameter.
     * Then, for each slot, create a child OrTree such that data.get(slotOccupant) = slot
     * @param
     */
    // shouldnt be altern, should be fLeaf
    public void altern(Slot_Occupant slotOccupant) {

        boolean isSlotOccupantCourse = slotOccupant instanceof Course ;

        Vector<Slot> slots = isSlotOccupantCourse ? parseData.Course_Slots : parseData.Lab_Slots;
        

        // Create successor nodes:
        for (Slot slot : slots){
            Map<Slot, Vector<Slot_Occupant>> new_candidate = new LinkedHashMap<>();
           // new_candidate.putAll(this.data);
            //Vector<Slot_Occupant> slotsVector = data.get(slot);
            
            
            //new_candidate.put(slotOccupant, slot);
            if (isSlotOccupantCourse) {
                //checkHardConstraint(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Course so, );

            } else {
                //checkHardConstraint(Map<Slot, Vector<Slot_Occupant>> data, Slot s, Lab so, );

            }
        
            // add it to the current node's children
            /* -------need to check constraints here before adding as a child  ----*/
        }
        
        // add new slot occupant to vector of a slot

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

    String getStringFormData(Map<Slot, Vector<Slot_Occupant>> data){

        StringBuilder sb = new StringBuilder();
        data.entrySet().stream().forEach( entry -> sb.append(entry.getKey() + ": " + entry.getValue() + "\n"));
        return sb.toString();
    }


}

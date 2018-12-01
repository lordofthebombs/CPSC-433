package OrTree;

import ParseData.ParseData;
import ParseData.Slot;
import Slot_Occupant.*;

import java.util.*;


public class OrTreeSearch {

    public class OrTreeNode{

        private ArrayList<Slot> possibleSlots;
        private HashMap<Slot,OrTreeNode> transitions;

        private OrTreeNode parent; 
        private boolean initialized;
        
        public OrTreeNode(){
        	this.initialized = false; 
            possibleSlots = new ArrayList<>();
            transitions = new HashMap<>();
            this.parent = null;
           
        }
        
        public OrTreeNode(OrTreeNode parent)
        {
        	this.initialized = false; 
            possibleSlots = new ArrayList<>();
            transitions = new HashMap<>();
            this.parent = parent; 
        }
    }

    private OrTreeNode root;
    private ParseData parseData;
    private ConstraintChecker constraints;
    private Random randGen;
    private Slot lastTried;
    private Slot_Occupant OccupantToMutate;
    private Vector<Slot> AllSlots;
    private Vector<Slot_Occupant> AllOccupants;

    public OrTreeSearch(ParseData parseData){
        this.parseData = parseData;
        root = new OrTreeNode();
        constraints = new ConstraintChecker(parseData);
        randGen = new Random();
        AllSlots = parseData.getSlots();
        AllOccupants = parseData.getOccupants();
    }

    /**
     * This function will get all the possible slots that could be paired with an working occupant.
     * This will run once per node.
     *
     *
     * @param currentSolution
     * @param workingOccupant
     * @return
     */
    private ArrayList<Slot> getPossibleSlots(LinkedHashMap<Slot_Occupant,Slot> currentSolution,Slot_Occupant workingOccupant){

        ArrayList<Slot> possibleSlots = new ArrayList<>();

        Vector<Slot> workingSlots = workingOccupant instanceof Course ? parseData.Course_Slots : parseData.Lab_Slots;

        for(Slot s : workingSlots){

            LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(currentSolution);
            attemptedSolution.put(workingOccupant,s);

            if(constraints.checkHardConstraints(attemptedSolution)){
                possibleSlots.add(s);
            }
        }

        return  possibleSlots;
    }


    /** ------------------------------------------------------------------------------------------------------------------
     * Ryan's Recursive Solution For the non Mutated Version;
     *
     * REMEMBER: No Backtracking.
     *
     */
    public Map<Slot_Occupant,Slot> OrTreeRecursiveSearch(){

        LinkedHashMap<Slot_Occupant,Slot> solution = new LinkedHashMap<>();
        solution = initializePr(solution);

        
        //Get the first non null position THIS CAN'T BE CHAGED
        Slot_Occupant workingOccupant = null;
        for(Map.Entry entry : solution.entrySet()){
            if(entry.getValue() == null){
                workingOccupant = (Slot_Occupant) entry.getKey();
            }
        }
        
        if(root.initialized == false) {
            root.possibleSlots = getPossibleSlots(solution, workingOccupant);
            root.initialized = true; 
        }
        while(!root.possibleSlots.isEmpty()){

            Slot attemptedSlot = root.possibleSlots.get(randGen.nextInt(root.possibleSlots.size()));
            LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(solution);
            attemptedSolution.put(workingOccupant,attemptedSlot);
            OrTreeNode nextNode;
            
            //If no branch has been made
            nextNode = root.transitions.get(attemptedSlot);

            //Thn make the next branch for the tree
            if(nextNode == null){
                nextNode = new OrTreeNode(root);
                root.transitions.put(attemptedSlot,nextNode);
            }
            else{
            	//If the current node has no possible transitions from it remove it from the set of possible transitions from this node
            	if(nextNode.initialized && nextNode.possibleSlots.size() == 0){
            		root.possibleSlots.remove(attemptedSlot); 
            	}
            	else{
            		lastTried = attemptedSlot; 
                    attemptedSolution = OrTreeRecursiveSearch(attemptedSolution,nextNode);

                    if(attemptedSolution != null){
                        return attemptedSolution;
                    }
            	}
            }   
        }

        return null; //The root's possibleSlots were all empty, no more solutions could be found.
    }
    public LinkedHashMap<Slot_Occupant,Slot>OrTreeRecursiveSearch(LinkedHashMap<Slot_Occupant,Slot> currentSolution, OrTreeNode currentNode){

        //Return the current working solution if it is a solution ------------------------------------------------------
        if(isSolved(currentSolution)){
        	currentNode.parent.possibleSlots.remove(lastTried);
        	return currentSolution;
        }

        //Get first Slot_Occupant to work on, find first non_null element ----------------------------------------------
        //Get first non null position //THIS CAN'T BE CHANGED
        Slot_Occupant workingOccupant = null;
        for(Map.Entry entry : currentSolution.entrySet()){
            if(entry.getValue() == null){
                workingOccupant =(Slot_Occupant) entry.getKey();
                break; 
            }
        }

        //Get a List of all possible Slots for the found Course/Slot ---------------------------------------------------
        if(!currentNode.initialized) {
            currentNode.possibleSlots = getPossibleSlots(currentSolution, workingOccupant);
            currentNode.initialized = true; 
        }

        //If we can  do nothing from this node return null;
        if(currentNode.possibleSlots.isEmpty()){
            currentNode.parent.possibleSlots.remove(lastTried);
            return null;
        }

        //Randomly Select a possible slot
        Slot attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));

        //make a new solution with the attempted slot
        LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(currentSolution);
        attemptedSolution.put(workingOccupant,attemptedSlot);

        OrTreeNode nextNode;

        //Get the branch for the attemptedSlot if it exists if it doesn't make a new node and transition to it.
        nextNode = currentNode.transitions.get(attemptedSlot);

        if(nextNode == null){
            nextNode = new OrTreeNode(currentNode);
            currentNode.transitions.put(attemptedSlot,nextNode);
        }
        else{
            if(nextNode.initialized && nextNode.possibleSlots.size() == 0){
                currentNode.possibleSlots.remove(attemptedSlot);
                return null;
            }
        }

        lastTried = attemptedSlot;
        attemptedSolution = OrTreeRecursiveSearch(attemptedSolution,nextNode);

        if(attemptedSolution == null){
            return null;
        }
        else{
            return attemptedSolution;
        }



    }

    public LinkedHashMap<Slot_Occupant,Slot> mutateSearch(Map<Slot_Occupant,Slot> parent){

        LinkedHashMap<Slot_Occupant,Slot> solution = new LinkedHashMap<>();
        solution = initializePr(solution);

        Vector<Slot_Occupant> leftToTry = new Vector<>(AllOccupants);

        //Loop to gurantee we get some solution eventually.
        while(!root.possibleSlots.isEmpty()) {

            //Generate the Mutant
            if(leftToTry.isEmpty()){
                return null;
                //No course/lab could generate a new answer.
            }

            OccupantToMutate = leftToTry.get(randGen.nextInt(leftToTry.size()));
            LinkedHashMap<Slot_Occupant,Slot> attemptedMutation = new LinkedHashMap<>(solution);
            attemptedMutation = continueMutatation(parent,attemptedMutation,root);

            if(attemptedMutation != null){
                return attemptedMutation;
            }

            leftToTry.remove(OccupantToMutate);
        }
        return null; //The root's possibleSlots were all empty, no more solutions could be found.
    }
    private LinkedHashMap<Slot_Occupant,Slot> continueMutatation(Map<Slot_Occupant,Slot> parent, LinkedHashMap<Slot_Occupant,Slot> currentSolution, OrTreeNode currentNode){

        //Return the current working solution if it is a solution ------------------------------------------------------
        if(isSolved(currentSolution)){
            if(currentNode.parent != null) {
                currentNode.parent.possibleSlots.remove(lastTried);
            }
            return currentSolution;
        }

        //Get first Slot_Occupant to work on, find first non_null element ----------------------------------------------
        //Get first non null position //THIS CAN'T BE CHANGED
        Slot_Occupant workingOccupant = null;
        for(Map.Entry entry : currentSolution.entrySet()){
            if(entry.getValue() == null){
                workingOccupant =(Slot_Occupant) entry.getKey();
                break;
            }
        }

        //Get a List of all possible Slots for the found Course/Slot ---------------------------------------------------
        if(!currentNode.initialized) {
            currentNode.possibleSlots = getPossibleSlots(currentSolution, workingOccupant);
            currentNode.initialized = true;
        }

        //If we can't do anything from this node return null,
        if(currentNode.possibleSlots.isEmpty()){
            if(parent != null) {
                currentNode.parent.possibleSlots.remove(lastTried);
            }
            return null;
        }

        //If the working occupant is not the one we are attempting to change, try to copy the parent if possible.
        Slot parentSlot = parent.get(workingOccupant);
        Slot attemptedSlot;

        if(!OccupantToMutate.equals(workingOccupant)){

            if(currentNode.possibleSlots.contains(parentSlot)){
                attemptedSlot = parentSlot;
            }
            else{
                attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));
            }
        }
        else{ //We are on the Mutate case, so try not to copy the parent if possible.

            if(!currentNode.possibleSlots.contains(parentSlot)){
                attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));
            }
            else{
                if(currentNode.possibleSlots.size() != 1){
                    do{
                        attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));
                    }while(!attemptedSlot.equals(parentSlot));
                }
                else{ //You are forced to choose the parent
                    attemptedSlot = parentSlot;
                }
            }
        }

        //make a new solution with the attempted slot
        LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(currentSolution);
        attemptedSolution.put(workingOccupant,attemptedSlot);

        OrTreeNode nextNode;

        //Get the branch for the attemptedSlot if it exists if it doesn't make a new node and transition to it.
        nextNode = currentNode.transitions.get(attemptedSlot);

        if(nextNode == null){
            nextNode = new OrTreeNode(currentNode);
            currentNode.transitions.put(attemptedSlot,nextNode);
        }
        else{
            //Quick to see if our current decision will break before recursion.
            if(nextNode.initialized && nextNode.possibleSlots.size() == 0){
                currentNode.possibleSlots.remove(attemptedSlot);
                return null;
            }
        }

        lastTried = attemptedSlot;
        attemptedSolution = OrTreeRecursiveSearch(attemptedSolution,nextNode);

        if(attemptedSolution == null){
            return null;
        }
        else{
            return attemptedSolution;
        }
    }

    public boolean isSolved(LinkedHashMap<Slot_Occupant,Slot> map){
        return !map.containsValue(null);
    }

    private LinkedHashMap<Slot_Occupant, Slot> initializePr(LinkedHashMap<Slot_Occupant,Slot> givenData){

        //This will have to be re-done.

        Vector<Slot_Occupant> all_Occupants = this.parseData.getOccupants();
        all_Occupants.forEach((item) -> givenData.put(item, null));

        HashMap<Slot_Occupant, Slot> allPartialAssignments = this.parseData.Partial_Assignments.getAllPartialAssignments();

        for(Map.Entry<Slot_Occupant, Slot> assignment : allPartialAssignments.entrySet()){
            givenData.put(assignment.getKey(),assignment.getValue());
        }

        return givenData;
    }


}

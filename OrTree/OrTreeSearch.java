package OrTree;

import ParseData.ParseData;
import ParseData.Slot;
import Slot_Occupant.*;
import sun.awt.image.ImageWatched;

import java.util.*;


public class OrTreeSearch {

    public class OrTreeNode{

        private ArrayList<Slot> possibleSlots;
        private HashMap<Slot,OrTreeNode> transitions;

        public OrTreeNode(){
            possibleSlots = null;
            transitions = new HashMap<>();
        }
    }

    private OrTreeNode root;
    private ParseData parseData;
    private ConstraintChecker constraints;
    private Random randGen;

    public OrTreeSearch(ParseData parseData){
        this.parseData = parseData;
        root = new OrTreeNode();
        constraints = new ConstraintChecker(parseData);
        randGen = new Random();
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

            LinkedHashMap<Slot_Occupant,Slot> attepmtedSolution = new LinkedHashMap<>(currentSolution);
            attepmtedSolution.put(workingOccupant,s);

            if(constraints.checkHardConstraints(attepmtedSolution)){
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

        //Get the first non null position;
        Slot_Occupant workingOccupant = null;
        for(Map.Entry entry : solution.entrySet()){
            if(entry.getValue() == null){
                workingOccupant = (Slot_Occupant)entry.getKey();
                break;
            }
        }

        //
        if(root.possibleSlots == null) {
            root.possibleSlots = getPossibleSlots(solution, workingOccupant);
        }
        while(!root.possibleSlots.isEmpty()){

            Slot attemptedSlot = root.possibleSlots.get(randGen.nextInt(root.possibleSlots.size()));
            LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(solution);
            attemptedSolution.put(workingOccupant,attemptedSlot);
            OrTreeNode nextNode;

            //If no branch has been made
            nextNode = root.transitions.get(attemptedSlot);

            if(nextNode == null){
                nextNode = new OrTreeNode();
                root.transitions.put(attemptedSlot,nextNode);
            }
            else{ // a branch already exists

                //If the path has been made and has no possible slots, then and only then remove the attempted slot
                if(nextNode.possibleSlots != null){
                    if(nextNode.possibleSlots.isEmpty()) {
                        root.possibleSlots.remove(attemptedSlot);
                    }
                }
            }

            attemptedSolution = OrTreeRecursiveSearch(attemptedSolution,nextNode);

            if(attemptedSolution != null){
                return attemptedSolution;
            }
        }

        return null; //The root's possibleSlots were all empty, no more solutions could be found.
    }
    public LinkedHashMap<Slot_Occupant,Slot>OrTreeRecursiveSearch(LinkedHashMap<Slot_Occupant,Slot> currentSolution, OrTreeNode currentNode){

        //Return the current working solution if it is a solution ------------------------------------------------------
        if(isSolved(currentSolution)){
            return currentSolution;
        }

        //Get first Slot_Occupant to work on, find first non_null element ----------------------------------------------
        Slot_Occupant workingOccupant = null;
        for(Map.Entry entry : currentSolution.entrySet()){
            if(entry.getValue() == null){
                workingOccupant = (Slot_Occupant)entry.getKey();
                break;
            }
        }

        //Get a List of all possible Slots for the found Course/Slot ---------------------------------------------------
        if(currentNode.possibleSlots == null) {
            currentNode.possibleSlots = getPossibleSlots(currentSolution, workingOccupant);
        }
        LinkedHashMap<Slot_Occupant,Slot> copyOfCurrentSolution = new LinkedHashMap<>(currentSolution);

        //Begin While Loop ---------------------------------------------------------------------------------------------
        while(!currentNode.possibleSlots.isEmpty()){

            //Randomly Select a possible slot
            Slot attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));

            //make a new solution with the attempted slot
            LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(copyOfCurrentSolution);
            attemptedSolution.put(workingOccupant,attemptedSlot);

            OrTreeNode nextNode;

            //If no branch has been made
            nextNode = currentNode.transitions.get(attemptedSlot);

            if(nextNode == null){
                nextNode = new OrTreeNode();
                currentNode.transitions.put(attemptedSlot,nextNode);
            }
            else{ // a branch already exists

                //If the path has been made and has no possible slots, then and only then remove the attempted slot
                if(nextNode.possibleSlots != null){
                    if(nextNode.possibleSlots.isEmpty()) {
                        currentNode.possibleSlots.remove(attemptedSlot);
                    }
                }
            }

            attemptedSolution = OrTreeRecursiveSearch(attemptedSolution,nextNode);

            if(attemptedSolution == null){
                return null;
            }
            else
                return attemptedSolution;

        }

        //No possible solution could be found, return null;
        return null;
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

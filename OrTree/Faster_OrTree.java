package OrTree;

import ParseData.*;
import Slot_Occupant.*;
import java.util.*;

public class Faster_OrTree {


    public class OrTreeNode{

        private Slot_Occupant myWorkingOccupant;
        // essentially altern
        private Vector<Slot> possibleSlots;
        // children nodes
        private HashMap<Slot,OrTreeNode> transitions;
        // parent node
        private OrTreeNode parent;
        private Slot lastTried;
        private HashMap<Slot,Boolean> verifiedSlots;

        public OrTreeNode(OrTreeNode parent, Slot_Occupant wo)
        {
            this.myWorkingOccupant = wo;
            possibleSlots = parseData.getSlots();
            transitions = new HashMap<>();
            this.parent = parent;
            this.lastTried = null;
            possibleSlots = wo instanceof Course ? new Vector<>(parseData.Course_Slots) : new Vector<>(parseData.Lab_Slots);
            verifiedSlots = new HashMap();
        }
    }

    //Faster_Or_Tree Fields:
    private LinkedList<Slot_Occupant> occupantOrder;
    private ConstraintChecker constraints;
    private OrTreeNode root;
    private Random randGen;
    private ParseData parseData;
    private LinkedHashMap<Slot_Occupant,Slot> startingSolution;
    private HashSet<Map<Slot_Occupant,Slot>> uniqueConstraint;


    public Faster_OrTree(ParseData parseData){

        this.parseData = parseData;
        constraints = new ConstraintChecker(parseData);
        randGen = new Random();

        //The starting state of the OrTree will always be the same give the same parseData;
        startingSolution = initializePr(new LinkedHashMap<>());

        //Get the order that the or tree WILL ALWAYS follow;
        occupantOrder = new LinkedList<>();

        for(Map.Entry entry : startingSolution.entrySet()){
            if(entry.getValue() == null){
                occupantOrder.add((Slot_Occupant) entry.getKey());
            }
        }

        parseData.setAllSlots();
        parseData.setAllOccupants();

        //This means we know what the root's occupant will be at this point
        root = new OrTreeNode(null, occupantOrder.get(0));

        uniqueConstraint = new HashSet<Map<Slot_Occupant,Slot>>();
    }
    public LinkedHashMap<Slot_Occupant,Slot> fasterSearch(){

        //Always start at the
        LinkedHashMap<Slot_Occupant,Slot> attemptedSolution;
        Slot attemptedSlot = null;
        OrTreeNode currentNode = root;
        OrTreeNode nextNode;

        int currentOccupant = 0;
        Slot_Occupant workingOccupant;

        //Run until you get a solution or the whole tree has been exhausted
        while(!root.possibleSlots.isEmpty()){

            //reset values;
            attemptedSolution = new LinkedHashMap<>(this.startingSolution);
            currentOccupant = 0;
            currentNode = root;

            while(true){

                //Test to see if currentNode has any more locatiions to go to.
                if(currentNode.possibleSlots.size() == 0){

                    OrTreeNode temp = currentNode;

                    while (temp.possibleSlots.size() == 0 && temp.parent != null) {
                        temp.parent.possibleSlots.remove(temp.parent.lastTried);
                        temp.parent.verifiedSlots.replace(temp.parent.lastTried,false);
                        temp = temp.parent;
                    }
                    break;

                }

                //Randomly select a possible slot from the current nodes possible options.
                attemptedSlot = getSlot(currentNode,attemptedSolution);

                //There were no possible solutions at this point;
                if (attemptedSlot == null) {
                    if(currentNode.possibleSlots.size() == 0 && currentNode.parent != null) {

                        OrTreeNode temp = currentNode;

                        while (temp.possibleSlots.size() == 0 && temp.parent != null) {
                            temp.parent.possibleSlots.remove(temp.parent.lastTried);
                            temp.parent.verifiedSlots.replace(temp.parent.lastTried,false);
                            temp = temp.parent;
                        }
                        break;
                    }
                }

                //attemptedSlot = currentNode.possibleSlots.get(0);
                currentNode.lastTried = attemptedSlot;

                //Add the choice to the currentSolution:
                attemptedSolution.put(currentNode.myWorkingOccupant,attemptedSlot);

                //We've reached the end of the branch if the solution has filled out all slots
                if(!attemptedSolution.containsValue(null)){

                    currentNode.possibleSlots.remove(attemptedSlot);

                    if(currentNode.possibleSlots.size() == 0 && currentNode.parent != null) {

                        OrTreeNode temp = currentNode;

                        while (temp.possibleSlots.size() == 0 && temp.parent != null) {
                            temp.parent.possibleSlots.remove(temp.parent.lastTried);
                            temp.parent.verifiedSlots.replace(temp.parent.lastTried,false);
                            temp = temp.parent;
                        }
                    }

                    if(!uniqueConstraint.add(attemptedSolution)){
                        break;
                    }
                    else{
                        return attemptedSolution;
                    }
                }

                //See if the current node has a transition already made for the slot
                nextNode = currentNode.transitions.get(attemptedSlot);

                //If we've never traveled down this branch before, and it's not the end of a branch
                if(nextNode == null ){

                    //As the end of tree nodes will have no possible slots this currentOccupant++ will never be out of bounds
                    nextNode = new OrTreeNode(currentNode,this.occupantOrder.get(currentOccupant+1));

                    //Add the transition to the current node.
                    currentNode.transitions.put(attemptedSlot,nextNode);
                }
                else {

                    if(nextNode.possibleSlots.isEmpty()){
                        currentNode.possibleSlots.remove(attemptedSlot);

                        OrTreeNode temp = currentNode;

                        while (temp.possibleSlots.size() == 0 && temp.parent != null) {
                            temp.parent.possibleSlots.remove(temp.parent.lastTried);
                            temp.parent.verifiedSlots.replace(temp.parent.lastTried,false);
                            temp = temp.parent;
                        }
                    }

                }
                //Go down a level in the tree, if the end of the branch has not been met.
                currentNode = nextNode;
                currentOccupant++;

            }
        }

        //As root.possibleSlots is empty this means that there that the OrTree is exhausted.
        return null;
    }
    public LinkedHashMap<Slot_Occupant,Slot> fasterMutate(Map<Slot_Occupant,Slot> parent) {

        //We know this solution has been already found in the tree so, first generate a possible mutant position;
        LinkedHashMap<Slot_Occupant,Slot> attemptedSolution;
        Slot attemptedSlot = null;
        int currentOccupant = 0;

        OrTreeNode currentNode = root;
        OrTreeNode nextNode;

        Vector<Slot_Occupant> possibleMutants = new Vector<>(this.occupantOrder);
        Slot_Occupant mutant;
        Slot parentSlot;

        while(!root.possibleSlots.isEmpty()){

            currentOccupant = 0;
            attemptedSolution = new LinkedHashMap<>(this.startingSolution);
            currentNode = root;

            //Generate a random mutant
            mutant = possibleMutants.elementAt(0);
            //mutant = possibleMutants.elementAt(randGen.nextInt(possibleMutants.size()));

            //Follow the parent tree until that mutant is solved.
            currentNode = root;

            while(!currentNode.myWorkingOccupant.equals(mutant)){
                parentSlot = parent.get(currentNode.myWorkingOccupant);
                attemptedSolution.put(currentNode.myWorkingOccupant,parentSlot);

                if(!currentNode.equals(root)){
                    ++currentOccupant;
                }
                currentNode = currentNode.transitions.get(parentSlot);

            }

            while(true){

                //Now we are at the mutant, if we are already skrewed here don't try this mutant again.
                if(currentNode.myWorkingOccupant.equals(mutant) && currentNode.possibleSlots.size() == 0){
                    possibleMutants.remove(mutant);
                    currentNode.parent.possibleSlots.remove(currentNode.parent.lastTried);
                    currentNode.parent.verifiedSlots.replace(currentNode.parent.lastTried,false);
                    break;
                }

                parentSlot = parent.get(currentNode.myWorkingOccupant);

                if(currentNode.myWorkingOccupant.equals(mutant)) {
                    //Randomly select a possible slot from the current nodes possible options.
                    attemptedSlot = getSlot(currentNode, attemptedSolution);
                }
                else{
                    if(currentNode.possibleSlots.contains(parentSlot)){
                        attemptedSlot = getSlot(currentNode, attemptedSolution, parentSlot);

                    }
                    else{
                        attemptedSlot = getSlot(currentNode, attemptedSolution);
                    }
                }

                if(attemptedSlot == null){
                    if(currentNode.possibleSlots.size() == 0 && currentNode.parent != null) {

                        OrTreeNode temp = currentNode;

                        while (temp.possibleSlots.size() == 0 && temp.parent != null) {
                            temp.parent.possibleSlots.remove(temp.parent.lastTried);
                            temp.parent.verifiedSlots.replace(temp.parent.lastTried,false);
                            temp = temp.parent;
                        }

                        if(currentNode.equals(mutant)){
                            possibleMutants.remove(mutant);
                        }

                        break;
                    }
                }
                //attempt the solution
                currentNode.lastTried = attemptedSlot;
                attemptedSolution.put(currentNode.myWorkingOccupant,attemptedSlot);

                if(!attemptedSolution.containsValue(null)){

                    currentNode.possibleSlots.remove(attemptedSlot);

                    if(currentNode.possibleSlots.size() == 0 && currentNode.parent != null) {

                        OrTreeNode temp = currentNode;

                        while (temp.possibleSlots.size() == 0 && temp.parent != null) {
                            temp.parent.possibleSlots.remove(temp.parent.lastTried);
                            temp.parent.verifiedSlots.replace(temp.parent.lastTried,false);
                            temp = temp.parent;
                        }
                    }

                    if(!uniqueConstraint.add(attemptedSolution)){
                        break;
                    }
                    else{
                        return attemptedSolution;
                    }
                }

                //See if the current node has a transition already made for the slot
                nextNode = currentNode.transitions.get(attemptedSlot);

                //If we've never traveled down this branch before, and it's not the end of a branch
                if(nextNode == null){

                    //As the end of tree nodes will have no possible slots this currentOccupant++ will never be out of bounds
                    nextNode = new OrTreeNode(currentNode,this.occupantOrder.get(currentOccupant + 1));

                    //Add the transition to the current node.
                    currentNode.transitions.put(attemptedSlot,nextNode);
                }
                else {

                    if (nextNode.possibleSlots.isEmpty()) {
                        currentNode.possibleSlots.remove(attemptedSlot);
                        break;
                    }
                }

                currentNode = nextNode;
                ++currentOccupant;
            }
        }
        return null;
    }
    private LinkedHashMap<Slot_Occupant, Slot> initializePr(LinkedHashMap<Slot_Occupant,Slot> givenData){

        Vector<Slot_Occupant> all_Occupants = this.parseData.getOccupants();
        all_Occupants.forEach((item) -> givenData.put(item, null));

        HashMap<Slot_Occupant, Slot> allPartialAssignments = this.parseData.Partial_Assignments.getAllPartialAssignments();

        for(Map.Entry<Slot_Occupant, Slot> assignment : allPartialAssignments.entrySet()){
            givenData.put(assignment.getKey(),assignment.getValue());
        }

        return givenData;
    }

    private Slot getSlot (OrTreeNode currentNode, LinkedHashMap<Slot_Occupant,Slot> currentSolution){

        Slot attemptedSlot;
        LinkedHashMap<Slot_Occupant,Slot> attemptedSolution;

        while(true){

            if(currentNode.possibleSlots.size() == 0){
                return null;
            }

            //attemptedSlot = currentNode.possibleSlots.get(0);
            attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));

            //If we have already determined this is valid
            if(currentNode.verifiedSlots.containsKey(attemptedSlot)){
                if(currentNode.verifiedSlots.get(attemptedSlot)){break;}
            }

            //Otherwise check if it is valid
            attemptedSolution = new LinkedHashMap<>(currentSolution);
            attemptedSolution.put(currentNode.myWorkingOccupant,attemptedSlot);

            if(!constraints.checkHardConstraints(attemptedSolution)){
                currentNode.possibleSlots.remove(attemptedSlot);
                currentNode.verifiedSlots.put(attemptedSlot,false);
            }
            else{
                currentNode.verifiedSlots.put(attemptedSlot,true);
                break;
            }
        }
        return attemptedSlot;
    }
    private Slot getSlot (OrTreeNode currentNode, LinkedHashMap<Slot_Occupant,Slot> currentSolution, Slot wantedSlot){

        Slot attemptedSlot;
        LinkedHashMap<Slot_Occupant,Slot> attemptedSolution;

        boolean triedWantedSlot = false;
        attemptedSlot = wantedSlot;

        while(true){

            if(currentNode.possibleSlots.size() == 0){
                return null;
            }

            if(triedWantedSlot) {
                //attemptedSlot = currentNode.possibleSlots.get(0);
                attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));
            }

            //If we have already determined this is valid
            if(currentNode.verifiedSlots.containsKey(attemptedSlot)){
                if(currentNode.verifiedSlots.get(attemptedSlot)){break;}
            }

            //Otherwise check if it is valid
            attemptedSolution = new LinkedHashMap<>(currentSolution);
            attemptedSolution.put(currentNode.myWorkingOccupant,attemptedSlot);

            if(!constraints.checkHardConstraints(attemptedSolution)){
                currentNode.possibleSlots.remove(attemptedSlot);
                currentNode.verifiedSlots.put(attemptedSlot,false);
            }
            else{
                currentNode.verifiedSlots.put(attemptedSlot,true);
                break;
            }

            triedWantedSlot = true;
        }
        return attemptedSlot;
    }















}


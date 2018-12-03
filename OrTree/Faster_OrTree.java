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
        private HashSet<Slot> verifiedSlots;

        public OrTreeNode(OrTreeNode parent, Slot_Occupant wo, Vector<Slot> AllSlots)
        {
            this.myWorkingOccupant = wo;
            possibleSlots = parseData.getSlots();
            transitions = new HashMap<>();
            this.parent = parent;
            this.lastTried = null;
            possibleSlots = new Vector<>(AllSlots);
            verifiedSlots = new HashSet<>();
        }
    }

    //Faster_Or_Tree Fields:
    private LinkedList<Slot_Occupant> occupantOrder;
    private ConstraintChecker constraints;
    private OrTreeNode root;
    private Random randGen;
    private ParseData parseData;
    private LinkedHashMap<Slot_Occupant,Slot> startingSolution;
    private Vector<Slot> AllSlots;

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

        //This means we know what the root's occupant will be at this point
        root = new OrTreeNode(null, occupantOrder.get(0),parseData.AllSlots);


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

                //Test to see if the currentNode has been traveled to before if not initialize the current node;
                if(currentNode.possibleSlots == null){

                    if(currentNode.possibleSlots.size() == 0 && currentNode.parent != null) {

                        OrTreeNode temp = currentNode;

                        while (temp.possibleSlots.size() == 0 && temp.parent != null) {
                            temp.parent.possibleSlots.remove(temp.parent.lastTried);
                            temp = temp.parent;
                        }
                        break;
                    }
                }

                //Randomly select a possible slot from the current nodes possible options.
                attemptedSlot = getSlot(currentNode);
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
                            temp = temp.parent;
                        }
                    }

                    return attemptedSolution;
                }

                //See if the current node has a transition already made for the slot
                nextNode = currentNode.transitions.get(attemptedSlot);

                //If we've never traveled down this branch before, and it's not the end of a branch
                if(nextNode == null ){

                    //As the end of tree nodes will have no possible slots this currentOccupant++ will never be out of bounds
                    nextNode = new OrTreeNode(currentNode,this.occupantOrder.get(currentOccupant+1),parseData.AllSlots);

                    //Add the transition to the current node.
                    currentNode.transitions.put(attemptedSlot,nextNode);
                }
                else {

                    if(nextNode.possibleSlots.isEmpty()){
                        System.out.println("What is going on here?");
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
    public LinkedHashMap<Slot_Occupant,Slot> fasterSearch(){


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

    private Slot getSlot (OrTreeNode currentNode){

        Slot attemptedSlot;
        LinkedHashMap<Slot_Occupant,Slot> attemptedSolution;

        while(true){
            attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));

            //If we have already determined this is valid
            if(currentNode.verifiedSlots.contains(attemptedSlot)){ break; }

            //Otherwise check if it is valid
            attemptedSolution = new LinkedHashMap<>(startingSolution);
            attemptedSolution.put(currentNode.myWorkingOccupant,attemptedSlot);

            if(!constraints.checkHardConstraints(attemptedSolution)){
                currentNode.possibleSlots.remove(attemptedSlot);
            }
            else{
                currentNode.verifiedSlots.add(attemptedSlot);
                break;
            }
        }
        return attemptedSlot;
    }















}


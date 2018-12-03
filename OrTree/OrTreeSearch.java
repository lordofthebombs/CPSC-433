package OrTree;

import ParseData.ParseData;
import ParseData.Slot;
import Slot_Occupant.*;

import java.util.*;


public class OrTreeSearch {

    public class OrTreeNode{
    	
    	private Slot_Occupant myWorkingOccupant;
    	// essentially altern
        private ArrayList<Slot> possibleSlots;
        // children nodes
        private HashMap<Slot,OrTreeNode> transitions;
        // parent node 
        private OrTreeNode parent; 
        
        public OrTreeNode(OrTreeNode parent, Slot_Occupant wo)
        {
        	this.myWorkingOccupant = wo;
        	possibleSlots = null;
            transitions = new HashMap<>();
            this.parent = parent; 
        }
        
        public OrTreeNode(OrTreeNode parent) {
        	this(parent, null);
        }
        
    }
   
    private OrTreeNode root;			 // root of all orTrees
    private ParseData parseData;
    private ConstraintChecker constraints;
    private Random randGen;
    private Slot lastTried;			// last tried isn't needed anymore really
    public Slot_Occupant mutatedOccupant;
    Vector<Slot_Occupant> possible_mutants;

    public OrTreeSearch(ParseData parseData){
        this.parseData = parseData;
        root = new OrTreeNode(null);
        constraints = new ConstraintChecker(parseData);
        randGen = new Random();
        // simply checks if partial assignments would lead to a solution
        if (!constraints.checkHardConstraints(initializePr(new LinkedHashMap<Slot_Occupant,Slot>()))) {

            System.out.println("Partial Solution has no solutions");
            System.exit(0);
        }

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
            
            /*
            if(attemptedSolution == null){			// never null ?
            	System.out.print("test");
            }
            */
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
        

        Slot_Occupant workingOccupant = null;
        if(root.myWorkingOccupant == null) {
        	//Get the first non null position;
            for(Map.Entry entry : solution.entrySet()){
                if(entry.getValue() == null){
                	workingOccupant = (Slot_Occupant)entry.getKey();
                    break;
                }
            }
            // assign roots working occupant and corresponding possible slots
            root.myWorkingOccupant = workingOccupant;
            root.possibleSlots = getPossibleSlots(solution, workingOccupant);
        }
        
        while(!root.possibleSlots.isEmpty()){
        	// find random possible slot
            Slot attemptedSlot = root.possibleSlots.get(randGen.nextInt(root.possibleSlots.size()));
            // create a new node with new slot and workingOccupant
            LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(solution);
            attemptedSolution.put(root.myWorkingOccupant, attemptedSlot);
            OrTreeNode nextNode;

            //If no branch has been made
            nextNode = root.transitions.get(attemptedSlot);

            if(nextNode == null){
                nextNode = new OrTreeNode(root);
                root.transitions.put(attemptedSlot,nextNode);
            }
            else{ // a branch already exists

                //If the path has been made and has no possible slots, then and only then remove the attempted slot
                if(nextNode.possibleSlots != null){		// impossible for possibleSlots to be null
                    if(nextNode.possibleSlots.isEmpty()) {
                        root.possibleSlots.remove(attemptedSlot);
                    }
                }
            }

            lastTried = attemptedSlot; 
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
        	
        	currentNode.parent.possibleSlots.remove(lastTried);		// remove from possible slots since it already leads to a solution
            return currentSolution;
        }

        
       // Slot_Occupant workingOccupant = null;
        //Get a List of all possible Slots for the found Course/Slot ---------------------------------------------------
        if(currentNode.myWorkingOccupant == null) {
        	//Get first Slot_Occupant to work on, find first non_null element ----------------------------------------------
            //workingOccupant = null;
            for(Map.Entry entry : currentSolution.entrySet()){
                if(entry.getValue() == null){
                	currentNode.myWorkingOccupant = (Slot_Occupant)entry.getKey();
                    break;
                }
            }
        	
            currentNode.possibleSlots = getPossibleSlots(currentSolution, currentNode.myWorkingOccupant);
        }
        LinkedHashMap<Slot_Occupant,Slot> copyOfCurrentSolution = new LinkedHashMap<>(currentSolution);

        //Begin While Loop ---------------------------------------------------------------------------------------------
        while(!currentNode.possibleSlots.isEmpty()){

            //Randomly Select a possible slot
            Slot attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));

            //make a new solution with the attempted slot
            LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(copyOfCurrentSolution);
            attemptedSolution.put(currentNode.myWorkingOccupant, attemptedSlot);

            OrTreeNode nextNode;

            //If no branch has been made
            nextNode = currentNode.transitions.get(attemptedSlot);

            if(nextNode == null){
                nextNode = new OrTreeNode(currentNode);
                currentNode.transitions.put(attemptedSlot,nextNode);
            }
            else{ // a branch already exists

                //If the path has been made and has no possible slots, then and only then remove the attempted slot
                if(nextNode.possibleSlots != null){
                    if(nextNode.possibleSlots.isEmpty()) {
                        OrTreeNode temp = currentNode;
                        OrTreeNode prevNode = currentNode;

                        while (temp.possibleSlots.size() == 0 && temp.parent != null) {
                            temp.possibleSlots.remove(prevNode.myWorkingOccupant);
                            prevNode = temp;
                            temp = temp.parent;

                        }
                    }
                }
            }

            lastTried = attemptedSlot; 
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

    public LinkedHashMap<Slot_Occupant,Slot> mutateSearch(Map<Slot_Occupant,Slot> parentSolution){
    
    	 LinkedHashMap<Slot_Occupant,Slot> solution = new LinkedHashMap<>();
         initializePr(solution);

         possible_mutants = parseData.getOccupants();

         //Remove partial assignments from possible mutants;'
        for(Map.Entry<Slot_Occupant,Slot> so : solution.entrySet()) {

            if(so.getValue() != null){
                possible_mutants.remove(so.getKey());
            }
        }

         //if the path hasn't been travelled down before generate all alterns. 
         if(root.possibleSlots == null) {
        	 root.myWorkingOccupant = mutatedOccupant;
             root.possibleSlots = getPossibleSlots(solution, root.myWorkingOccupant); //Altern Function 
         }

         //Get the parent slot
         Slot parentSlot = parentSolution.get(root.myWorkingOccupant);

         while(!root.possibleSlots.isEmpty()){

             //Get a random course/lab to mutate on that is not a partial assignments
             mutatedOccupant = possible_mutants.get(randGen.nextInt(possible_mutants.size()));

         	//Randomly Select a possible slot
        	//If you can copy the parent, do so, if you can't random selection. 
        	Slot attemptedSlot;		// will never be null
        	
        	// the case where workingOccupant != mutatedOccupant
        	if(parentSlot != null && root.possibleSlots.contains(parentSlot) && !root.myWorkingOccupant.equals(mutatedOccupant)){
        		attemptedSlot = parentSlot; 
        	}
        	else{
        		// ensures that mutatedOccupant does not take the same slot
        		// assuming theres more than 1 option for slots

                Vector<Slot> nonParent_Slots = new Vector<>(root.possibleSlots);
                nonParent_Slots.remove(parentSlot);

                if(nonParent_Slots.size() == 0){
                    attemptedSlot = parentSlot;
                }
                else{
                    attemptedSlot = nonParent_Slots.elementAt(randGen.nextInt(nonParent_Slots.size()));
                }
        		/*if (root.possibleSlots.contains(parentSlot) && root.possibleSlots.size() >= 2) {
        			do {
         				attemptedSlot = root.possibleSlots.get(randGen.nextInt(root.possibleSlots.size()));
         			} while (parentSlot == attemptedSlot);
        		} else {
        			attemptedSlot = root.possibleSlots.get(randGen.nextInt(root.possibleSlots.size()));
        		}*/
        	}
             
             LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(solution);
             attemptedSolution.put(root.myWorkingOccupant, attemptedSlot);
             OrTreeNode nextNode;

             //If no branch has been made
             nextNode = root.transitions.get(attemptedSlot);

             if(nextNode == null){
                 nextNode = new OrTreeNode(root);
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

             lastTried = attemptedSlot; 		
             attemptedSolution = mutateSearch(nextNode,parentSolution,attemptedSolution);

             if(attemptedSolution != null){
                 return attemptedSolution;
             }
         }

         return null; //The root's possibleSlots were all empty, no more solutions could be found.
    	
    }
    
    // does not function correctly unless mutatedOccupant is set to an occupant
    public LinkedHashMap<Slot_Occupant,Slot> mutateSearch(OrTreeNode currentNode, Map<Slot_Occupant,Slot> parent, LinkedHashMap<Slot_Occupant,Slot> currentSolution){
    	// mutatedOccupant must be defined
    	if (mutatedOccupant == null) return null;
    	
    	//Return the current working solution if it is a solution ------------------------------------------------------
        if(isSolved(currentSolution)){	
        	currentNode.parent.possibleSlots.remove(lastTried);
        	
            return currentSolution;
        }
        
        
        //Slot_Occupant workingOccupant = null;				// occupant to assign a slot
        //Get a List of all possible Slots for the found Course/Slot ---------------------------------------------------
        if(currentNode.myWorkingOccupant == null) {
        	//Get the first non null position;
            for(Map.Entry entry : currentSolution.entrySet()){
                if(entry.getValue() == null){
                	currentNode.myWorkingOccupant = (Slot_Occupant)entry.getKey();
                    break;
                }
            }
            // assign roots working occupant and corresponding possible slots
            //currentNode.myWorkingOccupant = workingOccupant;
            currentNode.possibleSlots = getPossibleSlots(currentSolution, currentNode.myWorkingOccupant);
        }
        
        /*
        if(mutatedOccupant == null){
	        //Get first Slot_Occupant to work on, find first non_null element ----------------------------------------------
	        for(Map.Entry entry : currentSolution.entrySet()){
	            if(entry.getValue() == null){
	                workingOccupant = (Slot_Occupant)entry.getKey();
	                break;
	            }
	        }
        }
        
        else{
        	workingOccupant = mutatedOccupant; 
        	mutatedOccupant = null; 
        }
        */
       
        LinkedHashMap<Slot_Occupant,Slot> copyOfCurrentSolution = new LinkedHashMap<>(currentSolution);

        //Begin While Loop ---------------------------------------------------------------------------------------------
        while(!currentNode.possibleSlots.isEmpty()){

            //Randomly Select a possible slot
        	//If you can copy the parent, do so, if you can't random selection. 
        	Slot parentSlot = parent.get(currentNode.myWorkingOccupant); 
        	Slot attemptedSlot = null;
        	
        	// the case where workingOccupant != mutatedOccupant
        	if(parentSlot != null && currentNode.possibleSlots.contains(parentSlot) && !currentNode.myWorkingOccupant.equals(mutatedOccupant)){
        		attemptedSlot = parentSlot; 
        	}
        	else{
        		// ensures that mutatedOccupant does not take the same slot
        		// assuming theres more than 1 option for slots
                if (currentNode.possibleSlots.size() == 0){
                    possible_mutants.remove(mutatedOccupant);
                    return null;
                }
                else{

                    Vector<Slot> nonParent_Slots = new Vector<>(currentNode.possibleSlots);
                    nonParent_Slots.remove(parentSlot);

                    if(nonParent_Slots.size() == 0){
                        attemptedSlot = parentSlot; // force to choose the parent.
                    }
                    else{
                        attemptedSlot = nonParent_Slots.elementAt(randGen.nextInt(nonParent_Slots.size()));
                    }
                }
        		/*else if (currentNode.possibleSlots.contains(parentSlot) && currentNode.possibleSlots.size() >= 2) {
        			do {
         				attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));
         				System.out.println(attemptedSlot);
         			} while (parentSlot == attemptedSlot);
        		} else {
        			attemptedSlot = currentNode.possibleSlots.get(randGen.nextInt(currentNode.possibleSlots.size()));
        		}*/
        	}
           

            //make a new solution with the attempted slot
            LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(copyOfCurrentSolution);
            attemptedSolution.put(currentNode.myWorkingOccupant,attemptedSlot);

            OrTreeNode nextNode;

            //If no branch has been made
            nextNode = currentNode.transitions.get(attemptedSlot);

            if(nextNode == null){
                nextNode = new OrTreeNode(currentNode);
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

            lastTried = attemptedSlot; 
            attemptedSolution = mutateSearch(nextNode,parent,attemptedSolution);

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

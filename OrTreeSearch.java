import ParseData.ParseData;
import ParseData.Slot;
import Slot_Occupant.Course;
import Slot_Occupant.Slot_Occupant;
import sun.awt.image.ImageWatched;

import java.util.*;

/**
 *
 */
public class OrTreeSearch {

    /**
     * Inner class for the actual OrTree data Structure
     */
    class OrTree {

        private Map<Slot_Occupant, Slot> data;
        private OrTree parent;

        // to keep track of the max number of course/labs that can be added
        private Map<Slot, Integer> labSlotToMaxCapacity = new LinkedHashMap<>();
        private Map<Slot, Integer> courseSlotToMaxCapacity = new LinkedHashMap<>();

        private List<OrTree> children = new ArrayList<>();
        private int mutationSelectionScore;


        public OrTree(Map<Slot_Occupant, Slot> data,
                      Map<Slot, Integer> labSlotCopy,
                      Map<Slot, Integer> courseSlotCopy){

            this.data = data;
            this.labSlotToMaxCapacity.putAll(labSlotCopy);
            this.courseSlotToMaxCapacity.putAll(courseSlotCopy);

        }
        //Comparator anonymous class implementation to compare the mutationscore for ortree
        private Comparator<OrTree> valComparator = new Comparator<OrTree>() {

            @Override
            public int compare(OrTree t1, OrTree t2) {
                return (t1.getMutationSelectionScore() - t2.getMutationSelectionScore());
            }
        };

        private  int getMutationSelectionScore(){
            return this.mutationSelectionScore;
        }


        /**
         * Method to add a child to a parent node
         * @param data
         */
        void addChild(Map<Slot_Occupant, Slot> data, int val,
                      Map<Slot, Integer> updatedLabSlots,
                      Map<Slot, Integer> updatedCourseSlots){
            OrTree child = new OrTree(data, updatedLabSlots,
                    updatedCourseSlots);
            child.mutationSelectionScore = val;
            child.parent = this;
            this.children.add(child);
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

        /**
         * added for debugging purpose
         * @param data
         * @return
         */
        private String getStringFormData(Map<Slot_Occupant, Slot> data){

            StringBuilder sb = new StringBuilder();
            data.entrySet().stream().forEach( entry -> sb.append(entry.getKey() + ": " + entry.getValue() + "\n"));
            return sb.toString();
        }

    }

    private OrTree orTree;
    private ParseData parseData;
    private ConstraintChecker constraintChecker;
    private Map<Slot_Occupant, Slot> initialPr;
    private Map<Slot, Integer> copyOfLabSlots = new LinkedHashMap<>();
    private Map<Slot, Integer> copyOfCourseSlots = new LinkedHashMap<>();
    private Random randGen = new Random();



    /**
     * Main Constructor for initializing the OrTreeSearch
     * This should be called outside this class in order to call one of the
     * search controls in the Set Based Search
     * @param parseData
     */
    public OrTreeSearch(ParseData parseData, ConstraintChecker constraintChecker){
        this.parseData = parseData;
        this.constraintChecker = constraintChecker;

        parseData.Course_Slots.stream().forEach( slot -> copyOfCourseSlots.put(slot, slot.max));
        parseData.Lab_Slots.stream().forEach( slot -> copyOfLabSlots.put(slot, slot.max));

        Map<Slot_Occupant, Slot> initialSolMap = initializePr();

        Map<Slot, Integer> initialLabSlots = new LinkedHashMap<>();
        Map<Slot, Integer> initialCourseSlots = new LinkedHashMap<>();
        initialLabSlots.putAll(this.copyOfLabSlots);
        initialCourseSlots.putAll(this.copyOfCourseSlots);
        this.orTree = new OrTree(initialSolMap, initialLabSlots, initialCourseSlots);
        this.initialPr = new LinkedHashMap<>();
        this.initialPr.putAll(initialSolMap);
    }


    /** ------------------------------------------------------------------------------------------------------------------
     * Ryan's Recursive Solution For the non Mutated Version;
     *
     *
     */
    public Map<Slot_Occupant,Slot>OrTreeRecursiveSearch(ParseData parseData, ConstraintChecker constraintChecker){

        LinkedHashMap<Slot_Occupant,Slot> solution = new LinkedHashMap<>();
        solution = initializePr(solution);

        return OrTreeRecursiveSearch(parseData,constraintChecker,solution);
    }

    public LinkedHashMap<Slot_Occupant,Slot>OrTreeRecursiveSearch(ParseData parseData, ConstraintChecker constraintChecker, LinkedHashMap<Slot_Occupant,Slot> currentSolution){

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
        Vector<Slot> possibleSlots;
        if(workingOccupant instanceof Course){
            possibleSlots = new Vector<>(parseData.Course_Slots);
        }else{
            possibleSlots = new Vector<>(parseData.Lab_Slots);
        }

        //Begin While Loop ---------------------------------------------------------------------------------------------
        while(possibleSlots.size() != 0){

            //Randomly Select a possible slot
            Slot attemptedSlot = possibleSlots.elementAt(randGen.nextInt(possibleSlots.size()));

            //make a new solution with the attempted slot
            LinkedHashMap<Slot_Occupant,Slot> attemptedSolution = new LinkedHashMap<>(currentSolution);
            attemptedSolution.put(workingOccupant,attemptedSlot);

            //Check Constraints
            if(constraintChecker.checkHardConstraints(attemptedSolution)){

                //Recursive Step
                currentSolution = OrTreeRecursiveSearch(parseData,constraintChecker,attemptedSolution);

                //If it is solved then return the solution back up.
                if(currentSolution != null){
                    return currentSolution;
                }
            }
            else{
                //Failed constraints so this slot is not a possible solution.
                possibleSlots.remove(attemptedSlot);
            }
        }

        //No possible solution could be found, return null;
        return null;
    }

    /**
     * Ryan's Mutated Version of the OrTree.
     *
     *
     */
    public HashMap<Slot_Occupant,Slot> MutateRecursiveSearch(ParseData parseData, ConstraintChecker constraintChecker, Map<Slot_Occupant,Slot> parentSolution){

        LinkedHashMap<Slot_Occupant,Slot> solution = new LinkedHashMap<>();
        solution = initializePr(solution);

        LinkedHashMap<Slot_Occupant,Slot> startingMap = new LinkedHashMap<>(solution);

        Vector<Slot_Occupant> possibleSlotOccupantsToChange = parseData.getOccupants();
        Vector<Slot> possibleSlotsToChangeTo = parseData.getSlots();

        //eventually try all Slot_Occupants if a mutation can't be found.
        do{
            //Generated a random slot_occupant to mutate on
            Slot_Occupant mutatedOccupant = possibleSlotOccupantsToChange.elementAt(randGen.nextInt(possibleSlotOccupantsToChange.size()));

            //Remove the ones we have chosen
            possibleSlotOccupantsToChange.remove(mutatedOccupant);

            //See if it can work.
            solution = MutateRecursiveSearch(parseData,constraintChecker,parentSolution,startingMap,mutatedOccupant);

            //If it did work return the value as the mutated value
            if(solution != null){ return solution; }

        }while(!possibleSlotOccupantsToChange.isEmpty());

        return null; //No mutation was possible, this means that no one thing could be changed and a new solution made.

        //THIS COULD BE CHANGE TO GO TO (Try removing 2 things), and (3 things) and so on (until n things is chosen).
        //That would grantee answers.
    }

    public LinkedHashMap<Slot_Occupant,Slot> MutateRecursiveSearch(ParseData parseData, ConstraintChecker constraintChecker, Map<Slot_Occupant,Slot> parentSolution, LinkedHashMap<Slot_Occupant,Slot> currentSolution, Slot_Occupant mutatedSlot){

        //Return the current working solution if it is a solution ------------------------------------------------------
        if(isSolved(currentSolution)){
            return currentSolution;
        }

        Slot_Occupant workingOccupant = null;
        LinkedHashMap<Slot_Occupant, Slot> attemptedSolution = new LinkedHashMap<>(currentSolution);

        if(mutatedSlot == null) { //Mutated slot is made null after it is used once, so only on the first recursion.

            //Get first Slot_Occupant to work on, find first non_null element unless it's the first time which case, work for the mutated element
            for(Map.Entry entry : currentSolution.entrySet()){
                if(entry.getValue() == null){
                    workingOccupant = (Slot_Occupant)entry.getKey();
                    break;
                }
            }
            //See if we can copy the parent.
            attemptedSolution.put(workingOccupant, parentSolution.get(workingOccupant));
            if (constraintChecker.checkHardConstraints(attemptedSolution)) {
                return MutateRecursiveSearch(parseData, constraintChecker, parentSolution, attemptedSolution, mutatedSlot);
            }
        }
        else {
            workingOccupant = mutatedSlot;
        }
        //If you can't copy the parent get a List of all possible Slots for the found Course/Slot ----------------------
        Vector<Slot> possibleSlots;
        if(workingOccupant instanceof Course){
            possibleSlots = new Vector<>(parseData.Course_Slots);
        }else{
            possibleSlots = new Vector<>(parseData.Lab_Slots);
        }

        //Don't try the parent slot case;
        possibleSlots.remove(parentSolution.get(workingOccupant));

        //Begin While Loop ---------------------------------------------------------------------------------------------
        while(possibleSlots.size() != 0){

            //Randomly Select a possible slot
            Slot attemptedSlot = possibleSlots.elementAt(randGen.nextInt(possibleSlots.size()));

            //make a new solution with the attempted slot
            attemptedSolution = new LinkedHashMap<>(currentSolution);
            attemptedSolution.put(workingOccupant,attemptedSlot);

            //Check Constraints
            if(constraintChecker.checkHardConstraints(attemptedSolution)){

                //Recursive Step
                currentSolution = MutateRecursiveSearch(parseData,constraintChecker,parentSolution,attemptedSolution,null);

                //If it is solved then return the solution back up.
                if(currentSolution != null){
                    return currentSolution;
                }
            }
            else{
                //Failed constraints so this slot is not a possible solution.
                possibleSlots.remove(attemptedSlot);
            }
        }

        //No possible solution could be found, return null;
        return null;
    }

    public boolean isSolved(LinkedHashMap<Slot_Occupant,Slot> map){
        return !map.containsValue(null);
    }
    /**
     * This method will create the template for Candidate solution
     * The size of the map will be equal to Courses + Labs
     * The slot value for each course or lab will be null unless there is a partial assignment
     */
    private Map<Slot_Occupant, Slot> initializePr(){
        Vector<Slot_Occupant> all = this.parseData.Courses;
        all.addAll(this.parseData.Labs);

        Map<Slot_Occupant, Slot> data = new LinkedHashMap<>();
        all.forEach((item) -> data.put(item, null));


        HashMap<Slot_Occupant, Slot> allPartialAssignments = this.parseData.Partial_Assignments.getAllPartialAssignments();

        for(Map.Entry<Slot_Occupant, Slot> assignment : allPartialAssignments.entrySet()){
            if(data.containsKey(assignment.getKey())){
                data.put(assignment.getKey(), assignment.getValue());
                if(assignment.getKey() instanceof Course){
                    int currentMaxVal = this.copyOfCourseSlots.get(assignment.getValue());
                    this.copyOfCourseSlots.put(assignment.getValue(), currentMaxVal - 1 );
                }else{
                    int currentMaxVal = this.copyOfLabSlots.get(assignment.getValue());
                    this.copyOfLabSlots.put(assignment.getValue(), currentMaxVal - 1 );
                }
            }
        }

        return data;
    }

    private LinkedHashMap<Slot_Occupant, Slot> initializePr(LinkedHashMap<Slot_Occupant,Slot> givenData){
        Vector<Slot_Occupant> all = this.parseData.Courses;
        all.addAll(this.parseData.Labs);

        Map<Slot_Occupant, Slot> data = new LinkedHashMap<>();
        all.forEach((item) -> givenData.put(item, null));


        HashMap<Slot_Occupant, Slot> allPartialAssignments = this.parseData.Partial_Assignments.getAllPartialAssignments();

        for(Map.Entry<Slot_Occupant, Slot> assignment : allPartialAssignments.entrySet()){
            if(givenData.containsKey(assignment.getKey())){
                givenData.put(assignment.getKey(), assignment.getValue());
                if(assignment.getKey() instanceof Course){
                    int currentMaxVal = this.copyOfCourseSlots.get(assignment.getValue());
                    this.copyOfCourseSlots.put(assignment.getValue(), currentMaxVal - 1 );
                }else{
                    int currentMaxVal = this.copyOfLabSlots.get(assignment.getValue());
                    this.copyOfLabSlots.put(assignment.getValue(), currentMaxVal - 1 );
                }
            }
        }

        return givenData;
    }


    /**
     * Find all possible slots, s, applicable to the Slot Occupant to altern on in the parameter.
     * Then, for each slot, create a  OrTree  and check constrainsts before adding as a successor
     * node such that data.get(slotOccupant) = slot
     * @param currentTree
     * @param slotOccupantToAltern
     * @param shouldHaveMutationScore
     * @param parentTree
     * @param slotOccupantToMutateOn
     */
    public void createSuccessorNodes(OrTree currentTree,
                                     Slot_Occupant slotOccupantToAltern,
                                     boolean shouldHaveMutationScore,
                                     Map<Slot_Occupant, Slot> parentTree,
                                     Slot_Occupant slotOccupantToMutateOn) {

        boolean isSlotOccupantCourse = slotOccupantToAltern instanceof Course ;

        Vector<Slot> slots = isSlotOccupantCourse ? this.parseData.Course_Slots : this.parseData.Lab_Slots;


        // Create successor nodes:
        for (int i = 0; i < slots.size(); i++){
            Map<Slot_Occupant, Slot> newCandidate = new LinkedHashMap<>();
            newCandidate.putAll(currentTree.data);

            Map<Slot, Integer> copyOfCourseSlots = new LinkedHashMap<>();
            Map<Slot, Integer> copyOfLabSlots = new LinkedHashMap<>();
            copyOfCourseSlots.putAll(currentTree.courseSlotToMaxCapacity);
            copyOfLabSlots.putAll(currentTree.labSlotToMaxCapacity);

            Map<Slot, Integer> slotMapToUpdate = isSlotOccupantCourse ? copyOfCourseSlots : copyOfLabSlots;

            int maxVal = slotMapToUpdate.get(slots.get(i));
            if (maxVal > 0) {
                    newCandidate.put(slotOccupantToAltern, slots.get(i));
                    // add it to the current node's children
                    /* -------need to check constraints here before adding as a child  ----*/

                    if(constraintChecker.checkHardConstraints(newCandidate)) {

                        slotMapToUpdate.put(slots.get(i), maxVal - 1);
                        int score = 0;
                        if (shouldHaveMutationScore) {
                            score = getMutationScore(parentTree, newCandidate, slotOccupantToMutateOn);
                        }
                        currentTree.addChild(newCandidate, score, copyOfLabSlots, copyOfCourseSlots );
                    }
            }else{
              //  System.out.println("Unable to add slot reached max already: " +  slots.get(i));
            }


        }
        if(shouldHaveMutationScore){
            Collections.sort(currentTree.children, currentTree.valComparator);
        }else {
            Collections.shuffle(currentTree.children, Driver.random);
        }

    }




    /**
     * This method gives score to the new child created
     * If a course/lab does not have the  same slot as parent, then -1
     * If the course/slot that is supposed to be mutated is same as parent -1
     * If the course/slot that is supposed to be mutated is NOT same as parent +1;
     * @param parentTree
     * @param newChild
     * @param slotOccupantToMutateOn
     * @return
     */
    private int getMutationScore(Map<Slot_Occupant, Slot> parentTree,
                                 Map<Slot_Occupant, Slot> newChild,
                                 Slot_Occupant slotOccupantToMutateOn){

        int score = 0;
        Slot mutatedSlot = newChild.get(slotOccupantToMutateOn);
        for( Map.Entry<Slot_Occupant, Slot> newEntry : newChild.entrySet()){

            if(newEntry.getKey().equals(slotOccupantToMutateOn) && newChild.get(slotOccupantToMutateOn)!= null){
                if(parentTree.get(slotOccupantToMutateOn).equals(mutatedSlot)){
                    score--;
                }else{
                    score++;
                }
            }else{
                if(newEntry.getValue() != null && !newEntry.getValue().equals(parentTree.get(newEntry.getKey()))){
                    score--;
                }
            }
        }

        return score;
    }

    /**
     * This is the search control to be used by Set Based Search to create the initial population
     * This result a single Map<Slot_Occupant, Slot> which has passed all hard constraints check
     *
     * @return Map<Slot_Occupant, Slot> of a valid assignments of courses and labs ; if no solution found
     * returns null
     */
    public Map<Slot_Occupant, Slot> buildValidCandidateSol() {

        //using a stack to keep track of which nodes to visit next
        Stack<OrTree> depthTraversal = new Stack<>();
        //pushing the root tree into the stack
        depthTraversal.push(this.orTree);

        return findValidSolWithDepthFirstSearch(depthTraversal, false,
                null,
                null);

    }



    private Map<Slot_Occupant, Slot> findValidSolWithDepthFirstSearch(Stack<OrTree> depthTraversal,
                                                                      boolean shouldMutate,
                                                                      Map<Slot_Occupant, Slot> parentTree,
                                                                      Slot_Occupant slotOccupantToMutateOn) {
        //using a stack to keep track of which nodes to visit next
        Stack<OrTree> traversalStack = depthTraversal;

        OrTree currentTree = null;

        while (!traversalStack.empty()) {

            currentTree = traversalStack.pop();
          // System.out.println("Next child chosen: " + currentTree.toString());
            Slot_Occupant toExpandOn = null;
            Map<Slot_Occupant, Slot> currentPr = currentTree.data;

            // finding the first entry with null value for slot
            // TO:DO we can improve on this and start with the one with most contrainsts associated with it
            for (Map.Entry<Slot_Occupant, Slot> entry : currentPr.entrySet()) {

                if (entry.getValue() == null) {
                    toExpandOn = entry.getKey();
                    break;
                }
            }

            if (toExpandOn != null) {
                //creating all the possible children
                this.createSuccessorNodes(currentTree, toExpandOn, shouldMutate, parentTree, slotOccupantToMutateOn);
                for(int j = 0 ; j < currentTree.children.size(); j++){
                    traversalStack.push(currentTree.children.get(j));
                }
            }else { // need to check if all constraints are met here since there is nothing to expand on anymore
                     return currentPr;
            }
        }

        //exhausted the whole tree, no valid solution was found
        return null;
    }


    /**
     * This is the search control to produce a new mutated solution based on a parent solution
     * The control picks a course/slot at random and attempts to change the slot if it produces a viable
     * solution keeping rest of the course/slot assignment same as parent
     *
     * @param parentData
     * @return mutant valid solution or null if no other solution possible
     */
    public  Map<Slot_Occupant, Slot> mutateParentSolution( Map<Slot_Occupant, Slot> parentData){

        //choose the slot_occupant to mutate on
        Map<Slot_Occupant, Slot> initialDataForChild = new LinkedHashMap<>();
        initialDataForChild.putAll(parentData);
        Slot_Occupant [] allCoursesAndLabs = null; // parentData.keySet().stream().toArray(Slot_Occupant [] :: new); This errors out I don't know why.

        int randomIndex = Driver.random.nextInt(allCoursesAndLabs.length);
        Slot_Occupant randomSlotOccupantToMutateOn = allCoursesAndLabs[randomIndex];

        //don't want to overrite and mutate on the partial assignment slots
        while(this.initialPr.get(randomSlotOccupantToMutateOn) != null){
            randomIndex = Driver.random.nextInt(allCoursesAndLabs.length);
            randomSlotOccupantToMutateOn = allCoursesAndLabs[randomIndex];
        }


        System.out.println("------Randomly chosen course to mutate on: " + randomSlotOccupantToMutateOn.toString());
        System.out.println();

        Stack<OrTree> depthTraversal = new Stack<>();

        Map<Slot, Integer> initialLabSlots = new LinkedHashMap<>();
        Map<Slot, Integer> initialCourseSlots = new LinkedHashMap<>();
        initialLabSlots.putAll(this.copyOfLabSlots);
        initialCourseSlots.putAll(this.copyOfCourseSlots);

        this.orTree = new OrTree(this.initialPr, initialLabSlots, initialCourseSlots);
        OrTree currentTree = this.orTree;

        //creating the possible children for the slot we want to mutate first so it given priority
        this.createSuccessorNodes(currentTree, randomSlotOccupantToMutateOn, true , parentData, randomSlotOccupantToMutateOn);
        for(int j = 0 ; j < currentTree.children.size(); j++){
            depthTraversal.push(currentTree.children.get(j));
        }


        return findValidSolWithDepthFirstSearch(depthTraversal, true, parentData, randomSlotOccupantToMutateOn);

    }


}

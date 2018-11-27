import ParseData.ParseData;
import ParseData.Slot;
import Slot_Occupant.Course;
import Slot_Occupant.Slot_Occupant;

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
        private List<OrTree> children = new ArrayList<>();
        private int mutationSelectionScore;


        public OrTree(Map<Slot_Occupant, Slot> data){
            this.data = data;
        }
        //Comparator anonymous class implementation to compare the val for ortree
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
        void addChild(Map<Slot_Occupant, Slot> data, int val){
            OrTree child = new OrTree(data);
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
    private Map<Slot_Occupant, Slot> initialPr;




    /**
     * Main Constructor for initializing the OrTreeSearch
     * This should be called outside this class in order to call one of the
     * search controls in the Set Based Search
     * @param parseData
     */
    public OrTreeSearch(ParseData parseData){
        this.parseData = parseData;
        Map<Slot_Occupant, Slot> initialMap = initializePr();
        this.orTree = new OrTree(initialMap);
        this.initialPr = new LinkedHashMap<>();
        this.initialPr.putAll(initialMap);
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
            }
        }

        return data;
    }


    /**
     * Find all possible slots, s, applicable to the Slot Occupant in the parameter.
     * Then, for each slot, create a child OrTree such that data.get(slotOccupant) = slot
     * @param currentTree orTree to add the child nodes
     * @param slotOccupant the slot_occupant to apply altern on
     */
    public void createSuccessorNodes(OrTree currentTree, Slot_Occupant slotOccupant) {

        boolean isSlotOccupantCourse = slotOccupant instanceof Course ;

        Vector<Slot> slots = isSlotOccupantCourse ? this.parseData.Course_Slots : this.parseData.Lab_Slots;

        // Create successor nodes:
        for (int i = 0; i < slots.size(); i++){
            Map<Slot_Occupant, Slot> newCandidate = new LinkedHashMap<>();
            newCandidate.putAll(currentTree.data);
            newCandidate.put(slotOccupant, slots.get(i) );

            // add it to the current node's children
            /* -------need to check constraints here before adding as a child  ----*/

            currentTree.addChild(newCandidate, 0);
        }

        Collections.shuffle(currentTree.children, Driver.random);

    }


    /**
     * This method will be used in the mutatant building search control
     * Find all possible slots, s, applicable to the Slot Occupant in the parameter.
     * Calculates the score for the new child nodes based on the similarly to parent solution
     *
     * @param parentTree to immitate
     * @param slotOccupantToMutateOn the course/lab that needs to be different from the parent
     * @param slotOccupantToAltern  the course/lab to create alternative solutions for
     * @param currentTree current node to add successors to
     */
    public void createSuccessorsForMutation(Map<Slot_Occupant, Slot> parentTree,
                                            Slot_Occupant slotOccupantToMutateOn,
                                            Slot_Occupant slotOccupantToAltern,
                                            OrTree currentTree) {

        boolean isSlotOccupantCourse = slotOccupantToAltern instanceof Course ;

        Vector<Slot> slots = isSlotOccupantCourse ? parseData.Course_Slots : parseData.Lab_Slots;

        // Create successor nodes:
        for (int i = 0; i < slots.size(); i++){
            Map<Slot_Occupant, Slot> newCandidate = new LinkedHashMap<>();
            newCandidate.putAll(currentTree.data);
            newCandidate.put(slotOccupantToAltern, slots.get(i) );

            // add it to the current node's children
            /* -------need to check constraints here before adding as a child  ----*/

            //need the score here
            int score = getMutationScore(parentTree, newCandidate, slotOccupantToMutateOn);
            currentTree.addChild(newCandidate, score);
        }

        Collections.sort(this.orTree.children, this.orTree.valComparator);

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

    private boolean isPrSolved( Map<Slot_Occupant, Slot> data){

        //should call constraint check here
        return false;
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

        OrTree currentTree = null;
        depthTraversal.push(this.orTree);
        int i = 0;

        while (!depthTraversal.empty()) {
            i++;
            currentTree = depthTraversal.pop();
            System.out.println("Next child chosen: " + currentTree.toString());
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
                this.createSuccessorNodes(currentTree, toExpandOn);
                for(int j = 0 ; j < currentTree.children.size(); j++){
                    depthTraversal.push(currentTree.children.get(j));
                }
            }else { // need to check if all constraints are met here since there is nothing to expand on anymore
                if (i % 2 == 0) {
                    return currentPr;
                }
            }
        }
        // traversed the whole tree but no result was result, no solution
        return null;

    }


    //TO:DO
    public  Map<Slot_Occupant, Slot> mutateParentSolution( Map<Slot_Occupant, Slot> parentData){

        //choose the slot_occupant to mutate on
        Map<Slot_Occupant, Slot> initialDataForChild = new LinkedHashMap<>();
        initialDataForChild.putAll(parentData);
        Slot_Occupant [] allCoursesAndLabs = parentData.keySet().stream().toArray(Slot_Occupant []::new);

        int randomIndex = Driver.random.nextInt(allCoursesAndLabs.length);
        Slot_Occupant randomSlotOccupantToMutateOn = allCoursesAndLabs[randomIndex];

        //don't want to mutate on the partial assignment slots
        while(this.initialPr.get(randomSlotOccupantToMutateOn) != null){
            randomIndex = Driver.random.nextInt(allCoursesAndLabs.length);
            randomSlotOccupantToMutateOn = allCoursesAndLabs[randomIndex];
        }

        System.out.println("Randomly chosen course to mutate on: " + randomSlotOccupantToMutateOn.toString());

//        //making the course/lab corresponding slot null, so we can try out different combinations leaving the rest intact
//        initialDataForChild.put(randomSlotOccupantToMutateOn, null);
//        this.orTree.data.putAll(initialDataForChild);
//        Map<Slot_Occupant, Slot> possibleMutation = this.buildValidCandidateSol();
//
//        //if a viable mutant was produced return it
//        if(possibleMutation != null && !possibleMutation.equals(parentData)){
//            return possibleMutation;
//        }

        // at this point there is no viable mutant with just changing one course
        // we will try to produce a different mutant keeping it as close as possible to the parent


        //using a stack to keep track of which nodes to visit next
        Stack<OrTree> depthTraversal = new Stack<>();

        OrTree currentTree = null;
        this.orTree = new OrTree(this.initialPr);
        depthTraversal.push(this.orTree);

        while (!depthTraversal.empty()) {
            currentTree = depthTraversal.pop();
            System.out.println("Next child chosen in mutation: " + currentTree.toString());
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
                //creating all the possible viable children
                this.createSuccessorsForMutation(parentData, randomSlotOccupantToMutateOn,toExpandOn, currentTree);
                for(int j = 0 ; j < currentTree.children.size(); j++){
                    depthTraversal.push(currentTree.children.get(j));
                }
            }else { // need to check if all constraints are met here since there is nothing to expand on anymore
                return currentPr;
            }
        }


        // traversed the whole tree but no result was result, no solution
        return null;

    }


}

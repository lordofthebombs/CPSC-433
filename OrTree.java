import ParseData.ParseData;
import ParseData.Slot;
import Slot_Occupant.Course;
import Slot_Occupant.Slot_Occupant;

import java.util.*;

/**
 *
 */
public class OrTree {

    private Map<Slot_Occupant, Slot> data;
    private OrTree parent;
    private List<OrTree> children = new ArrayList<>();
    private ParseData parseData;
    private int randomVal;
    private int index;
    private boolean isNotSolvable;

    /**
     * Constructor for using within this class
     * meant to use for creating a child node
     * @param data
     */
    public OrTree(ParseData parseData, Map<Slot_Occupant, Slot> data){
        this.data = data;
        this.parseData = parseData;
    }


    /**
     * Main Constructor for initializing the OrTree
     * @param parseData
     */
    public OrTree(ParseData parseData){
        this.parseData = parseData;
        initializePr();
    }


    public int getRandomVal(){
        return this.randomVal;
    }

    public void setRandomVal(int val){
        this.randomVal = val;
    }


    /**
     * Method to add a child to a parent node
     * @param data
     */
    void addChild(Map<Slot_Occupant, Slot> data){
        OrTree child = new OrTree(this.parseData, data);
        child.randomVal = Driver.random.nextInt(100);
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
            if(data.containsKey(assignment.getKey())){
                data.put(assignment.getKey(), assignment.getValue());
            }
        }

        this.data = data;
    }

    /**
     * Find all possible slots, s, applicable to the Slot Occupant in the parameter.
     * Then, for each slot, create a child OrTree such that data.get(slotOccupant) = slot
     * @param
     */
    public void createSuccessorNodes(Slot_Occupant slotOccupant) {

        boolean isSlotOccupantCourse = slotOccupant instanceof Course ;

        Vector<Slot> slots = isSlotOccupantCourse ? parseData.Course_Slots : parseData.Lab_Slots;

        // Create successor nodes:
        for (int i = 0; i < slots.size(); i++){
            Map<Slot_Occupant, Slot> newCandidate = new LinkedHashMap<>();
            newCandidate.putAll(this.data);

            slots.get(i).max--;
            newCandidate.put(slotOccupant, slots.get(i) );

            // add it to the current node's children
            /* -------need to check constraints here before adding as a child  ----*/
                this.addChild(newCandidate);
        }

        Collections.sort(this.children, valComparator);

    }


    private boolean isPrSolved( Map<Slot_Occupant, Slot> data){

        return false;
    }

    /**
     * This is the search control to create a valid solution for a given OrTree with initial Pr
     * including partial assignment
     * @return a valid Map<Slot_Occupant, Slot>
     */
    public  Map<Slot_Occupant, Slot> buildValidCandidateSol(){

        //using a stack to keep track of which nodes have been visited so far
        Stack<OrTree>  depthTraversal = new Stack<>();

        OrTree currentTree = this;
        depthTraversal.push(this);
        int i = 0 ;

        while(!depthTraversal.empty()) {
            i++;
            Slot_Occupant toExpandOn = null;
            Map<Slot_Occupant, Slot> currentPr = currentTree.data;
            // finding the first entry with null value for slot
            for (Map.Entry<Slot_Occupant, Slot> entry : currentPr.entrySet()) {

                if (entry.getValue() == null) {
                    toExpandOn = entry.getKey();
                    break;
                }
            }

            if (toExpandOn != null) {
                OrTree nextChildToVisit = null;
                //creating all the possible children
                currentTree.createSuccessorNodes(toExpandOn);
                if(currentTree.index < currentTree.children.size()) {
                    nextChildToVisit = currentTree.children.get(currentTree.index);
                }
                currentTree.index++;
                System.out.println("Next child chosen: " +  nextChildToVisit.toString());
                currentTree = nextChildToVisit;
                //putting in the stack to mark it as visited
                depthTraversal.push(nextChildToVisit);

            }else{   // there is nothing left to expand on, all the slot_occupant in the map has a slot assigned
                // need to check if all constraints are met here
                if(i % 2 == 0){
                    return currentPr;
                }else{
                    //getting the leaf out of the stack that was visited last
                    //OrTree lastVisited = depthTraversal.pop();

                    OrTree nextChildToVisit = getNextLeafToVisit(depthTraversal);

                    if(nextChildToVisit == null) {
                            continue;
                    }else {
                            System.out.println("Next child chosen: " + nextChildToVisit.toString());
                            currentTree = nextChildToVisit;
                            depthTraversal.push(nextChildToVisit);
                        }
                    }

                }

            }



        return null;

    }

    private OrTree getNextLeafToVisit(Stack<OrTree> leafStack) {
        OrTree nextChildToVisit = null;
        while(nextChildToVisit == null && !leafStack.empty()) {
            OrTree lastVisited = leafStack.pop();
            OrTree parentOfLastVisited = lastVisited.parent;
            if (parentOfLastVisited != null
                    && parentOfLastVisited.children.size() > 0
                    && parentOfLastVisited.index < parentOfLastVisited.children.size()) {
                nextChildToVisit = parentOfLastVisited.children.get(parentOfLastVisited.index);
                parentOfLastVisited.index++;
            }
        }
        return nextChildToVisit;
    }


    //Comparator anonymous class implementation
    public static Comparator<OrTree> valComparator = new Comparator<OrTree>(){

        @Override
        public int compare(OrTree t1, OrTree t2) {
            return (int) (t1.getRandomVal() - t2.getRandomVal());
        }
    };

    //utility method to add random data to Queue
    private void addLeafToQueue(Queue<OrTree> leafPriorityQueue, OrTree leaf) {
        Random rand = new Random();
        leaf.setRandomVal(rand.nextInt(1000));
        leafPriorityQueue.add(leaf);

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

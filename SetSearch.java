import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import java.util.Map;
import java.util.Random;
import Slot_Occupant.*;
import ParseData.*;

public class SetSearch{

  private static final int INIT_POPULATION = 100;
  private static final int MAX_FACTS = 1000;
  private static final int MAX_REPEATS = 3;
  private static final int MAX_CHILDREN_PER_PARENT = 10;

  //possibly use a heap?
  private List<Pair<Map<Slot_Occupant, Slot>, Integer>> workingSet;
  // Map<Slot_Occupant, Slot> is waht the or tree outputs
  // Integer is what the eval function gives
  private ParseData data;
  private OrTree solGen;
  private Random randGen;
  private int generation;


  public SetSearch(ParseData data){
    this.data = data;
    solGen = new OrTree(data);
    workingSet = new ArrayList<Pair<Map<Slot_Occupant, Slot>, Integer>>();
    randGen = new Random(System.currentTimeMillis());
    generation = 1;
    int repeats = 0;

    for(int i = 0; i < INIT_POPULATION && repeats < INIT_POPULATION/2; i++){ //stop genaration if ortree cant produce different solutions
      Map<Slot_Occupant, Slot> out = solGen.buildValidCandidateSolution();
      if(!addToSet(out)){
        i--;
        repeats++;
      }
    }

  }

  //keeps running untill a genaration has passed
  public void runGeneration(){
    int curgen = generation;
    while(curgen == generation){
      searchControl();
    }
  }

  //decides whether to mutate or trim
  private void searchControl(){
    if(workingSet.size() < MAX_FACTS){
      mutate();
    }
    else{
      trim();
      generation++;
    }
  }

  //returns... the best solution...
  public Pair<Map<Slot_Occupant, Slot>, Integer> getBestSolution(){
    return null;
  }

  //evaluates a particular solution
  private Integer eval(Map<Slot_Occupant, Slot> solution){
    //SHOULD USE PARSEDATA TO EVALUATE IT
    return new Integer(0);
  }

  private boolean addToSet(Map<Slot_Occupant, Slot> solution){
    Integer score = eval(solution);
    Pair<Map<Slot_Occupant, Slot>, Integer> fin = new Pair(solution, score);
    if(workingSet.contains(fin)){
      return false;
    }
    workingSet.add(fin);
  }

  //mutates upon a random fact
  private void mutate(){
    int repeats = 0;
    Map<Slot_Occupant, Slot> parent = workingSet.get(randGen.nextInt(workingSet.size()));

    for(int i = 0; i < MAX_CHILDREN_PER_PARENT && repeats < MAX_REPEATS && workingSet.size() < MAX_FACTS; i++){
      Map<Slot_Occupant, Slot> child = solGen.mutateParentSolution(parent);
      if(!addToSet(child)){
        i--;
        repeats++;
      }
    }
  }

  //removes facts with lowest scores
  private void trim(){

  }




}

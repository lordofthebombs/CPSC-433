import java.util.List;
import javafx.util.Pair;
import java.util.Map;
import Slot_Occupant.*;
import ParseData.*;

public class SetSearch{

  private static final INIT_POPULATION = 100;
  private static final MAX_FACTS = 1000;

  //possibly use a heap?
  private List<Pair<Map<Slot_Occupant, Slot>, Integer>> workingSet;
  // Map<Slot_Occupant, Slot> is waht the or tree outputs
  // Integer is what the eval function gives
  private ParseData data
  private int generation;


  public SetSearch(ParseData data){
    this.data = data;
    OrTree init = new OrTree(data);
    for(int i = 0; i < INIT_POPULATION; i++){
      Map<Slot_Occupant, Slot> out = init.buildValidCandidateSolution();
      //MAKE SURE IT IS NOT INSIDE ALREADY!!!
      //READ THIS
      Integer score = eval(out);
      Pair<Map<Slot_Occupant, Slot>, Integer> fin = new Pair(out, score)
      workingSet.add(fin);
    }
    generation = 1;
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

  //mutates upon a random fact
  private void mutate(){
    
  }

  //removes facts with lowest scores
  private void trim(){

  }




}

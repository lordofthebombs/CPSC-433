import java.util.ArrayList;
import javafx.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Random;
import Slot_Occupant.*;
import ParseData.*;
import OrTree.*;

public class SetSearch{

  private static final int INIT_POPULATION = 100;
  private static final int MAX_FACTS = 1000;
  private static final int TRIM_NUM = 800; //number of facts after trim, maxfacts-trimnum=num of facts removed
  private static final int MAX_REPEATS = 3;
  private static final int MAX_CHILDREN_PER_PARENT = 10;

  /*
  //possibly use a heap?
  private ArrayList<Pair<Map<Slot_Occupant, Slot>, Integer>> workingSet;
  // Map<Slot_Occupant, Slot> is waht the or tree outputs
  // Integer is what the eval function gives

  private OrTreeSearch solGen;
  private Random randGen;
  private int generation;
  private Eval eval;


  public SetSearch(ParseData data, String file){
    makeEval(data, file);
    solGen = new OrTreeSearch(data);
    workingSet = new ArrayList<Pair<Map<Slot_Occupant, Slot>, Integer>>(MAX_FACTS);
    randGen = new Random(System.currentTimeMillis());
    generation = 1;
    int repeats = 0;

    for(int i = 0; i < INIT_POPULATION && repeats < INIT_POPULATION/2; i++){ //stop genaration if ortree cant produce different solutions
      Map<Slot_Occupant, Slot> out = solGen.OrTreeRecursiveSearch();
      if(!addToSet(out)){
        i--;
        repeats++;
      }
    }

  }

  private void makeEval(ParseData data, String file){
    double labsmin, coursemin, notpaired, section;
    try{
      Scanner in = new Scanner(new File(file));
      labsmin = in.nextInt();
      coursemin = in.nextInt();
      notpaired = in.nextInt();
      section = in.nextInt();
    }
    catch(Exception e){
      System.out.println("Penelties file is bad: " + e);
      exit(-1);
    }
    eval = new Eval(data, labsmin, coursemin, notpaired, section);
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
    return workingSet.get(0);
  }

  //adds solution to set if it is not contained already
  //returns true if succesfull
  //SORTS IN DECENDING ORDER BASED ON EVAL SCORE
  //did it in decending order because we SHOULD be getting higher scores as we go
  //so that shortens the loop
  private boolean addToSet(Map<Slot_Occupant, Slot> solution){
    Integer score = eval.eval(solution);
    Pair<Map<Slot_Occupant, Slot>, Integer> fin = new Pair(solution, score);
    if(workingSet.contains(fin)){
      return false;
    }

    int setsize = workingSet.size();
    for(int i = 0; i <= setsize; i++){
      if(i == setsize){
        workingSet.add(fin);      //reached the end, add it to the end
      }
      else if(score >= workingSet.get(i).getValue()){  //sorted in decending order by eval score
        workingSet.add(i, fin);
        break;
      }
    }

  }

  //mutates upon a random fact
  private void mutate(){
    int repeats = 0;
    Map<Slot_Occupant, Slot> parent = workingSet.get(randGen.nextInt(workingSet.size())).getKey();

    for(int i = 0; i < MAX_CHILDREN_PER_PARENT && repeats < MAX_REPEATS && workingSet.size() < MAX_FACTS; i++){
      Map<Slot_Occupant, Slot> child = solGen.mutateSearch(parent);
      if(!addToSet(child)){
        i--;
        repeats++;
      }
    }
  }

  //removes facts with lowest scores
  private void trim(){
    workingSet.subList(TRIM_NUM, workingSet.size()).clear();
  }

*/

}

package SetBased;

import java.io.File;
import java.util.*;

import javafx.util.Pair;
import Slot_Occupant.*;
import ParseData.*;
import OrTree.*;

import static java.lang.System.exit;

public class SetSearch{

  //CONFIG////////////////////////////////////////
  private static final int INIT_POPULATION = 10;
  private static final int MAX_FACTS = 1000;
  private static final int TRIM_NUM = 800; //number of facts after trim, maxfacts-trimnum=num of facts removed
  private static final int MAX_REPEATS = 3;
  private static final int MAX_CHILDREN_PER_PARENT = 10;
  //CONFIG ENDS//////////////////////////////////

  //possibly use a heap?
  private ArrayList<Pair<Map<Slot_Occupant, Slot>, Double>> workingSet;
  // Map<Slot_Occupant, Slot> is waht the or tree outputs
  // Integer is what the eval function gives
  private ArrayList<Integer> badParents;

  private OrTreeSearch solGen;
  private Random randGen;
  private int generation;
  private Eval eval;
  private boolean done;


  public SetSearch(ParseData data, String file){
    makeEval(data, file);
    solGen = new OrTreeSearch(data);
    workingSet = new ArrayList<Pair<Map<Slot_Occupant, Slot>, Double>>(MAX_FACTS);
    badParents = new ArrayList<Integer>();
    randGen = new Random(System.currentTimeMillis());
    done = false;
    generation = 1;
    int repeats = 0;


    for(int i = 0; i < INIT_POPULATION && repeats < INIT_POPULATION/2; i++){ //more leniant on repeats because we want as much as possible initialy
      Map<Slot_Occupant, Slot> out = solGen.OrTreeRecursiveSearch();
      if(out == null){
        done = true;
        repeats = INIT_POPULATION;
      }
      if(!addToSet(out)){
        i--;
        repeats++;
      }
    }

  }

  private void makeEval(ParseData data, String file){
    double coursemin =0, preference =0, notpaired =0, section =0;
    try{
      Scanner in = new Scanner(new File(file));
      coursemin = in.nextInt();
      preference = in.nextInt();
      notpaired = in.nextInt();
      section = in.nextInt();
    }
    catch(Exception e){
      System.out.println("Penelties file is bad: " + e);
      exit(-1);
    }
    eval = new Eval(data,coursemin, preference ,notpaired, section);
  }

  //keeps running untill a genaration has passed or search can't continue
  //returns true if generation ran succesfully
  public boolean runGeneration(){
    int curgen = generation;
    while(curgen == generation && !done){
      searchControl();
    }
    return !done;
  }
  
  public int getGen(){
      return generation;
  }

  //decides whether to mutate or trim
  private void searchControl(){
    if(workingSet.size() < MAX_FACTS){
      while(workingSet.size() != badParents.size() && !mutate()){ //mutate untill all parents are deemed bad or mutation is sucessfull
      }

      if(workingSet.size() == badParents.size()){         //can no longer mutate, search has ended
        done = true;
      }
    }
    else{
      trim();
      generation++;
    }
  }

  //returns... the best solution...
  public Pair<Map<Slot_Occupant, Slot>, Double> getBestSolution(){
    return workingSet.get(0);
  }

  //adds solution to set if it is not contained already
  //returns true if succesfull
  //SORTS IN ACCENDING ORDER BASED ON EVAL SCORE
  //did it because we SHOULD be getting lower scores as we go
  //so that shortens the loop
  private boolean addToSet(Map<Slot_Occupant, Slot> solution){
    double score = eval.eval(solution);
    Pair<Map<Slot_Occupant, Slot>, Double> fin = new Pair(solution, score);
    if(workingSet.contains(fin)){
      return false;
    }

    int setsize = workingSet.size();
    for(int i = 0; i <= setsize; i++){
      if(i == setsize){
        workingSet.add(fin);      //reached the end, add it to the end
      }
      else if(score <= workingSet.get(i).getValue()){  //sorted in decending order by eval score
        workingSet.add(i, fin);
        break;
      }
    }
     return true;
  }

  //mutates upon a random fact
  //returns true if mutation was succesfull
  private boolean mutate(){
    int repeats = 0;
    int size = workingSet.size();
    int randnum;

    //select a parent that has not been deemed bad
    do{
      randnum = randGen.nextInt(workingSet.size());
    }while(badParents.contains(randnum));

    Map<Slot_Occupant, Slot> parent = workingSet.get(randnum).getKey();

    for(int i = 0; i < MAX_CHILDREN_PER_PARENT && repeats < MAX_REPEATS && workingSet.size() < MAX_FACTS; i++){
      Map<Slot_Occupant, Slot> child = solGen.mutateSearch(parent); //acquire a mutant
      if(child == null) break;          //parent is exhausted

      if(!addToSet(child)){
        i--;
        repeats++;
      }
    }

    if(size == workingSet.size()){  //parent didnt contribute to facts, it is a bad parent
      badParents.add(randnum);
      return false;
    }
    else{
      badParents.clear();
      return true;
    }
  }

  //removes facts with lowest scores
  private void trim(){
    workingSet.subList(TRIM_NUM, workingSet.size()).clear();
  }



}

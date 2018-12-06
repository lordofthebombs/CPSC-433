package SetBased;

import java.io.*;
import java.util.*;

import javafx.util.Pair;
import Slot_Occupant.*;
import ParseData.*;
import OrTree.*;


public class SetSearch{

  //CONFIG////////////////////////////////////////
  private static final int INIT_POPULATION = 200;
  private static final int MAX_FACTS = 2000;
  private static final int TRIM_NUM = 1500; //number of facts after trim, maxfacts-trimnum=num of facts removed
  private static final int MAX_REPEATS = 3;
  private static final int MAX_CHILDREN_PER_PARENT = 10;
  //CONFIG ENDS//////////////////////////////////

  //possibly use a heap?
  private ArrayList<Pair<Map<Slot_Occupant, Slot>, Double>> workingSet;
  // Map<Slot_Occupant, Slot> is waht the or tree outputs
  // Integer is what the eval function gives
  private ArrayList<Integer> badParents;


  //private OrTreeSearch solGen;
  public Faster_OrTree solGen;
  private Random randGen;
  private int generation;
  private Eval eval;
  private boolean done;

 public class ExhaustedError extends Exception{

     public Pair<Map<Slot_Occupant, Slot>, Double> solution ;

     ExhaustedError(Pair<Map<Slot_Occupant, Slot>, Double> solution ){
         this.solution = solution;
     }

 }

  public SetSearch(ParseData data, String file) throws ExhaustedError {
    makeEval(data, file);
    solGen = new Faster_OrTree(data);
    workingSet = new ArrayList<Pair<Map<Slot_Occupant, Slot>, Double>>(MAX_FACTS);
    badParents = new ArrayList<Integer>();
    randGen = new Random(System.currentTimeMillis());
    done = false;
    generation = 1;
    int repeats = 0;


    for(int i = 0; i < INIT_POPULATION && repeats < INIT_POPULATION/2; i++){ //more leniant on repeats because we want as much as possible initialy
      Map<Slot_Occupant, Slot> out = solGen.fasterSearch();
      if(out == null){
        done = true;
        throw new ExhaustedError(getBestSolution());
      }
      else if(!addToSet(out)){
        i--;
        repeats++;
      }
    }

  }

  public void resetOrTree(){
    solGen.reset();
  }

  private void makeEval(ParseData data, String file){
    double minFilledWeight = 0, prefWeight = 0, notPairedWeight = 0, secDiffWeight = 0;
    double pen_coursemin = 0 , pen_labmin = 0, pen_notPaired = 0 , pen_section = 0;

    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(new FileReader(new File(file)));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        line = line.replaceAll("\\s", "");
        String[] setting = line.split("=");
        if (setting[0].equals("minFilledWeight")) {
          minFilledWeight = Double.parseDouble(setting[1]);
        } else if (setting[0].equals("prefWeight")) {
          prefWeight = Double.parseDouble(setting[1]);
        } else if (setting[0].equals("notPairedWeight")) {
          notPairedWeight = Double.parseDouble(setting[1]);
        } else if (setting[0].equals("secDiffWeight")) {
          secDiffWeight = Double.parseDouble(setting[1]);
        } else if (setting[0].equals("pen_courseMin")) {
          pen_coursemin = Double.parseDouble(setting[1]);
        } else if (setting[0].equals("pen_labMin")) {
          pen_labmin = Double.parseDouble(setting[1]);
        } else if (setting[0].equals("pen_notPaired")) {
          pen_notPaired = Double.parseDouble(setting[1]);
        } else if (setting[0].equals("pen_section")) {
          pen_section = Double.parseDouble(setting[1]);
        }
      }

    } catch ( IOException e){
      System.out.println("Unable to load config file " + e);
      System.exit(0);
    }
    // Always close files.
    try {
      bufferedReader.close();
    } catch (IOException e) {
      System.out.println("Unable to close config file resource " +  e);
    }

    eval = new Eval(data,
                minFilledWeight,
                prefWeight,
                notPairedWeight,
                secDiffWeight,
                pen_coursemin,
                pen_labmin,
                pen_notPaired,
                pen_section);
  }

  //keeps running untill a genaration has passed or search can't continue
  //returns true if generation ran succesfully
  public boolean runGeneration() throws ExhaustedError {
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
  private void searchControl() throws ExhaustedError {
    if(workingSet.size() < MAX_FACTS){
      while(workingSet.size() != badParents.size() && !mutate()){ //mutate untill all parents are deemed bad or mutation is sucessfull
      }

      if(workingSet.size() == badParents.size()){         //can no longer mutate, search has ended
        done = true;
        throw new ExhaustedError(getBestSolution());
      }
    }
    else{
      trim();
      generation++;
    }
  }

  //returns... the best solution...
  public Pair<Map<Slot_Occupant, Slot>, Double> getBestSolution(){
      if(workingSet.size() > 0) {
          return workingSet.get(0);
      }

      return  null;
  }

  //adds solution to set if it is not contained already
  //returns true if succesfull
  //SORTS IN ACCENDING ORDER BASED ON EVAL SCORE
  //did it because we SHOULD be getting lower scores as we go
  //so that shortens the loop
  private boolean addToSet(Map<Slot_Occupant, Slot> solution){
    double score = eval.eval(solution);
    Pair<Map<Slot_Occupant, Slot>, Double> fin = new Pair(solution, score);

  /*  if(workingSet.size() > 0)
        if(score <= workingSet.get(0).getValue()){
            System.out.println(score + " " + workingSet.size());
        }
*/

    int setsize = workingSet.size();
    for(int i = 0; i <= setsize; i++){
      if(i == setsize){
        workingSet.add(fin);      //reached the end, add it to the end
      }
      else if(score < workingSet.get(i).getValue()){  //sorted in decending order by eval score
        workingSet.add(i, fin); //add if score is strictly less than
        break;
      }
      else if(score == workingSet.get(i).getValue())
          if(workingSet.get(i).equals(fin))
            return false;

    }
     return true;
  }

  //mutates upon a random fact
  //returns true if mutation was succesfull
  private boolean mutate() {
    int repeats = 0;
    int size = workingSet.size();
    int randnum;


    //select a parent that has not been deemed bad
    do{
      randnum = randGen.nextInt(workingSet.size());
    }while(badParents.contains(randnum));

    Map<Slot_Occupant, Slot> parent = workingSet.get(randnum).getKey();

    for(int i = 0; i < MAX_CHILDREN_PER_PARENT && repeats < MAX_REPEATS && workingSet.size() < MAX_FACTS; i++){
      Map<Slot_Occupant, Slot> child = solGen.fasterMutate(parent); //acquire a mutant
      if(child == null) {
          break;
      }
      //parent is exhausted

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

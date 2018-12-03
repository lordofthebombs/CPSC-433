package OrTree;

import ParseData.*;
import Parser.*;
import Slot_Occupant.*;

import java.io.FileNotFoundException;
import java.util.*;

public class testDriver {


    public static void main(String args[]){

        try {
            ParseData p = Parser.parse("medFile.txt");

            Faster_OrTree generator = new Faster_OrTree(p);
            OrTreeSearch test = new OrTreeSearch(p);

            ArrayList<Map<Slot_Occupant,Slot>> sol1 = new ArrayList<>();
            ArrayList<Map<Slot_Occupant,Slot>> sol2 = new ArrayList<>();

            Map<Slot_Occupant, Slot> solution = new LinkedHashMap<>();
            HashSet<Map<Slot_Occupant,Slot>> unique = new HashSet<>();
            ConstraintChecker c = new ConstraintChecker(p);
            long start1 = System.currentTimeMillis();

            for(int x = 0; x < 5000; x++){

                solution = generator.fasterSearch();

                //solution = test.OrTreeRecursiveSearch();
                if(solution == null){
                    System.out.println("Tree Exhausted " + x);
                    break;
                }
                if(!unique.add(solution)){
                    System.out.println("NON UNIQUE");
                    //break;
                }
               // printSolution(solution);

                System.out.println("Found Solution");

                solution = generator.fasterMutate(solution);
                if(solution == null){
                    System.out.println("Tree Exhausted " + x);
                    break;
                }

                if(!c.checkHardConstraints(solution)){
                    System.out.println("Hard Constraints do not hold");
                    break;
                }
               // printSolution(solution);
                System.out.println("Mutated");

                if(!unique.add(solution)){
                   System.out.println("NON UNIQUE");
                   //break;
                }
//                else {
//                    sol1.add(solution);
//                }

                //System.out.println("Mutation ----------------------------------");
                //printSolution(solution);
                //System.out.println("-------------------------------------------");
               // System.out.println("Found Solution " + x);
            }

            long end1,start2 = System.currentTimeMillis();
            end1 = start2;
           //unique.clear();

            for(int x = 0; x < 0; x++){

                solution = test.OrTreeRecursiveSearch();

                //solution = test.OrTreeRecursiveSearch();
                if(solution == null){
                    System.out.println("Tree Exhausted" + x);
                    break;
                }

                if(!c.checkHardConstraints(solution)){
                    System.out.println("Hard Constraints do not hold");
                    break;
                }


                if(!unique.add(solution)){
                    System.out.println("NON UNIQUE");
                    //break;
                }
                else {
                    sol2.add(solution);
                }

                //printSolution(solution);
                System.out.println("Found Solution " + x);
            }

            //long end2 = System.currentTimeMillis();
            System.out.println("Fast Time = " + ((end1 - start1)/1000) + " U: " + unique.size());
            //System.out.println("Original Time = " + ((end2 - start2)/1000));

        }catch (FileNotFoundException e){
            System.out.println("No such file");
        }catch (ParseError e){
            e.print();
        }

    }

    private static void printSolution(Map<Slot_Occupant, Slot> mutant) {
        Map<Slot_Occupant, Slot> map = new TreeMap<>(new Comparator<Slot_Occupant>() {
            @Override
            public int compare(Slot_Occupant o1, Slot_Occupant o2) {
                if( o1 == o2 )
                    return 0;
                if( o1 == null )
                    return 1;
                if( o2 == null )
                    return -1;
                return o1.toString().compareTo( o2.toString());
            }
        });
        map.putAll(mutant);
       // System.out.println("Eval-value: " + evalVal);
        for (Map.Entry<Slot_Occupant, Slot> entry : map.entrySet()) {
            System.out.printf("%-25s : %-10s%n", entry.getKey(), entry.getValue());
        }
    }
}

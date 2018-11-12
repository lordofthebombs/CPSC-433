package Parser;

import ParseData.ParseData;
import Slot_Occupant.Slot_Occupant;
import Slot_Occupant.*;

import java.io.*;

enum ReadState{
  Name,
  Course_Slots,
  Lab_Slots,
  Courses,
  Labs,
  Not_Compatable,
  Unwanted,
  Preferences,
  Pair,
  Partial_Assignments
};

public class Parser {

    public static ParseData parse(String fileName) throws FileNotFoundException, ParseError{

        ReadState currentState = ReadState.Name;
        ParseData output = new ParseData();
        BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
        int currentLineNum = 0;

        try {
            String currentLine = in.readLine();
            currentLineNum++;

            /*
                This main loop is going to dictate the flow of control, and determines
                'what is being read at the moment'. This has to be done because the order
                of labs/lab slots etc might not always be the same.

                If an error needs to be thrown, create a new ParseError object and throw it.

                    i.e. throw new ParseError("Missing Name at top of file");

                !!!!!! IMPORTANT !!!!!

                Every sub function should return so that the next line being read is a
                header section for the next 'type' i.e the next line to be read after processing
                data should be "Courses:"

                The current state variable may be unnecessary, it's exists to make sure things
                appear in a certain order if they do appear. I.e the name must appear first.


             */

            while(currentLine != null) //read line returns Null at end of file.
            {
                currentLine = currentLine.toLowerCase();

                if(currentLine.equals("name:")){
                        //Do nothing but read until the next category, this information is useless.
                }
                else if(currentLine.equals("course slots:")){
                    currentState = ReadState.Course_Slots;
                    processCourseSlots(output);
                }
                else if(currentLine.equals("lab slots:")){
                    currentState = ReadState.Lab_Slots;
                    processLabSlots(output);
                }
                else if(currentLine.equals("courses:")){
                    currentState = ReadState.Courses;
                    processCourses(output);
                }
                else if(currentLine.equals("labs:")){
                    currentState = ReadState.Labs;
                    processLabs(output);
                }
                else if(currentLine.equals("not compatible:")){
                    currentState = ReadState.Not_Compatable;
                    processNotCompat(output);
                }
                else if(currentLine.equals("unwanted:")){
                    currentState = ReadState.Unwanted;
                    processUwanted(output);
                }
                else if(currentLine.equals("preferences:")){
                    currentState = ReadState.Preferences;
                    processPreferences(output);
                }
                else if(currentLine.equals("pair:")){
                    currentState = ReadState.Pair;
                    processPairs(output);
                }
                else if(currentLine.equals("partial assignments:")){
                    currentState = ReadState.Partial_Assignments;
                    processPartialAssignments(output);
                }
                else{
                    throw new ParseError("File is ill formatted, met unexpected String: '" + currentLine + "' at line: " + currentLineNum);
                }

                currentLine = in.readLine();
                currentLineNum++;
            }

        }
        catch (IOException e){
            System.out.println("Failed to read a new line");
        }
        catch (ParseError e){
            e.print();
            throw new ParseError("File Failed to Parse");
        }

        return output;
    }

    /*
           all of these process functions read in and put data into the given ParseData object.
           The first 'readLine' is assumed to the the first line after the title of the section,
           which should have already been read in.
     */
    private static void processCourseSlots(ParseData data){

    }
    private static void processLabSlots(ParseData data){

    }
    private static void processCourses(ParseData data){

    }
    private static void processLabs(ParseData data){

    }
    private static void processNotCompat(ParseData data){

    }
    private static void processUwanted(ParseData data){

    }
    private static void processPreferences(ParseData data){

    }
    private static void processPairs(ParseData data){

    }
    private static void processPartialAssignments(ParseData data){

    }

    /*
        These are helper functions to help with the process data functions
     */
    private static Slot_Occupant makeCourseIdentifier(String identifier){
        String id = ""; //SENG,CPSC Etc.
        int courseNum = 0, lectSection = 0;

        Course newCcurse = new Course(id,courseNum,lectSection);


        //todo break the string down into a course here..

        return  newCcurse;
    }

    private static Slot_Occupant readLabIdentifier(String identifier){
        String id = ""; //SENG,CPSC Etc.
        int courseNum = 0, lectSection = 0, lab = 0; //lab can not exist

        Lab newLab = new Lab(id,courseNum,lectSection,lab);


        //todo break the string down into a lab here


        return newLab;
    }
}

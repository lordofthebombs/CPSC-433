package Parser;

import ParseData.ParseData;
import ParseData.Slot;
import Slot_Occupant.Slot_Occupant;
import Slot_Occupant.*;
import java.util.*;
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
	
	private static BufferedReader in;
	private static int currentLineNum = 0;
	

    public static ParseData parse(String fileName) throws FileNotFoundException, ParseError{

        ReadState currentState = ReadState.Name;
        ParseData output = new ParseData();
        in = new BufferedReader(new FileReader(new File(fileName)));
        
        /*
         * This will be mainly used for converting Time to floats cleanly
         * Using MashMap:
         * 		(e.g) timeConvert.get("8:00");   -> (float) 8.0
         */
        HashMap<String, Float> timeConvert = new HashMap<String, Float>();
        timeConvert.put("8:00",  (float) 8.0);  timeConvert.put("8:30",  (float) 8.5);
        timeConvert.put("9:00",  (float) 9.0);  timeConvert.put("9:30",  (float) 9.5);
        timeConvert.put("10:00", (float) 10.0); timeConvert.put("10:30", (float) 10.5);
        timeConvert.put("11:00", (float) 11.0); timeConvert.put("11:30", (float) 11.5);
        timeConvert.put("12:00", (float) 12.0); timeConvert.put("12:30", (float) 12.5);
        timeConvert.put("1:00",  (float) 13.0); timeConvert.put("1:30",  (float) 13.5);
        timeConvert.put("2:00",  (float) 14.0); timeConvert.put("2:30",  (float) 14.5);
        timeConvert.put("3:00",  (float) 15.0); timeConvert.put("3:30",  (float) 15.5);
        timeConvert.put("4:00",  (float) 16.0); timeConvert.put("4:30",  (float) 16.5);
        timeConvert.put("5:00",  (float) 17.0); timeConvert.put("5:30",  (float) 17.5);
        timeConvert.put("6:00",  (float) 18.0); timeConvert.put("6:30",  (float) 18.5);
        timeConvert.put("7:00",  (float) 19.0); timeConvert.put("7:30",  (float) 19.5);
        //timeConvert.put("8:00",  (float) 20.0); timeConvert.put("8:30",  (float) 20.5);
        //timeConvert.put("9:00",  (float) 21.0); timeConvert.put("9:30",  (float) 21.5);
        //timeConvert.put("10:00", (float) 22.0); timeConvert.put("10:30", (float) 22.5);
        
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
                	try {
                		currentLine = in.readLine();
                        currentLineNum++;
                        
                        if (currentLine.equals("")) { throw new ParseError("No title in file"); }
                        
               	 	} catch (Exception e) { throw new ParseError("Error reading name section of file"); }
                }
                else if(currentLine.equals("course slots:")){
                    currentState = ReadState.Course_Slots;
                    output = processCourseSlots(output, timeConvert);
                }
                else if(currentLine.equals("lab slots:")){
                    currentState = ReadState.Lab_Slots;
                    output = processLabSlots(output);
                }
                else if(currentLine.equals("courses:")){
                    currentState = ReadState.Courses;
                    output = processCourses(output);
                }
                else if(currentLine.equals("labs:")){
                    currentState = ReadState.Labs;
                    output = processLabs(output);
                }
                else if(currentLine.equals("not compatible:")){
                    currentState = ReadState.Not_Compatable;
                    output = processNotCompat(output);
                }
                else if(currentLine.equals("unwanted:")){
                    currentState = ReadState.Unwanted;
                    output = processUwanted(output);
                }
                else if(currentLine.equals("preferences:")){
                    currentState = ReadState.Preferences;
                    output = processPreferences(output);
                }
                else if(currentLine.equals("pair:")){
                    currentState = ReadState.Pair;
                    output = processPairs(output);
                }
                else if(currentLine.equals("partial assignments:")){
                    currentState = ReadState.Partial_Assignments;
                    output = processPartialAssignments(output);
                }
                else if(currentLine.equals("")){
                    //Skips Empty Lines
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
        try {
			in.close();
		} catch (IOException e) {
			System.out.println("Failed to Close IO");
			e.printStackTrace();
		}
        return output;
    }
    
    
    
    

    /*
           all of these process functions read in and put data into the given ParseData object.
           The first 'readLine' is assumed to the the first line after the title of the section,
           which should have already been read in.
     */
    
    /* Course Slots
     * 		Format:		 "MO,  8:00,  3,   2"
     * 		Convert to:	 <Day, float, int, int>
     */
    private static ParseData processCourseSlots(ParseData data, HashMap<String, Float> timeConvert) throws ParseError{
    	Vector<Slot> vectCourseSlots = new Vector<Slot>();
    	try{ 
    		String currentLine = in.readLine();
	        currentLineNum++;
	        
	        while (!currentLine.equals("")) {
	        	currentLine = currentLine.replaceAll(" ", "");			//Gets rid of Spaces
	        	String[] courseSlots = currentLine.split(",");			//Splits line by "," into array
	        	
	        	Slot.Day day;
	        	
	        	if (courseSlots[0].equals("MO")) {						//Converts String to enum Day
	        		day = Slot.Day.Mon;
	        	} else if (courseSlots[0].equals("TU")) {
	        		day = Slot.Day.Tues;
	        	} else if (courseSlots[0].equals("FR")) {
	        		day = Slot.Day.Fri;
	        	} else {
	        		throw new ParseError("Error reading Course slots: Day");
	        	}
	        	
	        	float time = timeConvert.get(courseSlots[1]);			//Converts time to float using HashMap
	        	int max = Integer.valueOf(courseSlots[2]);				//Converts String max to int max
	        	int min = Integer.valueOf(courseSlots[3]);				//Converts String min to int min
	        	
	        	Slot slot = new Slot(day, time, max, min);				//Creates a slot using information
	        	vectCourseSlots.addElement(slot);						//Places the current slot into a Vector
	        	
	        	currentLine = in.readLine();							
	            currentLineNum++;
	        }
    	} catch (IOException e){
            System.out.println("Failed to read a new line(Course Slots)");
        }
        
        data.setCourse_Slots(vectCourseSlots); 					//Puts vector into ParseData
        return data;
    }
    
    /*Lab Slots
     * 		Format:		 "MO,  8:00,  3,   2"
     * 		Convert to:	 <Day, float, int, int>
     */
    private static ParseData processLabSlots(ParseData data) throws ParseError{
    	return data;
    }
    
    /*Courses
     * 		Format:		 "CPSC 433 LEC 01"
     * 		Convert to:	 
     */
    private static ParseData processCourses(ParseData data) throws ParseError{
    	return data;
    }
    
    /*Labs
     * 		Format:		 "CPSC 433 LEC 01 TUT 01"
     * 		Convert to:	 
     */
    private static ParseData processLabs(ParseData data) throws ParseError{
    	return data;
    }
    
    /*Not Compatible
     * 		Format:		 "CPSC 433 LEC 01 TUT 01, CPSC 433 LEC 02 LAB 02"
     * 		Convert to:	 
     */
    private static ParseData processNotCompat(ParseData data) throws ParseError{
    	return data;
    }
    
    /*Unwanted
     * 		Format:		 "CPSC 433 LEC 01, MO, 8:00"
     * 		Convert to:	 
     */
    private static ParseData processUwanted(ParseData data) throws ParseError{
    	return data;
    }
    
    /*Preferences
     * 		Format:		 "TU,  9:00, CPSC 433 LEC 01, 10"
     * 		Convert to:	 
     */
    private static ParseData processPreferences(ParseData data) throws ParseError{
    	return data;
    }
    
    /*Pairs
     * 		Format:		 "SENG 311 LEC 01, CPSC 567  LEC    01"
     * 		Convert to:	 
     */
    private static ParseData processPairs(ParseData data) throws ParseError{
    	return data;
    }
    
    /*Partial Assignment
     * 		Format:		 "SENG 311 LEC 01, MO, 8:00"
     * 		Convert to:	 
     */
    private static ParseData processPartialAssignments(ParseData data) throws ParseError{
    	return data;
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

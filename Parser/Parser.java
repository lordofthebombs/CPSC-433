package Parser;

import ParseData.ParseData;
import ParseData.Slot;
import ParseData.*;
import Slot_Occupant.Slot_Occupant;
import Slot_Occupant.*;

import java.util.*;
import java.io.*;


public class Parser {
	
	private static BufferedReader in;
	private static int currentLineNum = 0;
	private static TimeConverter timeConvert;

    public static ParseData parse(String fileName) throws FileNotFoundException{

        ParseData output = new ParseData();
        in = new BufferedReader(new FileReader(new File(fileName)));

        //This changes strings to floats cleanly by using timeConvert.convertTime(string time).
        timeConvert = new TimeConverter();
        
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
                else if(currentLine.equals("")){
                    //Skip Empty Lines
                }
                else if(currentLine.equals("course slots:")) { processCourseSlots(output); }
                else if(currentLine.equals("lab slots:")){ processLabSlots(output); }
                else if(currentLine.equals("courses:")){ processCourses(output); }
                else if(currentLine.equals("labs:")){ processLabs(output); }
                else if(currentLine.equals("not compatible:")){ processNotCompat(output); }
                else if(currentLine.equals("unwanted:")){ output = processUwanted(output); }
                else if(currentLine.equals("preferences:")){ processPreferences(output); }
                else if(currentLine.equals("pair:")){ processPairs(output); }
                else if(currentLine.equals("partial assignments:")){ processPartialAssignments(output); }

                else{
                    throw new ParseError("File is ill formatted, met unexpected String: '" + currentLine + "' at line: " + currentLineNum);
                }

                currentLine = in.readLine();
                currentLineNum++;
            }

        }
        catch (IOException e){
            System.out.println("Failed to read a new line at: " + currentLineNum);
        }
        catch (ParseError e){
            e.print();
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
    private static ParseData processCourseSlots(ParseData data) throws ParseError, IOException{

        Vector<Slot> vectCourseSlots = new Vector<Slot>();

    	try{
    		String entry = readLine();

            while (entry != null && !entry.equals("")) {

                entry = entry.replaceAll(" ", "");			//Gets rid of Spaces
	        	String[] courseSlots = entry.split(",");			//Splits line by "," into array
	        	
	        	Slot.Day day;

                try {

                    day = Slot.toDay(courseSlots[0]);

                    if(day.equals(Slot.Day.Fri)){
                        throw new ParseError("Friday is an invalid day for a course at line: " + currentLineNum);
                    }

                }catch(Slot.DayConvertError e){
                    throw new ParseError("Invalid Day Provided for a Course slots at line: " + currentLineNum);
                }
	        	
	        	float time = timeConvert.convertTime(courseSlots[1]);			//Converts time to float using HashMap
	        	int max = parseInt(courseSlots[2]);				                //Converts String max to int max
	        	int min = parseInt(courseSlots[3]);				                //Converts String min to int min

	        	Slot slot = new Slot(day, time, max, min);				        //Creates a slot using information

                if(!data.Courses.contains(slot)){
                    vectCourseSlots.addElement(slot);						    //Places the current slot into a Vector
                }
	        	else
	        	    throw new ParseError("Duplicate Course Slot declared on line: " + currentLineNum);

                entry = readLine();
	        }
    	}
        catch (TimeConverter.TimeFormatException e){
            throw new ParseError("Invalid time provided on line: " + currentLineNum);
        }
        
        data.setCourse_Slots(vectCourseSlots); 					//Puts vector into ParseData
        return data;
    }
    
    /*Lab Slots
     * 		Format:		 "MO,  8:00,  3,   2"
     * 		Convert to:	 <Day, float, int, int>
     */
    private static ParseData processLabSlots(ParseData data) throws ParseError, IOException{

        Vector<Slot> vectLabSlots = new Vector<Slot>();

        try{
            String entry = readLine();

            while (entry != null && !entry.equals("")) {

                entry = entry.replaceAll(" ", "");			//Gets rid of Spaces
                String[] courseSlots = entry.split(",");			//Splits line by "," into array

                Slot.Day day;

                try {

                    day = Slot.toDay(courseSlots[0]);

                }catch(Slot.DayConvertError e){
                    throw new ParseError("Invalid Day Provided for a Course slots at line : " + currentLineNum);
                }

                float time = timeConvert.convertTime(courseSlots[1]);			//Converts time to float using HashMap
                int max = parseInt(courseSlots[2]);				        //Converts String max to int max
                int min = parseInt(courseSlots[3]);				        //Converts String min to int min

                Slot slot = new Slot(day, time, max, min);				        //Creates a slot using information

                if(!data.Labs.contains(slot)){
                    vectLabSlots.addElement(slot);						//Places the current slot into a Vector
                }
                else
                    throw new ParseError("Duplicate Lab Slot declared on line: " + currentLineNum);

                entry = readLine();
            }
        }
        catch (TimeConverter.TimeFormatException e){
            throw new ParseError("Invalid time provided on line: " + currentLineNum);
        }

        data.setLab_Slots(vectLabSlots); 					                //Puts vector into ParseData
        return data;
    }
    
    /*Courses
     * 		Format:		 "CPSC 433 LEC 01"
     * 		Convert to:	 <String, int, int>
     */
    private static ParseData processCourses(ParseData data) throws ParseError, IOException{

        Vector<Slot_Occupant> coursesVect = new Vector<Slot_Occupant>();

        String entry = readLine();

        while (entry != null && !entry.equals("")) {

            Course temp = makeCourseFromIdentifier(entry);

            if(!alreadyContains(data,temp)){
                coursesVect.add(temp);
            }
            else
                throw new ParseError("Duplicate Course stated on line: " + currentLineNum);

            entry = readLine();
        }

        data.setCourses(coursesVect);
    	return data;
    }
    
    /*Labs
     * 		Format:		 "CPSC 433 LEC 01 TUT 01"
     * 		Convert to:	 
     */
    private static ParseData processLabs(ParseData data) throws ParseError, IOException{

    	Vector<Slot_Occupant> Labs = new Vector<Slot_Occupant>();

    	String entry = readLine();

        while (entry != null && !entry.equals("")) {

    	    Lab temp = makeLabFromIdentifier(entry);

    	    if(!alreadyContains(data,temp)){
                    Labs.add(temp);
    	    }
    	    else
    	        throw new ParseError("Duplicate Lab stated on line: " + currentLineNum);

    	    entry = readLine();
    	}

        data.setLabs(Labs);
        return data;
    }
    
    /*Not Compatible
     * 		Format:		 "CPSC 433 LEC 01 TUT 01, CPSC 433 LEC 02 LAB 02"
     * 	    //Can be course-course
     * 	             lab-lab
     * 	             course-lab
     * 		Convert to:	 NonCompat Object
     */
    private static ParseData processNotCompat(ParseData data) throws ParseError, IOException{

        Non_Compatable NonCompat = new Non_Compatable();
        Slot_Occupant t1,t2;

        String entry = readLine();

        while (entry != null && !entry.equals("")) {

            String occupants[] = entry.split(","); //Assumes that the non-compat has comma separated occupants
            occupants[0] = occupants[0].trim();
            occupants[1] = occupants[1].trim();

            //Check to make sure the two arguments are of the same argument length

            if(occupants[0].trim().split("\\s+").length == occupants[1].trim().split("\\s+").length && occupants.length == 2){

                t1 = getOccupant(data,occupants[0]);
                t2 = getOccupant(data,occupants[1]);

                //Add the pairing
                if(!NonCompat.addEntry(t1,t2)){
                    throw new ParseError("Duplicate Non Compat statement on line: " + currentLineNum);
                }
            }
            else{
                throw new ParseError("Non Compatibility statement is not valid on line: " + currentLineNum);
            }

            entry = readLine();
        }

        data.Non_Compat = NonCompat;
    	return data;
    }
    
    /*Unwanted
     * 		Format:		 "CPSC 433 LEC 01, MO, 8:00"
     * 		Convert to:
     *
     * 	        can be Course, Day, Slot day , Slot time
     * 	               Lab, Day, Slot day, Slot time
     *
     */
    private static ParseData processUwanted(ParseData data) throws ParseError, IOException{

        Unwanted Unwanted = new Unwanted();

        Slot s1;
        Slot_Occupant t1;

        String entry = readLine();

        while (entry != null && !entry.equals("")) {

            entry = entry.trim();
            String splitValues[] = entry.split(","); //Assumes that the non-compat has comma separated details

            if(splitValues.length == 3){

                t1 = getOccupant(data,splitValues[0]);
                s1 = getSlot(data,splitValues[1],splitValues[2],t1.getClass());

                if(!Unwanted.addEntry(t1,s1)){
                    throw new Error("Duplicate Unwanted statement found on line: " + currentLineNum);
                }

            }
            else{
                throw new ParseError("Invalid Unwanted Statement on line: " + currentLineNum);
            }

            entry = readLine();
        }

        data.Unwanted = Unwanted;
    	return data;
    }
    
    /*Preferences
     * 		Format:		 "TU,  9:00, CPSC 433 LEC 01, 10"
     */
    private static ParseData processPreferences(ParseData data) throws ParseError, IOException{

        Preferences pref = new Preferences();
        String entry = readLine();
        int prefVal;
        Slot s1;
        Slot_Occupant t1;

        while (entry != null && !entry.equals("")) {

            entry = entry.trim();
            String splitValues[] = entry.split(","); //Assumes that the non-compat has comma separated details

            if(splitValues.length != 3){

                t1 = getOccupant(data,splitValues[2]);
                s1 = getSlot(data,splitValues[0],splitValues[1],t1.getClass());

                prefVal = parseInt(splitValues[3]);

                if(pref.isPreference(t1,s1) || !pref.addEntry(t1,s1,prefVal) ){
                    throw new ParseError("Duplicate Preference statement found on line: " + currentLineNum);
                }
            }
            else
                throw new ParseError("Invalid number of arguments for Preference statement on line: " + currentLineNum);

            entry = readLine();
        }

        data.Preferences = pref;
    	return data;
    }
    
    /*Pairs
     * 		Format:		 "SENG 311 LEC 01, CPSC 567  LEC    01"
     */
    private static ParseData processPairs(ParseData data) throws ParseError, IOException{

        Pairs pairs = new Pairs();
        Slot_Occupant t1,t2;

        String entry = readLine();

        while (entry != null && !entry.equals("")) {

            entry = entry.trim();
            String occupants[] = entry.split(","); //Assumes that the non-compat has comma separated occupants

            //Check to make sure the two arguments are of the same argument length
            int i = occupants[0].trim().split("\\s+").length;
            int j = occupants[1].trim().split("\\s+").length;

            if(occupants[0].trim().split("\\s+").length == occupants[1].trim().split("\\s+").length && occupants.length == 2){

                t1 = getOccupant(data,occupants[0]);
                t2 = getOccupant(data,occupants[1]);

                if(!pairs.addEntry(t1,t2)){
                    throw new ParseError("Duplicate pair statement on line: " + currentLineNum);
                }

            }
            else{
                throw new ParseError("Pair statement is not valid on line: " + currentLineNum);
            }

            entry = readLine();
        }

        data.Pairs = pairs;
        return data;
    }
    
    /*Partial Assignment
     * 		Format:		 "SENG 311 LEC 01, MO, 8:00"
     */
    private static ParseData processPartialAssignments(ParseData data) throws ParseError, IOException{

        Partial_Assignments partials = new Partial_Assignments();

        Slot s1;
        Slot_Occupant t1;

        String entry = readLine();


        while (entry != null && !entry.equals("")) {

            entry = entry.trim();
            String splitValues[] = entry.split(","); //Assumes that the non-compat has comma separated details

            if(splitValues.length == 3){

                t1 = getOccupant(data,splitValues[0]);
                s1 = getSlot(data,splitValues[1],splitValues[2],t1.getClass());

                if(!partials.addEntry(t1,s1)){
                    throw new Error("Duplicate partial assignment statement found on line: " + currentLineNum);
                }

            }
            else{
                throw new ParseError("Invalid Unwanted Statement on line: " + currentLineNum);
            }

            entry = readLine();
        }

        data.Partial_Assignments = partials;
        return data;

    }


    /*
        These are helper functions to help with the process data functions

            Format:		 "CPSC 433 LEC 01"
     		Convert to:	 <String, int, int>

     	We assume we can ignore the 'LEC' part of this description.

     */
    private static Course makeCourseFromIdentifier(String entry) throws ParseError{

        String id = ""; //SENG,CPSC Etc.
        int courseNum = 0, lectSection = 0; //have to be integers.
        Course newCourse = new Course(id,courseNum,lectSection);

        entry = entry.trim();
        String tokens[] = entry.split("\\s+"); //IMPORTANT ASSUMES THAT DATA IS SEPARATED BY SPACES

        if (tokens.length == 4) {

            //Error Check my string matching
            if (!tokens[0].equals("SENG") && !tokens[0].equals("CPSC")) {
                throw new ParseError("Invalid Course Identifier encountered at line: " + currentLineNum);
            }
            if(!tokens[2].equals("LEC")){
                throw new ParseError("Expected LEC on line " + currentLineNum + " instead of " + tokens[2]);
            }

            newCourse.id = tokens[0];
            newCourse.courseNum = parseInt(tokens[1]);
            newCourse.lectSection = parseInt(tokens[3]);

        } else
            throw new ParseError("Invalid Course description at line: " + currentLineNum);

        return  newCourse;
    }


    /*
            Helpler function for data processing.

            Format:		 "CPSC 433 LEC 01 TUT 01"
       		Convert to:  <String, int, int, maybe int>
    */
    private static Lab makeLabFromIdentifier(String entry) throws ParseError{

        String id = ""; //SENG,CPSC Etc.

        int labSect;
        int courseNum = 0, lectSection = 0, lab = 0; //lab can not exist

        Lab newLab = new Lab(id,courseNum,lectSection,lab);

        entry = entry.trim();
        String tokens[] = entry.split("\\s+"); //IMPORTANT ASSUMES THAT DATA IS SEPARATED BY SPACES

        if (!tokens[0].equals("SENG") && !tokens[0].equals("CPSC")) {
            throw new ParseError("Invalid Course Identifier encountered at line: " + currentLineNum);
        }


        //Case with no LEC specified
        if (tokens.length == 4) {

            if(!tokens[2].equals("TUT") && !tokens[2].equals("LAB")){
                throw new ParseError("Expected TUT or LAB on line " + currentLineNum + " instead of " + tokens[2]);
            }

            newLab.id = tokens[0];
            newLab.courseNum = parseInt(tokens[1]);
            newLab.labSect = parseInt(tokens[3]);
            newLab.lectSection = -1; //It has no specified lecture section.
        }
        else if(tokens.length == 6){

            if(!tokens[2].equals("LEC")){
                throw new ParseError("Expected LEC on line " + currentLineNum + " instead of " + tokens[2]);
            }
            if(!tokens[4].equals("TUT") && !tokens[4].equals("LAB")){
                throw new ParseError("Expected TUT or LAB on line " + currentLineNum + " instead of " + tokens[2]);
            }

            newLab.id = tokens[0];
            newLab.courseNum = parseInt(tokens[1]);
            newLab.lectSection = parseInt(tokens[3]);
            newLab.labSect = parseInt(tokens[5]);

        }
        else{
            throw new ParseError("Invalid Course description at line: " + currentLineNum);
        }

        return newLab;
    }

    /**
     *
     * @param data
     * @param entry
     * @return
     * @throws ParseError
     *
     * Function that gets a Slot_Occupant from parseData set, this assumes the
     * courses and slots have already been read in.
     *
     * If it is used and the given string entry for a slot occupant identifier is not contained
     * it will throw a parse error.
     *
     */
    private static Slot_Occupant getOccupant(ParseData data, String entry) throws ParseError{

        int i;
        Slot_Occupant s_o = genOccupant(entry);

        if(s_o.getClass() == Course.class){

            i = data.Courses.indexOf(s_o);

            if(i != -1){
                return data.Courses.get(i);
            }
            else
                throw new ParseError("Undeclared Course is being used on line: " + currentLineNum);

        }
        else{                               //it's a lab
            i = data.Labs.indexOf(s_o);

            if(i != -1){
                return data.Labs.get(i);
            }
            else
                throw new ParseError("Undeclared Lab is being used on line: " + currentLineNum);
        }
    }

    /*
        genOccupant(string entry)

        this function assumes that entry is either a course or lab description.
        It determines what iit is an returns either a course or a lab depending on the result.
     */
    private static Slot_Occupant genOccupant(String entry) throws ParseError{

        entry = entry.trim();
        String tokens[] = entry.split("\\s+"); //assumes spaces split course/lab descriptions.

        Slot_Occupant newOccupant;

        if(tokens.length == 4){

            if(tokens[2].equals("LEC")){
                newOccupant = makeCourseFromIdentifier(entry);
            }
            else{
                newOccupant = makeLabFromIdentifier(entry);
            }
        }
        else if(tokens.length == 6) //must be a lab
        {
            newOccupant = makeLabFromIdentifier(entry);
        }
        else{
            throw new ParseError("Encountered invalid slot occupant description on line: " + currentLineNum);
        }

        return newOccupant;
    }

    /**
     * @param data
     * @param day
     * @param time
     * @param ctype //the type of the list that is being generated from (usually will be the class of the coresponding slot)
     * @return
     * @throws ParseError
     *
     * Similar to getOccupant
     *
     * Function that gets a Slot from parseData set, this assumes the
     * courses and slots have already been read in.
     *
     * If it is used and the given strings entry for a slot time and day don't correspond to a
     * already read in slot an error will be thrown.
     */
    private static Slot getSlot(ParseData data, String day, String time, Class ctype) throws ParseError{

        try {
            int i = -1;
            Slot temp = new Slot(Slot.toDay(day.trim()), timeConvert.convertTime(time.trim()), 0, 0); //

            if(ctype == Course.class){
                i = data.Course_Slots.indexOf(temp); //This has to be done, otherwise course min/max will be lost. The reason this works is because the equals method is overriden.
            }
            else{
                i = data.Lab_Slots.indexOf(temp);
            }

            if(i != -1){

                if(ctype == Course.class){
                    return data.Course_Slots.get(i);
                }
                else{
                    return data.Lab_Slots.get(i);
                }
            }
            else
                throw new ParseError("Undeclared Slot is being used on line: " + currentLineNum);

        }catch (Slot.DayConvertError e){
            throw new ParseError("Provided Day is invalid on line: " + currentLineNum);
        }catch (TimeConverter.TimeFormatException e){
            throw new ParseError("Invalid Time format provided on line: " + currentLineNum);
        }

    }
    private static boolean alreadyContains(ParseData p, Slot_Occupant s){

        if(s.getClass() == Course.class){
            return p.Courses.contains(s);
        }
        else {
            return p.Labs.contains(s);
        }
    }

    private static int parseInt(String str) throws ParseError{

        try{
            return Integer.parseInt(str.trim());

        }catch (NumberFormatException e) {
            throw new ParseError("Encountered non Integer value when integer was expected on line " + currentLineNum);
        }
    }

    //This makes it so currentLine num doesn't have to be managed.
    private static String readLine() throws IOException{
        currentLineNum++;
        return in.readLine();
    }
}

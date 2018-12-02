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

        boolean courseSlots,labSlots,courses,labs,notCompat,unwanted,preference,pair,partialAssignments;
        courseSlots = labSlots = courses = labs = notCompat = unwanted = preference = pair = partialAssignments = false;

        ParseData output = new ParseData();
        in = new BufferedReader(new FileReader(new File(fileName)));

        //This changes strings to floats cleanly by using timeConvert.convertTime(string time).
        timeConvert = new TimeConverter();
        
        try {
            String currentLine = readLine();

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
                		currentLine = readLine();

                        if (currentLine.equals("")) { throw new ParseError("No title in file"); }
                        
               	 	} catch (Exception e) { throw new ParseError("Error reading name section of file"); }
                }
                else if(currentLine.equals("")){
                    //Skip Empty Lines
                }
                else if(currentLine.equals("course slots:")) { processCourseSlots(output); courseSlots = true; }
                else if(currentLine.equals("lab slots:")){ processLabSlots(output); labSlots = true;}
                else if(currentLine.equals("courses:")){ processCourses(output); courses = true;}
                else if(currentLine.equals("labs:")){ processLabs(output); labs = true;}
                else if(currentLine.equals("not compatible:")){ processNotCompat(output); notCompat = true;}
                else if(currentLine.equals("unwanted:")){ output = processUwanted(output); unwanted = true;}
                else if(currentLine.equals("preferences:")){ processPreferences(output); preference = true;}
                else if(currentLine.equals("pair:")){ processPairs(output); pair = true;}
                else if(currentLine.equals("partial assignments:")){ processPartialAssignments(output); partialAssignments = true;}

                else{
                    throw new ParseError("File is ill formatted, met unexpected String: '" + currentLine + "' at line: " + currentLineNum);
                }

                currentLine = readLine();
            }

            if(!courseSlots || !labSlots || !courses || !labs || !notCompat || !unwanted || !preference || !pair || !partialAssignments){

                //Case where one of the title areas is missing.
                String missingTags = "";

                if(!courseSlots){missingTags += " course slots ";}
                if(!labSlots) {missingTags += " lab slots ";}
                if(!courses){ missingTags += " courses ";}
                if(!labs){missingTags += " labs ";}
                if(!notCompat){missingTags += " not compatible ";}
                if(!unwanted){missingTags += " unwanted ";}
                if(!preference){missingTags += " preferences ";}
                if(!pair){missingTags += " pair ";}
                if(!partialAssignments){missingTags += " partial assignments ";}

                throw new ParseError("Invalid file format, missing the following tags statements :" + missingTags );
            }
            //Check for the CPSC 813 and CPSC 913 Special Cases and add the non-compats statements if need be
            checkCPSC(output);

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
	        	
	        	float time = timeConvert.convertTime(courseSlots[1],day,Course.class);			//Converts time to float using HashMap

                if(day == Slot.Day.Tues && time == 11.0){
                    printWarning("Course Slot exists for a time a course cannot be scheduled for");
                }


	        	int max = parseInt(courseSlots[2]);				                //Converts String max to int max
	        	int min = parseInt(courseSlots[3]);				                //Converts String min to int min

	        	Slot slot = new Slot(day, time, max, min);				        //Creates a slot using information

                if(!vectCourseSlots.contains(slot)){
                    vectCourseSlots.addElement(slot);						    //Places the current slot into a Vector
                }
	        	else
	        	    printWarning("Duplicate Course Slot statement");

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

                float time = timeConvert.convertTime(courseSlots[1],day,Lab.class);			//Converts time to float using HashMap
                int max = parseInt(courseSlots[2]);				        //Converts String max to int max
                int min = parseInt(courseSlots[3]);				        //Converts String min to int min

                Slot slot = new Slot(day, time, max, min);				        //Creates a slot using information

                if(!vectLabSlots.contains(slot)){
                    vectLabSlots.addElement(slot);						//Places the current slot into a Vector
                }
                else
                    printWarning("Duplicate Lab Slot statement");

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

            if(temp.lectSection > 4){
                System.out.println("meme");
            }
            if(!coursesVect.contains(temp)){
                coursesVect.add(temp);
            }
            else
                printWarning("Duplicate Course statement");

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

    	    if(!Labs.contains(temp)){

    	        if(temp.hasLectSect()){
    	           Course matchingCourse = new Course(temp.id,temp.courseNum,temp.lectSection);
    	           if(!data.Courses.contains(matchingCourse)){
    	               printWarning("Declared Lab has a lecture section which corresponds to no declared course LEC");
                   }
                }

                Labs.add(temp);
    	    }
    	    else
    	        printWarning("Duplicate Lab statement");

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

            if(occupants.length == 2){

                t1 = getOccupant(data,occupants[0]);
                t2 = getOccupant(data,occupants[1]);

                if(t1.equals(t2)){
                    throw new ParseError("A course/lab can't be not compatible with itself");
                }
                //Add the pairing
                if(!NonCompat.addEntry(t1,t2)){
                    printWarning("Duplicate Non Compat statement");
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

                t1 = getOccupant(data,splitValues[0].trim());
                s1 = getSlot(data,splitValues[1].trim(),splitValues[2].trim(),t1.getClass());

                if(!Unwanted.addEntry(t1,s1)){
                    printWarning("Duplicate Unwanted statement");
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
        Slot s1 = null;
        Slot_Occupant t1 = null;

        while (entry != null && !entry.equals("")) {

            entry = entry.trim();
            String splitValues[] = entry.split(","); //Assumes that the non-compat has comma separated details

            if(splitValues.length == 4){

                //if a slot_occupant or slot is invalid in preference list, we will show warning and not add
                // that entry to Preferences,
                try {
                    t1 = getOccupant(data, splitValues[2].trim());
                    s1 = getSlot(data, splitValues[0].trim(), splitValues[1].trim(), t1.getClass());
                }catch (ParseError e){
                    printWarningWithOutLineNum("Invalid Preference entry due to: " + e.getMessage());
                }

                prefVal = parseInt(splitValues[3].trim());

                if(t1 != null && s1 != null) {
                    if (pref.isPreference(t1, s1) || !pref.addEntry(t1, s1, prefVal)) {
                        printWarning("Duplicate Preference statement");
                    }
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

            if(occupants.length == 2){

                t1 = getOccupant(data,occupants[0].trim());
                t2 = getOccupant(data,occupants[1].trim());

                if(t1.equals(t2)){
                    printWarning("A course can't be not compatible with itself");
                }
                if(!pairs.addEntry(t1,t2)){
                    printWarning("Duplicate pair statement");
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

        LinkedList<Slot_Occupant> AlreadyAssigned = new LinkedList<>();

        while (entry != null && !entry.equals("")) {

            entry = entry.trim();
            String splitValues[] = entry.split(","); //Assumes that the non-compat has comma separated details

            if(splitValues.length == 3){

                t1 = getOccupant(data,splitValues[0].trim());
                if(AlreadyAssigned.contains(t1)){
                    throw new ParseError("You cannot have 2 partial assignments for 1 course/lab");
                }
                s1 = getSlot(data,splitValues[1].trim(),splitValues[2].trim(),t1.getClass());

                if(!partials.addEntry(t1,s1)){
                    printWarning("Duplicate partial assignment statement found");
                }

                AlreadyAssigned.add(t1);

            }
            else{
                throw new ParseError("Invalid partial assignment Statement on line: " + currentLineNum);
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

            return new Lab(tokens[0],parseInt(tokens[1]),-1,parseInt(tokens[3]));
        }
        else if(tokens.length == 6){

            if(!tokens[2].equals("LEC")){
                throw new ParseError("Expected LEC on line " + currentLineNum + " instead of " + tokens[2]);
            }
            if(!tokens[4].equals("TUT") && !tokens[4].equals("LAB")){
                throw new ParseError("Expected TUT or LAB on line " + currentLineNum + " instead of " + tokens[2]);
            }

            return new Lab(tokens[0],parseInt(tokens[1]),parseInt(tokens[3]),parseInt(tokens[5]));
        }
        else{
            throw new ParseError("Invalid Course description at line: " + currentLineNum);
        }
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

        if(tokens.length == 4){

            if(tokens[2].equals("LEC")){
                return makeCourseFromIdentifier(entry);
            }
            else{
                return makeLabFromIdentifier(entry);
            }
        }
        else if(tokens.length == 6) //must be a lab
        {
            return makeLabFromIdentifier(entry);
        }
        else{
            throw new ParseError("Encountered invalid slot occupant description on line: " + currentLineNum);
        }
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
            Slot.Day d = Slot.toDay(day);
            Slot temp;

            if(ctype == Course.class){
                temp = new Slot(Slot.toDay(day.trim()), timeConvert.convertTime(time.trim(),d,Course.class), 0, 0); //
                i = data.Course_Slots.indexOf(temp); //This has to be done, otherwise course min/max will be lost. The reason this works is because the equals method is overriden.
            }
            else{
                temp = new Slot(Slot.toDay(day.trim()), timeConvert.convertTime(time.trim(),d,Lab.class), 0, 0); //
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

    private static void printWarning(String str){
        System.out.println("Warning: " + str + " on line: " + currentLineNum);
    }

    private static void printWarningWithOutLineNum(String str){
        System.out.println("Warning: " + str);
    }

    private static void checkCPSC(ParseData p) throws ParseError{

        //Add CPSC 813 / 913

        boolean added813, added913;
        added813 = added913 = false;
        Lab cpsc813 = new Lab("CPSC",813,-1,1);
        Lab cpsc913 = new Lab("CPSC",913,-1,1);
        Slot mandatorySlot;

        try {
            mandatorySlot = getSlot(p, "TU", "18:00", Lab.class);
        }
        catch (ParseError e){
            mandatorySlot = null;
        }

        //We need to first check to see if the 813/courses are already in the course list.
        //Can't really use contains in the case the file defines the classes differently.
        for(Slot_Occupant c : p.Courses){

            if(c.courseNum == 813 && c.id.equals("CPSC")){
                added813 = true;
            }
            else if(c.courseNum == 913 && c.id.equals("CPSC")){
                added913 = true;
            }
            if(added813 && added913){break;}
        }


        //Next we Have to check to see if we have any 313 or 413 courses that don't have a 813/913
        //And if they don't add them
        for(Slot_Occupant c : p.Courses){

            if((c.courseNum == 313 || c.courseNum == 413) && c.id.equals("CPSC")){

                //If they haven't been added and don't exist already then remove them.
                if(c.courseNum == 313){

                    //add 813 if it doesn't already exist.
                    if(!added813){
                        p.Labs.add(cpsc813);
                        added813 = true;
                    }
                }
                else {

                    //Add 913 if it isn't already in the list.
                    if(!added913){
                        p.Labs.add(cpsc913);
                        added913 = true;
                    }
                }
            }

            //Make all evening courses unwanted with all nonevening slots;
            if(c.lectSection == 9){
                for(Slot s : p.Course_Slots){
                    if(s.time < 18){
                        p.Unwanted.addEntry(c,s);
                    }
                }
            }
        }

        //Create partial Assignments for 813/913 if they exist
        if(added813){
            if(mandatorySlot != null){
                p.Partial_Assignments.addEntry(cpsc813,mandatorySlot);
            }
            else{
                throw new ParseError("813 needs to be assigned but the necessary slot TU, 11:00 hsa not been declared");
            }
        }
        if(added913){
            if(mandatorySlot != null){
                p.Partial_Assignments.addEntry(cpsc913,mandatorySlot);
            }
            else{
                throw new ParseError("913 needs to be assigned but the necessary slot TU, 11:00 hsa not been declared");
            }
        }

        //Now make sure the CPSC 313/413 non-compats are generated.
        //Add not compat statements with all CPSC 413 labs / tutorials and courses
        for(Slot_Occupant lab : p.Labs){

            if(lab.equals(cpsc813)){
                for(Slot_Occupant c2 : p.Courses){
                    if(c2.id.equals("CPSC") && c2.courseNum == 313){
                        p.Non_Compat.addEntry(lab,c2);

                        //If something is incompatible with CPSC 313 then it is also incompatible with 813
                        for(Slot_Occupant so : p.Non_Compat.isNonCompatableWith(c2)){
                            p.Non_Compat.addEntry(lab,so);
                        }

                    }
                }
                for(Slot_Occupant lab2 : p.Labs){
                    if(lab.id.equals("CPSC") && lab.courseNum == 313){
                        p.Non_Compat.addEntry(lab,lab2);

                        //If something is incompatible with CPSC 313 then it is also incompatible with 813
                        for(Slot_Occupant so : p.Non_Compat.isNonCompatableWith(lab2)){
                            p.Non_Compat.addEntry(lab,so);
                        }
                    }
                }
            }
            else if(lab.equals(cpsc913)){

                for(Slot_Occupant c2 : p.Courses){
                    if(c2.id.equals("CPSC") && c2.courseNum == 413){
                        p.Non_Compat.addEntry(lab,c2);

                        //If something is incompatible with CPSC 413 then it is also incompatible with 913
                        for(Slot_Occupant so : p.Non_Compat.isNonCompatableWith(c2)){
                            p.Non_Compat.addEntry(lab,so);
                        }

                    }
                }
                for(Slot_Occupant lab2 : p.Labs){
                    if(lab.id.equals("CPSC") && lab.courseNum == 413){
                        p.Non_Compat.addEntry(lab,lab2);

                        //If something is incompatible with CPSC 413 then it is also incompatible with 913
                        for(Slot_Occupant so : p.Non_Compat.isNonCompatableWith(lab2)){
                            p.Non_Compat.addEntry(lab,so);
                        }
                    }
                }
            }
        }


        //Should I add an unwanted for every course and every slot that starts at 11:00 on tues?
        /* Handled in Constraint Checker
        for(Slot cs : p.Course_Slots){

            if(cs.day == Slot.Day.Tues && cs.time == 11.0){

                for(Slot_Occupant c : p.Courses){
                    p.Unwanted.addEntry(c,cs);
                }
            }
        }*/
    }
}

package Parser;

import ParseData.Slot;
import Slot_Occupant.Course;
import Slot_Occupant.Lab;

import java.sql.Time;
import java.util.HashMap;

public class TimeConverter {

    public static class TimeFormatException extends Exception{
        public String Message;
        public TimeFormatException(String msg){this.Message = msg;}
    }

    private HashMap<String, Float> timeConvert;

    public TimeConverter() {

        /*
         * This will be mainly used for converting Time to floats cleanly
         * Using MashMap:
         * 		(e.g) timeConvert.get("8:00");   -> (float) 8.0
         */
        timeConvert = new HashMap<String, Float>();
        timeConvert.put("8:00", (float) 8.0);
        timeConvert.put("8:30", (float) 8.5);
        timeConvert.put("9:00", (float) 9.0);
        timeConvert.put("9:30", (float) 9.5);
        timeConvert.put("10:00", (float) 10.0);
        timeConvert.put("10:30", (float) 10.5);
        timeConvert.put("11:00", (float) 11.0);
        timeConvert.put("11:30", (float) 11.5);
        timeConvert.put("12:00", (float) 12.0);
        timeConvert.put("12:30", (float) 12.5);
        timeConvert.put("13:00", (float) 13.0);
        timeConvert.put("13:30", (float) 13.5);
        timeConvert.put("14:00", (float) 14.0);
        timeConvert.put("14:30", (float) 14.5);
        timeConvert.put("15:00", (float) 15.0);
        timeConvert.put("15:30", (float) 15.5);
        timeConvert.put("16:00", (float) 16.0);
        timeConvert.put("16:30", (float) 16.5);
        timeConvert.put("17:00", (float) 17.0);
        timeConvert.put("17:30", (float) 17.5);
        timeConvert.put("18:00", (float) 18.0);
        timeConvert.put("18:30", (float) 18.5);
        timeConvert.put("19:00", (float) 19.0);
        timeConvert.put("19:30", (float) 19.5);
        timeConvert.put("20:00", (float) 20.0);
        timeConvert.put("20:30", (float) 20.5);

    }

     public float convertTime(String Time, Slot.Day day, Class labOrTut) throws TimeFormatException{ //Needs the day to check if it is a valid time

        Float t = this.timeConvert.get(Time);

        if(t == null){ //The case where the time is not valid or ill-formatted !!! THIS NEEDS TO BE HANDLED.
            throw new TimeFormatException("given an invalid valid time");
        }

        if(labOrTut == Course.class){

            switch (day){
                case Mon:
                    if(Math.ceil(t) != t){
                        throw new TimeFormatException("Courses cannot be scheduled at the given time. ");
                    }
                    break;
                case Tues:
                    if(t != 8.0 && t != 9.5 && t != 11.0 && t != 12.5 && t != 14.0 && t != 15.5 && t != 17 && t != 18.5){
                        throw new TimeFormatException("Courses cannot be scheduled at the given time. ");
                    }
                    break;
            }

        }
        else if(labOrTut == Lab.class){

            switch (day){
                case Mon:
                    if(Math.ceil(t) != t){
                        throw new TimeFormatException("Lab cannot be scheduled at the given time. ");
                    }
                    break;
                case Tues:
                    if(Math.ceil(t) != t){
                        throw new TimeFormatException("Lab cannot be scheduled at the given time. ");
                    }
                    break;
                case Fri:
                    if(t != 8.0 && t != 10.0 && t != 12.0 && t != 14.0 && t != 16.0 && t != 18){
                        throw new TimeFormatException("Lab cannot be scheduled at this time. ");
                    }
                    break;
            }
        }
        return t;
     }

}

package Parser;

import java.security.Key;
import java.util.HashMap;

public class TimeConverter {

    private HashMap<String, Float> timeConvert;

    public TimeConverter(){

        /*
         * This will be mainly used for converting Time to floats cleanly
         * Using MashMap:
         * 		(e.g) timeConvert.get("8:00");   -> (float) 8.0
         */
        timeConvert = new HashMap<String, Float>();
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


    }

     public float convertTime(String Time){
        return this.timeConvert.get(Time);
     }

}

package Parser;

import ParseData.ParseData;

public class ParseError extends Exception {

    private String message;

    public ParseError(String message){
        this.message = message;
    }

    public void print(){
        System.out.println(message);
    }



}


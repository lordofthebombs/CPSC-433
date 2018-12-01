package Parser;


public class ParseError extends Exception {

    private String message;

    public ParseError(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
       return  this.message;
    }
    public void print(){
        System.out.println(message);
    }



}


package ParseData;

import java.io.IOException;

public class test {

    public static void main(String args[]){

        try {
            ParseData d = Parser.Parser.parse("largefile.txt");





        }catch(IOException e){
            System.out.println("File not found");
        }
    }
}

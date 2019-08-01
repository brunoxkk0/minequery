package br.com.brunoxkk0;

import br.com.brunoxkk0.custom.IHandler;
import br.com.brunoxkk0.custom.Staff;

import java.util.HashMap;

public class Hook {

    static private HashMap<String, IHandler> handlers = new HashMap<>();


    public static void setup(){

        Staff staff = new Staff();


        handlers.put(staff.identifier(), staff);
    }

    public static boolean canHandle(String request){
        return handlers.containsKey(request.toLowerCase());
    }


    public static String handle(String request){
        return handlers.get(request.toLowerCase()).process().toString();
    }

}

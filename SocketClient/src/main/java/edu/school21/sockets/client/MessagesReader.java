package edu.school21.sockets.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.lang.reflect.Type;

public class MessagesReader extends Thread {
    private final Gson gson;
    private final BufferedReader in;

    public MessagesReader(BufferedReader input, Gson gson) {
        this.in = input;
        this.gson = gson;
    }

    @Override
    public void run()  {
        try {
            while (true) {
                Type mapType = new TypeToken<Map<String, String>>(){}.getType();  
                Map<String, String> message = gson.fromJson(in.readLine(), mapType);

               // JsonObject message = gson.fromJson(in.readLine(), JsonObject.class);
                if (message != null && !message.isEmpty()) {

                    System.out.println(message.get("author") + ": " + message.get("text"));
                }
            }
        } catch (IOException e) {
        }
    }

}




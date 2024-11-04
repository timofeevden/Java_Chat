package edu.school21.sockets.app;

import edu.school21.sockets.client.Client;

public class Main {
    public static void main(String[] args) {
        String portFlag = "--port=";
        if (!args[0].startsWith(portFlag)) {
            System.err.println("You need to write '--port=' in first argument!");
            System.exit(-1);
        }
        try {
            int port = Integer.parseInt(args[0].substring(portFlag.length()));
            Client client = new Client(port);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
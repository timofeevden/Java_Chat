package edu.school21.sockets.app;

import edu.school21.sockets.config.SocketsApplicationConfig;
import edu.school21.sockets.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        String portFlag = "--server-port=";
        if (!args[0].startsWith(portFlag)) {
            System.err.println("You need to write '--server-port=' in first argument!");
            System.exit(-1);
        }
        try {
            int port = Integer.parseInt(args[0].substring(portFlag.length()));
            ApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
            Server server = context.getBean(Server.class);
            server.init(port);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
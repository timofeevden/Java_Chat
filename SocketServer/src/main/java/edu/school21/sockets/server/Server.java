package edu.school21.sockets.server;

import edu.school21.sockets.services.UsersService;
import edu.school21.sockets.services.MessagesService;
import edu.school21.sockets.services.ChatroomsService;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

@Component
public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clientList = new LinkedList<>();
    @Autowired
    private UsersService usersService;
    @Autowired
    private MessagesService messagesService;
    @Autowired
    private ChatroomsService chatroomsService;

    public void init(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server was started!");
    }

    public void start() throws IOException {    
         try {
            while (true) {
                Socket newClientSocket = serverSocket.accept();
                try {
                    ClientHandler newClient = new ClientHandler(newClientSocket, usersService, chatroomsService, messagesService, clientList);
                    clientList.add(newClient);
                } catch (IOException e) {
                    newClientSocket.close();
                }
            }
        } finally {
            System.out.println("Server is closed!");
            clientList.clear();
            serverSocket.close();
        }
    }

}
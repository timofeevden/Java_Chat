package edu.school21.sockets.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.school21.sockets.models.User;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.services.UsersService;
import edu.school21.sockets.services.MessagesService;
import edu.school21.sockets.services.ChatroomsService;
import javafx.util.Pair;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.util.Optional;
import java.util.LinkedList;
import java.util.List;

class ClientHandler extends Thread {
    private static final String SIGN_UP = "signUp";
    private static final String SIGN_IN = "signIn";
    private static final String CREATE_ROOM = "createRoom";
    private static final String CHOOSE_ROOM = "chooseRoom";
    private static final String SUCCESS = "Successful!";
    private static final String HELLO_FROM_SERVER = "Hello from Server!";
    private static final String ENTER_USERNAME = "Enter username:";
    private static final String ENTER_PASSWORD = "Enter password:";
    private static final String EXIT = "exit";
    private static final int CNT_FOR_MESSAGES_STORY = 30;
    private final Gson gson;
    private final List<ClientHandler> aliveClients;
    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final UsersService usersService;
    private final MessagesService messagesService;
    private final ChatroomsService chatroomsService;
    private Optional<User> user;
    private Optional<Chatroom> chatroom;

    public ClientHandler(Socket clientSocket, UsersService usersService, ChatroomsService chatroomsService, MessagesService messagesService, List<ClientHandler> clientList) throws IOException {
        this.user = Optional.empty(); 
        this.chatroom = Optional.empty(); 
        this.clientSocket = clientSocket;
        this.usersService = usersService;
        this.messagesService = messagesService;
        this.chatroomsService = chatroomsService;
        this.aliveClients = clientList;
        this.gson = new GsonBuilder()
			.registerTypeAdapter(Message.class, new MessageSerializer())
			.create();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        try {
            out.write(gson.toJson(HELLO_FROM_SERVER) + "\n");
            out.flush();            
            String commandFromClient = gson.fromJson(in.readLine(), String.class);
            this.user = logIn(commandFromClient);
            this.chatroom = getChatroom();
            if (this.user.isPresent() && this.chatroom.isPresent()) {
                startMessaging();
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
            }
            aliveClients.remove(this);
        }
    }

    private Optional<User> logIn(String command) throws IOException {
        if (command == null || command.equals("null")) {
            throw new IOException("Input command in LogIn is null!");
        }
        Optional<User> enteredUser = Optional.empty();

        if (command.equalsIgnoreCase(SIGN_UP)) {
            enteredUser =  signUp();
        } else if (command.equalsIgnoreCase(SIGN_IN)) {
            enteredUser = signIn();
        }
        if (enteredUser.isPresent() ) {
            out.write(gson.toJson(SUCCESS) + "\n");
            out.flush();
        }
        return enteredUser;
    }

    private Optional<User> signUp() throws IOException {
        Pair<String, String> userInfo = getUsernamePassword();
        Optional<User> newUser = usersService.signUp(userInfo.getKey(), userInfo.getValue());
        if (newUser.isPresent() == false) {
            out.write(gson.toJson("Registration failed!") + "\n");
            out.flush();
        }
        return newUser;
    }

    private Optional<User> signIn() throws IOException {
        Pair<String, String> userInfo = getUsernamePassword();
        if (userInfo.getKey() == null || userInfo.getKey().isEmpty() || userInfo.getValue() == null || userInfo.getValue().isEmpty()) {
            return Optional.empty();
        }
        Optional<User> enteredUser = usersService.signIn(userInfo.getKey(), userInfo.getValue());
        if (enteredUser.isPresent() == false) {
            out.write(gson.toJson("Failed: User\\Password is uncorrect! Connection is closed.") + "\n");
            out.flush();
        }
        return enteredUser;
    }

    private Pair<String, String> getUsernamePassword() throws IOException {
        out.write(gson.toJson(ENTER_USERNAME) + "\n");
        out.flush();
        String username = gson.fromJson(in.readLine(), String.class);
        out.write(gson.toJson(ENTER_PASSWORD) + "\n");
        out.flush();
        String password = gson.fromJson(in.readLine(), String.class);
        
        return new Pair<>(username, password);
    }

    private Optional<Chatroom> getChatroom() throws IOException {
        Optional<Chatroom> choosenRoom = Optional.empty();
        String roomOption = gson.fromJson(in.readLine(), String.class);
        if (roomOption == null) {
            throw new IOException("Can't read null roomOption!");
        }
        if (roomOption.equals(CREATE_ROOM)) {
            choosenRoom = createNewChatroom();
        } else if (roomOption.equals(CHOOSE_ROOM)) {
            choosenRoom = chooseExistRoom();
        }
        if (choosenRoom.isPresent()) {
            out.write(gson.toJson(SUCCESS) + "\n");
            out.flush();
        }
        return choosenRoom;
    }

    private Optional<Chatroom> createNewChatroom() throws IOException {
        out.write(gson.toJson("Enter new chatrooms name:") + "\n");
        out.flush();
        String newRoomName = gson.fromJson(in.readLine(), String.class);
        if (newRoomName == null || newRoomName.isEmpty()) {
            throw new IOException("Can't read null newRoomName!");
        }
        Optional<Chatroom> newChatroom = chatroomsService.createChatroom(this.user.get(), newRoomName);

        if (newChatroom.isPresent() == false || newRoomName == null) {
            out.write(gson.toJson("Can't create new room!") + "\n");
            out.flush();
        }
        return newChatroom;
    }

    private Optional<Chatroom> chooseExistRoom() throws IOException {
        List<Chatroom> allRooms = chatroomsService.findAll();
        out.write(gson.toJson("Rooms:") + "\n");
        out.flush();

        int cnt = 1;
        String point = ". ";
        List<String> roomListStrings = new LinkedList<>();
        for(Chatroom room : allRooms) {
            String str = cnt + point + room.getName();
            roomListStrings.add(str);
            ++cnt;
        }

        String lastExit = cnt + point + "Exit";
        roomListStrings.add(lastExit);
        out.write(gson.toJson(roomListStrings) + "\n");
        out.flush();

        Optional<Chatroom> choosenChatroom = Optional.empty();
        String number = gson.fromJson(in.readLine(), String.class);
        int numberOfRoom = -1;
        try {
            numberOfRoom = Integer.parseInt(number) - 1;
        } catch (NumberFormatException e) {
            out.write(gson.toJson("Can't read number of room! " + number) + "\n");
            out.flush();
            return choosenChatroom;
        }
        if (numberOfRoom >= 0 && numberOfRoom < allRooms.size()) {
            choosenChatroom = Optional.of(allRooms.get(numberOfRoom));
        }
        if (choosenChatroom.isPresent() == false) {
            if (numberOfRoom != allRooms.size()) {
                out.write(gson.toJson("Can't choose this room!") + "\n");
                out.flush();
            }
        }
        return choosenChatroom;
    }

    private void startMessaging() throws IOException {
        if (this.user.isPresent() && this.chatroom.isPresent()) {
            out.write(gson.toJson(this.chatroom.get().getName() + "---") + "\n");
            out.flush();
            sendRoomStoryMessages(CNT_FOR_MESSAGES_STORY);
            String text = "";
            while (true) {
                text = gson.fromJson(in.readLine(), String.class);
                if (text == null || text.equals(EXIT) || text.equals("null") || !clientSocket.isConnected()) {
                    for (ClientHandler otherClient : aliveClients) {
                        if (otherClient != this) {
                            otherClient.sendLeaveNotification(this.user.get(), this.chatroom.get());
                        }
                    }
                    break;
                } else if (!text.equals("")) {
                    Message message = new Message(null, this.user.get(), this.chatroom.get(), text);
                    messagesService.saveMessage(message);
                    for (ClientHandler otherClient : aliveClients) {
                        otherClient.sendMessage(message);
                    }
                }
            }
        }
    }

    private void sendRoomStoryMessages(int cntMessages) throws IOException {
        List<Message> storyMessages = messagesService.getRoomStory(this.chatroom.get().getId(), cntMessages);
        out.write(gson.toJson(storyMessages) + "\n");
        out.flush();
    }

    private void sendMessage(Message message) {
        if (this.user.isPresent() && this.chatroom.isPresent() && this.chatroom.get().getId() == message.getRoom().getId()) {
            try {
                out.write(gson.toJson(message) + "\n");
                out.flush();
            } catch (IOException ignore) {}
        }
    }

    private void sendLeaveNotification(User leaveUser, Chatroom room) {   
        if (this.user.isPresent() && this.chatroom.isPresent() && this.chatroom.get().getId() == room.getId() && !this.user.get().equals(leaveUser)) {        
            Message notification = new Message(leaveUser, room, leaveUser.getName() + " leave the chat.");
            notification.getAuthor().setName("System");
            try {
                out.write(gson.toJson(notification) + "\n");
                out.flush();
            } catch (IOException ignore) {}
        }
    }
    
}






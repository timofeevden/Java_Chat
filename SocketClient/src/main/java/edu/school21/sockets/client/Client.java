package edu.school21.sockets.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Client {
    private static final String SIGN_UP = "signUp";
    private static final String SIGN_IN = "signIn";
    private static final String CREATE_ROOM = "createRoom";
    private static final String CHOOSE_ROOM = "chooseRoom";
    private static final String SUCCESS = "Successful!";
    private static final String EXIT = "exit";
    private final Gson gson;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputUser;
    private final int port;

    public Client(int port) {
        this.port = port;
        gson = new Gson();
    }

    public void start() {
        try {
            socket = new Socket("localhost", port);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            String fromServer = gson.fromJson(in.readLine(), String.class);
            System.out.println(fromServer);

            if (menuLoginToAccount() && menuChooseChatroom()) {
                startMessaging();
            }
        } catch (IOException e) {
            System.out.println("Connection lost");
        } finally {
            try {
                socket.close();
                out.close();
                in.close();
                inputUser.close();
            } catch (IOException e) {
            //    e.printStackTrace();
            }
        }
    }

    private boolean menuLoginToAccount() throws IOException {
        while (true) {
            System.out.println("1. signIn\n2. SignUp\n3. Exit");
            String input = inputUser.readLine();
            if (input == null || input.equals(EXIT)) {
                return false;
            }
            int choosenOption = -1;
            try {
                choosenOption = Integer.parseInt(input);  
            } catch (NumberFormatException e) {
                System.out.println("You need write number of option!");
                continue;
            }
            if (choosenOption == 1) {
                return signIn();
            } else if (choosenOption == 2) {
                return signUp();
            } else if (choosenOption == 3) {
                return false;
            } else {
                System.out.println("Incorrect option: " + input);
            }
        }
    }

    private boolean signUp() throws IOException {
        out.write(gson.toJson(SIGN_UP) + "\n");
        out.flush();
        return enterUsernameAndPassword();
    }

    private boolean signIn() throws IOException {
        out.write(gson.toJson(SIGN_IN) + "\n");
        out.flush();
        return enterUsernameAndPassword();
    }

    private boolean enterUsernameAndPassword() throws IOException {
        System.out.println(gson.fromJson(in.readLine(), String.class));
        String inputInfo = inputUser.readLine();
        out.write(gson.toJson(inputInfo) + "\n");
        out.flush();

        System.out.println(gson.fromJson(in.readLine(), String.class));
        inputInfo = inputUser.readLine();
        out.write(gson.toJson(inputInfo) + "\n");
        out.flush();
        String answer = gson.fromJson(in.readLine(), String.class);
        if (answer != null) {
            if (answer.equals(SUCCESS)) {
                return true;
            } else {
                System.out.println(answer);
            }
        }
        return false;
    }

    private boolean menuChooseChatroom() throws IOException {
        while (true) {
            System.out.println("1. Create room\n2. Choose room\n3. Exit");
            String input = inputUser.readLine();
            if (input == null || input.equals(EXIT)) {
                return false;
            }
            int choosenOption = -1;
            try {
                choosenOption = Integer.parseInt(input);  
            } catch (NumberFormatException e) {
                System.out.println("You need write number of option!");
                continue;
            }
            if (choosenOption == 1) {
                return createNewRoom();
            } else if (choosenOption == 2) {
                return chooseRoom();
            } else if (choosenOption == 3) {
                return false;
            } else {
                System.out.println("Incorrect option: " + input);
            }
        }
    }

    private boolean createNewRoom() throws IOException {
        out.write(gson.toJson(CREATE_ROOM) + "\n");
        out.flush();
        System.out.println(gson.fromJson(in.readLine(), String.class));
        String roomName = inputUser.readLine();
        out.write(gson.toJson(roomName) + "\n");
        out.flush();
        String answer = gson.fromJson(in.readLine(), String.class);
        if (answer != null) {
            if (answer.equals(SUCCESS)) {
                return true;
            } else {
                System.out.println(answer);
            }
        }
        return false;
    }

    private boolean chooseRoom() throws IOException {
        out.write(gson.toJson(CHOOSE_ROOM) + "\n");
        out.flush();
        System.out.println(gson.fromJson(in.readLine(), String.class));  //  "Rooms:"

        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> roomsList = gson.fromJson(in.readLine(), listType);
        for(String roomString : roomsList) {
             System.out.println(roomString);
        }

        int roomsAmount = roomsList.size();
        int choosenRoom = -1;
        while (choosenRoom < 1 || choosenRoom > (roomsAmount + 1)) {
            String input = inputUser.readLine();
            if (input == null || input.equals(EXIT)) {
                return false;
            }
            try {
                choosenRoom = Integer.parseInt(input);
                if (choosenRoom < 1 || choosenRoom > (roomsAmount + 1)) {
                    System.out.println("Number of room is incorrect!");
                } else if (choosenRoom == (roomsAmount + 1)) {
                    break;  //  choosen exit
                }
            } catch (NumberFormatException e) {
                System.out.println("You need write number of room!");
                continue;
            }
        }
        out.write(gson.toJson(choosenRoom) + "\n");
        out.flush();
        String answer = gson.fromJson(in.readLine(), String.class);
        if (answer != null && answer.equals(SUCCESS)) {
            return true;
        } else {
            if (answer != null) {
                System.out.println(answer);
            }
            return false;
        }
    }

    private void startMessaging() throws IOException {
        String roomName = gson.fromJson(in.readLine(), String.class);
        System.out.println(roomName);
        getRoomStoryMessages();
        MessagesReader messagesReader = new MessagesReader(in, gson);
        messagesReader.start();

        String message = "";
        while (true) {
            message = inputUser.readLine();
            out.write(gson.toJson(message) + "\n");
            out.flush();
            if (message.equals(EXIT)) {
                System.out.println("You have left the chat.");
                break;
            }
        }
    }

    private void getRoomStoryMessages() throws IOException {
        Type listMapType = new TypeToken<List<Map<String, String>>>() {}.getType();
        List<Map<String, String>> messagesStoryObjects = gson.fromJson(in.readLine(), listMapType);

        for(Map<String, String> message : messagesStoryObjects) {
            System.out.println(message.get("author") + ": " + message.get("text"));
        }
    }
}

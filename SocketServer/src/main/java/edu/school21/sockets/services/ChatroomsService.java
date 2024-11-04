package edu.school21.sockets.services;

import edu.school21.sockets.models.User;
import edu.school21.sockets.models.Chatroom;
import java.util.Optional;
import java.util.List;

public interface ChatroomsService {
    Optional<Chatroom> createChatroom(User owner, String name);

    List<Chatroom> findAll();

}

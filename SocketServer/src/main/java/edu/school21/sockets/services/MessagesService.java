package edu.school21.sockets.services;

import edu.school21.sockets.models.Message;
import java.util.List;

public interface MessagesService {
    void saveMessage(Message message);

    List<Message> getRoomStory(Long room_id, int amount);
}

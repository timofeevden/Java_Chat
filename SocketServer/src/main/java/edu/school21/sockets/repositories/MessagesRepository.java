package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Message;

import java.util.List;
import java.util.Optional;

public interface MessagesRepository {
    Optional<Message> findById(Long id);

    List<Message> getRoomStory(Long room_id, int amount);

    void save(Message message);

    void update(Message message);

    void delete(Long id);
}


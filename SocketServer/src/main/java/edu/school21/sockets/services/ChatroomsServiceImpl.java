package edu.school21.sockets.services;

import edu.school21.sockets.models.User;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.repositories.UsersRepository;
import edu.school21.sockets.repositories.MessagesRepository;
import edu.school21.sockets.repositories.ChatroomsRepository;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Optional;

@Component("chatroomsService")
@Scope("singleton")
public class ChatroomsServiceImpl implements ChatroomsService {
    private final UsersRepository usersRepository;
    private final MessagesRepository messagesRepository;
    private final ChatroomsRepository chatroomsRepository;

    @Autowired
    public ChatroomsServiceImpl(@Qualifier("usersRepositoryJdbc") UsersRepository usersRepository, 
        @Qualifier("messagesRepositoryJdbc") MessagesRepository messagesRepository,
        @Qualifier("chatroomsRepositoryJdbc")ChatroomsRepository chatroomsRepository) {
        this.usersRepository = usersRepository;
        this.messagesRepository = messagesRepository;
        this.chatroomsRepository = chatroomsRepository;
    }

    @Override
    public Optional<Chatroom> createChatroom(User owner, String name) {
        List<Chatroom> allRooms = chatroomsRepository.findAll();
        Long id = allRooms.isEmpty() ? 1 : (Long) allRooms.get(allRooms.size() - 1).getId() + 1;
        Chatroom newChatroom = new Chatroom(id, name, owner);
        chatroomsRepository.save(newChatroom);
        return Optional.of(newChatroom);
    }

    @Override
    public List<Chatroom> findAll() {
        return chatroomsRepository.findAll();
    }
}
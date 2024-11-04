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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Component("messagesService")
@Scope("singleton")
public class MessagesServiceImpl implements MessagesService {
    private final UsersRepository usersRepository;
    private final MessagesRepository messagesRepository;
    private final ChatroomsRepository chatroomsRepository;

    @Autowired
    public MessagesServiceImpl(@Qualifier("usersRepositoryJdbc") UsersRepository usersRepository, 
        @Qualifier("messagesRepositoryJdbc") MessagesRepository messagesRepository,
        @Qualifier("chatroomsRepositoryJdbc")ChatroomsRepository chatroomsRepository,
        @Qualifier("bCryptPasswordEncoder")PasswordEncoder pswdEncoder) {
        this.usersRepository = usersRepository;
        this.messagesRepository = messagesRepository;
        this.chatroomsRepository = chatroomsRepository;
    }

    @Override
    public void saveMessage(Message message) {
        messagesRepository.save(message);
    }

    @Override
    public List<Message> getRoomStory(Long room_id, int amount) {
        return messagesRepository.getRoomStory(room_id, amount);
    }
}
package edu.school21.sockets.services;

import edu.school21.sockets.models.User;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.repositories.UsersRepository;
import edu.school21.sockets.repositories.MessagesRepository;
import edu.school21.sockets.repositories.ChatroomsRepository;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Component("userService")
@Scope("singleton")
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final MessagesRepository messagesRepository;
    private final ChatroomsRepository chatroomsRepository;
    private final PasswordEncoder pswdEncoder;

    @Autowired
    public UsersServiceImpl(@Qualifier("usersRepositoryJdbc") UsersRepository usersRepository, 
        @Qualifier("messagesRepositoryJdbc") MessagesRepository messagesRepository,
        @Qualifier("chatroomsRepositoryJdbc")ChatroomsRepository chatroomsRepository,
        @Qualifier("bCryptPasswordEncoder")PasswordEncoder pswdEncoder) {
        this.usersRepository = usersRepository;
        this.messagesRepository = messagesRepository;
        this.chatroomsRepository = chatroomsRepository;
        this.pswdEncoder = pswdEncoder;
    }

    @Override
    public Optional<User> signUp(String name) {
        return signUp(name, UUID.randomUUID().toString());
    }

    @Override
    public Optional<User> signUp(String name, String password) {
        Optional<User> foundUser = usersRepository.findByName(name);
        if (foundUser.isPresent()) {
            return Optional.empty();
        }
        List<User> allUsers = usersRepository.findAll();
        Long id = allUsers.isEmpty() ? 1 : (Long) allUsers.get(allUsers.size() - 1).getId() + 1;
        User newUser = new User(id, name, pswdEncoder.encode(password));
        usersRepository.save(newUser);
        return Optional.of(newUser);
    }

    @Override
    public Optional<User> signIn(String name, String password) {
        Optional<User> foundUser = usersRepository.findByName(name);
        if (foundUser.isPresent() && pswdEncoder.matches(password, foundUser.get().getPassword())) {
            return foundUser;
        }
        return Optional.empty();
    }
}
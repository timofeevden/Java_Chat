package edu.school21.sockets.services;

import edu.school21.sockets.models.User;
import java.util.Optional;

public interface UsersService {
    Optional<User> signUp(String name);

    Optional<User> signUp(String name, String password);

    Optional<User> signIn(String name, String password);
}

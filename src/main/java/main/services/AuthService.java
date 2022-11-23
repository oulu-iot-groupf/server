package main.services;

import main.database.Database;
import main.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

  public User loginUserByUsernameAndPassword(String username, String password) {
    Optional<User> optionalUser = Database.users.stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password)).findFirst();
    if(optionalUser.isPresent()) {
      return optionalUser.get().withoutPassword();
    } else {
      return null;
    }
  }

}

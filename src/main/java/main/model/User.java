package main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class User {

  private int id;
  private String username;

  private String password;

  public User withoutPassword() {
    return new User(id, username, "");
  }

}

package main.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServerMessage {

  private String message;
  private String[] params;

}

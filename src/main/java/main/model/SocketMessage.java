package main.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocketMessage {

  private String message;
  private String[] params;


}

package main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Device {

  private int id;
  private String name;
  private int ownerId;
  private boolean open;

}

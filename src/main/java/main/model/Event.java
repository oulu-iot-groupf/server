package main.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Event {

  public enum Type {
	DOOR_OPEN_SUCCESS,
	DOOR_OPEN_FAILURE,
	LOCK_ONLINE,
  }

  private Type type;
  private Date timestamp;
  private String message;
  private String payload;
  private int deviceId;

}

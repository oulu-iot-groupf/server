package main.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeviceMessage {

  private int deviceId;
  private String message;
  private String[] params;

}

package main.database;

import main.model.Event;
import main.model.User;
import main.model.Device;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Database {

  public static final List<Event> events = new ArrayList<>();
  public static final List<User> users = new ArrayList<>();
  public static final List<Device> devices = new ArrayList<>();

  static {
    users.add(new User(0, "daniel99", "password123"));
    users.add(new User(1, "tarek_boss", "password123"));
    users.add(new User(2, "miikka_ninja", "password123"));

    devices.add(new Device(0, "Daniel's Device", 0, false));
    devices.add(new Device(1, "Device", -1, false));
    devices.add(new Device(2, "Device", -1, false));

    // devices.add(new Device(1, "Tarek's Device", 1));
    // devices.add(new Device(2, "Miikka's Device", 2));

    events.add(new Event(Event.Type.DOOR_OPEN_SUCCESS,
        new Date(), // current date
        "Opened door successfully",
        null, // later an image can be added as payload
        0 // device 0, so Daniel's Device
    ));

    events.add(new Event(Event.Type.DOOR_OPEN_FAILURE,
        new Date(), // current date
        "Tried to open door but failed (access denied)",
        null, // later an image can be added as payload
        0 // device 0, so Daniel's Device
    ));

  }

  private Database() {
  }

  public static Device getDeviceById(int id) {
    return devices.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
  }

  public static List<Device> unconfiguredDevices() {
    return devices.stream().filter(d -> d.getOwnerId() == -1).collect(Collectors.toList());
  }

  public static User getUserBy(int id) {
    return users.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
  }

}

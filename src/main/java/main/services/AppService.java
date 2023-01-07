package main.services;

import main.database.Database;
import main.model.Event;
import main.model.User;
import main.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Service exposing functionality to the mobile app
@Service
public class AppService {

  @Autowired
  private AuthService authService;

  public List<Device> getDevices(int userId) {
    return Database.devices.stream().filter(d -> d.getOwnerId() == userId).collect(Collectors.toList());
  }

  public List<Device> getUnconfiguredDevices() {
    return Database.unconfiguredDevices();
  }

  public boolean ownDevice(int ownerId, int deviceId) {
    Device device = Database.getDeviceById(deviceId);
    if (device != null) {
      device.setOwnerId(ownerId);
      return true;
    } else {
      return false;
    }
  }

  public List<Event> getEvents(int userId) {
    return Database.events.stream().filter(e -> getDeviceById(e.getDeviceId()).getOwnerId() == userId)
        .collect(Collectors.toList());
  }

  public User signIn(String username, String password) {
    return authService.loginUserByUsernameAndPassword(username, password);
  }

  public boolean open(int deviceId) {
    Database.getDeviceById(deviceId).setOpen(true);
    return true;
  }

  public boolean lock(int deviceId) {
    Database.getDeviceById(deviceId).setOpen(false);
    return true;
  }

  public User getUserById(int userId) {
    return Database.users.stream().filter(u -> u.getId() == userId).findFirst().orElse(null);
  }

  public Device getDeviceById(int userId) {
    return Database.devices.stream().filter(u -> u.getId() == userId).findFirst().orElse(null);
  }
}

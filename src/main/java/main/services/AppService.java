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

  @Autowired
  private LockService socketService;

  public List<Device> getDevices(int userId) {
	return Database.devices.stream().filter(d -> d.getOwnerId() == userId).collect(Collectors.toList());
  }

  public List<Device> getUnconfiguredDevices() {
	return Database.unconfiguredDevices();
  }

  public boolean ownDevice(int ownerId, int deviceId) {
	Device device = Database.getDeviceById(deviceId);
	if(device != null) {
	  device.setOwnerId(ownerId);
	  return true;
	} else {
	  return false;
	}
  }

  public List<Event> getEvents(int deviceId) {
	return Database.events.stream().filter(e -> e.getDeviceId() == deviceId).collect(Collectors.toList());
  }

  public User signIn(String username, String password){
	return authService.loginUserByUsernameAndPassword(username, password);
  }

  public boolean open(int deviceId) {
	return socketService.open(deviceId);
  }

  public boolean lock(int deviceId) {
	return socketService.lock(deviceId);
  }

  public User getById(int userId) {
	return Database.users.stream().filter(u -> u.getId() == userId).findFirst().orElse(null);
  }
}


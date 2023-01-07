package main.web;

import main.model.Device;
import main.model.DeviceMessage;
import main.model.Event;
import main.model.ServerMessage;
import main.model.User;
import main.services.AppService;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import main.database.Database;

@RestController
@RequestMapping()
public class WebController {

  @Autowired
  private AppService appService;

  // 1. Endpoints for the mobile application

  @PostMapping("login")
  public ResponseEntity postLogin(String username, String password) {

    System.out.println("Logging in with username " + username);

    User user = appService.signIn(username, password);
    if (user != null) {
      return new ResponseEntity(user, HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("devices")
  public ResponseEntity postDevices(int userId) {

    System.out.println("Querying devices of user " + userId);

    if (appService.getUserById(userId) != null) {
      return new ResponseEntity(appService.getDevices(userId), HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("unconfigured")
  public ResponseEntity postUnconfiguredDevices() {

    System.out.println("Querying unconfigured devices");

    return new ResponseEntity(appService.getUnconfiguredDevices(), HttpStatus.OK);
  }

  @PostMapping("claimdevice")
  public ResponseEntity postClaimDevice(int ownerId, int deviceId) {

    System.out.println("Claiming device " + deviceId + " by user " + ownerId);

    if (appService.ownDevice(ownerId, deviceId)) {
      return new ResponseEntity("success", HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("open")
  public ResponseEntity postOpen(int deviceId) {

    System.out.println("Unlocking device " + deviceId);

    if (appService.open(deviceId)) {
      return new ResponseEntity("success", HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("lock")
  public ResponseEntity postLock(int deviceId) {

    System.out.println("Locking device " + deviceId);

    if (appService.lock(deviceId)) {
      return new ResponseEntity("success", HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("events")
  public ResponseEntity<User> postEvents(int userId) {

    System.out.println("Querying events of user " + userId);

    if (appService.getUserById(userId) != null) {
      return new ResponseEntity(appService.getEvents(userId), HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  // 2. Endpoints for the raspberry pi lock devices

  // endpoint to quiery the server's state for the given device (preferably the
  // device itself, not some other lock)
  // returns a Device object as JSON
  @PostMapping("device")
  public ResponseEntity postDevice(@RequestBody DeviceMessage message) {

    int deviceId = Integer.parseInt(message.getParams()[0]);

    System.out.println("Querying device status of device " + deviceId);

    if (appService.getDeviceById(deviceId) != null) {
      return new ResponseEntity(appService.getDeviceById(deviceId), HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  // endpoint to send a new event to the server, such as a door opening event
  @PostMapping("event")
  public ResponseEntity postEvent(@RequestBody DeviceMessage message) {

    Event event = new Event(
        Event.Type.valueOf(message.getParams()[0]), // message type
        new Date(),
        message.getParams()[1], // extra message
        message.getParams()[2], // binary payload (optional)
        message.getDeviceId());

    System.out.println("New event:\n" + event);

    Database.events.add(event);

    return new ResponseEntity(HttpStatus.OK);
  }

  // endpoint to obtain a new deviceId from the server
  // returns a Device object that now has the assigned deviceId
  // it does not have an owner or a name yet, it needs to be claimed
  // via the mobile app
  @PostMapping("handshake")
  public ResponseEntity postHandshake(@RequestBody DeviceMessage message) {

    int id = Database.devices.size();
    Device device = new Device(id, "Device", -1, false);

    System.out.println("A new device with ID " + id + " has been registered.");

    Database.devices.add(device);

    return new ResponseEntity(new ServerMessage("id", new String[] { String.valueOf(id) }), HttpStatus.OK);
  }

}

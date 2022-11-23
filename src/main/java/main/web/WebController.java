package main.web;

import main.model.User;
import main.services.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class WebController {

  @Autowired
  private AppService appService;

  @PostMapping("login")
  public ResponseEntity postLogin(String username, String password) {
    User user = appService.signIn(username, password);
    if(user != null) {
      return new ResponseEntity(user, HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("devices")
  public ResponseEntity postDevices(int userId) {
    if(appService.getById(userId) != null) {
      return new ResponseEntity(appService.getDevices(userId), HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("unconfigured")
  public ResponseEntity postUnconfiguredDevices() {
    return new ResponseEntity(appService.getUnconfiguredDevices(), HttpStatus.OK);
  }

  @PostMapping("claimdevice")
  public ResponseEntity postClaimDevice(int ownerId, int deviceId) {
    if (appService.ownDevice(ownerId, deviceId)) {
      return new ResponseEntity("success", HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("open")
  public ResponseEntity postOpen(int deviceId) {
    if (appService.open(deviceId)) {
      return new ResponseEntity("success", HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("lock")
  public ResponseEntity postLock(int deviceId) {
    if (appService.lock(deviceId)) {
      return new ResponseEntity("success", HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("events")
  public ResponseEntity<User> postEvents(int userId) {
    if(appService.getById(userId) != null) {
      return new ResponseEntity(appService.getEvents(userId), HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.FORBIDDEN);
    }
  }

}

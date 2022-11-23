## main.Server

### How to use

0. Make sure you have [JDK 19](https://jdk.java.net/19/) and [Maven](https://maven.apache.org/download.cgi) installed
1. Clone project: `git clone https://github.com/oulu-iot-groupf/server.git`
2. Run:  `mvn spring-boot:run`

It will be listening for HTTP requests on port `8080` and socket connections on port `8081`

To change the default values in the database at startup, edit `src/main/java/database/Database.java`. There are already a few users, devices and events added.

### HTTP API for Mobile App

#### `POST /login?username=[username]&password=[password]`

Returns a User object with password hidden on successful login:

```json
{
	"id": 0,
	"username": "daniel99",
	"password": ""
}
```

#### `POST /unconfigured`

Returns the list of devices not owned by anyone yet:

```json
[
	{
		"id": 1,
		"name": "Device",
		"ownerId": -1
	},
	{
		"id": 2,
		"name": "Device",
		"ownerId": -1
	}
]
```

#### `POST /devices?userId=[userId]`

Returns the list of devices owned by the specified user

```json
[
	{
		"id": 0,
		"name": "Daniel's Device",
		"ownerId": 0
	}
]
```

#### `POST /events?userId=[userId]`

Returns the events related to the devices owned by the specified user

```json
[
	{
		"type": "DOOR_OPEN_SUCCESS",
		"timestamp": "2022-11-23T17:15:24.371+00:00",
		"message": "Opened door successfully",
		"payload": null,
		"deviceId": 0
	},
	{
		"type": "DOOR_OPEN_FAILURE",
		"timestamp": "2022-11-23T17:15:24.371+00:00",
		"message": "Tried to open door but failed (access denied)",
		"payload": null,
		"deviceId": 0
	}
]
```

#### `POST /claimdevice?ownerId=[userId]&deviceId=[deviceId]`

Sets the owner of an unconfigured device

Responds with 200 OK and "success" as the body - no json

#### `POST /open?deviceId=[deviceId]`

Opens the device with the specified id. Depending on whether there is a connection with the device, or a device with the given ID even exists, 

Responds with `200 OK` and "`success`" as the body - no json

OR

Responds with `403 Forbidden` and "`fail`" as the body - no json

#### `POST /lock?deviceId=[deviceId]`

Locks the device with the specified id. Depending on whether there is a connection with the device, or a device with the given ID even exists,

Responds with `200 OK` and "`success`" as the body - no json

OR

Responds with `403 Forbidden` and "`fail`" as the body - no json

### Socket main.Server for Raspberry Pi 

#### Handshake

The handhake has to happen at the start of each message sequence.

If the device is connecting for the first time, it should send the following message to the server (port `8081`):

```json
{
  "message" : "id",
  "params" : [ "-1" ]
}

```

and it will receive this back:

```json
{
  "message" : "id",
  "params" : [ "3" ]
}
```
Where 3 is the device ID assigned by the server sequentally. The device should hold onto this ID.

If the device already registered itself, it should send the following:

```json
{
  "message": "id",
  "params": [ "3" ]
}
```

and it will receive this back:

```json
{
  "message": "id",
  "params": [ "3" ]
}
```

Where 3 is the device's own ID again.

After the handshake, the client should keep the connection alive, send events to the server through the established connection and also listen to commands from the server. The server will be polling for incoming messages every 1000 milliseconds.

#### Events

Use these messages to push events to the server:


Successful facial recognition door opening by user:

```json
{
  "message": "event",
  "params": [ "DOOR_OPEN_SUCCESS", "Miikka opened the door", "" ]
}
```

Where the first parameter is the event type, the second is an extra message attached to the event and the third one will be the binary payload (e.g. image) later.

Failed facial recognition door opening by user:

```json
{
  "message": "event",
  "params": [ "DOOR_OPEN_FAILURE", "Someone tried to open the door", "" ]
}
```

#### Commands

The server can also send instructions to the raspberry pi:

The following should open the lock:

```json
{
  "message": "open",
  "params": []
}
```

The following should lock the lock:

```json
{
  "message": "lock",
  "params": []
}
```



## Notes

To send socket messages to the server from MacOS terminal:

```
echo "-1" | nc localhost 8081
```

```
echo "{
\"message\" : \"id\",
\"params\" : [ \"-1\" ]
}" | nc localhost 8081
```
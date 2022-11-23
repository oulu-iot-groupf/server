package main.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import main.database.Database;
import main.model.Event;
import main.model.Device;
import main.model.SocketMessage;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
public class LockService {

  private static final Map<Device, Socket> connections = new HashMap<>();

  static {
	new Thread(new Runnable() {
	  @Override
	  public void run() {

		try {
		  // 1. start server socket
		  System.out.println("Binding socket server to port 8081");
		  ServerSocket serverSocket = new ServerSocket(8081);

		  // 2. wait for incoming connections
		  System.out.println("Waiting for incoming connections on port 8081...");
		  while (true) {
			Socket connection = serverSocket.accept();
			new Thread(() -> {
			  try {
				System.out.println("Connected from " + connection.getInetAddress());

				System.out.println("Initiationg handshake");
				// 3. wait for handshake
				Device device = handshake(connection);
				System.out.println("Shook hands with " + device.getName());

				sendMessageToDevice(device, new SocketMessage("OK", null));

				// 4. process message from client

				String content = readFromConnection(connection);

				SocketMessage socketMessage = fromJSON(content);

				if(socketMessage != null) {
				  handleMessage(socketMessage, device);
				}

				// 5. disconnect client
				connection.close();
				System.out.println(connection.getInetAddress() + " disconnected");
			  } catch (Exception e) {
				e.printStackTrace();
			  }
			}).start();
		  }

		} catch (IOException e) {
		  e.printStackTrace();
		}

	  }
	}).start();
  }

  private static boolean handleMessage(SocketMessage message, Device device) {
	switch (message.getMessage()) {
	  case "exit" -> {
		return false;
	  }
	  case "event" -> {
		Database.events.add(new Event(
			Event.Type.valueOf(message.getParams()[0]), // message type
			new Date(),
			message.getParams()[1], // extra message
			message.getParams()[2], // binary payload (optional)
			device.getId()
		));
		return true;
	  }
	}
	return false;
  }

  private static boolean writeToConnection(Socket connection, String message) {
	try (PrintWriter out = new PrintWriter(connection.getOutputStream(), true)) {
	  out.print(message);
	  return true;
	} catch (IOException e) {
	  throw new RuntimeException(e);
	}
  }

  private static String readFromConnection(Socket connection) {
	BufferedReader in = null;
	try {
	  in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	  StringBuilder message = new StringBuilder();
	  String line;
	  while ((line = in.readLine()) != null) {
		message.append(line);
	  }
	  System.out.println("Incoming socket message: \"" + message + "\"");
	  return message.toString();
	} catch (IOException e) {
	  throw new RuntimeException(e);
	}
  }

  private static Device handshake(Socket connection) {

	int id;
	try {
	  id = Integer.parseInt(fromJSON(readFromConnection(connection)).getParams()[0]);

	} catch (NumberFormatException e) {
	  return null;
	}

	// no id yet, so we assign one
	if (id == -1) {
	  id = Database.devices.size();
	  Device device = new Device(id, "Device", -1);
	  writeToConnection(connection, toJSON(new SocketMessage("id", new String[]{String.valueOf(id)})));
	  Database.devices.add(device);
	}

	return Database.getDeviceById(id);

  }


  public static boolean open(int deviceId) {
	return sendMessageToDevice(Database.getDeviceById(deviceId), new SocketMessage("open", null));
  }

  public static boolean lock(int deviceId) {
	return sendMessageToDevice(Database.getDeviceById(deviceId), new SocketMessage("lock", null));
  }

  private static boolean sendMessageToDevice(Device device, SocketMessage message) {
	Socket conn = connections.get(device);
	if (conn == null) return false;
	return writeToConnection(conn, toJSON(message));
  }

  private static String toJSON(SocketMessage o) {
	ObjectWriter ow = new ObjectMapper().writer();
	String json = null;
	try {
	  json = ow.writeValueAsString(o);
	} catch (JsonProcessingException e) {
	  throw new RuntimeException(e);
	}
	return json;

  }

  private static SocketMessage fromJSON(String json) {

	ObjectMapper mapper = new ObjectMapper();

	try {
	  SocketMessage socketMessage = mapper.readValue(json, SocketMessage.class);

	  return socketMessage;
	} catch (Exception e) {
	  e.printStackTrace();
	  System.out.println("JSON error while parsing: \"" + json + "\"");
	  return null;
	}

  }
}

package io.bobba.poc.core.rooms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.bobba.poc.BobbaEnvironment;
import io.bobba.poc.communication.outgoing.roomdata.HeightMapComposer;
import io.bobba.poc.communication.outgoing.roomdata.RoomDataComposer;
import io.bobba.poc.communication.outgoing.roomdata.RoomModelInfoComposer;
import io.bobba.poc.core.rooms.gamemap.RoomModel;
import io.bobba.poc.core.rooms.roomdata.LockType;
import io.bobba.poc.core.rooms.roomdata.RoomData;
import io.bobba.poc.core.users.User;

public class RoomManager {
	private Map<Integer, Room> rooms;
	private Map<String, RoomModel> models;
	
	public RoomManager() {
		this.rooms = new HashMap<>();
		this.models = new HashMap<>();
	}
	
	public RoomModel getModel(String modelId) {
		return models.getOrDefault(modelId, null);
	}
	
	public Room getLoadedRoom(int roomId) {
		return rooms.getOrDefault(roomId, null);
	}
	
	public void initialize() throws SQLException {
		this.loadModelsFromDb();
		this.createDummyRoom();
	}
	
	private void loadModelsFromDb(){	
        try{
        		models = RoomDAO.loadModelsFromDb(models);
        	}catch(SQLException e) {
        		e.printStackTrace();
        	}catch(NullPointerException e) {
        		System.out.println("Erro[bobba.poc.core.rooms] --> RoomDAO.loadModelsFromDb retornou null");
        	}
	}
	
	private void createDummyRoom() {
		//ResultSet rs =RoomDAO.loadDBRooms();
		Map<Integer,Room> listroom = RoomDAO.loadDBRooms();
		for(Entry<Integer, Room> room : listroom.entrySet()) {
			this.rooms.put(room.getKey(), room.getValue());
		}
	}
	
	public void onCycle() {
		List<Room> cyclingRooms = new ArrayList<>(rooms.values());
		for (Room room : cyclingRooms) {
			room.onCycle();
		}
	}
	
	public void prepareRoomForUser(User user, int roomId, String password) {
		Room currentRoom = user.getCurrentRoom();
		if (currentRoom != null) {
			currentRoom.getRoomUserManager().removeUserFromRoom(user);
		}
		Room newRoom = null;
		if (roomId == -1 && this.getLoadedRooms().size() > 0) {
			newRoom = this.getLoadedRooms().get(0); //Home room
		} else {
			newRoom = this.getLoadedRoom(roomId);	
		}
		 
		if (newRoom != null) {
			user.setLoadingRoomId(newRoom.getRoomData().getId());
			user.getClient().sendMessage(new RoomModelInfoComposer(newRoom.getRoomData().getModelId(), newRoom.getRoomData().getId()));
		}
	}
	
	public void prepareHeightMapForUser(User user) {
		Room room = this.getLoadedRoom(user.getLoadingRoomId());
		if (room != null) {
			user.getClient().sendMessage(new HeightMapComposer(room.getGameMap().getRoomModel()));
		}
	}

	public void finishRoomLoadingForUser(User user) {
		Room room = this.getLoadedRoom(user.getLoadingRoomId());
		if (room != null) {
			room.getRoomUserManager().addUserToRoom(user);
			user.setLoadingRoomId(0);
			user.getClient().sendMessage(new RoomDataComposer(room.getRoomData()));
		}
	}

	public void handleUserLeaveRoom(User user) {
		Room currentRoom = user.getCurrentRoom();
		if (currentRoom != null) {
			currentRoom.getRoomUserManager().removeUserFromRoom(user);
		}
	}
	
	public List<Room> getLoadedRooms() {
		return new ArrayList<>(rooms.values());
	}
	
	public int getLastId() {
		return this.rooms.get(this.rooms.size()).getRoomData().getId();
	}

	public void createRoom(User user, String roomName, String modelId) {
		if (roomName.length() > 0) {
			RoomModel model = getModel(modelId);
			if (model != null) {
				RoomData roomData = new RoomData(this.getLastId()+ 1, roomName, user.getUsername(), "", 25, "", modelId, LockType.Open);
				Room room = new Room(roomData, model);
				room.saveRoom();
				
				this.rooms.put(room.getRoomData().getId(), room);
				
				prepareRoomForUser(user, room.getRoomData().getId(), "");
			}
		}
	}
}

package io.bobba.poc.core.users;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.bobba.poc.communication.outgoing.users.LoginOkComposer;
import io.bobba.poc.communication.outgoing.users.UpdateCreditsBalanceComposer;
import io.bobba.poc.core.gameclients.GameClient;
import io.bobba.poc.core.rooms.Room;
import io.bobba.poc.core.rooms.users.RoomUser;
import io.bobba.poc.core.users.inventory.Inventory;
import io.bobba.poc.core.users.messenger.Messenger;
import io.bobba.poc.misc.logging.LogLevel;
import io.bobba.poc.misc.logging.Logging;

public class User {
	private int id;
	private int rank;
	private int credits;
	private int homeRoomId;
	private int loadingRoomId;
	private String username;
	private String password;
	private String look;
	private String motto;
	private GameClient client;
	private boolean disconnected;
	private Room currentRoom;
	private Inventory inventory;
	private Messenger messenger;
	private UserDAO userDAO;

	public Room getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(Room currentRoom) {
		this.currentRoom = currentRoom;
	}

	public int getId() {
		return id;
	}

	public GameClient getClient() {
		return client;
	}

	public String getUsername() {
		return username;
	}
	
	public int getRank() {
		return rank;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLook() {
		return look;
	}

	public void setLook(String look) {
		this.look = look;
		notifyChange();
	}

	public String getMotto() {
		return motto;
	}

	public void setMotto(String motto) {
		this.motto = motto;
		notifyChange();
	}
	
	public void setCredits(int credits) {
		this.credits = credits;
		client.sendMessage(new UpdateCreditsBalanceComposer(credits));
	}
	
	public int getCredits() {
		return credits;
	}

	public Inventory getInventory() {
		return inventory;
	}
	
	public Messenger getMessenger() {
		return messenger;
	}
	
	public int getHomeRoomId() {
		return homeRoomId;
	}

	private void notifyChange() {
		if (this.getCurrentRoomUser() != null) {
			this.getCurrentRoomUser().getRoom().getRoomUserManager().serializeUser(this.getCurrentRoomUser());
		}
		client.sendMessage(new LoginOkComposer(getId(), getUsername(), getLook(), getMotto()));
	}

	public User(ResultSet rs, GameClient client, UserDAO userDao) {
		this.userDAO = userDao;
		try {
			this.id = rs.getInt("id");
			this.username = rs.getString("username");
			this.motto = rs.getString("motto");
			this.look = rs.getString("look");
			this.rank = 1;
			this.credits = rs.getInt("credits");
			this.password = rs.getString("password");
			this.homeRoomId = 1;
			this.loadingRoomId = 0;
			this.client = client;
			this.disconnected = false;
			this.inventory = new Inventory(this);
			this.messenger = new Messenger(this);	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	public RoomUser getCurrentRoomUser() {
		if (currentRoom != null) {
			return currentRoom.getRoomUserManager().getUser(id);
		}
		return null;
	}

	public void onDisconnect() {
		if (disconnected)
			return;
		disconnected = true;
		Logging.getInstance().writeLine(username + " has logged out", LogLevel.Verbose, this.getClass());
		this.getUserDAO().saveUser();
		if (currentRoom != null) {
			currentRoom.getRoomUserManager().removeUserFromRoom(this);
		}
		if (messenger != null) {
			messenger.notifyDisconnection();
		}
	}
	
	public boolean isConnected() {
		return !disconnected;
	}

	public int getLoadingRoomId() {
		return loadingRoomId;
	}

	public void setLoadingRoomId(int loadingRoomId) {
		this.loadingRoomId = loadingRoomId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

}

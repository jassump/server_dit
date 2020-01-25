package io.bobba.poc.core.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.bobba.poc.communication.outgoing.users.LoginOkComposer;
import io.bobba.poc.communication.outgoing.users.UpdateCreditsBalanceComposer;
import io.bobba.poc.core.gameclients.GameClient;
import io.bobba.poc.misc.logging.LogLevel;
import io.bobba.poc.misc.logging.Logging;

public class UserManager {
	private Map<Integer, User> users;
	
	public UserManager() {
		this.users = new HashMap<>();
	}
	
	public User getUser(int id) {
		return users.getOrDefault(id, null);
	}
	
	@SuppressWarnings("unused")
	private User addUser(String sso, GameClient client) {
		UserDAO userDAO = new UserDAO(client);
		if(userDAO == null)
			throw new NullPointerException();
		User user = userDAO.loadUserDBFromSSO(sso);
		this.users.put(user.getId(), user);
		return user;
	}
	
	private void addDummyFriends(User user) {
		for (User otherUser: new ArrayList<>(users.values())) {
			if (user != otherUser) {
				user.getMessenger().addHardFriendship(otherUser);	
			}
		}
	}
	
	public void tryLogin(GameClient client, String sso) {
        if (client.getUser() == null) {
        	User user = addUser(sso, client);
            client.setUser(user);            
            Logging.getInstance().writeLine(client.getUser().getUsername() + " (" + client.getUser().getId() + ") has logged in!", LogLevel.Verbose, this.getClass());           

            client.sendMessage(new LoginOkComposer(user.getId(), user.getUsername(), user.getLook(), user.getMotto()));
            client.sendMessage(new UpdateCreditsBalanceComposer(user.getCredits()));
            
            addDummyFriends(user);
        } else {
            Logging.getInstance().writeLine("Client already logged!", LogLevel.Warning, this.getClass());
            client.stop();
        }
    }
}

package io.bobba.poc.core.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.bobba.poc.BobbaEnvironment;
import io.bobba.poc.core.gameclients.GameClient;

public class UserDAO {
	private GameClient client;
	private User user;
	
	public UserDAO(GameClient client) {
		this.client = client;
	}
	
	public User loadUserDBFromSSO(String sso){
		try (Connection conexao = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection()){
			PreparedStatement prepStan = conexao.prepareStatement("SELECT * FROM users WHERE sso=?");
			prepStan.setString(1,"1234");
			ResultSet rs = prepStan.executeQuery();
			while(rs.next()) {
				User user = new User(rs,this.client,this);
				this.user = user;
				return user;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void saveUser() {
		try (Connection conexao = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection()){
			PreparedStatement prepStan = conexao.prepareStatement("UPDATE `users` SET "
					+ "`username` = ?, "
					+ "`password` = ?, "
					+ "`motto` = ?, "
					+ "`number_phone` = ?, "
					+ "`look` = ?, "
					+ "`credits` = ? "
					+ "WHERE `users`.`id` = ?;");
		prepStan.setString(1, this.user.getUsername());
		prepStan.setString(2, this.user.getPassword());
		prepStan.setString(3,this.user.getMotto());
		prepStan.setInt(4, 1235);
		prepStan.setString(5,	this.user.getLook());
		prepStan.setInt(6, this.user.getCredits());
		prepStan.setInt(7, this.user.getId());
		prepStan.execute();
		System.out.println("User -> Usuário "+this.user.getUsername()+" salvo com sucesso! ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}

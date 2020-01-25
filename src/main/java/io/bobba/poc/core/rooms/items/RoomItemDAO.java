package io.bobba.poc.core.rooms.items;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import io.bobba.poc.BobbaEnvironment;
import io.bobba.poc.core.items.BaseItem;
import io.bobba.poc.core.rooms.Room;

public class RoomItemDAO {
	
	public static HashMap<Integer,RoomItem> getLoadDBItem(Room room){
		HashMap<Integer,RoomItem> items = new HashMap<Integer,RoomItem>();
		try(Connection conexao = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection()){
			Statement st = conexao.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM furnidata");
			while(rs.next()) {
				BaseItem baseItem  = BobbaEnvironment.getGame().getItemManager().getItem(rs.getInt("id_base"));
				items.put(rs.getInt("id"), new RoomItem(rs.getInt("id"),rs.getInt("posicao_x"), rs.getInt("posicao_y"),rs.getInt("posicao_z"), rs.getInt("id_dono"),rs.getInt("status"),room,baseItem));
			}
			
			return items;
		}catch(SQLException e) {
			e.printStackTrace();
		}catch(NullPointerException e) {
			System.out.println("[ERRO]RoomItemDAO --> Error Import SQLBase");
		}
		return null;
	}
	
	public static void saveRoomItem(RoomItem roomItem) {
		try(Connection conexao = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection()){
			PreparedStatement pst = conexao.prepareStatement("INSERT INTO furnidata"
					+ "(id_base,id_room,id_dono,posicao_x,posicao_y)"
					+" VALUES ("
					+ "'"+roomItem.getBaseItem().getId()+"','"
					+ roomItem.getRoom().getRoomData().getId()+"',"
					+ "1','"
					+ roomItem.getX()+"','"
					+ roomItem.getY()+"')");
			pst.execute();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateItem(RoomItem roomItem) {
		try(Connection conexao = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection()){
			PreparedStatement pst = conexao.prepareStatement("UPDATE furnidata set "
					+ "id_room = '"+roomItem.getRoom().getRoomData().getId()+"',"
					+ "id_base = '"+roomItem.getBaseItem().getId()+"',"
					+ "posicao_x = '"+roomItem.getX()+"',"
					+ "posicao_y = '"+roomItem.getY()+"'"
					+ " WHERE id ='"+roomItem.getId()+"'"
					);
			pst.execute();
		}catch(SQLException e) {
			System.out.println("[ERRO]bobba.poc.core.rooms.item --> Error SQL Saved RoomItemDAO ");
		}
	} 
	
}

package io.bobba.poc.core.rooms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import io.bobba.poc.BobbaEnvironment;
import io.bobba.poc.core.rooms.gamemap.RoomModel;
import io.bobba.poc.core.rooms.roomdata.LockType;
import io.bobba.poc.core.rooms.roomdata.RoomData;
import io.bobba.poc.database.Query;

public class RoomDAO {
	
	public void updateRoom(Room room) {
		try(Connection conexao = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection()){
			Query query = new Query();
			Statement st = conexao.createStatement();
			st.execute(query
					.UPDATE("rooms")
					.SET("name = "+room.getRoomData().getName())
					.WHERE("id = "+room.getRoomData().getId())
					.getQuery()
				);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveRoom(Room room) {
		try(Connection conexao = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection()){
			Statement st = conexao.createStatement();
			st.execute("INSERT INTO rooms "
					+ "(id, user_id, name, status, description, model_id) "
					+ "VALUES "
					+ "(null,"
					+ "'1',"
					+ "'"+room.getRoomData().getName()+"',"
							+ "'0','"+room.getRoomData().getDescription()+"',"
									+ "'"+room.getRoomData().getModelId()+"')"
					
					);
			System.out.println("ROOM --> "+room.getRoomData().getName()+" saved");
		}catch(SQLException e) {
			System.out.println("[Erro]bobba.poc.core.rooms --> Erro SQL salvar Room");
		}
	}
	
	public static Map<String, RoomModel> loadModelsFromDb(Map<String, RoomModel> models) throws SQLException {	
        try (Connection connection = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement()) {
            if (statement.execute("SELECT id, door_x, door_y, door_z, door_dir, heightmap FROM room_models")) {
                try (ResultSet set = statement.getResultSet()) {
                    while (set.next()) {                    	
                    	String name = set.getString("id");
        				int doorX = set.getInt("door_x");
        				int doorY = set.getInt("door_y");
        				int doorZ = set.getInt("door_z");
        				int doorDir = set.getInt("door_dir");
        				String heightmap = set.getString("heightmap");

        				models.put(name, new RoomModel(doorX, doorY, doorZ, doorDir, heightmap));
        				return models;
                    }
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        
        return null;
	}
	
	@SuppressWarnings("null")
	public static Map<Integer, Room> loadDBRooms() {
		Query query = new Query();
		Map<Integer,Room> list = new HashMap<Integer,Room>();
		ResultSet rs;
		String sql = query
				.SELECT("*")
				.FROM("rooms")
				.getQuery();
		
		try(Connection conexao = BobbaEnvironment.getGame().getDatabase().getDataSource().getConnection()){
			rs = conexao.createStatement().executeQuery(sql);
			if(rs == null)
				return null;
			while(rs.next()) {
				RoomData roomData = new RoomData(rs.getInt("id"), rs.getString("name"), "Wares", rs.getString("description"), 25, "", rs.getString("model_id"), LockType.Open);
				
				RoomModel roomModel= BobbaEnvironment.getGame().getRoomManager().getModel(rs.getString("model_id"));
				Room room = new Room(roomData, roomModel);
				list.put(rs.getInt("id"), room);
			}
			return list;
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}
}

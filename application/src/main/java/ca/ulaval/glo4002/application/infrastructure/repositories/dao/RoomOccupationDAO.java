package ca.ulaval.glo4002.application.infrastructure.repositories.dao;

import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.RoomOccupantDTO;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.RoomOccupationDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomOccupationDAO{
  private final Connection connection;

  public RoomOccupationDAO(Connection connection) {
    this.connection = connection;
  }

  public RoomOccupationDTO findByRoomId(RoomId roomId) throws SQLException{
    String sql = "SELECT room_id, current_occupation, supports_counting "
        + "FROM RoomOccupation WHERE room_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,roomId.toString());
      ResultSet rs = stmt.executeQuery();

      if (rs.next()){
        return new RoomOccupationDTO(rs.getString("room_id"),rs.getInt("current_occupation"),
            rs.getBoolean("supports_counting"));
      }
      return null;
    }
  }

  public void insertOrReplace(RoomOccupationDTO occupation) throws SQLException{
    String sql = "INSERT OR REPLACE INTO RoomOccupation "
        + "(room_id, current_occupation, supports_counting) VALUES (?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,occupation.roomId());
      stmt.setInt(2,occupation.currentOccupation());
      stmt.setBoolean(3,occupation.supportsCounting());
      stmt.executeUpdate();
    }
  }

  public List<RoomOccupantDTO> findOccupantsByRoomId(RoomId roomId) throws SQLException{
    List<RoomOccupantDTO> occupants = new ArrayList<>();
    String sql = "SELECT room_id, idul, consecutive_accesses "
        + "FROM RoomOccupants WHERE room_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,roomId.toString());
      ResultSet rs = stmt.executeQuery();

      while (rs.next()){
        occupants.add(new RoomOccupantDTO(rs.getString("room_id"),rs.getString("idul"),
            rs.getInt("consecutive_accesses")));
      }
    }
    return occupants;
  }

  public void clearOccupantsByRoomId(RoomId roomId) throws SQLException{
    String sql = "DELETE FROM RoomOccupants WHERE room_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,roomId.toString());
      stmt.executeUpdate();
    }
  }

  public void insertOccupantsBatch(List<RoomOccupantDTO> occupants) throws SQLException{
    if (occupants == null || occupants.isEmpty()){
      return;
    }

    String sql = "INSERT INTO RoomOccupants (room_id, idul, consecutive_accesses) "
        + "VALUES (?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      for (RoomOccupantDTO occupant : occupants){
        stmt.setString(1,occupant.roomId());
        stmt.setString(2,occupant.idul());
        stmt.setInt(3,occupant.consecutiveAccesses());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  public void deleteAll() throws SQLException{
    try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM RoomOccupation")){
      stmt.executeUpdate();
    }
    try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM RoomOccupants")){
      stmt.executeUpdate();
    }
  }
}

package ca.ulaval.glo4002.application.infrastructure.repositories.dao;

import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.DoorStateDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoorStateDAO{
  private final Connection connection;

  public DoorStateDAO(Connection connection) {
    this.connection = connection;
  }

  public List<DoorStateDTO> findByZoneId(ZoneId zoneId) throws SQLException{
    List<DoorStateDTO> doorStates = new ArrayList<>();
    String sql = "SELECT door_id, is_locked, is_open, initial_locked, initial_open "
        + "FROM DoorStates WHERE zone_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      ResultSet rs = stmt.executeQuery();

      while (rs.next()){
        doorStates.add(new DoorStateDTO(rs.getString("door_id"),rs.getBoolean("is_locked"),
            rs.getBoolean("is_open"),rs.getBoolean("initial_locked"),
            rs.getBoolean("initial_open")));
      }
    }
    return doorStates;
  }

  public void insertOrReplace(String doorId,ZoneId zoneId,DoorStateDTO doorState)
      throws SQLException{
    String sql = "INSERT OR REPLACE INTO DoorStates "
        + "(door_id, zone_id, is_locked, is_open, initial_locked, initial_open) "
        + "VALUES (?, ?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,doorId);
      stmt.setString(2,zoneId.toString());
      stmt.setBoolean(3,doorState.isLocked());
      stmt.setBoolean(4,doorState.isOpen());
      stmt.setBoolean(5,doorState.initialLocked());
      stmt.setBoolean(6,doorState.initialOpen());
      stmt.executeUpdate();
    }
  }

  public void deleteAll() throws SQLException{
    String sql = "DELETE FROM DoorStates";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.executeUpdate();
    }
  }
}

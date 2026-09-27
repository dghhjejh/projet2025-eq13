package ca.ulaval.glo4002.application.infrastructure.repositories.dao;

import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.ZoneStateDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ZoneStateDAO{
  private final Connection connection;

  public ZoneStateDAO(Connection connection) {
    this.connection = connection;
  }

  public ZoneStateDTO findByZoneId(ZoneId zoneId) throws SQLException{
    String sql = "SELECT urgency_state, smoke_concentration, are_alarms_active "
        + "FROM ZoneStates WHERE zone_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      ResultSet rs = stmt.executeQuery();

      if (rs.next()){
        return new ZoneStateDTO(rs.getString("urgency_state"),rs.getInt("smoke_concentration"),
            rs.getBoolean("are_alarms_active"));
      }
      return null;
    }
  }

  public void insertOrReplace(ZoneId zoneId,ZoneStateDTO state) throws SQLException{
    String sql = "INSERT OR REPLACE INTO ZoneStates "
        + "(zone_id, urgency_state, smoke_concentration, are_alarms_active) "
        + "VALUES (?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      stmt.setString(2,state.urgencyState());
      stmt.setInt(3,state.smokeConcentration());
      stmt.setBoolean(4,state.areAlarmsActive());
      stmt.executeUpdate();
    }
  }

  public void deleteAll() throws SQLException{
    String sql = "DELETE FROM ZoneStates";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.executeUpdate();
    }
  }
}

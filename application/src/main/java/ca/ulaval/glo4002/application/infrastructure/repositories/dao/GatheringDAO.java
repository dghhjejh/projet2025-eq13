package ca.ulaval.glo4002.application.infrastructure.repositories.dao;

import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.GatheringDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GatheringDAO{
  private final Connection connection;

  public GatheringDAO(Connection connection) {
    this.connection = connection;
  }

  public List<GatheringDTO> findByZoneId(ZoneId zoneId) throws SQLException{
    List<GatheringDTO> gatherings = new ArrayList<>();
    String sql = "SELECT gathering_id, expected_attendees, gathering_type, zone_id "
        + "FROM Gatherings WHERE zone_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      ResultSet rs = stmt.executeQuery();

      while (rs.next()){
        gatherings
            .add(new GatheringDTO(rs.getString("gathering_id"),rs.getInt("expected_attendees"),
                rs.getString("gathering_type"),rs.getString("zone_id")));
      }
    }
    return gatherings;
  }

  public void deleteByZoneId(ZoneId zoneId) throws SQLException{
    String sql = "DELETE FROM Gatherings WHERE zone_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      stmt.executeUpdate();
    }
  }

  public void insertBatch(List<GatheringDTO> gatherings) throws SQLException{
    if (gatherings == null || gatherings.isEmpty()){
      return;
    }

    String sql = "INSERT INTO Gatherings "
        + "(gathering_id, expected_attendees, gathering_type, zone_id) " + "VALUES (?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      for (GatheringDTO gathering : gatherings){
        stmt.setString(1,gathering.gatheringId());
        stmt.setInt(2,gathering.expectedAttendees());
        stmt.setString(3,gathering.gatheringType());
        stmt.setString(4,gathering.zoneId());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  public void deleteAll() throws SQLException{
    String sql = "DELETE FROM Gatherings";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.executeUpdate();
    }
  }
}

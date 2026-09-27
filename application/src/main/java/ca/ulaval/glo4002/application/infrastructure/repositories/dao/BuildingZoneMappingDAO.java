package ca.ulaval.glo4002.application.infrastructure.repositories.dao;

import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.BuildingZoneMappingDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BuildingZoneMappingDAO{
  private final Connection connection;

  public BuildingZoneMappingDAO(Connection connection) {
    this.connection = connection;
  }

  public BuildingId findBuildingIdByZoneId(ZoneId zoneId) throws SQLException{
    String sql = "SELECT building_id FROM Zones WHERE zone_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      ResultSet rs = stmt.executeQuery();

      if (rs.next()){
        return new BuildingId(rs.getString("building_id"));
      }
      return null;
    }
  }

  public void insertBuildingIfNotExists(BuildingId buildingId) throws SQLException{
    String sql = "INSERT OR IGNORE INTO Buildings (building_id) VALUES (?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,buildingId.toString());
      stmt.executeUpdate();
    }
  }

  public void insertZoneMappingsBatch(List<BuildingZoneMappingDTO> mappings) throws SQLException{
    if (mappings == null || mappings.isEmpty()){
      return;
    }

    String sql = "INSERT OR IGNORE INTO Zones (zone_id, building_id) VALUES (?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      for (BuildingZoneMappingDTO mapping : mappings){
        stmt.setString(1,mapping.zoneId());
        stmt.setString(2,mapping.buildingId());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  public void deleteAllBuildings() throws SQLException{
    String sql = "DELETE FROM Buildings";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.executeUpdate();
    }
  }

  public void deleteAllZones() throws SQLException{
    String sql = "DELETE FROM Zones";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.executeUpdate();
    }
  }
}

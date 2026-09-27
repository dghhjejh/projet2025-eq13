package ca.ulaval.glo4002.application.infrastructure.repositories.dao;

import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.VentilationSystemDTO;
import java.sql.*;

public class VentilationSystemDAO{
  private final Connection connection;

  public VentilationSystemDAO(Connection connection) {
    this.connection = connection;
  }

  public VentilationSystemDTO findByZoneId(ZoneId zoneId) throws SQLException{
    String sql = "SELECT zone_id, vitesse_retour, vitesse_distribution, is_open "
        + "FROM VentilationSystems WHERE zone_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      ResultSet rs = stmt.executeQuery();

      if (rs.next()){
        Integer returnSpeed = null;
        Integer distributionSpeed = null;
        Integer isOpen = null;

        int returnSpeedValue = rs.getInt("vitesse_retour");
        if (!rs.wasNull()){
          returnSpeed = returnSpeedValue;
        }

        int distributionSpeedValue = rs.getInt("vitesse_distribution");
        if (!rs.wasNull()){
          distributionSpeed = distributionSpeedValue;
        }

        int isOpenValue = rs.getInt("is_open");
        if (!rs.wasNull()){
          isOpen = isOpenValue;
        }

        return new VentilationSystemDTO(rs.getString("zone_id"),returnSpeed,distributionSpeed,
            isOpen);
      }
      return null;
    }
  }

  public void deleteByZoneId(ZoneId zoneId) throws SQLException{
    String sql = "DELETE FROM VentilationSystems WHERE zone_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      stmt.executeUpdate();
    }
  }

  public void insert(VentilationSystemDTO ventilationSystem) throws SQLException{
    String sql = "INSERT INTO VentilationSystems "
        + "(zone_id, vitesse_retour, vitesse_distribution, is_open) " + "VALUES (?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,ventilationSystem.zoneId());

      if (ventilationSystem.returnSpeed() != null){
        stmt.setInt(2,ventilationSystem.returnSpeed());
      } else{
        stmt.setNull(2,Types.INTEGER);
      }

      if (ventilationSystem.distributionSpeed() != null){
        stmt.setInt(3,ventilationSystem.distributionSpeed());
      } else{
        stmt.setNull(3,Types.INTEGER);
      }

      if (ventilationSystem.isOpen() != null){
        stmt.setInt(4,ventilationSystem.isOpen());
      } else{
        stmt.setNull(4,Types.INTEGER);
      }

      stmt.executeUpdate();
    }
  }

  public void deleteAll() throws SQLException{
    String sql = "DELETE FROM VentilationSystems";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.executeUpdate();
    }
  }
}

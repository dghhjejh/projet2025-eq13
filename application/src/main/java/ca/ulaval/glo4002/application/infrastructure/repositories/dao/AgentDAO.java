package ca.ulaval.glo4002.application.infrastructure.repositories.dao;

import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.AgentDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgentDAO{
  private final Connection connection;

  public AgentDAO(Connection connection) {
    this.connection = connection;
  }

  public List<AgentDTO> findDeployedByZoneId(ZoneId zoneId) throws SQLException{
    return findAgentsForZone("AgentsDeployed",zoneId);
  }

  public List<AgentDTO> findDeployedByRoomId(RoomId roomId) throws SQLException{
    return findAgentsForRoom("AgentsDeployed",roomId);
  }

  public void clearDeployedForZone(ZoneId zoneId) throws SQLException{
    clearAgents("AgentsDeployed",zoneId,null);
  }

  public void clearDeployedForRoom(RoomId roomId) throws SQLException{
    clearAgents("AgentsDeployed",null,roomId);
  }

  public void insertDeployedBatch(List<AgentDTO> agents) throws SQLException{
    insertAgentsBatch("AgentsDeployed",agents);
  }

  public List<AgentDTO> findArrivedByZoneId(ZoneId zoneId) throws SQLException{
    return findAgentsForZone("AgentsArrived",zoneId);
  }

  public List<AgentDTO> findArrivedByRoomId(RoomId roomId) throws SQLException{
    return findAgentsForRoom("AgentsArrived",roomId);
  }

  public void clearArrivedForZone(ZoneId zoneId) throws SQLException{
    clearAgents("AgentsArrived",zoneId,null);
  }

  public void clearArrivedForRoom(RoomId roomId) throws SQLException{
    clearAgents("AgentsArrived",null,roomId);
  }

  public void insertArrivedBatch(List<AgentDTO> agents) throws SQLException{
    insertAgentsBatch("AgentsArrived",agents);
  }

  public List<AgentDTO> findRequestedByZoneId(ZoneId zoneId) throws SQLException{
    String sql = "SELECT intervention_id, agent_id, priority, zone_id, room_id "
        + "FROM AgentRequests WHERE zone_id = ? AND room_id IS NULL";
    return executeAgentQuery(sql,zoneId.toString());
  }

  public List<AgentDTO> findRequestedByRoomId(RoomId roomId) throws SQLException{
    String sql = "SELECT intervention_id, agent_id, priority, zone_id, room_id "
        + "FROM AgentRequests WHERE room_id = ?";

    List<AgentDTO> agents = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,roomId.toString());
      ResultSet rs = stmt.executeQuery();

      while (rs.next()){
        agents.add(mapResultSetToAgentDTO(rs));
      }
    }
    return agents;
  }

  public void clearRequestedForZone(ZoneId zoneId) throws SQLException{
    String sql = "DELETE FROM AgentRequests WHERE zone_id = ? AND room_id IS NULL";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      stmt.executeUpdate();
    }
  }

  public void clearRequestedForRoom(RoomId roomId) throws SQLException{
    String sql = "DELETE FROM AgentRequests WHERE room_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,roomId.toString());
      stmt.executeUpdate();
    }
  }

  public void insertRequestedBatch(List<AgentDTO> agents) throws SQLException{
    String sql = "INSERT INTO AgentRequests "
        + "(intervention_id, agent_id, priority, zone_id, room_id) " + "VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      for (AgentDTO agent : agents){
        stmt.setString(1,agent.interventionId());
        setNullableString(stmt,2,agent.agentId());
        stmt.setString(3,agent.priority());
        setNullableString(stmt,4,agent.zoneId());
        setNullableString(stmt,5,agent.roomId());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  public void deleteAll() throws SQLException{
    try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM AgentsDeployed")){
      stmt.executeUpdate();
    }
    try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM AgentsArrived")){
      stmt.executeUpdate();
    }
    try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM AgentRequests")){
      stmt.executeUpdate();
    }
  }

  private List<AgentDTO> findAgentsForZone(String tableName,ZoneId zoneId) throws SQLException{
    String sql = String.format("SELECT intervention_id, agent_id, priority "
        + "FROM %s WHERE zone_id = ? AND room_id IS NULL",tableName);

    List<AgentDTO> agents = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,zoneId.toString());
      ResultSet rs = stmt.executeQuery();

      while (rs.next()){
        agents.add(new AgentDTO(rs.getString("intervention_id"),rs.getString("agent_id"),
            rs.getString("priority"),null,null));
      }
    }
    return agents;
  }

  private List<AgentDTO> findAgentsForRoom(String tableName,RoomId roomId) throws SQLException{
    String sql = String.format("SELECT intervention_id, agent_id, priority "
        + "FROM %s WHERE room_id = ? AND zone_id IS NULL",tableName);

    List<AgentDTO> agents = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,roomId.toString());
      ResultSet rs = stmt.executeQuery();

      while (rs.next()){
        agents.add(new AgentDTO(rs.getString("intervention_id"),rs.getString("agent_id"),
            rs.getString("priority"),null,null));
      }
    }
    return agents;
  }

  private void clearAgents(String tableName,ZoneId zoneId,RoomId roomId) throws SQLException{
    String idColumn = zoneId != null ? "zone_id" : "room_id";
    String idValue = zoneId != null ? zoneId.toString() : roomId.toString();
    String otherColumn = zoneId != null ? "room_id" : "zone_id";

    String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s IS NULL",tableName,idColumn,
        otherColumn);

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,idValue);
      stmt.executeUpdate();
    }
  }

  private void insertAgentsBatch(String tableName,List<AgentDTO> agents) throws SQLException{
    if (agents == null || agents.isEmpty()){
      return;
    }

    String sql = String
        .format("INSERT INTO %s (intervention_id, agent_id, priority, zone_id, room_id) "
            + "VALUES (?, ?, ?, ?, ?)",tableName);

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      for (AgentDTO agent : agents){
        stmt.setString(1,agent.interventionId());
        setNullableString(stmt,2,agent.agentId());
        stmt.setString(3,agent.priority());
        setNullableString(stmt,4,agent.zoneId());
        setNullableString(stmt,5,agent.roomId());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  private List<AgentDTO> executeAgentQuery(String sql,String idValue) throws SQLException{
    List<AgentDTO> agents = new ArrayList<>();

    try (PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setString(1,idValue);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()){
        agents.add(mapResultSetToAgentDTO(rs));
      }
    }
    return agents;
  }

  private AgentDTO mapResultSetToAgentDTO(ResultSet rs) throws SQLException{
    return new AgentDTO(rs.getString("intervention_id"),rs.getString("agent_id"),
        rs.getString("priority"),rs.getString("zone_id"),rs.getString("room_id"));
  }

  private void setNullableString(PreparedStatement stmt,int index,String value) throws SQLException{
    if (value != null){
      stmt.setString(index,value);
    } else{
      stmt.setNull(index,Types.VARCHAR);
    }
  }
}

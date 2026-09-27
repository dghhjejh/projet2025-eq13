package ca.ulaval.glo4002.application.infrastructure.repositories.mappers;

import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentId;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.AgentDTO;

public class AgentMapper{

  public AgentDTO toDeployedDTO(Agent agent,ZoneId zoneId,RoomId roomId){
    return new AgentDTO(agent.interventionId().toString(),
        agent.agentId() != null ? agent.agentId().toString() : null,agent.priority().toString(),
        zoneId != null ? zoneId.toString() : null,roomId != null ? roomId.toString() : null);
  }

  public AgentDTO toArrivedDTO(Agent agent,ZoneId zoneId,RoomId roomId){
    return new AgentDTO(agent.interventionId().toString(),
        agent.agentId() != null ? agent.agentId().toString() : null,agent.priority().toString(),
        zoneId != null ? zoneId.toString() : null,roomId != null ? roomId.toString() : null);
  }

  public AgentDTO toRequestedDTO(RequestAgent requestAgent,ZoneId zoneId,RoomId roomId){
    Agent agent = requestAgent.getCorrespondingAgent();
    return new AgentDTO(requestAgent.getInterventionId().toString(),
        agent.agentId() != null ? agent.agentId().toString() : null,agent.priority().toString(),
        zoneId.toString(),roomId != null ? roomId.toString() : null);
  }

  public Agent toAgent(AgentDTO dto){
    if (dto == null){
      return null;
    }

    return new Agent(dto.agentId() != null ? new AgentId(dto.agentId()) : null,
        new InterventionId(dto.interventionId()),Priority.valueOf(dto.priority()));
  }

  public RequestAgent toRequestAgent(AgentDTO dto){
    if (dto == null){
      return null;
    }

    return new RequestAgent(dto.agentId() != null ? new AgentId(dto.agentId()) : null,
        new ZoneId(dto.zoneId()),Priority.valueOf(dto.priority()),
        new InterventionId(dto.interventionId()));
  }
}

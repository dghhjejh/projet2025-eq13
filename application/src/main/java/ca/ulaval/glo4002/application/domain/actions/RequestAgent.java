package ca.ulaval.glo4002.application.domain.actions;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentId;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Objects;

public record RequestAgent(AgentId agentId, ZoneId zoneId, Priority priority,
    InterventionId interventionId, RoomId roomId) implements Action {
  private static final ActionName ACTION_NAME = ActionName.RequestAgent;

  public RequestAgent(AgentId agentId,ZoneId zoneId,Priority priority,
      InterventionId interventionId) {
    this(agentId, zoneId, priority, interventionId, null);
  }

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  public Agent getCorrespondingAgent(){
    return new Agent(this.agentId,this.interventionId,this.priority);
  }

  public InterventionId getInterventionId(){
    return interventionId;
  }

  @Override
  public boolean equals(Object o){
    if (o == null || getClass() != o.getClass())
      return false;
    RequestAgent that = (RequestAgent) o;
    return Objects.equals(zoneId(),that.zoneId()) && Objects.equals(roomId(),that.roomId())
        && Objects.equals(agentId(),that.agentId()) && priority() == that.priority()
        && Objects.equals(interventionId(),that.interventionId());
  }

  @Override
  public int hashCode(){
    return Objects.hash(agentId(),zoneId(),priority(),interventionId(),roomId());
  }
}

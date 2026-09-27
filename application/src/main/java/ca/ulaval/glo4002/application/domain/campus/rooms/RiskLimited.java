package ca.ulaval.glo4002.application.domain.campus.rooms;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class RiskLimited extends Room{

  public RiskLimited(RoomId roomId,boolean supportsElectricClosure,Idul labSafetyResponsible,
      List<RequestAgent> agentsRequested,List<Agent> agentsDeployedInRoom,
      List<Agent> agentsArrivedInRoom) {
    super(roomId, RoomType.RISK_LIMITED, supportsElectricClosure, labSafetyResponsible,
        agentsRequested, agentsDeployedInRoom, agentsArrivedInRoom);
  }

  @Override
  public List<Action> registerEntryWithoutCard(BuildingId buildingId,ActionBuilder actionBuilder,
      ZoneId zoneId,Runnable activateAlarm){
    incrementUnidentifiedUserOccupation();
    List<Action> actionsToSend = new ArrayList<>();

    Action action = actionBuilder.createRequestAgentActionInRoom(zoneId,Priority.P1,this.id);
    agentContext.addRequested((RequestAgent) action);
    actionsToSend.add(action);

    return actionsToSend;
  }
}

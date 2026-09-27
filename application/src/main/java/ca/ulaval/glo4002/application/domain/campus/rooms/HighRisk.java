package ca.ulaval.glo4002.application.domain.campus.rooms;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.*;

public class HighRisk extends Room{

  public HighRisk(RoomId roomId,boolean supportsElectricClosure,Idul labSafetyResponsible,
      List<RequestAgent> agentsRequested,List<Agent> agentsDeployedInRoom,
      List<Agent> agentsArrivedInRoom) {
    super(roomId, RoomType.HIGH_RISK, supportsElectricClosure, labSafetyResponsible,
        agentsRequested, agentsDeployedInRoom, agentsArrivedInRoom);
  }

  @Override
  public List<Action> registerEntryWithoutCard(BuildingId buildingId,ActionBuilder actionBuilder,
      ZoneId zoneId,Runnable activateAlarm){
    incrementUnidentifiedUserOccupation();
    List<Action> actionsToSend = new ArrayList<>();

    actionsToSend.add(requestAgentInRoom(zoneId,actionBuilder));
    actionsToSend.add(actionBuilder.createActivateFireAlarmAction(buildingId,zoneId,true));
    activateAlarm.run();

    return actionsToSend;
  }

  @Override
  public List<Action> registerAgentArrived(InterventionId interventionId,
      ActionBuilder actionBuilder,BuildingId buildingId,ZoneId zoneId,boolean isZoneOnFire,
      Runnable activateFireAlarm){
    List<Action> actionsToSend = new ArrayList<>();

    boolean agentArrived = agentContext.registerArrival(interventionId);

    if (agentArrived){
      if (!isZoneOnFire){
        activateFireAlarm.run();
        actionsToSend.add(actionBuilder.createActivateFireAlarmAction(buildingId,zoneId,false));
      }
    }

    return actionsToSend;
  }

  private Action requestAgentInRoom(ZoneId zoneId,ActionBuilder actionBuilder){
    Action action = actionBuilder.createRequestAgentActionInRoom(zoneId,Priority.P1,this.id);
    agentContext.addRequested((RequestAgent) action);
    return action;
  }
}

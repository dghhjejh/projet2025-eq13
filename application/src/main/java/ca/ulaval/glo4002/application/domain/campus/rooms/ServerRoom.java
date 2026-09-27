package ca.ulaval.glo4002.application.domain.campus.rooms;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServerRoom extends Room{

  public ServerRoom(RoomId roomId,boolean supportsElectricClosure,Idul labSafetyResponsible,
      List<RequestAgent> agentsRequested,List<Agent> agentsDeployedInRoom,
      List<Agent> agentsArrivedInRoom) {
    super(roomId, RoomType.SERVER_ROOM, supportsElectricClosure, labSafetyResponsible,
        agentsRequested, agentsDeployedInRoom, agentsArrivedInRoom);
  }

  @Override
  public List<Action> triggerFireEmergency(BuildingId buildingId,ZoneId zoneId,
      ActionBuilder actionBuilder,UserRepository userRepository,Set<Idul> notifiedResponsibles){
    List<Action> actions = new ArrayList<>();

    if (this.supportsElectricClosure){
      actions.add(closeElectricityAction(buildingId,zoneId,actionBuilder));
    }
    return actions;
  }

  @Override
  public List<Action> resolveFireExtinguished(BuildingId buildingId,ZoneId zoneId,
      ActionBuilder actionBuilder,UserRepository userRepository,Set<Idul> notifiedResponsibles){
    List<Action> actions = new ArrayList<>();

    if (this.supportsElectricClosure){
      actions.add(openElectricityAction(buildingId,zoneId,actionBuilder));
    }
    return actions;
  }

  private Action buildOpenCloseElectricity(BuildingId buildingId,ZoneId zoneId,boolean close,
      ActionBuilder actionBuilder){
    return actionBuilder.createOpenCloseElectricityAction(buildingId,zoneId,this.id,close);
  }

  private Action closeElectricityAction(BuildingId buildingId,ZoneId zoneId,
      ActionBuilder actionBuilder){
    return buildOpenCloseElectricity(buildingId,zoneId,true,actionBuilder);
  }

  private Action openElectricityAction(BuildingId buildingId,ZoneId zoneId,
      ActionBuilder actionBuilder){
    return buildOpenCloseElectricity(buildingId,zoneId,false,actionBuilder);
  }
}

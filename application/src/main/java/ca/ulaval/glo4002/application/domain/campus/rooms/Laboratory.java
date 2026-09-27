package ca.ulaval.glo4002.application.domain.campus.rooms;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.*;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Laboratory extends Room{

  public Laboratory(RoomId roomId,boolean supportsElectricClosure,Idul labSafetyResponsible,
      List<RequestAgent> agentsRequested,List<Agent> agentsDeployedInRoom,
      List<Agent> agentsArrivedInRoom) {
    super(roomId, RoomType.LABORATORY, supportsElectricClosure, labSafetyResponsible,
        agentsRequested, agentsDeployedInRoom, agentsArrivedInRoom);
  }

  @Override
  public List<Action> triggerFireEmergency(BuildingId buildingId,ZoneId zoneId,
      ActionBuilder actionBuilder,UserRepository userRepository,Set<Idul> notifiedResponsibles){
    return sendPredefinedFireMessageToLaboratoryResponsible(userRepository,true,actionBuilder,
        notifiedResponsibles);
  }

  @Override
  public List<Action> resolveFireExtinguished(BuildingId buildingId,ZoneId zoneId,
      ActionBuilder actionBuilder,UserRepository userRepository,Set<Idul> notifiedResponsibles){
    return sendPredefinedFireMessageToLaboratoryResponsible(userRepository,false,actionBuilder,
        notifiedResponsibles);
  }

  private List<Action> sendPredefinedFireMessageToLaboratoryResponsible(
      UserRepository userRepository,boolean isFireEvent,ActionBuilder actionBuilder,
      Set<Idul> notifiedResponsibles){
    List<Action> actions = new ArrayList<>();

    if (notifiedResponsibles.contains(labSafetyResponsible)){
      return new ArrayList<>();
    }

    PredefinedMessages message = isFireEvent
        ? PredefinedMessages.FIRE_ALERT_IN_ROOM_YRE_RESP
        : PredefinedMessages.END_OF_ALERT;

    User responsible = userRepository.findUserByIDUL(labSafetyResponsible);
    actions.add(sendPredefinedSMS(responsible,message,actionBuilder));

    if (responsible.status() == Status.AVAILABLE || !isFireEvent){
      actions.add(sendPredefinedTeams(message,actionBuilder));
    }

    notifiedResponsibles.add(labSafetyResponsible);
    return actions;
  }

  private Action sendPredefinedSMS(User responsible,PredefinedMessages message,
      ActionBuilder actionBuilder){
    return actionBuilder.createSendPredefinedSMSAction(responsible.contact_no(),responsible.name(),
        message);
  }

  private Action sendPredefinedTeams(PredefinedMessages message,ActionBuilder actionBuilder){
    return actionBuilder.createSendPredefinedTeamsAction(labSafetyResponsible,message);
  }
}

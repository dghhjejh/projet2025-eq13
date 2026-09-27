package ca.ulaval.glo4002.application.domain.builders;

import ca.ulaval.glo4002.application.domain.actions.*;
import ca.ulaval.glo4002.application.domain.actions.enums.FireFighterCallReason;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.AgentId;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.PhoneNumber;
import ca.ulaval.glo4002.application.domain.bottin.PredefinedMessages;
import ca.ulaval.glo4002.application.domain.campus.buildings.PostalAddress;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.function.Supplier;

public class ActionBuilder{
  private final Supplier<InterventionId> interventionIdSupplier;
  private final Supplier<AgentId> agentIdSupplier;

  public ActionBuilder() {
    this(InterventionId::new, AgentId::new);
  }

  public ActionBuilder(Supplier<InterventionId> interventionIdSupplier,
      Supplier<AgentId> agentIdSupplier) {
    this.interventionIdSupplier = interventionIdSupplier;
    this.agentIdSupplier = agentIdSupplier;
  }

  public Action createRequestAgentAction(ZoneId zoneId,Priority priority){
    return new RequestAgent(agentIdSupplier.get(),zoneId,priority,interventionIdSupplier.get());
  }

  public Action createRequestAgentActionInRoom(ZoneId zoneId,Priority priority,RoomId roomId){
    return new RequestAgent(agentIdSupplier.get(),zoneId,priority,interventionIdSupplier.get(),
        roomId);
  }

  public Action createActivateFireAlarmAction(BuildingId buildingId,ZoneId zone,
      boolean shouldActivate){
    return new ActivateFireAlarm(buildingId,zone,shouldActivate);
  }

  public Action createOpenCloseElectricityAction(BuildingId buildingId,ZoneId zone,RoomId roomId,
      boolean isClosed){
    return new OpenCloseElectricity(buildingId,zone,roomId,isClosed);
  }

  public Action createOpenCloseDoorAction(BuildingId buildingId,ZoneId zone,DoorId doorId,
      boolean isOpen){
    return new OpenCloseDoor(buildingId,zone,doorId,isOpen);
  }

  public Action createLockUnlockDoorAction(BuildingId buildingId,ZoneId zone,DoorId doorId,
      boolean isLocked){
    return new LockUnlockDoor(buildingId,zone,doorId,isLocked);
  }

  public Action createCallFireFighterAction(PostalAddress address,FireFighterCallReason reason){
    return new CallFireFighter(address,reason);
  }

  public Action createOpenCloseVentilationAction(BuildingId buildingId,ZoneId zoneId,int isOpen){
    return new OpenCloseVentilation(buildingId,zoneId,isOpen);
  }

  public Action createAdjustVentilationSpeedAction(BuildingId buildingId,ZoneId zoneId,
      double distributionSpeed,double returnSpeed){
    return new AdjustVentilationSpeed(buildingId,zoneId,distributionSpeed,returnSpeed);
  }

  public Action createSendPredefinedSMSAction(PhoneNumber phoneNumber,String responsibleName,
      PredefinedMessages message){
    return new SendPredefinedSMS(phoneNumber,responsibleName,message);
  }

  public Action createSendPredefinedTeamsAction(Idul responsibleId,PredefinedMessages message){
    return new SendPredefinedTeams(responsibleId,message);
  }
}

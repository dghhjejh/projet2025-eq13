package ca.ulaval.glo4002.application.infrastructure.factories;

import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.rooms.*;
import java.util.List;

public class RoomFactory{

  public static Room createRoom(RoomId id,RoomType type,boolean supportsElectricClosure,
      Idul labSafetyResponsible,List<RequestAgent> agentsRequests,
      List<Agent> agentsDispatchedInRoom,List<Agent> agentsArrivedInRoom){
    return switch (type){
      case SERVER_ROOM -> new ServerRoom(id,supportsElectricClosure,labSafetyResponsible,
          agentsRequests,agentsDispatchedInRoom,agentsArrivedInRoom);
      case LABORATORY -> new Laboratory(id,supportsElectricClosure,labSafetyResponsible,
          agentsRequests,agentsDispatchedInRoom,agentsArrivedInRoom);
      case OTHER -> new OtherRoom(id,supportsElectricClosure,labSafetyResponsible,agentsRequests,
          agentsDispatchedInRoom,agentsArrivedInRoom);
      case OFFICE -> new Office(id,supportsElectricClosure,labSafetyResponsible,agentsRequests,
          agentsDispatchedInRoom,agentsArrivedInRoom);
      case RISK_LIMITED -> new RiskLimited(id,supportsElectricClosure,labSafetyResponsible,
          agentsRequests,agentsDispatchedInRoom,agentsArrivedInRoom);
      case HIGH_RISK -> new HighRisk(id,supportsElectricClosure,labSafetyResponsible,agentsRequests,
          agentsDispatchedInRoom,agentsArrivedInRoom);
      case GENERAL_LIMITED -> new GeneralLimited(id,supportsElectricClosure,labSafetyResponsible,
          agentsRequests,agentsDispatchedInRoom,agentsArrivedInRoom);
      case GENERAL_PUBLIC -> new GeneralPublic(id,supportsElectricClosure,labSafetyResponsible,
          agentsRequests,agentsDispatchedInRoom,agentsArrivedInRoom);
    };
  }
}

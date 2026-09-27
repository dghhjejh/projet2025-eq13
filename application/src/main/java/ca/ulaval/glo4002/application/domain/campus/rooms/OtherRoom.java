package ca.ulaval.glo4002.application.domain.campus.rooms;

import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import java.util.List;

public class OtherRoom extends Room{

  public OtherRoom(RoomId roomId,boolean supportsElectricClosure,Idul labSafetyResponsible,
      List<RequestAgent> agentsRequested,List<Agent> agentsDeployedInRoom,
      List<Agent> agentsArrivedInRoom) {
    super(roomId, RoomType.OTHER, supportsElectricClosure, labSafetyResponsible, agentsRequested,
        agentsDeployedInRoom, agentsArrivedInRoom);
  }
}

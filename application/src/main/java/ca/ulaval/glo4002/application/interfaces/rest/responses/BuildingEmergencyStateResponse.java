package ca.ulaval.glo4002.application.interfaces.rest.responses;

import java.util.List;

public record BuildingEmergencyStateResponse(String id, AgentResponse agents,
    List<ZoneResponse> zones, List<DoorResponse> portes) {
}

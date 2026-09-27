package ca.ulaval.glo4002.application.infrastructure.repositories.dto;

public record RoomOccupationDTO(String roomId, int currentOccupation, boolean supportsCounting) {
}

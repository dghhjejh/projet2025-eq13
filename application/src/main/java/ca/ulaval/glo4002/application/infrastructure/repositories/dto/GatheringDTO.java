package ca.ulaval.glo4002.application.infrastructure.repositories.dto;

public record GatheringDTO(String gatheringId, int expectedAttendees, String gatheringType,
    String zoneId) {
}

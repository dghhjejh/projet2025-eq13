package ca.ulaval.glo4002.application.infrastructure.repositories.dto;

public record AgentDTO(String interventionId, String agentId, String priority, String zoneId,
    String roomId) {
}

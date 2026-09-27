package ca.ulaval.glo4002.application.infrastructure.repositories.dto;

public record ZoneStateDTO(String urgencyState, int smokeConcentration, boolean areAlarmsActive) {
}

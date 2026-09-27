package ca.ulaval.glo4002.application.infrastructure.repositories.dto;

public record VentilationSystemDTO(String zoneId, Integer returnSpeed, Integer distributionSpeed,
    Integer isOpen) {
}

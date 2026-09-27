package ca.ulaval.glo4002.application.infrastructure.repositories.dto;

public record DoorStateDTO(String doorId, boolean isLocked, boolean isOpen, boolean initialLocked,
    boolean initialOpen) {
}

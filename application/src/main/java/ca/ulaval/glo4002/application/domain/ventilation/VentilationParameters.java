package ca.ulaval.glo4002.application.domain.ventilation;

public record VentilationParameters(int medianSpeed, int pressureVariation,
    SimpleVentilationState isOpen) {
}

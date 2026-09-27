package ca.ulaval.glo4002.application.interfaces.rest.dto;

import java.util.List;

public record ActionDTO(String nom, List<ParameterDTO> parametres) {
}

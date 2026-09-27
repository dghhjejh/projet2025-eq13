package ca.ulaval.glo4002.application.interfaces.rest.mappers;

import ca.ulaval.glo4002.application.domain.actions.*;
import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.interfaces.rest.dto.ActionDTO;
import ca.ulaval.glo4002.application.interfaces.rest.dto.ActionsResponseDTO;
import ca.ulaval.glo4002.application.interfaces.rest.dto.ParameterDTO;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionMapper{
  public ActionsResponseDTO toResponseDto(List<Action> actions){
    return new ActionsResponseDTO(toDtoList(actions));
  }

  private List<ActionDTO> toDtoList(List<Action> actions){
    return actions.stream().map(this::toDto).collect(Collectors.toList());
  }

  private ActionDTO toDto(Action action){
    String name = convertActionNameValueToString(action.getName());
    Map<String, String> parameters = toParameters(action);
    List<ParameterDTO> parametersDto = parameters.entrySet().stream()
        .map(entry -> new ParameterDTO(entry.getKey(),entry.getValue()))
        .collect(Collectors.toList());
    return new ActionDTO(name,parametersDto);
  }

  private Map<String, String> toParameters(Action action){
    return switch (action){
      case OpenCloseDoor openCloseDoor -> OpenCloseDoorParameters.toParameters(openCloseDoor);
      case ActivateFireAlarm activateFireAlarm ->
        ActivateFireAlarmParameters.toParameters(activateFireAlarm);
      case AdjustVentilationSpeed adjustVentilationSpeed ->
        AdjustVentilationSpeedParameters.toParameters(adjustVentilationSpeed);
      case CallFireFighter callFireFighter ->
        CallFireFighterParameters.toParameters(callFireFighter);
      case LockUnlockDoor lockUnlockDoor -> LockUnlockDoorParameters.toParameters(lockUnlockDoor);
      case OpenCloseElectricity openCloseElectricity ->
        OpenCloseElectricityParameters.toParameters(openCloseElectricity);
      case OpenCloseVentilation openCloseVentilation ->
        OpenCloseVentilationParameters.toParameters(openCloseVentilation);
      case RequestAgent requestAgent -> RequestAgentParameters.toParameters(requestAgent);
      case SendPredefinedSMS sendPredefinedSMS ->
        SendPredefinedSMSParameters.toParameters(sendPredefinedSMS);
      case SendPredefinedTeams sendPredefinedTeams ->
        SendPredefinedTeamsParameters.toParameters(sendPredefinedTeams);
      default -> throw new IllegalStateException("Unexpected value: " + action);
    };
  }

  private String convertActionNameValueToString(ActionName actionName){
    return switch (actionName){
      case ActionName.RequestAgent -> "DemanderAgent";
      case ActionName.AdjustVentilationSpeed -> "ReglerVitesseVentilation";
      case ActionName.OpenCloseVentilation -> "OuvrirFermerVentilation";
      case ActionName.ActivateFireAlarm -> "ActiverAlarmeIncendie";
      case ActionName.CallFireFighter -> "AppelerPompiers";
      case ActionName.SendPredefinedSMS -> "EnvoyerSMSMessagePredefini";
      case ActionName.OpenCloseElectricity -> "OuvrirFermerElectricite";
      case ActionName.SendPredefinedTeams -> "EnvoyerTeamsMessagePredefini";
      case ActionName.OpenCloseDoor -> "OuvrirFermerPorte";
      case ActionName.LockUnlockDoor -> "VerrouillerDeverrouillerPorte";
    };
  }
}

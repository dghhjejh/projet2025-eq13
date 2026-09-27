package ca.ulaval.glo4002.application.domain.access.accessRules;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;

public class DoorAccessDuringFireRule implements AccessRule{
  @Override
  public AccessStatus evaluate(AccessContext context){
    if (context.buildingHasAnyZoneOnFire() && context.isAccessRequestedForBuildingAccess()
        && !context.isSecurityAgent() && !context.isUserIsALabResponsibleInThisBuilding()){
      return AccessStatus.DENIED;
    }
    return AccessStatus.ALLOWED;
  }
}

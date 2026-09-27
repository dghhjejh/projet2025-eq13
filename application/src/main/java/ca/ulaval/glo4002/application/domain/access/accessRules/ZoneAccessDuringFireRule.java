package ca.ulaval.glo4002.application.domain.access.accessRules;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;

public class ZoneAccessDuringFireRule implements AccessRule{
  @Override
  public AccessStatus evaluate(AccessContext context){
    if (!context.doesSpecifiedZoneHaveFireOrPrelarmState() || context.isSecurityAgent()){
      return AccessStatus.ALLOWED;
    }

    if ((context.isAccessRequestedForRoom() && context.isLabResponsibleForRoom())
        || (!context.isAccessRequestedForRoom()
            && context.isUserIsALabResponsibleInThisBuilding())){
      return AccessStatus.ALLOWED;
    }

    return AccessStatus.DENIED;
  }
}

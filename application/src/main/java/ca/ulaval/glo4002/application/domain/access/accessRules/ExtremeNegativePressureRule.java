package ca.ulaval.glo4002.application.domain.access.accessRules;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;

public class ExtremeNegativePressureRule implements AccessRule{
  @Override
  public AccessStatus evaluate(AccessContext context){
    if (context.isAccessDeniedByExtremeNegativePressure()){
      return AccessStatus.DENIED;
    }
    return AccessStatus.ALLOWED;
  }
}

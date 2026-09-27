package ca.ulaval.glo4002.application.domain.access.accessRules;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;

public interface AccessRule{
  AccessStatus evaluate(AccessContext context);
}

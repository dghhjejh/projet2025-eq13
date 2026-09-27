package ca.ulaval.glo4002.application.domain.access;

import ca.ulaval.glo4002.application.domain.access.accessRules.AccessRule;
import java.util.List;

public class AccessEvaluator{
  private final List<AccessRule> rules;

  public AccessEvaluator(List<AccessRule> rules) {
    this.rules = rules;
  }

  public AccessStatus evaluate(AccessContext context){
    for (AccessRule rule : rules){
      AccessStatus status = rule.evaluate(context);
      if (status == AccessStatus.DENIED){
        return AccessStatus.DENIED;
      }
    }
    return AccessStatus.ALLOWED;
  }
}

package ca.ulaval.glo4002.application.domain.access;

import ca.ulaval.glo4002.application.domain.bottin.Idul;

public record AccessCard(Idul idul, SecurityRole securityRole) {

  public boolean isSecurityAgent(){
    return securityRole == SecurityRole.SECURITY_AGENT;
  }

  public boolean userDoesNotExist(){
    return idul.isNull();
  }
}

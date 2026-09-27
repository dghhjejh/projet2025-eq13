package ca.ulaval.glo4002.application.domain.access;

public enum SecurityRole{
  SECURITY_AGENT, NONE;

  public static SecurityRole fromString(String securityRole){
    if (securityRole != null && securityRole.equals("AGENT-SECURITE")){
      return SECURITY_AGENT;
    }
    return NONE;
  }
}

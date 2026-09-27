package ca.ulaval.glo4002.application.domain.access;

public class AccessContext{
  private boolean buildingHasAnyZoneOnFire;
  private boolean specifiedZoneHasFireOrPrealarm;
  private final boolean userIsASecurityAgent;
  private boolean userIsALabResponsibleInThisBuilding;
  private boolean userIsTheLabResponsibleForSpecifiedRoom;
  private boolean accessRequestedForBuildingAccess;
  private boolean accessRequestedForRoom;
  private boolean accessDeniedByExtremeNegativePressure;

  public AccessContext(AccessCard accessCard) {
    this.userIsASecurityAgent = accessCard.isSecurityAgent();
    this.userIsALabResponsibleInThisBuilding = false;
    this.userIsTheLabResponsibleForSpecifiedRoom = false;
    this.buildingHasAnyZoneOnFire = false;
    this.specifiedZoneHasFireOrPrealarm = false;
    this.accessRequestedForRoom = false;
    this.accessDeniedByExtremeNegativePressure = false;
  }

  public boolean buildingHasAnyZoneOnFire(){
    return this.buildingHasAnyZoneOnFire;
  }

  public boolean doesSpecifiedZoneHaveFireOrPrelarmState(){
    return specifiedZoneHasFireOrPrealarm;
  }

  public boolean isSecurityAgent(){
    return userIsASecurityAgent;
  }

  public boolean isAccessDeniedByExtremeNegativePressure(){
    return accessDeniedByExtremeNegativePressure;
  }

  public boolean isUserIsALabResponsibleInThisBuilding(){
    return userIsALabResponsibleInThisBuilding;
  }

  public boolean isLabResponsibleForRoom(){
    return userIsTheLabResponsibleForSpecifiedRoom;
  }

  public boolean isAccessRequestedForBuildingAccess(){
    return accessRequestedForBuildingAccess;
  }

  public boolean isAccessRequestedForRoom(){
    return accessRequestedForRoom;
  }

  public void setAccessDeniedByExtremeNegativePressure(boolean denied){
    this.accessDeniedByExtremeNegativePressure = denied;
  }

  public void setUserIsALabResponsibleInThisBuilding(boolean isALabResponsibleInThisBuilding){
    this.userIsALabResponsibleInThisBuilding = isALabResponsibleInThisBuilding;
  }

  public void setUserIsTheLabResponsibleForSpecifiedRoom(
      boolean isTheLabResponsibleForSpecifiedRoom){
    this.userIsTheLabResponsibleForSpecifiedRoom = isTheLabResponsibleForSpecifiedRoom;
    this.accessRequestedForRoom = true;
  }

  public void setBuildingHasAnyZoneOnFire(boolean buildingHasAnyZoneOnFire){
    this.buildingHasAnyZoneOnFire = buildingHasAnyZoneOnFire;
  }

  public void setSpecifiedZoneHasFireOrPrealarm(boolean specifiedZoneHasFireOrPrealarm){
    this.specifiedZoneHasFireOrPrealarm = specifiedZoneHasFireOrPrealarm;
  }

  public void setAccessRequestedForBuildingAccess(boolean accessRequestedForBuildingAccess){
    this.accessRequestedForBuildingAccess = accessRequestedForBuildingAccess;
  }
}

package ca.ulaval.glo4002.application.domain.campus;

import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import java.util.List;

public class Campus{
  private String id;

  private String name;

  private List<Building> buildings;

  public Campus() {
  }

  public String getId(){
    return this.id;
  }

  public void setId(String id){
    this.id = id;
  }

  public String getName(){
    return this.name;
  }

  public void setName(String name){
    this.name = name;
  }

  public List<Building> getBuildings(){
    return this.buildings;
  }

  public void setBuildings(List<Building> buildings){
    this.buildings = buildings;
  }
}

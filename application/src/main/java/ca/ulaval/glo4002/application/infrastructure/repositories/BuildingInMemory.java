package ca.ulaval.glo4002.application.infrastructure.repositories;

import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.domain.campus.Campus;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.infrastructure.api.CampusLoaderFromAPI;
import ca.ulaval.glo4002.application.infrastructure.exceptions.BuildingNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingInMemory implements BuildingRepository{

  private final CampusLoaderFromAPI campusLoader;
  private final Map<BuildingId, Building> buildings;

  public BuildingInMemory(CampusLoaderFromAPI campusLoader) {
    this.buildings = new HashMap<>();
    this.campusLoader = campusLoader;
  }

  @Override
  public Building findBuildingById(BuildingId buildingId){
    mergeStateWithBuildingsStructure();
    Building building = this.buildings.get(buildingId);
    if (building == null){
      throw new BuildingNotFoundException("There is no building with id: " + buildingId);
    }
    return building;
  }

  @Override
  public Building getParentBuilding(ZoneId zoneId){
    mergeStateWithBuildingsStructure();
    for (Building building : this.buildings.values()){
      for (Zone zone : building.getZones()){
        if (zoneId.equals(zone.getId())){
          return building;
        }
      }
    }
    throw new BuildingNotFoundException("Parent building not found for zone id: " + zoneId);
  }

  @Override
  public void saveBuildingState(Building changedBuilding){
    // Campus state changes when objects change in domain logic for InMemory Campus.
  }

  @Override
  public void clear(){
    this.buildings.clear();
  }

  private void mergeStateWithBuildingsStructure(){
    Campus newCampus = campusLoader.loadCampus();
    for (Building newBuilding : newCampus.getBuildings()){
      Building existingBuilding = this.buildings.get(newBuilding.getId());
      if (existingBuilding == null){
        this.buildings.put(newBuilding.getId(),newBuilding);
      } else{
        mergeZonesIntoBuilding(existingBuilding,newBuilding.getZones());
      }
    }
  }

  private void mergeZonesIntoBuilding(Building existingBuilding,List<Zone> newZones){
    for (Zone newZone : newZones){
      Zone existingZone = findZoneInBuilding(existingBuilding,newZone.getId());

      if (existingZone == null){
        existingBuilding.addZone(newZone);
      } else{
        mergeRoomsIntoZone(existingZone,newZone.getRooms());
        mergeDoorsIntoZone(existingZone,newZone.getDoors());
      }
    }
  }

  private Zone findZoneInBuilding(Building building,ZoneId zoneId){
    return building.getZones().stream().filter(zone -> zoneId.equals(zone.getId())).findFirst()
        .orElse(null);
  }

  private void mergeRoomsIntoZone(Zone existingZone,List<Room> newRooms){
    for (Room newRoom : newRooms){
      Room existingRoom = findRoomInZone(existingZone,newRoom.getId());
      if (existingRoom == null){
        existingZone.addRoom(newRoom);
      }
    }
  }

  private void mergeDoorsIntoZone(Zone existingZone,List<Door> newDoors){
    for (Door newDoor : newDoors){
      Door existingDoor = findDoorInZone(existingZone,newDoor.getId());
      if (existingDoor == null){
        existingZone.addDoor(newDoor);
      }
    }
  }

  private Room findRoomInZone(Zone zone,RoomId roomId){
    return zone.getRooms().stream().filter(room -> roomId.equals(room.getId())).findFirst()
        .orElse(null);
  }

  private Door findDoorInZone(Zone zone,DoorId doorId){
    return zone.getDoors().stream().filter(door -> doorId.equals(door.getId())).findFirst()
        .orElse(null);
  }

  public Map<BuildingId, Building> getBuildings(){
    return this.buildings;
  }
}

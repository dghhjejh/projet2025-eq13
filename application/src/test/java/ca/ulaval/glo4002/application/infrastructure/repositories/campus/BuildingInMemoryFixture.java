package ca.ulaval.glo4002.application.infrastructure.repositories.campus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import ca.ulaval.glo4002.application.infrastructure.repositories.BuildingInMemory;
import java.util.ArrayList;
import java.util.List;

public class BuildingInMemoryFixture{
  public static final String CAMPUS_ID = "campus-001";
  public static final BuildingId BUILDING_ID = new BuildingId("PLT");
  public static final BuildingId OTHER_BUILDING_ID = new BuildingId("VCH");
  public static final ZoneId ZONE_ID = new ZoneId("PLT200");
  public static final ZoneId OTHER_ZONE_ID = new ZoneId("PLT300");
  public static final DoorId DOOR_ID = new DoorId("PLT200-COUPE-FEU-01");
  public static final DoorId OTHER_DOOR_ID = new DoorId("PLT200-COUPE-FEU-02");
  public static final RoomId ROOM_ID = new RoomId("PLT200-R001");
  public static final RoomId OTHER_ROOM_ID = new RoomId("PLT200-R002");

  public static final BuildingId NON_EXISTING_BUILDING_ID = new BuildingId("non-existing-building");
  public static final ZoneId NON_EXISTING_ZONE_ID = new ZoneId("non-existing-zone");

  private final Campus campus;
  private final Building building;
  private final Building otherBuilding;
  private final Zone zone;
  private final Zone otherZone;
  private final Door door;
  private final Door otherDoor;
  private final Room room;
  private final Room otherRoom;

  private List<Zone> buildingZones;
  private List<Room> zoneRooms;
  private List<Door> zoneDoors;

  private final CampusLoaderFromAPI campusLoader;

  public BuildingInMemoryFixture() {
    this.campusLoader = mock(CampusLoaderFromAPI.class);
    this.campus = mock(Campus.class);
    this.building = mock(Building.class);
    this.otherBuilding = mock(Building.class);
    this.zone = mock(Zone.class);
    this.otherZone = mock(Zone.class);
    this.door = mock(Door.class);
    this.otherDoor = mock(Door.class);
    this.room = mock(Room.class);
    this.otherRoom = mock(Room.class);

    setupDefaultMockBehavior();
  }

  private void setupDefaultMockBehavior() {
    when(campus.getId()).thenReturn(CAMPUS_ID);
    when(building.getId()).thenReturn(BUILDING_ID);
    when(zone.getId()).thenReturn(ZONE_ID);
    when(door.getId()).thenReturn(DOOR_ID);
    when(room.getId()).thenReturn(ROOM_ID);

    when(otherBuilding.getId()).thenReturn(OTHER_BUILDING_ID);
    when(otherZone.getId()).thenReturn(OTHER_ZONE_ID);
    when(otherDoor.getId()).thenReturn(OTHER_DOOR_ID);
    when(otherRoom.getId()).thenReturn(OTHER_ROOM_ID);

    buildingZones = new ArrayList<>(List.of(zone));
    zoneRooms = new ArrayList<>(List.of(room));
    zoneDoors = new ArrayList<>(List.of(door));

    when(building.getZones()).thenReturn(buildingZones);
    when(zone.getRooms()).thenReturn(zoneRooms);
    when(zone.getDoors()).thenReturn(zoneDoors);
    when(campus.getBuildings()).thenReturn(List.of(building));
    when(campusLoader.loadCampus()).thenReturn(campus);
  }

  public Campus getCampus(){
    return campus;
  }

  public Zone getZone(){
    return zone;
  }

  public BuildingInMemory createTestRepository(){
    return new BuildingInMemory(campusLoader);
  }
}

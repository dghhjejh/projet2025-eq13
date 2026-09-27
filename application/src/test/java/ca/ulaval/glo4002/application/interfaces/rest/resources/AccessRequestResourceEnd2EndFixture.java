package ca.ulaval.glo4002.application.interfaces.rest.resources;

public class AccessRequestResourceEnd2EndFixture{

  String validAccessRequestJson(){
    return """
                  {
                    "card_id": "384643",
                    "zone_id": "PLT200",
                    "room_id": "PLT200-R001",
                    "door_id": "PLT200-COUPE-FEU-01",
                    "date_heure": "14:30:00"
                  }
        """;
  }

  String missingZoneIdJson(){
    return """
                  {
                    "card_id": "384643",
                    "room_id": "PLT200-R001",
                    "door_id": "PLT200-COUPE-FEU-01",
                    "date_heure": "14:30:00"
                  }
        """;
  }
}

package ca.ulaval.glo4002.application.interfaces.rest.resources;

public class SecurityEventResourceEnd2EndFixture{
  String validFirePrealarmEventJson(){
    return """
        {
          "nom": "Préalarme d'incendie",
          "heure": "14:30:00",
          "zone": "PLT200",
          "parametres": [
          ]
        }
        """;
  }

  String invalidNameEventJson(){
    return """
        {
          "nom": "Prealarme",
          "heure": "14:30:00",
          "zone": "PLT200",
          "parametres": [
          ]
        }
        """;
  }

  String invalidTimeEventJson(){
    return """
        {
          "nom": "Préalarme d'incendie",
          "heure": "25:30:00",
          "zone": "PLT200",
          "parametres": [

          ]
        }
        """;
  }

  String nullEventNameJson(){
    return """
        {
          "nom": null,
          "heure": "14:30:00",
          "zone": "PLT200",
          "parametres": [
          ]
        }
        """;
  }

  String invalidParametersEventJson(){
    return """
        {
          "nom": null,
          "heure": "14:30:00",
          "zone": "PLT200",
          "parametres": [
            {"parametre": "température", "valeur": "45"},
            {"parametre": "humidité", "valeur": "80"}
          ]
        }

        """;
  }

  String smokePresenceNoConcentrationEventJson(){
    return """
        {
          "nom": "Présence de fumée",
          "heure": "14:30:00",
          "zone": "PLT200",
          "parametres": []
        }
        """;
  }

  String smokePresenceNegativeConcentrationEventJson(){
    return """
        {
          "nom": "Présence de fumée",
          "heure": "14:30:00",
          "zone": "A1",
          "parametres": [
            {"parametre": "concentration", "valeur": "0"}
          ]
        }
        """;
  }
}

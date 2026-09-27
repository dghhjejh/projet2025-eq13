package ca.ulaval.glo4002.application.infrastructure.api;

import ca.ulaval.glo4002.application.infrastructure.api.exceptions.CampusAPIException;
import ca.ulaval.glo4002.application.infrastructure.dto.BuildingMapDto;
import ca.ulaval.glo4002.application.infrastructure.dto.CampusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CampusAPIClient{
  public static final String BUILDING_MAP_V_2 = "/building-map/v2";
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final URI baseUrl;

  public CampusAPIClient(URI url) {
    this.baseUrl = url;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  public CampusDto getCampus(){
    HttpResponse<String> response = sendGetCampusRequest();

    if (response.statusCode() != 200){
      throw new CampusAPIException("Failed to fetch building map: HTTP " + response.statusCode());
    }

    BuildingMapDto buildingMapDto;
    try{
      buildingMapDto = this.objectMapper.readValue(response.body(),BuildingMapDto.class);
    } catch (JsonProcessingException e){
      throw new CampusAPIException("Failed to convert API response to campus: " + e.getMessage());
    }

    return buildingMapDto.campus;
  }

  private HttpResponse<String> sendGetCampusRequest(){
    try{
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(this.baseUrl + BUILDING_MAP_V_2))
          .header("Accept",MediaType.APPLICATION_JSON).GET().build();
      return this.httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    } catch (IOException e){
      throw new CampusAPIException("Network error while fetching campus data: " + e.getMessage());
    } catch (InterruptedException e){
      throw new CampusAPIException(
          "Request interrupted while fetching campus data: " + e.getMessage());
    }
  }
}

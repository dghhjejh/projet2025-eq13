package ca.ulaval.glo4002.application.infrastructure.api;

import ca.ulaval.glo4002.application.domain.campus.Campus;
import ca.ulaval.glo4002.application.infrastructure.dto.CampusDto;
import ca.ulaval.glo4002.application.infrastructure.mappers.CampusMapper;

public class CampusLoaderFromAPI{
  private final CampusAPIClient campusAPIClient;
  private final CampusMapper campusMapper;

  public CampusLoaderFromAPI(CampusAPIClient campusAPIClient,CampusMapper campusMapper) {
    this.campusAPIClient = campusAPIClient;
    this.campusMapper = campusMapper;
  }

  public Campus loadCampus(){
    CampusDto campusDto = this.campusAPIClient.getCampus();
    return this.campusMapper.toCampus(campusDto);
  }
}

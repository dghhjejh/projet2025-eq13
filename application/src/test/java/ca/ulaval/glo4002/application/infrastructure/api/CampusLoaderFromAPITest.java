package ca.ulaval.glo4002.application.infrastructure.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.campus.Campus;
import ca.ulaval.glo4002.application.infrastructure.dto.CampusDto;
import ca.ulaval.glo4002.application.infrastructure.mappers.CampusMapper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CampusLoaderFromAPITest{
  private CampusAPIClient campusAPIClient;

  private CampusMapper campusMapper;

  private CampusLoaderFromAPI campusLoader;

  private CampusDto campusDto;

  @BeforeEach
  void setAPIReturnValue(){
    campusDto = new CampusDto();
    campusDto.id = "campus-id";
    campusDto.name = "Test Campus";
    campusDto.buildings = Collections.emptyList();
    campusAPIClient = mock(CampusAPIClient.class);

    when(campusAPIClient.getCampus()).thenReturn(campusDto);
  }

  @BeforeEach
  void createLoader(){
    campusMapper = mock(CampusMapper.class);
    campusLoader = new CampusLoaderFromAPI(campusAPIClient,campusMapper);
  }

  @Test
  void whenLoadCampus_thenCallAPIClient(){
    campusLoader.loadCampus();

    verify(campusAPIClient).getCampus();
  }

  @Test
  void whenLoadCampus_thenMapDtoToDomain(){
    campusLoader.loadCampus();

    verify(campusMapper).toCampus(campusDto);
  }

  @Test
  void whenLoadCampus_thenReturnMappedCampus(){
    Campus expectedCampus = mock(Campus.class);
    when(campusMapper.toCampus(campusDto)).thenReturn(expectedCampus);

    Campus result = campusLoader.loadCampus();

    assertNotNull(result);
    assertEquals(expectedCampus,result);
  }
}

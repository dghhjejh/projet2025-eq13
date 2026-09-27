package ca.ulaval.glo4002.application.domain.access.accessRules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtremeNegativePressureRuleTest{
  private ExtremeNegativePressureRule extremeNegativePressureRule;
  private AccessContext accessContext;

  @BeforeEach
  void setUp(){
    extremeNegativePressureRule = new ExtremeNegativePressureRule();
    accessContext = mock(AccessContext.class);
  }

  @Test
  void givenZoneDeniesAccessWithExtremePressure_whenEvaluate_thenShouldReturnDenied(){
    boolean accessIsDenied = true;
    when(accessContext.isAccessDeniedByExtremeNegativePressure()).thenReturn(accessIsDenied);

    AccessStatus status = extremeNegativePressureRule.evaluate(accessContext);

    assertEquals(AccessStatus.DENIED,status);
  }

  @Test
  void givenZoneAllowsAccessWithExtremePressure_whenEvaluate_thenShouldReturnDenied(){
    boolean accessIsAllowed = false;
    when(accessContext.isAccessDeniedByExtremeNegativePressure()).thenReturn(accessIsAllowed);

    AccessStatus status = extremeNegativePressureRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED,status);
  }
}

package ca.ulaval.glo4002.application.domain.campus.rooms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4002.application.domain.bottin.Idul;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class OccupationCounterTest{

  private static final Idul IDUL_1 = new Idul("alice01");
  private static final Idul IDUL_2 = new Idul("bob02");
  private static final Idul IDUL_3 = new Idul("charlie03");
  private static final int ZERO_OCCUPANT = 0;
  private static final int ONE_OCCUPANT = 1;
  private static final int TWO_OCCUPANTS = 2;
  private static final int THREE_OCCUPANTS = 3;
  private static final int FOUR_OCCUPANTS = 4;
  private static final int ONE_ACCESS = 1;
  private static final int TWO_ACCESSES = 2;
  private static final int THREE_ACCESSES = 3;
  private static final int TOTAL_ACCESSES = 2;
  private static final int INCREMENT_ONCE = 1;
  private static final int INCREMENT_TWICE = 2;
  private static final int INCREMENT_THRICE = 3;

  private OccupationCounter occupationCounter;

  @BeforeEach
  void setUp(){
    occupationCounter = new OccupationCounter();
  }

  @Nested
  class IncrementIdentifiedUserOccupationTest{

    @Test
    void whenIncrementIdentifiedUser_thenUserIsAddedToIdentifiedOccupants(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);

      assertTrue(occupationCounter.getIdentifiedOccupants().contains(IDUL_1));
    }

    @Test
    void whenIncrementIdentifiedUser_thenConsecutiveAccessesIsOne(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);

      assertEquals(ONE_ACCESS,occupationCounter.getConsecutiveAccessesByPerson().get(IDUL_1));
    }

    @Test
    void whenIncrementSameUserTwice_thenOccupationIsTwo(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_TWICE);

      assertEquals(TWO_OCCUPANTS,occupationCounter.getCurrentOccupation());
    }

    @Test
    void whenIncrementSameUserTwice_thenConsecutiveAccessesIsTwo(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_TWICE);

      assertEquals(TWO_ACCESSES,occupationCounter.getConsecutiveAccessesByPerson().get(IDUL_1));
    }

    @Test
    void whenIncrementUsers_thenOccupationIsIncrementedAccordingly(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);
      incrementIdentifiedOccupants(IDUL_3,INCREMENT_ONCE);

      assertEquals(THREE_OCCUPANTS,occupationCounter.getCurrentOccupation());
    }

    @Test
    void whenIncrementMultipleUsers_thenAllUsersAreIdentified(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);
      incrementIdentifiedOccupants(IDUL_3,INCREMENT_ONCE);

      assertEquals(THREE_OCCUPANTS,occupationCounter.getIdentifiedOccupants().size());
      assertThat(occupationCounter.getIdentifiedOccupants()).hasSize(THREE_OCCUPANTS)
          .containsExactlyInAnyOrder(IDUL_1,IDUL_2,IDUL_3);
    }

    @Test
    void whenIncrementMultipleUsersWithRepeats_thenConsecutiveAccessesAreCorrect(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_THRICE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);

      Map<Idul, Integer> accesses = occupationCounter.getConsecutiveAccessesByPerson();
      assertEquals(THREE_ACCESSES,accesses.get(IDUL_1));
      assertEquals(ONE_ACCESS,accesses.get(IDUL_2));
    }
  }

  @Nested
  class IncrementUnidentifiedUserOccupationTest{
    @Test
    void whenIncrementUnidentifiedUser_thenOccupationIncreases(){
      incrementUnidentifiedOccupants(INCREMENT_ONCE);

      assertEquals(ONE_OCCUPANT,occupationCounter.getCurrentOccupation());
    }

    @Test
    void whenIncrementUnidentifiedUserMultipleTimes_thenOccupationIncreasesCorrectly(){
      incrementUnidentifiedOccupants(INCREMENT_THRICE);

      assertEquals(THREE_OCCUPANTS,occupationCounter.getCurrentOccupation());
    }

    @Test
    void whenIncrementUnidentifiedUser_thenNoIdentifiedOccupantsAreAdded(){
      incrementUnidentifiedOccupants(INCREMENT_ONCE);

      assertTrue(occupationCounter.getIdentifiedOccupants().isEmpty());
    }

    @Test
    void whenIncrementUnidentifiedUser_thenNoConsecutiveAccessesAreRecorded(){
      incrementUnidentifiedOccupants(INCREMENT_ONCE);

      assertTrue(occupationCounter.getConsecutiveAccessesByPerson().isEmpty());
    }

    @Test
    void whenMixIdentifiedAndUnidentifiedUsers_thenOccupationIsCorrect(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);
      incrementUnidentifiedOccupants(INCREMENT_TWICE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);

      assertEquals(FOUR_OCCUPANTS,occupationCounter.getCurrentOccupation());
      assertEquals(TWO_OCCUPANTS,occupationCounter.getIdentifiedOccupants().size());
    }
  }

  @Nested
  class DecrementOccupationTest{
    @Test
    void givenOccupationOfOne_whenDecrement_thenOccupationIsZero(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);

      occupationCounter.decrementOccupation();

      assertEquals(ZERO_OCCUPANT,occupationCounter.getCurrentOccupation());
    }

    @Test
    void givenOccupationOfThree_whenDecrement_thenOccupationIsTwo(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);
      incrementUnidentifiedOccupants(INCREMENT_ONCE);

      occupationCounter.decrementOccupation();

      assertEquals(TWO_OCCUPANTS,occupationCounter.getCurrentOccupation());
    }

    @Test
    void givenZeroOccupation_whenDecrement_thenOccupationStaysZero(){
      occupationCounter.decrementOccupation();

      assertEquals(ZERO_OCCUPANT,occupationCounter.getCurrentOccupation());
    }

    @Test
    void givenOccupationOfOne_whenDecrement_thenIdentifiedOccupantsAreCleared(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);

      occupationCounter.decrementOccupation();

      assertTrue(occupationCounter.getIdentifiedOccupants().isEmpty());
    }

    @Test
    void givenOccupationOfOne_whenDecrement_thenConsecutiveAccessesAreCleared(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);

      occupationCounter.decrementOccupation();

      assertTrue(occupationCounter.getConsecutiveAccessesByPerson().isEmpty());
    }

    @Test
    void givenOccupationOfTwo_whenDecrement_thenIdentifiedOccupantsAreNotCleared(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);

      occupationCounter.decrementOccupation();

      assertEquals(TWO_OCCUPANTS,occupationCounter.getIdentifiedOccupants().size());
      assertFalse(occupationCounter.getConsecutiveAccessesByPerson().isEmpty());
    }

    @Test
    void givenOccupationOfTwo_whenDecrementTwice_thenAllDataIsCleared(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);

      occupationCounter.decrementOccupation();
      occupationCounter.decrementOccupation();

      assertEquals(ZERO_OCCUPANT,occupationCounter.getCurrentOccupation());
      assertTrue(occupationCounter.getIdentifiedOccupants().isEmpty());
      assertTrue(occupationCounter.getConsecutiveAccessesByPerson().isEmpty());
    }
  }

  @Nested
  class SupportsOccupationCountingTest{
    @Test
    void givenNewCounter_whenSupportsOccupationCounting_thenReturnsFalse(){
      assertFalse(occupationCounter.supportsOccupationCounting());
    }

    @Test
    void givenCounterAfterIncrement_whenSupportsOccupationCounting_thenReturnsTrue(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);

      assertTrue(occupationCounter.supportsOccupationCounting());
    }

    @Test
    void givenCounterAfterUnidentifiedIncrement_whenSupportsOccupationCounting_thenReturnsTrue(){
      incrementUnidentifiedOccupants(INCREMENT_ONCE);

      assertTrue(occupationCounter.supportsOccupationCounting());
    }

    @Test
    void givenCounterAfterDecrement_whenSupportsOccupationCounting_thenReturnsTrue(){
      occupationCounter.decrementOccupation();

      assertTrue(occupationCounter.supportsOccupationCounting());
    }
  }

  @Nested
  class GetCurrentOccupationTest{
    @Test
    void givenNewCounter_whenGetCurrentOccupation_thenReturnsZero(){
      assertEquals(ZERO_OCCUPANT,occupationCounter.getCurrentOccupation());
    }

    @Test
    void givenOccupationOfThree_whenGetCurrentOccupation_thenReturnsThree(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_ONCE);
      incrementUnidentifiedOccupants(INCREMENT_ONCE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);

      assertEquals(THREE_OCCUPANTS,occupationCounter.getCurrentOccupation());
    }
  }

  @Nested
  class GetConsecutiveAccessesByPersonTest{
    @Test
    void givenNewCounter_whenGetConsecutiveAccesses_thenReturnsEmptyMap(){
      assertTrue(occupationCounter.getConsecutiveAccessesByPerson().isEmpty());
    }

    @Test
    void givenUnidentifiedOccupants_whenGetConsecutiveAccesses_thenReturnsEmptyMap(){
      incrementUnidentifiedOccupants(INCREMENT_TWICE);

      assertTrue(occupationCounter.getConsecutiveAccessesByPerson().isEmpty());
    }

    @Test
    void givenIdentifiedOccupants_whenGetConsecutiveAccesses_thenReturnsCorrectMap(){
      incrementIdentifiedOccupants(IDUL_1,INCREMENT_TWICE);
      incrementIdentifiedOccupants(IDUL_2,INCREMENT_ONCE);

      Map<Idul, Integer> accesses = occupationCounter.getConsecutiveAccessesByPerson();
      assertEquals(TOTAL_ACCESSES,accesses.size());
      assertEquals(TWO_ACCESSES,accesses.get(IDUL_1));
      assertEquals(ONE_ACCESS,accesses.get(IDUL_2));
    }
  }

  private void incrementIdentifiedOccupants(Idul idul,int times){
    for (int i = 0; i < times; i++){
      occupationCounter.incrementIdentifiedUserOccupation(idul);
    }
  }

  private void incrementUnidentifiedOccupants(int times){
    for (int i = 0; i < times; i++){
      occupationCounter.incrementUnidentifiedUserOccupation();
    }
  }
}

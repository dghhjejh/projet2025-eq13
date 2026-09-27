package ca.ulaval.glo4002.application.domain.campus.rooms;

import ca.ulaval.glo4002.application.domain.bottin.Idul;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OccupationCounter{
  private Integer currentOccupation;
  private final Set<Idul> identifiedOccupants;
  private final Map<Idul, Integer> consecutiveAccessesByPerson;
  private static final int FIRST_ENTRY_OCCURRENCE = 1;
  private static final int OCCUPATION_DECREMENT = 1;

  public OccupationCounter() {
    this.currentOccupation = null;
    this.identifiedOccupants = new HashSet<>();
    this.consecutiveAccessesByPerson = new HashMap<>();
  }

  public OccupationCounter(int initialOccupation,Map<Idul, Integer> initialAccesses) {
    this.currentOccupation = initialOccupation;
    this.identifiedOccupants = new HashSet<>(initialAccesses.keySet());
    this.consecutiveAccessesByPerson = new HashMap<>(initialAccesses);
  }

  private void initializeOccupationIfNeeded(){
    if (currentOccupation == null){
      currentOccupation = 0;
    }
  }

  public boolean supportsOccupationCounting(){
    return currentOccupation != null;
  }

  public Map<Idul, Integer> getConsecutiveAccessesByPerson(){
    if (!supportsOccupationCounting()){
      return Map.of();
    }
    return new HashMap<>(consecutiveAccessesByPerson);
  }

  public int getCurrentOccupation(){
    if (!supportsOccupationCounting()){
      return 0;
    }
    return currentOccupation;
  }

  public Set<Idul> getIdentifiedOccupants(){
    return new HashSet<>(identifiedOccupants);
  }

  public void incrementIdentifiedUserOccupation(Idul idul){
    initializeOccupationIfNeeded();
    currentOccupation++;
    identifiedOccupants.add(idul);
    consecutiveAccessesByPerson.merge(idul,FIRST_ENTRY_OCCURRENCE,Integer::sum);
  }

  public void incrementUnidentifiedUserOccupation(){
    initializeOccupationIfNeeded();
    currentOccupation++;
  }

  public void decrementOccupation(){
    initializeOccupationIfNeeded();
    currentOccupation = Math.max(0,currentOccupation - OCCUPATION_DECREMENT);

    if (currentOccupation == 0){
      identifiedOccupants.clear();
      consecutiveAccessesByPerson.clear();
    }
  }
}

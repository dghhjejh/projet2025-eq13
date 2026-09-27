# Architecture et justifications

## Architecture hexagonale

Notre système suit une architecture hexagonale avec un domaine riche.

### Structure des couches
```
Couche application (Orchestration)

Couche domain (Logique métier)

Couche infrastructure (Adapters)
```

## Domaine riche
```java
// Zone encapsule sa propre logique
public class Zone {
    public List<Action> handleFire(BuildingId buildingId) {
        this.urgencyState = UrgencyState.FIRE;
        List<Action> actions = new ArrayList<>();
        actions.addAll(closeElectricityInServerRooms(buildingId));
        
        return actions;
    }
}
```

`Zone` sait ce qu'elle doit faire quand un incendie arrive. Elle encapsule son comportement. Les interactions entre `Building` --> `Zone` --> `Door` --> `Room` émergent sans qu'un service externe ne les manipule (c'est-à-dire sans rendre le domaine anémique).
`Building` est la tête d'aggrégat. La classe s'occupe de gérer les divers évènements de sécurité qui se passent dans `Building`. La classe reçoit les évènements de sécurité et s'occupe de traiter la logique ou de déléguer à des entités internes de l'aggrégat.
La méthode handle() de `Building` permet à la classe de décider comment son état interne doit réagir à chaque événement. La méthode handle() est légitime car la classe `Building`, étant la tête de l'aggrégat, centralise la logique de décision. C'est le seul point d'entrée pour
les changements du domaine d'affaires. `Building` agit sur lui-même et ses sous-objets, sans pour autant voler la logique de ses entités internes.

## Gestion de l'état dynamique du Campus

### Le problème

L'API `building-map` retourne une structure statique (zones, portes, rooms), mais notre système doit garder un état dynamique (agents déployés, portes ouvertes/fermées, pompiers appelés).

### Solution : on fusionne les deux sources
```
API building-map (rechargée à chaque requête)
    |
CampusLoader.loadCampus() --> Campus (structure fraîche)
    |
CampusRepository.saveCampusStructure(newCampus)
    |
Merge avec état existant
    --> Garde les agents déployés
    --> Garde les états des portes (isOpen, isLocked)
    --> Garde les états de la ventilation
    --> Ajoute nouvelles zones/portes si l'API a changé
```

### Ce n'est  pas un cache
Ce n'est  pas un cache, car un cache implique une copie temporaire qu'on peut jeter, mais ici, c'est différent :

1. L'API ne garde pas nos modifications : Si on active une alarme ou déploie un agent, l'API externe ne le sait pas
2. On doit persister les changements : Les agents déployés, les portes ouvertes, les pompiers appelés sont des données du domaine qu'on ne peut pas perdre
3. Logique de merge : On fusionne la structure fraîche de l'API avec notre état du domaine


### Implémentation dans `CampusInMemory`
```java
@Override
public void saveCampusStructure(Campus newCampus) {
    for (Building newBuilding : newCampus.getBuildings()) {
        Building existingBuilding = buildings.get(newBuilding.getId());
        if (existingBuilding == null) {
            buildings.put(newBuilding.getId(), newBuilding);
        } else {
            mergeZonesIntoBuilding(existingBuilding, newBuilding.getZones());
            // On merge, on n'écrase pas
        }
    }
}
```
- Si un bâtiment existe déjà --> merge (garde l'état)
- Si une zone existe déjà --> merge (garde les agents, l'urgencyState)
- Si une porte existe déjà --> on ne la recrée pas (garde isOpen, isLocked modifié)

## Justifications supplémentaires

### Switch case pour les évènements dans Building

Dans notre classe `Building`, nous avons une méthode `handleEvent()` qui nous permet de gérer les différents évènements à l’aide d’un switch case sur le nom de l’évènement.
Nous savons que ce switch case viole le principe de l’OCP (Open/Closed Principle), et nous avons discuté en équipe des différentes alternatives possibles.

L’une des options envisagées aurait été de déplacer cette logique dans un objet créationnel, mais cette solution aurait alors violé le principe du TDA (Tell, Don’t Ask).
Nous avons donc décidé de conserver la méthode `Building.handleEvent()`, car il est impossible de respecter l’OCP à 100 %.
Nous avons jugé préférable de garder cette approche afin d’éviter de contrevenir à d’autres principes de conception.

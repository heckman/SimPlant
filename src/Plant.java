import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class Plant {

//  private static final int STEP_SECONDS = 60 * 60 * 24; // one day
//  private static final int STEP_SECONDS = 3; // for testing
//  private static final int STEP_SECONDS = 8; // for presentation

  // for presentation...
//  private static final long STEP_NANOS = 8571428571L; // 60 seconds = 1 week
  private static final long STEP_NANOS = 12857142857L; // 90 seconds = 1 week
//  private static final long STEP_NANOS = 17142857142L; // 120 seconds = 1 week

  static final String STEP_STRING = "day";

  private Instant born;
  private int lastUpdated; // steps since born

  private Map<PlantAction, TrackerInterface> trackerMap;

  Plant() {
    born = Instant.now();
    trackerMap = new LinkedHashMap<>(4);

    // the order here is important
    // as the order of the status messages is important
    trackerMap.put(PlantAction.WATER, new WaterTracker());
    trackerMap.put(PlantAction.FEED, new FeedTracker());
    trackerMap.put(PlantAction.SPRAY, new BugTracker());
    trackerMap.put(PlantAction.BLOOM, new BloomTracker(this));
//    trackerMap.put(PlantAction.CAT, new RandomCat(this));
  }

  String action(PlantAction action) {
    update();
    if ( !isDead() && trackerMap.containsKey(action) )
      trackerMap.get(action).apply();
    if ( isDead() )
      return getDeathMessage();
    else
      return getAliveMessage(action);
  }

  boolean isDead() {
//    return false; // for bloom-testing
    return trackerMap.values().stream().anyMatch(TrackerInterface::isDead);
  }

  boolean isHealthy() {
//    return true; // for bloom-testing
    return trackerMap.values().stream().allMatch(TrackerInterface::isHealthy);
  }

  private void update() {
    long nanosOld = Duration.between(born, Instant.now()).toNanos();
//    long stepsOld = (nanosOld / 1000000000) / STEP_SECONDS;
    long stepsOld = nanosOld / STEP_NANOS;
    while ( lastUpdated < stepsOld && !isDead() ) {
      step();
      lastUpdated++;
    }
  }

  private void step() {
    trackerMap.values().forEach(TrackerInterface::step);
  }

  private String getAliveMessage(PlantAction action) {
    return getDebugStatus() +
           action.getFeedback() +
           "Your plant is " + getDaysOld() + ". " +
           getStatus().trim();

  }

  int getAge() {
    return lastUpdated;
  }

  static String ageString(int age) {
    return age == 0 ?
               "less than a day old" :
               (age + " " + STEP_STRING +
                (age == 1 ? " " : "s ") + "old"
               );
  }

  private String getDaysOld() {
    return ageString(lastUpdated);
  }

  private String getStatus() {
    return trackerMap.values().stream()
               .map(TrackerInterface::getStatus)
               .filter(Objects::nonNull)
               .map(String::trim)
               .collect(Collectors.joining(" "));
  }

  private String getDeathMessage() {
    return getDebugStatus() +
           "Your plant is dead. " +
           getCauseOfDeath() + " " +
           "It was " + getDaysOld() + ". ";
  }

  private String getCauseOfDeath() {
    return trackerMap.values().stream()
               .filter(TrackerInterface::isDead)
               .map(TrackerInterface::getCauseOfDeath)
               .filter(Objects::nonNull)
               .map(String::trim)
               .collect(Collectors.joining(" "));
  }

  private String getDebugStatus() {
     return "";
/*
     return trackerMap.values().stream()
               .map(TrackerInterface::getLevelCode)
               .filter(Objects::nonNull)
               .map(String::trim)
               .collect(Collectors.joining(",", "[", "] "));
*/

  }
}
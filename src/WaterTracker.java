import java.util.Random;
import java.util.stream.Stream;

public class WaterTracker implements TrackerInterface {

  // LEVEL LOGIC
  // One application adds the same amount as 8 days removes.
  // If the plant is at the ideal level,
  // it wall die after 3 applications at once and
  // it will die after 16 days without an application.

  private static final double IDEAL_LEVEL = 40.0;

  private static final double DECREMENT = 2.5;
  // 68% of the time it will be ± this amount:
  private static final double DECREMENT_SD = 0.125; // about 8%


  private static final double INCREMENT = 20; //
  // 68% of the time it will be ± this amount:
  private static final double INCREMENT_SD = 1; // 8%

  private enum Zone {
    // minimum percentage, healthy, deadly, array of status messages

    // IDEAL + 4 applications
    MORE_XX(100.0, false, true,
            new String[]{
                "It has drowned. ",
                "Your plant got so wet it developed a gnarly fungus that ate everything."
            }
    ),
    // IDEAL + 2.8 applications
    MORE_02(80.0, false, false,
            new String[]{
                "Its leaves are drooping quite badly. "
            }
    ),
    // IDEAL + 1.6 applications
    MORE_01(65.0, false, false,
            new String[]{
                "Its leaves are a little bit limp. "
            }
    ),
    // IDEAL + 8 days
    PERFECT(30.0, true, false,
            new String[]{
                "Its leaves are perfectly perky. "
            }
    ),
    // IDEAL + 12 days
    LESS_01(10.0, false, false,
            new String[]{
                "Its leaves are a little bit limp. "
            }
    ),
    // not quite dead
    LESS_02(0.0, false, false,
            new String[]{
                "Its leaves are drooping quite badly. "
            }
    ),
    // IDEAL +
    LESS_XXX(-1.0, false, true,
             new String[]{
                 "There is nothing left of it but a dried husk. "
             }
    );

    private double min;
    private boolean healthy;
    private boolean deadly;
    private String[] statuses;

    public double getMin() {
      return min;
    }

    Zone(double min, boolean healthy, boolean deadly, String[] statuses) {
      this.min = min;
      this.healthy = healthy;
      this.deadly = deadly;
      this.statuses = statuses;
    }

    static Zone getZone(double percentage) {
      return Stream
                 .of(Zone.values())
                 .filter(g -> percentage >= g.getMin())
                 .findFirst()
                 .orElse(null);
    }

    public boolean isHealthy() {
      return healthy;
    }

    public boolean isDeadly() {
      return deadly;
    }

    public String getStatus() {
      return statuses[new Random().nextInt(statuses.length)];
    }
  }

  private Random random = new Random();
  private double level; // percentage

  public WaterTracker() {
    level = IDEAL_LEVEL;
  }

  @Override
  public void step() {
    level -= DECREMENT + DECREMENT_SD * random.nextGaussian();
    if ( level < 0 ) level = 0; // > -1 and <= 0 is dead
  }

  @Override
  public void apply() {
    level += INCREMENT + INCREMENT_SD * random.nextGaussian();
    if ( level > 100 ) level = 101; // > 100 is dead
  }

  @Override
  public String getStatus() {
    return Zone.getZone(level).getStatus();
  }

  @Override
  public boolean isHealthy() {
    return Zone.getZone(level).isHealthy();
  }

  @Override
  public boolean isDead() {
    return Zone.getZone(level).isDeadly();
  }

  @Override
  public String getCauseOfDeath() {
    return Zone.getZone(level).getStatus();
  }
}

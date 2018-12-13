import java.util.Random;

public class RandomCat implements TrackerInterface {

	//	chance of random cat action
	private static final Double RANDOM_CAT = 0.05;

//	private final Plant plant;
	private Random random = new Random();
	private boolean catAction;

	/**
	 * Comments appended to status message. One is chosen at random.
	 */
	private static final String[] COMMENTS = {
			"A cat peed in your plant! ",
			"A cat knocked down your plant. ",
			"A cat has nibbled on your plant. "
	};

	RandomCat (Plant plant) {
//		this.plant = plant;
		catAction = false;
	}

	@Override
	public void step() {
		Double number = random.nextDouble();
		if (!catAction){
			if (number < RANDOM_CAT)
				catAction = true;
		}else {
			catAction = false;
		}

	}

	@Override
	public void apply(){
		throw new UnsupportedOperationException("Cannot apply RandomCat.");
	}

	//  no status updates
	@Override
	public String getStatus(){
		if (catAction){
			return COMMENTS[new Random().nextInt(COMMENTS.length)];
		}
		return null;
	}

	@Override
	public boolean isHealthy() {
		return true; // will never make plant unhealthy
	}

	@Override
	public boolean isDead() {
		return false; // will never kill plant
	}

	@Override
	public String getCauseOfDeath(){
		return null; // will never kill plant
	}

	@Override
	public String getLevelCode(){
		return null;
	}

}

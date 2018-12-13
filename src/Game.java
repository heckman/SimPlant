// Parses Incoming Messages
// Has a PlantModel object
//
class Game {
  private String id;
  private Plant plant;
  private GameLoader loader;
  private State state;
  private int oldestPlant;

  private enum State {
    IN_GAME,
    CONFIRM_QUIT,
    BETWEEN_GAMES,
    CONFIRM_NEW
  }

  private static final int NO_PLANTS_TO_AGE = -1;

  Game(GameLoader loader, String id) {
    state = State.BETWEEN_GAMES;
    oldestPlant = NO_PLANTS_TO_AGE;
    //// in a future version Game will call loader with id to delete a player
    // this.id = id;
    // this.loader = loader;
  }

  // all threads enter through here
  // therefore this is the only method that needs to be synchronized
  synchronized String processCommand(String rawCommand) {
    String command = rawCommand.toLowerCase();
    try {
      switch ( state ) {
        case BETWEEN_GAMES:
          return betweenGames();
        case IN_GAME:
          return gameCommand(command);
        case CONFIRM_NEW:
          return yesOrNo(command) ? confirmNew() : abortNew();
        case CONFIRM_QUIT:
          return yesOrNo(command) ? confirmQuit() : abortQuit();
      }
    } catch ( IllegalArgumentException e ) {
      switch ( state ) {
        case CONFIRM_NEW:
          return "Please answer 'yes' or 'no': " + askNew();
        case CONFIRM_QUIT:
          return "Please answer 'yes' or 'no': " + askQuit();
        case IN_GAME:
          return helpMessage();
        // BETWEEN_GAMES: doesn't throw exceptions
      }
    }
    throw new IllegalStateException();
  }

  private boolean yesOrNo(String command)
      throws IllegalArgumentException {
    switch ( command ) {
      case "yes": // yes
      case "'yes'":
      case "y":
      case "ya":
      case "yea":
      case "ok":
      case "for sure": // for sure
      case "do it": // do it
        return true;
      case "no":
      case "'no'":
      case "n":
      case "no way":
      case "abort":
        return false;
      default:
        throw new IllegalArgumentException();
    }
  }


  private void setState(State newState) {
    // used to do more here like keep track of previous state
    state = newState;
  }

  private boolean isNewPlayer() {
    return oldestPlant == NO_PLANTS_TO_AGE;
  }

  private String gameCommand(String command)
      throws IllegalArgumentException {
    switch ( command ) {

      // PLANT COMMANDS

      // TODO: put these definitions in the trackers classes
      //       or in the PlantAction enum

      case "": // return status message to empty command
      case "check":
      case "che":
      case "c":
      case "'check'":
      case "look":
      case "loo":
      case "l":
      case "status":
      case "stat":
      case "sta":
        return doAction(PlantAction.CHECK);
      case "water":
      case "wat":
      case "w":
      case "'water'":
        return doAction(PlantAction.WATER);
      case "feed":
      case "fee":
      case "f":
      case "fertilize":
      case "fer":
        return doAction(PlantAction.FEED);
      case "spray":
      case "spr":
      case "s":
      case "bug":
      case "b":
        return doAction(PlantAction.SPRAY);

      // GAME COMMANDS

      case "help":  // help
      case "hel":
      case "h":
      case "'help'":  // 'help'
        return helpMessage();
      case "end":  // end
      case "quit":  // quit
      case "exit":  // exit
      case "kill":  // kill
        return askQuit();
      default:
        throw new IllegalArgumentException();
    }
  }

  private String helpMessage() {
    return "You can \'water\', \'feed', \'spray\' or \'check\' your plant. \n " +
           "You can also \'kill\' your plant to start again.";
  }

  private String doAction(PlantAction action) {
    return checkForDead(plant.action(action));
  }

  private String checkForDead(String message) {
    if ( plant.isDead() ) {
      // end current game... STATE: IN_GAME -> CONFIRM_NEW
      return message + endGame();
    } else {
      return message;
    }
  }

  // STATE:  IN_GAME or CONFIRM_QUIT -> CONFIRM_NEW
  private String endGame() {
    int age = plant.getAge();
    if ( age < oldestPlant )
      return "Your oldest plant was "
             + Plant.ageString(oldestPlant) + ". "
             + askNew();
    //  age >= oldestPlant
    oldestPlant = age;
    return "Your oldest so far! "
           + askNew();
  }

  // STATE:  NEW_PLAYER -> CONFIRM NEW
  private String betweenGames() {
    return (isNewPlayer()
                ? "Welcome to SimPlant! "
                : "Welcome back to SimPlant! Your oldest plant was "
                  + Plant.ageString(oldestPlant) + ". "
           ) + askNew();
  }

  // STARTING A NEW GAME

  // STATE:  NEW_PLAYER or BETWEEN_GAMES -> CONFIRM_NEW
  private String askNew() {
    setState(State.CONFIRM_NEW);
    return "Would you like to grow a "
           + (isNewPlayer() ? "plant?" : "new plant?");
  }

  // STATE:  CONFIRM_NEW -> IN_GAME
  private String confirmNew() {
    setState(State.IN_GAME);
    plant = new Plant();
    return "You have a new plant! " +
           plant.action(PlantAction.CHECK) +
           " " + // action status strips trailing space
           "(Send 'help' for help!)";
  }

  // STATE:  CONFIRM_NEW -> BETWEEN_GAMES
  private String abortNew() {
    setState(State.BETWEEN_GAMES);
    return "Let me know when you change your mind! (Any text will do.)";
  }

  // QUITTING A GAME

  // STATE:  IN_GAME -> CONFIRM_QUIT
  private String askQuit() {
    setState(State.CONFIRM_QUIT);
    return "Are you sure you want to quit the game? " +
           "(Your plant will be lost!)";
  }

  // STATE:  CONFIRM_QUIT -> IN_GAME
  private String abortQuit() {
    setState(State.IN_GAME);
    return "Ok good. " + doAction(PlantAction.CHECK);
  }

  // STATE:  CONFIRM_QUIT -> CONFIRM_NEW
  private String confirmQuit() {
    return "Your plant was " + Plant.ageString(plant.getAge()) + ". "
           + endGame();
  }


}

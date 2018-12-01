// Parses Incoming Messages
// Has a PlantModel object
//
class Game {
  private String id;
  private Plant plant;
  private GameLoader loader;
  private GameState gameState;
  private GameState lastState;
  private int oldestPlant;

  Game(GameLoader loader, String id) {
    gameState = GameState.NEW_PLAYER;
    oldestPlant = -1;
    //// in a future version Game will call loader with id to delete a player
    // this.id = id;
    // this.loader = loader;
  }

  // all threads enter through here
  // therefore this is the only method that needs to be synchronized
  synchronized String processCommand(String rawCommand) {
    String command = (rawCommand + "   ").substring(0, 3).toLowerCase();
    switch ( gameState ) {
      case NEW_PLAYER:
        return newPlayer();
      case GAME_IS_ON:
        return gameIsOn(command);
      case GAME_OVER:
        return gameOver();
      case CONFIRM_NEW:
        return yesOrNo(command) ? confirmNew() : abortNew();
      case CONFIRM_QUIT:
        return yesOrNo(command) ? confirmQuit() : abortQuit();
    }
    return null;
  }

  private boolean yesOrNo(String command) {
    switch ( command ) {
      case "yes": // yes
      case "ye ":
      case "y  ":
      case "ya ":
      case "ok ":
      case "for": // for sure
      case "do ": // do it
        return true;
      default: // no
        return false;
    }
  }

  private void revertState() {
    gameState = lastState;
  }

  private void setState(GameState newState) {
    lastState = gameState;
    gameState = newState;
  }

  private String gameIsOn(String command) {
    switch ( command ) {

      // PLANT COMMANDS

      // comment this line out to disable status-on-empty
      case "   ":  // EMPTY MESSAGE

      case "loo":  // look
      case "che":  // check
      case "sta":  // status
        return doAction(PlantAction.CHECK);
      case "wat":  // water
        return doAction(PlantAction.WATER);
      case "fee":  // feed
      case "fer":  // fertilize
        return doAction(PlantAction.FEED);
      case "spr":  // spray
      case "bug":  // bug-spray
      case "pes":  // pesticide
        return doAction(PlantAction.SPRAY);

      // GAME COMMANDS

      case "hel":  // help
      case "'he":  // 'help'
        return helpMessage();
      case "end":  // end
      case "qui":  // quit
      case "exi":  // exit
      case "kil":  // kill
        return requestQuit();
      default:
        return getUnknownCommandResponse(command);

    }
  }

  private String doAction(PlantAction action) {
    return checkForDead(plant.action(action));
  }

  private String checkForDead(String message) {
    if ( plant.isDead() ) {
      onDead();
    }
    return message;
  }

  private void onDead() {
    int age = plant.getAge();
    if ( age > oldestPlant ) oldestPlant = age;
    setState(GameState.GAME_OVER);
  }

  private String helpMessage() {
    return "You can \'water\', \'feed', \'spray\' or \'check\' your plant. \n " +
           "You can also \'kill\' your plant to start again.";
  }

  private String getUnknownCommandResponse(String message) {
    return (message.length() == 0 ?
                "Please send a command. " :
                "I don't know how to '" + message + "'. "
           ) + getHowToGetHelp();
  }

  private String getHowToGetHelp() {
    return "(Send 'help' for help!)";
  }

  // asks to confirm starting a new plant (new players)
  private String newPlayer() {
    return "Welcome to SimPlant! " + askNew();
  }

  // asks to confirm starting a new plant (returning players)
  private String gameOver() {
    return "Your oldest plant was " + Plant.ageString(oldestPlant) + ". "
           + askNew();
  }

  // asks to confirm starting a new plant
  private String askNew() {
    setState(GameState.CONFIRM_NEW);
    return "Would you like to grow a new plant?";
  }

  // starts a new plant
  private String confirmNew() {
    setState(GameState.GAME_IS_ON);
    plant = new Plant();
    return "You have a new plant! " +
           plant.action(PlantAction.CHECK) +
           " " + // action status strips trailing space
           getHowToGetHelp();
  }

  // returns to previous state
  private String abortNew() {
    revertState();
    return "Let me know when you change your mind! (Any text will do.)";
  }

  // asks to confirm quitting a plant
  private String requestQuit() {
    setState(GameState.CONFIRM_QUIT);
    return "Are you sure you want to quit the game? " +
           "(Your plant will be lost!)";
  }

  // returns to previous state
  private String abortQuit() {
    revertState();
    return "Ok good. " + doAction(PlantAction.CHECK);
  }

  // quits the game
  private String confirmQuit() {
    onDead();
    setState(GameState.GAME_OVER);
    return gameOver();
  }


}

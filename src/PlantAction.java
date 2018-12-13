public enum PlantAction {
  CHECK(""), // No additional feedback for checking status
  WATER("Water applied. "),
  FEED("Fertilizer applied. "),
  SPRAY("Pesticides applied. "),

  // these next two "actions" should never happen
  BLOOM("You have somehow made your plant bloom. "), // This shouldn't happen.
  CAT("You have somehow made a cat come by. "); // This shouldn't happen.

  private String feedback;

  PlantAction(String feedback) {
    this.feedback = feedback;
  }

  public String getFeedback() {
    return feedback;
  }
}

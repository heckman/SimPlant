public enum PlantAction {
  CHECK(""), // No additional feedback for checking status
  WATER("Water applied. "),
  FEED("Fertilizer applied. "),
  SPRAY("Pesticides applied. "),
  BLOOM("You have somehow made your plant bloom. "), // This shouldn't happen.
  CAT("The cat came by.");

  private String feedback;

  PlantAction(String feedback) {
    this.feedback = feedback;
  }

  public String getFeedback() {
    return feedback;
  }
}

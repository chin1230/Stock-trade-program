package stock;

/**
 * the mock class for testing.
 */
public class MockView implements IView {
  private String lastDisplayedMessage;

  @Override
  public void displayMenu() {
    // Not needed for testing GuiController
  }

  @Override
  public void setPrompt(String prompt) {
    this.lastDisplayedMessage = prompt;
  }

  @Override
  public String getUserInput() {
    return "";
  }

  @Override
  public void displayMessage(String message) {
    this.lastDisplayedMessage = message;
  }

  public String getLastDisplayedMessage() {
    return lastDisplayedMessage;
  }
}

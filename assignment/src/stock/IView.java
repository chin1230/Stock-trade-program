package stock;

/**
 * public interface represents the view component in the MVC  pattern.
 * It defines methods for displaying menus, receiving user input, and showing messages to the user.
 */
public interface IView {

  /**
   * display the menu.
   */
  void displayMenu();


  void setPrompt(String prompt);

  /**
   * get the user input.
   *
   * @return a String.
   */
  String getUserInput();

  /**
   * display the message for user.
   *
   * @param message - String message.
   */
  void displayMessage(String message);
}
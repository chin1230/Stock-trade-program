package stock;

/**
 * public interface represents a controller in the MVC pattern.
 * It defines methods for controlling the application flow and interacting with the user interface.
 */
public interface IController {

  /**
   * start the program's view.
   *
   * @param view represent the view
   */
  void start(IView view);


}

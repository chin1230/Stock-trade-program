package stock;

import java.util.Scanner;


/**
 * The View component of the MVC design, represent what user can see when they first start the
 * program.
 */
public class StockView implements IView {
  private final Scanner scanner;
  private IController stockController;

  /**
   * public constructor.
   */
  public StockView() {
    this.scanner = new Scanner(System.in);
  }

  public void setStockController(StockController stockController) {
    this.stockController = stockController;
  }

  @Override
  public void displayMenu() {
    System.out.print("Choose an option: "
            + System.lineSeparator()
            + "1. display the stock you have"
            + System.lineSeparator()
            + "2. display the portfolio you have"
            + System.lineSeparator()
            + "3. buy stock"
            + System.lineSeparator()
            + "4. check gain or loss"
            + System.lineSeparator()
            + "5. check the average of a stock in a certain time"
            + System.lineSeparator()
            + "6. if a stock have a crossover over a period of time"
            + System.lineSeparator()
            + "7. create a portfolio"
            + System.lineSeparator()
            + "8. see the value of a portfolio"
            + System.lineSeparator()
            + "9. add stock to a portfolio"
            + System.lineSeparator()
            + "10. remove stock from a portfolio"
            + System.lineSeparator()
            + "11. display composition, value and distribution of value of a portfolio on a date."
            + System.lineSeparator()
            + "12. rebalance the given portfolio"
            + System.lineSeparator()
            + "13. Plot a bar chart that illustrates the performance of a " +
            "given stock or existing portfolio over a given time frame."
            + System.lineSeparator()
            + "quit or q"
            + System.lineSeparator());
  }

  @Override
  public void setPrompt(String prompt) {
    this.scanner.nextLine();
  }

  /**
   * get user input.
   *
   * @return a String.
   */
  public String getUserInput() {
    return scanner.nextLine();
  }

  /**
   * display message for the user.
   *
   * @param message - a String representing the message.
   */
  public void displayMessage(String message) {
    System.out.println(message);
  }

  /**
   * get the stock controller .
   * @return the stockController
   */
  private IController getStockController() {
    return stockController;
  }
}

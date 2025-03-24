import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import stock.StockView;

import static org.junit.Assert.assertEquals;

/**
 * public class testing the view.
 */
public class StockViewTest {
  private ByteArrayOutputStream outputStream;

  private StockView stockView;

  /**
   * set up the variables.
   */
  @Before
  public void setUp() {
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    stockView = new StockView();
  }

  /**
   * test display the menu.
   */
  @Test
  public void testDisplayMenu() {
    // Redirect System.out to capture output
    stockView.displayMenu();
    String expectedOutput = "Choose an option: \n" +
            "1. display the stock you have\n" +
            "2. display the portfolio you have\n" +
            "3. buy stock\n" +
            "4. check gain or loss\n" +
            "5. check the average of a stock in a certain time\n" +
            "6. if a stock have a crossover over a period of time\n" +
            "7. create a portfolio\n" +
            "8. see the value of a portfolio\n" +
            "9. add stock to a portfolio\n" +
            "10. remove stock from a portfolio\n" +
            "quit or q\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  /**
   * test display message.
   */
  @Test
  public void testDisplayMessage() {
    stockView.displayMessage("Test message");
    String expectedOutput = "Test message\n";
    assertEquals(expectedOutput, outputStream.toString());
  }
}

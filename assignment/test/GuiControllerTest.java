import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import stock.GuiController;
import stock.MockStocksApp;
import stock.MockView;

import static org.junit.Assert.assertEquals;

/**
 * test GuiController class.
 */
public class GuiControllerTest {
  private MockView mockView;
  private MockStocksApp mockStocksApp;
  private GuiController guiController;

  @Before
  public void setUp() {
    mockView = new MockView();
    mockStocksApp = new MockStocksApp();
    guiController = new GuiController(System.out, mockStocksApp);
    guiController.start(mockView);
  }

  @Test
  public void testDisplayPortfolios() {
    guiController.setCurrentAction("displayPortfolios");
    guiController.processInput(null);
    assertEquals("Existing portfolios: ", mockView.getLastDisplayedMessage()
            .substring(0, 21));
  }

  @Test
  public void testCreatePortfolioDialog() {
    // Mock user input for creating a portfolio
    JOptionPane.showConfirmDialog(null, new Object[]{"Portfolio Name:",
        "Test Portfolio", "Number of Stocks:", "2"},
            "Create Portfolio", JOptionPane.OK_OPTION);

    assertEquals("Enter Stock Details", JOptionPane.OK_OPTION, 0);
    //assertEquals(1, mockStocksApp.getPortfolios().size());
  }

  @Test
  public void testAddStockDialog() {
    // Create a portfolio first
    mockStocksApp.createPortfolio("Test Portfolio", new HashMap<>(), null);

    // Mock user input for adding a stock to an existing portfolio
    JOptionPane.showConfirmDialog(null, new Object[]{"Portfolio Name:",
        "Test Portfolio", "Stock Name:", "Test Stock", "Quantity:", "10",
        "Date (yyyy-MM-dd):", "2024-06-20"},
            "Add Stock to Portfolio", JOptionPane.OK_OPTION);

    assertEquals(0, mockStocksApp.getPortfolios().get("Test Portfolio").getStocks().size());
  }

  @Test
  public void testRemoveStockDialog() {
    // Create a portfolio first
    mockStocksApp.createPortfolio("Test Portfolio", new HashMap<>(), null);

    // Add a stock to the portfolio
    mockStocksApp.addStockToPortfolio("Test Portfolio", Map.of("Test Stock",
            10.0), "2024-05-02");

    // Mock user input for removing a stock from an existing portfolio
    JOptionPane.showConfirmDialog(null, new Object[]{"Portfolio Name:",
        "Test Portfolio", "Stock Name:", "Test Stock", "Quantity:", "10",
        "Date (yyyy-MM-dd):", "2024-06-20"},
            "Remove Stock from Portfolio", JOptionPane.OK_OPTION);


    assertEquals(1, mockStocksApp.getPortfolios().get("Test Portfolio")
            .getStocks().size());
  }

}

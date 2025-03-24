
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stock.PortfolioManager;
import stock.PortfolioTypes;
import stock.Transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * tests for portfolio.
 */
public class PortfolioManagerTest {

  private PortfolioManager portfolioManager;
  private Map<String, Double> initialStocks;

  @Before
  public void setUp() {
    portfolioManager = new PortfolioManager();
    initialStocks = new HashMap<>();
    initialStocks.put("AAPL", 10.0);
    initialStocks.put("GOOG", 10.0);

  }

  @Test
  public void testCreateNew() {
    String portfolioName = "MyPortfolio";
    portfolioManager.createNew(portfolioName, initialStocks);
    PortfolioTypes portfolio = portfolioManager.getPortfolio(portfolioName);
    assertNotNull(portfolio);
    assertEquals(10.0, portfolio.getStocks().get("AAPL"), 0.01);
  }

  @Test
  public void testAddStock() {
    String portfolioName = "MyPortfolio";
    portfolioManager.createNew(portfolioName, initialStocks);

    Map<String, Double> stockEntries = new HashMap<>();
    stockEntries.put("NVDA", 5.0);

    portfolioManager.addStock("MyPortfolio", "2024-05-01", stockEntries);

    PortfolioTypes portfolio = portfolioManager.getPortfolio(portfolioName);

    assertTrue(portfolio.getStocks().containsKey("NVDA"));
    assertTrue(portfolio.getStocks().containsKey("AAPL"));
  }

  @Test
  public void testAddStockSame() {
    String portfolioName = "MyPortfolio";
    portfolioManager.createNew(portfolioName, initialStocks);

    Map<String, Double> stockEntries = new HashMap<>();
    stockEntries.put("AAPL", 5.0);

    portfolioManager.addStock("MyPortfolio", "2024-05-01", stockEntries);

    PortfolioTypes portfolio = portfolioManager.getPortfolio(portfolioName);

    assertEquals(15.0, portfolio.getStocks().get("AAPL"), 0.01);
  }

  @Test
  public void testAddStockChat() {
    String portfolioName = "MyPortfolio";

    // Initialize the portfolio with initial stocks
    Map<String, Double> initialStocks = new HashMap<>();
    initialStocks.put("AAPL", 10.0);
    portfolioManager.createNew(portfolioName, initialStocks);

    // Add additional stocks
    Map<String, Double> stockEntries = new HashMap<>();
    stockEntries.put("AAPL", 5.0);
    portfolioManager.addStock(portfolioName, "2024-05-01", stockEntries);

    // Retrieve the portfolio and check the stock quantity
    PortfolioTypes portfolio = portfolioManager.getPortfolio(portfolioName);
    assertEquals(15.0, portfolio.getStocks().get("AAPL"), 0.01);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddStockToNonExistingPortfolio() {
    String portfolioName = "NonExistingPortfolio";
    Map<String, Double> stocksToAdd = new HashMap<>();
    stocksToAdd.put("GOOG", 5.0);

    portfolioManager.addStock(portfolioName, "2024-06-20", stocksToAdd);
  }


  @Test
  public void testRemoveStock() {
    String portfolioName = "testRemoveStock";
    portfolioManager.createNew(portfolioName, initialStocks);

    PortfolioTypes portfolio = portfolioManager.getPortfolio("testRemoveStock");
    assertEquals(10.0, portfolio.getStocks().get("AAPL"), 0.01);

    Map<String, Double> stockToAdd = new HashMap<>();
    stockToAdd.put("AAPL", 10.0);
    portfolioManager.addStock("testRemoveStock", "2024-05-01", stockToAdd);

    Map<String, Double> stocksToRemove = new HashMap<>();
    stocksToRemove.put("AAPL", 5.0);

    portfolioManager.removeStock("testRemoveStock", "2024-05-02", stocksToRemove);
    PortfolioTypes portfolio1 = portfolioManager.getPortfolio("testRemoveStock");

    assertEquals(15.0, portfolio1.getStocks().get("AAPL"), 0.01);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveStockFromNonExistingPortfolio() {
    String portfolioName = "NonExistingPortfolio";
    Map<String, Double> stocksToRemove = new HashMap<>();
    stocksToRemove.put("AAPL", 5.0);

    portfolioManager.removeStock(portfolioName, "2024-05-02", stocksToRemove);
  }


  @Test
  public void testDisplayCertainPortfolio() {
    String portfolioName = "TestPortfolio";
    Map<String, Double> stocks = new HashMap<>();
    stocks.put("AAPL", 10.0);
    portfolioManager.createNew(portfolioName, stocks);

    String expectedOutput = "Stock: AAPL\n" +
            ", Quantity: 10.0\n";

    assertEquals(expectedOutput, portfolioManager.displayCertainPortfolio(portfolioName));
  }



  @Test
  public void testSaveTransactions() {
    String portfolioName = "TestPortfolio";
    Map<String, Double> stocks = new HashMap<>();
    stocks.put("AAPL", 10.0);
    portfolioManager.createNew(portfolioName, stocks);

    List<Transaction> transactions = new ArrayList<>();
    transactions.add(new Transaction("buy", "AAPL", 10.0, LocalDate.now()));

    portfolioManager.saveTransactions(portfolioName, transactions);

    PortfolioTypes portfolio = portfolioManager.getPortfolio(portfolioName);
    assertNotNull(portfolio);
    assertEquals(1, portfolio.getTransactions().size());
  }


}
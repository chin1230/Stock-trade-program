
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stock.Account;
import stock.PortfolioManager;
import stock.StocksApp;
import stock.Transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * test methods in account.
 */
public class AccountTest {
  private StocksApp account;

  @Before
  public void setUp() {
    account = new Account();
  }

  @Test
  public void testBuyStock() {
    account.buyStock("AAPL", 10.0);
    String stockDisplay = account.displayStock();
    assertTrue(stockDisplay.contains("AAPL: 10.0"));
  }

  @Test
  public void testSeeGainOrLoss() {
    long gainOrLoss = account.seeGainOrLoss("AAPL", "2023-05-01", "2023-05-05");
    assertTrue(gainOrLoss != 0);
  }

  @Test
  public void testSeeMovingAverage() {
    double movingAverage = account.seeMovingAverage("AAPL", "2023-06-10", 5);
    assertTrue(movingAverage > 0);
  }

  @Test
  public void testCrossOver() {
    List<String> crossOvers = account.crossOver("AAPL", "2023-06-01", "2023-06-10", 5);
    assertNotNull(crossOvers);
  }

  @Test
  public void testCreatePortfolio() {
    Map<String, Double> stock = new HashMap<>();
    stock.put("AAPL", 1.0);

    LocalDate buyDate = LocalDate.parse("2024-05-01");

    // Verify the transaction log
    Transaction t1 = new Transaction("buy", "AAPL", 1, buyDate);
    List<Transaction> log = new ArrayList<>();
    log.add(t1);
    assertEquals(1, log.size());

    account.createPortfolio("test2", stock, log);

    String display = account.displayPortfolio();
    assertTrue(display.contains("test2"));
    assertEquals(169.3, account.getValue("test2", "2024-05-01"), 0.01);
  }

  @Test
  public void testCreatePortfolioWithSameName() {
    Map<String, Double> stock = new HashMap<>();
    stock.put("AAPL", 2.0);

    LocalDate buyDate = LocalDate.parse("2024-05-01");

    // Verify the transaction log
    Transaction t1 = new Transaction("buy", "AAPL", 2, buyDate);
    List<Transaction> log = new ArrayList<>();
    log.add(t1);
    assertEquals(1, log.size());

    PortfolioManager portfolioManager = new PortfolioManager();

    // Assume we have already added a portfolio with the name "existingPortfolio"
    portfolioManager.createNew("test", stock);

    try {
      // This should throw an IllegalArgumentException
      portfolioManager.createNew("test", stock);
    } catch (IllegalArgumentException e) {
      assertEquals("An existing portfolio with this name (" + "test" + ") already exists."
              + "\nPlease choose another name.\n", e.getMessage());
    }

  }

  @Test
  public void testGetValue() {
    Map<String, Double> stock = new HashMap<>();
    stock.put("AAPL", 2.0);

    LocalDate buyDate = LocalDate.parse("2024-05-01");

    // Verify the transaction log
    Transaction t1 = new Transaction("buy", "AAPL", 2, buyDate);
    List<Transaction> log = new ArrayList<>();
    log.add(t1);
    assertEquals(1, log.size());

    PortfolioManager portfolioManager = new PortfolioManager();

    account.createPortfolio("testGetValue", stock, log);
    double value = account.getValue("testGetValue", "2024-05-01");
    assertEquals(338.6, value, 0.001);

    double value1 = account.getValue("testGetValue", "2024-05-06");
    assertEquals(363.42, value1, 0.001);
  }

  @Test
  public void testGetValueBeforeAddStock() {
    Map<String, Double> stock = new HashMap<>();
    stock.put("AAPL", 2.0);

    LocalDate buyDate = LocalDate.parse("2024-05-01");

    // Verify the transaction log
    Transaction t1 = new Transaction("buy", "AAPL", 2, buyDate);
    List<Transaction> log = new ArrayList<>();
    log.add(t1);
    assertEquals(1, log.size());

    PortfolioManager portfolioManager = new PortfolioManager();

    account.createPortfolio("testGetValueBefore", stock, log);
    double value = account.getValue("testGetValueBefore", "2023-06-10");
    assertEquals(0.0, value, 0.001);
  }

  @Test
  public void testGetDistribution() {
    Map<String, Double> distribution = account.getDistribution("testGetValue", "2024-05-01");
    assertNotNull(distribution);
    System.out.println(account.getDistribution("testGetValue", "2024-05-01"));
    assertTrue(distribution.containsKey("AAPL"));
    assertTrue(distribution.containsValue(1.0));
  }


  @Test
  public void testDisplayAllBefore() {
    String display = account.displayAll("testGetValue", "2023-06-10");
    assertNotNull(display);
    assertTrue(display.contains("Composition:"));
    assertTrue(display.contains("AAPL: 2.0"));
    assertTrue(display.contains("(2024-05-01, 2.0, buy)"));
    assertTrue(display.contains("Distribution: {}"));
  }


  @Test
  public void testAddStockToPortfolio() {
    Map<String, Double> newStock = new HashMap<>();
    newStock.put("NFLX", 1.0);

    account.addStockToPortfolio("bug44", newStock, "2024-05-02");

    String display = account.displayAll("bug44", "2024-05-02");
    System.out.println(display);
    assertNotNull(display);

    assertTrue(display.contains("Composition:"));
    assertTrue(display.contains("NFLX"));
    assertTrue(display.contains("(2024-05-02, 1.0, buy)"));
  }


}

import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import stock.Portfolio;
import stock.PortfolioTypes;
import stock.Transaction;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;



/**
 * public class representing tests for the Portfolio class.
 */
public class PortfolioTest {
  private PortfolioTypes portfolio;

  @Before
  public void setUp() {
    portfolio = new Portfolio();
  }



  @Test
  public void testAddStock() {
    setUp();

    String date = "2024-06-10";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate buyDate = LocalDate.parse(date, formatter);

    portfolio.addStock("AAPL", "2024-06-10", 10);

    // Verify the transaction log
    Transaction t1 = new Transaction("buy", "AAPL", 10, buyDate);
    List<Transaction> log = new ArrayList<>();
    log.add(t1);
    assertEquals(1, log.size());

    portfolio.addStock("AAPL", "2024-06-10", 5);
    // Verify the transaction log
    Transaction t2 = new Transaction("buy", "AAPL", 5, buyDate);
    log.add(t2);
    assertEquals(2, log.size());

    Transaction test = log.get(1);
    assertEquals("buy", test.getTransactionType());
    assertEquals("AAPL", test.getStockName());
    assertEquals(5.0, test.getQuantity(), 0.01);
    assertEquals(LocalDate.parse(date), test.getDate());

    assertEquals(15.0, portfolio.getStocks().get("AAPL"), 0.01);
  }


  @Test
  public void testRemoveStockSameDay() {
    String date = "2024-06-10";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate buyDate = LocalDate.parse(date, formatter);

    portfolio.addStock("AAPL", "2024-06-10", 10);

    // Verify the transaction log
    Transaction t1 = new Transaction("buy", "AAPL", 10, buyDate);
    List<Transaction> log = new ArrayList<>();
    log.add(t1);
    assertEquals(1, log.size());

    portfolio.removeStock("AAPL", "2024-06-10", 5);
    // Verify the transaction log
    Transaction t2 = new Transaction("sell", "AAPL", 5, buyDate);
    log.add(t2);
    assertEquals(2, log.size());

    Transaction test = log.get(1);
    assertEquals("sell", test.getTransactionType());
    assertEquals("AAPL", test.getStockName());
    assertEquals(5.0, test.getQuantity(), 0.01);
    assertEquals(LocalDate.parse(date), test.getDate());

    assertEquals(5.0, portfolio.getStocks().get("AAPL"), 0.01);
  }

  @Test
  public void testRemoveStock() {
    String date = "2024-06-10";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate buyDate = LocalDate.parse(date, formatter);

    portfolio.addStock("AAPL", "2024-06-10", 10);

    // Verify the transaction log
    Transaction t1 = new Transaction("buy", "AAPL", 10, buyDate);
    List<Transaction> log = new ArrayList<>();
    log.add(t1);
    assertEquals(1, log.size());

    portfolio.removeStock("AAPL", "2024-06-11", 5);
    // Verify the transaction log
    Transaction t2 = new Transaction("sell", "AAPL", 5, buyDate);
    log.add(t2);
    assertEquals(2, log.size());

    Transaction test = log.get(1);
    assertEquals("sell", test.getTransactionType());
    assertEquals("AAPL", test.getStockName());
    assertEquals(5.0, test.getQuantity(), 0.01);
    assertEquals(LocalDate.parse(date), test.getDate());

    assertEquals(5.0, portfolio.getStocks().get("AAPL"), 0.01);
  }



  @Test
  public void testChart() {
    portfolio.addStock("AAPL", "2024-05-01", 10);
    portfolio.addStock("GOOG", "2024-05-01", 5);
    LocalDate startDate = LocalDate.of(2024, 5, 1);
    LocalDate endDate = LocalDate.of(2024, 5, 5);
    String chart = portfolio.chart(startDate, endDate);

    String expected = "Performance of portfolio from 2024-05-01 to 2024-05-05\n"
            + System.lineSeparator()
            + "2024-05-01: *************************\n"
            + "2024-05-02: **************************\n"
            + "2024-05-03: ***************************\n"
            + "2024-05-04: ***************************\n"
            + "2024-05-05: ***************************\n"
            + System.lineSeparator()
            + "Scale: * = 100 units";
    assertEquals(expected, chart);
  }

  @Test
  public void testBalance() {
    setUp();
    portfolio.addStock("AAPL", "2024-05-01", 10);
    portfolio.addStock("GOOG", "2024-05-01", 5);
    portfolio.addStock("IBM", "2024-05-01", 20);
    portfolio.addStock("META", "2024-05-01", 30);

    portfolio.displayAll("2024-06-05");
    // Mock weight map
    Map<String, Integer> weight = new HashMap<>();
    weight.put("AAPL", 25);
    weight.put("GOOG", 25);
    weight.put("IBM", 25);
    weight.put("META", 25);

    portfolio.balanceValue(weight, "2024-06-05");

    Map<String, Double> stocks = portfolio.getStocks();

    double aapl = stocks.get("AAPL");

    assertEquals(26.858949813651908, aapl, 0.0);
  }

  @Test
  public void testAddStock1() {
    Portfolio portfolio = new Portfolio();
    portfolio.addStock("AAPL", "2023-06-01", 10);
    assertEquals(Double.valueOf(10.0), portfolio.getStocks().get("AAPL"));
    assertEquals(1, portfolio.getTransactions().size());
  }

  @Test
  public void testRemoveStock1() {
    Portfolio portfolio = new Portfolio();
    portfolio.addStock("AAPL", "2023-06-01", 10);
    portfolio.removeStock("AAPL", "2023-06-15", 5);
    assertEquals(Double.valueOf(5.0), portfolio.getStocks().get("AAPL"));
    assertEquals(2, portfolio.getTransactions().size());
  }

  @Test
  public void testGetDistribution() {
    Portfolio portfolio = new Portfolio();
    portfolio.addStock("AAPL", "2023-06-01", 10);
    portfolio.addStock("GOOGL", "2023-06-01", 5);
    Map<String, Double> distribution = portfolio.getDistribution("2023-06-15");
    assertTrue(distribution.containsKey("AAPL"));
    assertTrue(distribution.containsKey("GOOGL"));
  }

  @Test
  public void testGetValue() {
    Portfolio portfolio = new Portfolio();
    portfolio.addStock("AAPL", "2023-06-01", 10);
    double value = portfolio.getValue("2023-06-15");
    assertTrue(value > 0);
  }

  @Test
  public void testDisplayAll() {
    Portfolio portfolio = new Portfolio();
    portfolio.addStock("AAPL", "2023-06-01", 10);
    String result = portfolio.displayAll("2023-06-15");
    assertTrue(result.contains("Composition:"));
    assertTrue(result.contains("Value:"));
    assertTrue(result.contains("Distribution:"));
  }

  @Test
  public void testBalanceValue1() {
    Portfolio portfolio = new Portfolio();
    portfolio.addStock("AAPL", "2023-06-01", 10);
    portfolio.addStock("GOOGL", "2023-06-01", 5);
    Map<String, Integer> weight = new HashMap<>();
    weight.put("AAPL", 50);
    weight.put("GOOGL", 50);
    portfolio.balanceValue(weight, "2023-06-15");
    Map<String, Double> distribution = portfolio.getDistribution("2023-06-15");
    assertTrue(distribution.get("AAPL") <= 0.55);
    assertTrue(distribution.get("GOOGL") <= 0.55);
  }

  @Test
  public void testChart1() {
    Portfolio portfolio = new Portfolio();
    portfolio.addStock("AAPL", "2023-06-01", 10);
    portfolio.addStock("GOOGL", "2023-06-01", 5);
    String chart = portfolio.chart(LocalDate.of(2023, 6, 1), LocalDate.of(2023, 6, 15));
    assertTrue(chart.contains("Performance of portfolio"));
    assertTrue(chart.contains("Scale:"));
  }
}
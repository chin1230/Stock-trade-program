import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import stock.StockData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static stock.StockData.getStockData;

/**
 * class representing tests for the StockData class.
 */
public class StockDataTest {
  private static final String STOCK_TICKER = "AAPL";
  private static final String TEST_CSV_FILE = STOCK_TICKER + ".csv";


  /**
   * set up.
   * @throws IOException an exception show the error
   */
  @Before
  public void setup() throws IOException {
    // Create a test CSV file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEST_CSV_FILE))) {
      writer.write("timestamp,open,high,low,close,volume\n");
      writer.write("2024-06-01,150,155,149,154,1000000\n");
      writer.write("2024-06-02,151,156,150,155,1100000\n");
    }
  }

  /**
   * set up.
   */
  @After
  public void cleanup() {
    // Delete the test CSV file
    File file = new File(TEST_CSV_FILE);
    if (file.exists()) {
      file.delete();
    }
  }

  /**
   * test get stock data.
   */
  @Test
  public void testGetStockData() {
    Map<String, String> stockData = getStockData(STOCK_TICKER, "2024-06-01");
    assertNotNull(stockData);
    assertEquals("2024-06-01", stockData.get("date"));
    assertEquals("150", stockData.get("open"));
    assertEquals("155", stockData.get("high"));
    assertEquals("149", stockData.get("low"));
    assertEquals("154", stockData.get("close"));
    assertEquals("1000000", stockData.get("volume"));
  }

  /**
   * test get stock data.
   */
  @Test
  public void testGetStockDataInPeriod() {
    Map<LocalDate, Map<String, String>> stockDataInPeriod =
            StockData.getStockDataInPeriod(STOCK_TICKER, "2024-06-01", "2024-06-02");
    assertNotNull(stockDataInPeriod);
    assertEquals(2, stockDataInPeriod.size());

    Map<String, String> day1Data = stockDataInPeriod.get(LocalDate.of(2024, 6, 1));
    assertEquals("150", day1Data.get("open"));
    assertEquals("155", day1Data.get("high"));
    assertEquals("149", day1Data.get("low"));
    assertEquals("154", day1Data.get("close"));
    assertEquals("1000000", day1Data.get("volume"));

    Map<String, String> day2Data = stockDataInPeriod.get(LocalDate.of(2024, 6, 2));
    assertEquals("151", day2Data.get("open"));
    assertEquals("156", day2Data.get("high"));
    assertEquals("150", day2Data.get("low"));
    assertEquals("155", day2Data.get("close"));
    assertEquals("1100000", day2Data.get("volume"));
  }

  /**
   * test get stock data.
   */
  @Test(expected = RuntimeException.class)
  public void testGetStockDataInvalidTicker() {
    getStockData("INVALID", "2024-06-01");
  }

  /**
   * test get stock data.
   */
  @Test(expected = RuntimeException.class)
  public void testFetchDataAndSaveToCSVInvalidTicker() {
    StockData.fetchDataAndSaveToCSV("INVALID");
  }

  @Test
  public void testDataAvailable() {
    boolean actual = StockData.isDataAvailable("GOOG", "2024-06-08");
    assertEquals(false, actual);
  }


}
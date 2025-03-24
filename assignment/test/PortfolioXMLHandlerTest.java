import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import stock.Portfolio;
import stock.PortfolioTypes;
import stock.PortfolioXMLHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * public class representing tests for the portfolio xml files.
 */
public class PortfolioXMLHandlerTest {
  private PortfolioXMLHandler handler;
  private final String testFileName = "testPortfolio";
  private final File testFile = new File(testFileName + ".xml");

  @Before
  public void setUp() {
    handler = new PortfolioXMLHandler();
  }

  @After
  public void tearDown() {
    if (testFile.exists()) {
      testFile.delete();
    }
  }

  @Test
  public void testSavePortfolioToXML() {
    Map<String, Double> stockData = new HashMap<>();
    stockData.put("AAPL", 10.0);

    PortfolioTypes port = new Portfolio(stockData);

    handler.savePortfolioToXML(testFileName, port);

    assertTrue(testFile.exists());
  }

  @Test
  public void testLoadPortfolioFromXML() {
    // First, save a portfolio to ensure the XML file exists
    Map<String, Double> stockData = new HashMap<>();
    stockData.put("NVDA", 10.0);
    PortfolioTypes port = new Portfolio(stockData);

    handler.savePortfolioToXML(testFileName, port);

    PortfolioTypes portfolio = handler.loadPortfolioFromXML(testFileName);
    assertNotNull(portfolio);
    assertEquals(1, portfolio.getStocks().size());
    assertTrue(portfolio.getStocks().containsKey("NVDA"));
    assertEquals(10.0, portfolio.getStocks().get("NVDA"), 0.001);
  }

  @Test
  public void testUpdatePortfolioInXML_Adding() {
    // First, save a portfolio to ensure the XML file exists
    Map<String, Double> stockData = new HashMap<>();
    stockData.put("AAPL", 10.0);

    PortfolioTypes port = new Portfolio(stockData);

    handler.savePortfolioToXML(testFileName, port);

    port.addStock("AAPL", "2024-05-01", 5);

    handler.savePortfolioToXML(testFileName, port);

    PortfolioTypes por = handler.loadPortfolioFromXML(testFileName);
    assertNotNull(port);
    assertEquals(1, por.getStocks().size());
    assertTrue(por.getStocks().containsKey("AAPL"));

    por.getStocks().get("AAPL");

    assertEquals(15.0, por.getStocks().get("AAPL"), 0.001);
  }


  @Test
  public void testUpdatePortfolioInXML_Removing() {
    // First, save a portfolio to ensure the XML file exists
    Map<String, Double> stockData = new HashMap<>();
    stockData.put("GOOG", 15.0);
    PortfolioTypes port = new Portfolio(stockData);
    port.addStock("AAPL", "2024-05-01", 20);
    handler.savePortfolioToXML(testFileName, port);

    // Now, update the portfolio by removing some stocks
    port.removeStock("AAPL", "2024-05-06", 20);

    handler.saveOrUpdatePortfolioToXML(testFileName, port);

    PortfolioTypes portfolio = handler.loadPortfolioFromXML(testFileName);
    assertNotNull(portfolio);
    assertEquals(1, portfolio.getStocks().size());
    assertFalse(portfolio.getStocks().containsKey("AAPL"));

  }
}

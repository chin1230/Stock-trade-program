package stock;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A public class representing a Portfolio manager
 * where user can add, remove, or edit multiple portfolios
 * if they have more than one portfolio.
 */
public class PortfolioManager implements PortfolioApp {
  private final Map<String, PortfolioTypes> portfolios;
  private final PortfolioXMLHandler xmlHandler;
  protected static final Logger logger = Logger.getLogger(PortfolioManager.class.getName());

  /**
   * Public constructor.
   */
  public PortfolioManager() {
    this.portfolios = new HashMap<>();
    this.xmlHandler = new PortfolioXMLHandler();
    loadAllPortfolios();
  }


  /**
   * Create a new portfolio in the manager.
   *
   * @param name  name of the new portfolio
   * @param stock the stock this new portfolio will have
   * @return the new portfolio
   */
  public PortfolioTypes createNew(String name, Map<String, Double> stock) {
    PortfolioTypes port = new Portfolio(stock);
    xmlHandler.savePortfolioToXML(name, port);

    return this.portfolios.put(name, port);
  }

  /**
   * Load and add the portfolios to the HashMap.
   *
   * @param portName name of the portfolio to load
   */
  protected void loadPortfolioIntoHashMap(String portName) {
    PortfolioTypes portfolio = xmlHandler.loadPortfolioFromXML(portName);
    if (portfolio != null) {
      this.portfolios.put(portName, portfolio);
      logger.log(Level.INFO, "Portfolio loaded successfully!");
    } else {
      logger.log(Level.WARNING, "Failed to load portfolio from XML.");
    }
  }

  /**
   * Load all portfolios from XML during initialization.
   */
  private void loadAllPortfolios() {
    String workingDir = System.getProperty("user.dir");
    File folder = new File(workingDir);

    if (!folder.exists() || !folder.isDirectory()) {
      throw new RuntimeException("Portfolio directory does not exist: " + folder.getAbsolutePath());
    }

    for (File file : folder.listFiles()) {
      if (file.isFile() && file.getName().endsWith(".xml")) {
        String portfolioName = file.getName().replace(".xml", "");
        loadPortfolioIntoHashMap(portfolioName);
      }
    }
  }

  /**
   * To check if there is a portfolio in the current manager with the same name as a new one.
   *
   * @param name the name of the new portfolio
   * @return boolean indicating if the name exists
   */
  public boolean checkSame(String name) {
    File file = new File(name + ".xml");
    return file.exists();
  }

  /**
   * Add stock to one portfolio in the manager.
   *
   * @param name  the name of the portfolio
   * @param stock the stock the user wants to add
   */
  public void addStock(String name, String date, Map<String, Double> stock) {
    File file = new File(name + ".xml");
    if (file.exists()) {
      PortfolioTypes portfolio = xmlHandler.loadPortfolioFromXML(name);

      for (Map.Entry<String, Double> stockEntry : stock.entrySet()) {
        String stockName = stockEntry.getKey();
        int quantity = stockEntry.getValue().intValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate buyDate = LocalDate.parse(date, formatter);

        portfolio.addStock(stockName, date, quantity);
        portfolio.addTransaction(new Transaction("buy", stockName, quantity, buyDate));
        xmlHandler.saveOrUpdatePortfolioToXML(name, portfolio);
      }

    } else {
      throw new IllegalArgumentException("You do not have a portfolio with this name: "
              + name + ".");
    }
  }


  /**
   * Remove stocks from one portfolio in the manager.
   *
   * @param name  the name of the portfolio
   * @param stock the stocks the user wants to remove
   */
  public void removeStock(String name, String date, Map<String, Double> stock) {
    File file = new File(name + ".xml");
    if (file.exists()) {
      PortfolioTypes portfolio = xmlHandler.loadPortfolioFromXML(name);
      for (Map.Entry<String, Double> stockEntry : stock.entrySet()) {
        String stockName = stockEntry.getKey();
        int quantity = stockEntry.getValue().intValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sellDate = LocalDate.parse(date, formatter);

        portfolio.removeStock(stockName, date, quantity);
        portfolio.addTransaction(new Transaction("sell", stockName, quantity, sellDate));

        xmlHandler.saveOrUpdatePortfolioToXML(name, portfolio);
      }
    } else {
      throw new IllegalArgumentException("You do not have a portfolio with this name: "
              + name + ".");
    }
  }

  /**
   * Get a portfolio using its name.
   *
   * @param name - name of the portfolio
   * @return the Portfolio
   */
  public PortfolioTypes getPortfolio(String name) {
    return xmlHandler.loadPortfolioFromXML(name);
  }

  /**
   * Get the value of one of the portfolios in the manager.
   *
   * @param name the name of the portfolio
   * @param date the date of the value of the portfolio
   * @return a double representing the value of the portfolio on a given day
   */
  public double getValue(String name, String date) {
    PortfolioTypes portfolio = xmlHandler.loadPortfolioFromXML(name);
    loadPortfolioIntoHashMap(name);
    return portfolio.getValue(date);
  }

  /**
   * Display the content of the portfolios.
   *
   * @return the string representing the portfolios
   */
  public String displayPortfolio() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, PortfolioTypes> entry : portfolios.entrySet()) {
      String portfolioName = entry.getKey();
      PortfolioTypes portfolio = entry.getValue();
      sb.append("Portfolio: ").append(portfolioName).append("\n");
      sb.append("Stocks:\n");

      for (Map.Entry<String, Double> stockEntry : portfolio.getStocks().entrySet()) {
        String stockName = stockEntry.getKey();
        sb.append("  Stock: ").append(stockName).append("\n");
        sb.append(", Quantity: ").append(stockEntry.getValue()).append("\n");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  @Override
  public String displayCertainPortfolio(String portfolioName) {
    StringBuilder sb = new StringBuilder();
    PortfolioTypes certain = this.portfolios.get(portfolioName);

    for (Map.Entry<String, Double> stockEntry : certain.getStocks().entrySet()) {
      String stockName = stockEntry.getKey();
      sb.append("Stock: ").append(stockName).append("\n");
      sb.append(", Quantity: ").append(stockEntry.getValue()).append("\n");
    }
    return sb.toString();
  }

  /**
   * Display the distribution of a Portfolio.
   *
   * @param portfolioName - name of the portfolio
   * @param date          - specific date
   * @return a Map of stock name & value
   */
  public Map<String, Double> getDistribution(String portfolioName, String date)
          throws IllegalArgumentException {
    PortfolioTypes portfolio = xmlHandler.loadPortfolioFromXML(portfolioName);
    return portfolio.getDistribution(date);
  }

  /**
   * Display all info relating to a Portfolio using its name & a specific date.
   *
   * @param portfolioName - name of the portfolio
   * @param date          - specific date
   * @return a String
   */
  public String displayAll(String portfolioName, String date) {
    PortfolioTypes portfolio = xmlHandler.loadPortfolioFromXML(portfolioName);
    return portfolio.displayAll(date);
  }

  /**
   * Balance the value with the current weight.
   *
   * @param porName the name of the portfolio
   * @param weight  the weight the user wants
   * @param date    the date of the desired price
   */
  @Override
  public void balanceValue(String porName, Map<String, Integer> weight, String date) {
    PortfolioTypes stock = xmlHandler.loadPortfolioFromXML(porName);
    stock.balanceValue(weight, date);
    xmlHandler.saveOrUpdatePortfolioToXML(porName, stock);
  }

  @Override
  public String chart(String portfolioName, LocalDate startDate, LocalDate endDate) {
    PortfolioTypes portfolio = xmlHandler.loadPortfolioFromXML(portfolioName);
    return portfolio.chart(startDate, endDate);
  }

  @Override
  public void saveTransactions(String portfolioName, List<Transaction> transactions) {
    PortfolioTypes portfolio = xmlHandler.loadPortfolioFromXML(portfolioName);
    if (portfolio != null) {
      for (Transaction transaction : transactions) {
        portfolio.addTransaction(transaction);
      }
      xmlHandler.savePortfolioToXML(portfolioName, portfolio);
    } else {
      // Handle the case where the portfolio does not exist
      logger.log(Level.SEVERE, "Portfolio not found: " + portfolioName);
    }
  }
}

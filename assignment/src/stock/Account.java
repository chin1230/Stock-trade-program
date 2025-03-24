package stock;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * public class representing an account of a user and the things they can do.
 * The user can buyStock, check their stock gain or loss, see the moving average of their stocks
 * check how many crossovers, create new portfolios, get the value of certain portfolio.
 * get the distribution of a certain portfolio, display all the information of a portfolio.
 * add and remove stocks, display any required information, draw a chart and even rebalance the
 * current portfolio.
 */
public class Account implements StocksApp {
  private final Map<String, Double> stocks = new HashMap<>();
  private final PortfolioApp portfolioManager = new PortfolioManager();


  /**
   * buy stock for the account.
   * @param name     - name of the stock / its ticker.
   * @param quantity - quantity of the stock.
   */
  public void buyStock(String name, double quantity) {
    // check if ticker user enters in is real.
    if (!isValidStockTicker(name)) {
      System.out.printf("Invalid stock ticker: %s\n", name);
      // throw new IllegalArgumentException("Invalid stock ticker: " + name);
    }

    // if valid, then update amount of stock.
    // default amount is 0, if there is already an amount of this stock, then update the quantity.
    stocks.put(name, stocks.getOrDefault(name, 0.0) + quantity);
  }

  // determine if the ticker is valid.
  private boolean isValidStockTicker(String ticker) {
    return StockData.isValidStockTicker(ticker);
  }

  @Override
  public long seeGainOrLoss(String stockName, String startingDate, String endingDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate start = LocalDate.parse(startingDate, formatter);
    LocalDate end = LocalDate.parse(endingDate, formatter);

    Map<LocalDate, Map<String, String>> stockValues =
            StockData.getStockDataInPeriod(stockName, startingDate, endingDate);

    // get closing price for the stock on starting date
    double startingPrice = Double.parseDouble(stockValues.get(start).get("close"));
    // get closing price for the stock on ending date
    double closingPrice = Double.parseDouble(stockValues.get(end).get("close"));

    return Math.round(closingPrice - startingPrice);
  }

  @Override
  public double seeMovingAverage(String stockName, String specificDate, int days) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate endDate = LocalDate.parse(specificDate, formatter);
    LocalDate startDate = endDate.minusDays(days - 1);

    Map<LocalDate, Map<String, String>> stockValues =
            StockData.getStockDataInPeriod(stockName, startDate.toString(), endDate.toString());

    double totalMoving = 0;
    int count = 0;

    for (Map.Entry<LocalDate, Map<String, String>> entry : stockValues.entrySet()) {
      Map<String, String> dailyValues = entry.getValue();
      double close = Double.parseDouble(dailyValues.get("close"));
      totalMoving += close;
      count++;
    }

    // Calculate average only for the available days
    if (count == 0) {
      throw new RuntimeException("No stock data available for the given date range");
    }

    return totalMoving / count;
  }


  @Override
  public List<String> crossOver(String stockName, String startingDate,
                                String endingDate, int x) {
    if (x < 0) {
      throw new IllegalArgumentException("The number of days must be positive.");
    }

    // If the number of days is zero, return an empty list
    if (x == 0) {
      return new ArrayList<>();
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate start = LocalDate.parse(startingDate, formatter);
    LocalDate end = LocalDate.parse(endingDate, formatter);

    // Check if the ending date is before the starting date
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("Ending date cannot be before starting date.");
    }

    Map<LocalDate, Map<String, String>> stockValues = StockData.getStockDataInPeriod(stockName,
            startingDate, endingDate);

    TreeMap<LocalDate, Double> closingPrices = new TreeMap<>();

    for (Map.Entry<LocalDate, Map<String, String>> entry : stockValues.entrySet()) {
      LocalDate date = entry.getKey();
      double close = Double.parseDouble(entry.getValue().get("close"));
      closingPrices.put(date, close);
    }

    ArrayList<String> crossovers = new ArrayList<>();

    LocalDate[] dates = closingPrices.keySet().toArray(new LocalDate[0]);

    for (int i = x - 1; i < dates.length; i++) {
      LocalDate date = dates[i];
      LocalDate startDate = dates[i - x + 1];

      double movingAverage = seeMovingAverage(stockName, startDate.toString(), x);
      double close = closingPrices.get(date);
      double prevClose = closingPrices.get(dates[i - 1]);

      if ((prevClose < movingAverage && close > movingAverage) ||
              (prevClose > movingAverage && close < movingAverage)) {
        crossovers.add(date.toString());
      }
    }
    return crossovers;
  }


  /**
   * create a new Por in manager.
   *
   * @param name  - name of the portfolio.
   * @param stock - map representing stock names and its quantities.
   */
  @Override
  public void createPortfolio(String name, Map<String, Double> stock,
                              List<Transaction> transactions) {
    if (this.portfolioManager.checkSame(name)) {
      throw new IllegalArgumentException("An existing portfolio with this name (" + name + ") " +
              "already exists.\n" + "Please choose another name.\n");
    }
    this.portfolioManager.createNew(name, stock);

    // Add transactions to the portfolio
    portfolioManager.saveTransactions(name, transactions);
  }

  /**
   * get the value of one of the portfolio.
   *
   * @param name - the name of the portfolio
   * @param date the date user want the value
   * @return a double represent the value of a por in a given date
   */
  @Override
  public double getValue(String name, String date) {
    return this.portfolioManager.getValue(name, date);
  }

  /**
   * return a map represent the distribution of the portfolio in a given day.
   *
   * @param portfolioName the name of the por
   * @param date          the date user want to know
   * @return a map represent the distribution of the por in a given day
   */
  @Override
  public Map<String, Double> getDistribution(String portfolioName, String date) {
    return this.portfolioManager.getDistribution(portfolioName, date);
  }

  /**
   * display the required por.
   *
   * @param portfolioName the name of the por
   * @param date          the date required
   * @return the string represent the information of the por
   */
  @Override
  public String displayAll(String portfolioName, String date) {
    return this.portfolioManager.displayAll(portfolioName, date);
  }


  /**
   * add stock to one por in manager.
   *
   * @param name  the name of the por
   * @param stock the stock user want to add
   */
  @Override
  public void addStockToPortfolio(String name, Map<String, Double> stock, String date) {
    if (this.portfolioManager.checkSame(name)) {
      this.portfolioManager.addStock(name, date, stock);
    } else {
      throw new IllegalArgumentException("Do not have such a portfolio");
    }
  }


  /**
   * remove stock to one por in manager.
   *
   * @param name  the name of the por
   * @param stock the stock user want to remove
   */
  @Override
  public void removeStockFromPortfolio(String name, Map<String, Double> stock, String date) {
    if (this.portfolioManager.checkSame(name)) {
      this.portfolioManager.removeStock(name, date, stock);
    } else {
      throw new IllegalArgumentException("You have no portfolio with this name: " + name);
    }
  }


  /**
   * represent the current stocks as String.
   *
   * @return a string represent the current stock users have.
   */
  @Override
  public String displayStock() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Double> entry : stocks.entrySet()) {
      sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
    }
    return sb.toString();
  }

  @Override
  public String displayPortfolio() {
    return this.portfolioManager.displayPortfolio();
  }

  @Override
  public String displayCertainPortfolio(String portfolioName) {
    return this.portfolioManager.displayCertainPortfolio(portfolioName);
  }

  @Override
  public void reBalance(String porName, Map<String, Integer> weight, String date) {
    this.portfolioManager.balanceValue(porName, weight, date);
  }

  @Override
  public String chart(String portfolioName, LocalDate startDate, LocalDate endDate) {
    return this.portfolioManager.chart(portfolioName, startDate, endDate);
  }

  @Override
  public void saveTransactions(String portfolioName, List<Transaction> transactions) {
    this.portfolioManager.saveTransactions(portfolioName, transactions);
  }


}

package stock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * public interface representing basic methods that are available.
 * the interface support the controller to buy stocks, see gain or loss of the stock and many
 * other special requirements for stocks.
 * for the users.
 */
public interface StocksApp {

  /**
   * allow user to buy stock without any monetary restriction or date.
   *
   * @param name     - name of the stock / its ticker.
   * @param quantity - quantity of the stock.
   */
  void buyStock(String name, double quantity);

  /**
   * see the gain or loss of a stock in a range of time.
   *
   * @param stockName    the name of the stock
   * @param startingDate the start date of the stock
   * @param endingDate   the end day of the stock
   * @return the long represent gain or loss of the stock
   */
  long seeGainOrLoss(String stockName, String startingDate, String endingDate);

  /**
   * allow user to see the x-day moving average.
   *
   * @param stockName    - the name of the stock.
   * @param days         - number of days that the user wants to the moving average of.
   * @param specificDate - specific date we want to see the average moving of.
   * @return a long number indicating the moving average of given no. of days.
   */
  double seeMovingAverage(String stockName, String specificDate, int days);

  /**
   * determine if a crossover occurs for a specific stock in a specified period.
   *
   * @param stockName    - name of stock.
   * @param startingDate - starting date of the period.
   * @param endDate      - starting date of the period.
   * @param days         - how many days in the period.
   * @return a list of date that crossovers happen.
   */
  List<String> crossOver(String stockName, String startingDate, String endDate, int days);


  /**
   * allow user to create 1 or more portfolio with shares of 1 or more stock.
   * also allow user to find the value of that portfolio on a specific date.
   *
   * @param name  the name of the por
   * @param stock the stock user want to add
   *
   */
  void createPortfolio(String name, Map<String, Double> stock, List<Transaction> transactions);

  /**
   * add stock to one por.
   *
   * @param name  the name of the por
   * @param stock the stock user want to add
   */
  void addStockToPortfolio(String name, Map<String, Double> stock, String date);


  /**
   * remove the stock from the por.
   *
   * @param name  the name of the stock
   * @param stock the stock want to be removed
   */
  void removeStockFromPortfolio(String name, Map<String, Double> stock, String date);

  // void removeStockFromPortfolio(String name, Map<String, Double> stock);
  /**
   * get the value of a stock.
   *
   * @param name the name of the stock
   * @param date the date of the close value
   * @return the double represent the value
   */
  double getValue(String name, String date);


  /**
   * return a map represent the distribution of the porfolio in a given day.
   * @param portfolioName - the name of portfolio,
   * @param date - date.
   * @return a map represent the distribution of the por in a given day.
   */
  Map<String, Double> getDistribution(String portfolioName, String date);


  /**
   * display everything like composition, value and distribution of value of a portfolio.
   * @param portfolioName - the portfolio name.
   * @param date - date.
   * @return a String returning all the portfolio's info.
   */
  String displayAll(String portfolioName, String date);


  /**
   * turn the StocksApp into string.
   *
   * @return the String represent the stock of StocksApp
   */
  String displayStock();

  /**
   * display the portfolio as a string.
   *
   * @return string represent the por
   */
  String displayPortfolio();

  String displayCertainPortfolio(String portfolioName);

  void reBalance(String porName, Map<String, Integer> weight, String date);


  String chart(String portfolioName, LocalDate startDate, LocalDate endDate);

  void saveTransactions(String portfolioName, List<Transaction> transactions);
}

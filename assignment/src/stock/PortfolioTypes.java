package stock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * This interface represents the operations that can be performed on a portfolio of stocks.
 */
public interface PortfolioTypes {

  /**
   * Gets the stocks inside the portfolio.
   *
   * @return a Map containing the stock name and its quantity.
   */
  Map<String, Double> getStocks();

  /**
   * Gets the list of transactions associated with the portfolio.
   *
   * @return a List of transactions.
   */
  List<Transaction> getTransactions();

  /**
   * Adds stocks to the portfolio.
   *
   * @param stockSymbol the symbol of the stock to add.
   * @param date the date the stock is added.
   * @param quantity the quantity of the stock to add.
   */
  void addStock(String stockSymbol, String date, int quantity);

  /**
   * Removes stocks from the portfolio.
   *
   * @param stockSymbol the symbol of the stock to remove.
   * @param date the date the stock is removed.
   * @param quantity the quantity of the stock to remove.
   */
  void removeStock(String stockSymbol, String date, int quantity);

  /**
   * Draws a chart representing the value of the portfolio over a given period.
   *
   * @param startDate the start time of the period.
   * @param endDate the end time of the period.
   * @return a string representing a chart showing the value of the portfolio over the period.
   */
  String chart(LocalDate startDate, LocalDate endDate);

  /**
   * Gets the value of the entire portfolio on a specific date.
   *
   * @param date the date to get the value for.
   * @return the value of the portfolio on the specified date.
   */
  double getValue(String date);

  /**
   * Displays all the details of the portfolio on a specific date.
   *
   * @param date the date to display details for.
   * @return a string containing all the details of the portfolio on the specified date.
   */
  String displayAll(String date);

  /**
   * Balances the value of the portfolio according to the given weights on a specific date.
   *
   * @param weight a map containing the stock symbols and their corresponding weights.
   * @param date the date to balance the portfolio on.
   */
  void balanceValue(Map<String, Integer> weight, String date);

  /**
   * Gets the distribution of the portfolio on a specific date.
   *
   * @param date the date to get the distribution for.
   * @return a Map containing the stock symbols and their corresponding values.
   */
  Map<String, Double> getDistribution(String date);

  /**
   * Adds a transaction to the portfolio.
   *
   * @param buy the transaction to add.
   */
  void addTransaction(Transaction buy);
}

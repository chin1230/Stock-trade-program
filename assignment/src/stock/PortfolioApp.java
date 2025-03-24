package stock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * public interface representing a general Portfolio.
 * In this interface, users can:
 * - create a new portfolio.
 * - add stock to a portfolio.
 * - remove stock from a portfolio.
 * - get a portfolio using its name.
 * - check if a portfolio's name exist yet.
 * - display all types of info about the portfolio.
 * - draw a chart of the performance of the portfolio.
 */
public interface PortfolioApp {

  /**
   * create a new por in the manager.
   *
   * @param name  name of the new por
   * @param stock the stock this new por will have
   * @return a new por
   */
  PortfolioTypes createNew(String name, Map<String, Double> stock);


  /**
   * add stock to one por in manager.
   *
   * @param name  the name of the por
   * @param stock the stock user want to add
   */
  void addStock(String name, String date, Map<String, Double> stock);


  /**
   * remove stocks from one por in manager.
   *
   * @param name  the name of the por
   * @param stock the stocks user want to remove
   */
  void removeStock(String name, String date, Map<String, Double> stock);

  /**
   * get a portfolio using its name.
   * @param name - name of the portfolio.
   * @return the Portfolio.
   */
  PortfolioTypes getPortfolio(String name);


  /**
   * to check if there is a por in current manager have same name with a new one.
   *
   * @param name the name of the new por
   * @return a boolean checks if same names
   */
  boolean checkSame(String name);


  /**
   * get the value of one of the portfolio in the manager.
   *
   * @param name the name of the por
   * @param date the date of the value of the por
   * @return a double represent the value of a por in a given day
   */
  double getValue(String name, String date);

  /**
   * display the content of por.
   * @return the string represent the por
   */
  String displayPortfolio();


  /**
   * display the information of a certain portfolio.
   * @param portfolioName the name of the portfolio
   * @return a string represent the information of a certain portfolio.
   */
  String displayCertainPortfolio(String portfolioName);


  /**
   * display the distribution of a Portfolio.
   * @param portfolioName - name of the portfolio.
   * @param date - specific date.
   * @return a Map of stock name & value.
   */
  Map<String, Double> getDistribution(String portfolioName, String date);

  /**
   * display all info relating to a Portfolio using its name & a specific date.
   * @param portfolioName - name of the portfolio.
   * @param date - specific date.
   * @return a String.
   */
  String displayAll(String portfolioName, String date);

  /**
   * balance the value with current weight.
   * @param porName the name of the por
   * @param weight the weight user want
   * @param date the date of price want
   */
  void balanceValue(String porName, Map<String, Integer> weight, String date);

  /**
   * draw the performance of a portfolio over specified timespan.
   * @param porname - portfolio name.
   * @param startDate - start date.
   * @param endDate - end date.
   * @return a chart drawn using String.
   */
  String chart(String porname, LocalDate startDate, LocalDate endDate);

  /**
   * save the transaction log.
   * @param portfolioName - name of the portfolio of the transactions.
   * @param transactions - list of transactions log.
   */
  void saveTransactions(String portfolioName, List<Transaction> transactions);
}

package stock;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * represent a portfolio which have many different functions.
 */
public class Portfolio implements PortfolioTypes {
  protected Map<String, Double> stocks;
  public List<Transaction> transactionsLog;

  public Portfolio() {
    this.stocks = new HashMap<>();
    this.transactionsLog = new ArrayList<>();
  }

  public Portfolio(Map<String, Double> stocks) {
    this.stocks = stocks;
    this.transactionsLog = new ArrayList<>();
  }

  /**
   * the public constructor.
   *
   * @param stocks the stock inside the por
   */
  public Portfolio(Map<String, Double> stocks, List<Transaction> transactionsLog) {
    this.stocks = stocks;
    this.transactionsLog = transactionsLog;
  }


  @Override
  public Map<String, Double> getStocks() {
    return new HashMap<>(stocks);
  }

  @Override
  public void addStock(String stockSymbol, String date, int quantity) {
    if (!StockData.isValidStockTicker(stockSymbol)) {
      throw new IllegalArgumentException("Invalid stock ticker: " + stockSymbol);
    }

    if (!StockData.isDataAvailable(stockSymbol, date)) {
      throw new IllegalArgumentException("Stock data is not available on the given date: " + date);
    }

    // Update stocks map
    stocks.put(stockSymbol, stocks.getOrDefault(stockSymbol, 0.0) + quantity);

    // Add transaction
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate transactionDate = LocalDate.parse(date, formatter);
    transactionsLog.add(new Transaction("buy", stockSymbol,
            quantity, transactionDate));
  }


  @Override
  public void removeStock(String stockSymbol, String date, int quantity) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate removeDate = LocalDate.parse(date, formatter);

    if (!stocks.containsKey(stockSymbol)) {
      throw new IllegalArgumentException("You do not have " + stockSymbol
              + " stocks in your portfolio.");
    }

    // Get all transactions for the given stock
    List<Transaction> transactions = transactionsLog.stream()
            .filter(t -> t.getStockName().equals(stockSymbol))
            .sorted(Comparator.comparing(Transaction::getDate))
            .collect(Collectors.toList());

    double currentQuantity = quantity;

    // Iterate over transactions to calculate remaining quantity to remove
    for (Transaction transaction : transactions) {
      LocalDate buyDate = transaction.getDate();

      if (removeDate.isBefore(buyDate)) {
        throw new IllegalArgumentException("You cannot remove " + stockSymbol
                + " stocks before the purchase date of " + buyDate);
      }

      if (currentQuantity <= 0) {
        break; // No more stocks to remove
      }

      if (transaction.getQuantity() >= currentQuantity) {
        // Reduce the quantity in this transaction
        transaction.setQuantity(transaction.getQuantity() - currentQuantity);
        currentQuantity = 0;
      } else {
        // Remove this transaction entirely
        currentQuantity -= transaction.getQuantity();
        transaction.setQuantity(0); // Set the transaction quantity to 0
      }
    }

    if (currentQuantity > 0) {
      throw new IllegalArgumentException("Insufficient quantity of " + stockSymbol
              + " stocks on date " + date);
    }

    // Clean up empty transactions from the log
    transactionsLog.removeIf(t -> t.getQuantity() <= 0);

    // Update the stock quantity in the portfolio
    double nowQuantity = stocks.get(stockSymbol);
    double updatedQuantity = nowQuantity - quantity;
    if (updatedQuantity > 0) {
      // stocks.put(stockSymbol, updatedQuantity);
      stocks.put(stockSymbol, stocks.getOrDefault(stockSymbol, 0.0) - quantity);
    } else {
      stocks.remove(stockSymbol); // Remove stock entry if quantity drops to 0 or below
    }

    // Add transaction
    transactionsLog.add(new Transaction("sell", stockSymbol, quantity, removeDate));
  }

  /**
   * Get the distribution of a portfolio.
   *
   * @param date - specified date.
   * @return a map representing the distribution of the portfolio.
   */
  public Map<String, Double> getDistribution(String date) {
    Map<String, Double> distribution = new HashMap<>();
    double totalValue = this.getValue(date);

    if (totalValue == 0) {
      throw new IllegalArgumentException("Total value of the portfolio is zero on the given date.");
    }

    for (Map.Entry<String, Double> entry : stocks.entrySet()) {
      String stockName = entry.getKey();
      double quantity = entry.getValue();

      Map<String, String> stockData = StockData.getStockDataWithFallback(stockName, date);

      if (stockData != null && stockData.containsKey("close")) {
        double closePrice = Double.parseDouble(stockData.get("close"));
        double stockValue = closePrice * quantity;
        double weight = stockValue / totalValue;
        distribution.put(stockName, weight);
      } else {
        System.out.println("No valid data for stock: " + stockName + " on date: " + date);
      }
    }
    return distribution;
  }


  @Override
  public void addTransaction(Transaction transaction) {
    this.transactionsLog.add(transaction);
  }

  @Override
  public List<Transaction> getTransactions() {
    return transactionsLog;
  }

  /**
   * get the total value of this portfolio.
   * @param date the date to get the value for.
   * @return a double represent the total value of the portfolio
   */
  public double getValue(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate checkDate = LocalDate.parse(date, formatter);

    double totalValue = 0.0;

    // Iterate over transactions in the transaction log
    for (Transaction transaction : transactionsLog) {
      LocalDate transactionDate = transaction.getDate();

      if (transactionDate.isBefore(checkDate) || transactionDate.isEqual(checkDate)) {
        // Get the stock data for the given date or fallback to the nearest available date
        Map<String, String> stockData =
                StockData.getStockDataWithFallback(transaction.getStockName(), date);
        double closePrice = Double.parseDouble(stockData.get("close"));

        totalValue += closePrice * transaction.getQuantity();
      }
    }
    return totalValue;
  }

  /**
   * Display different information for a portfolio.
   *
   * @param date the date user wants the price
   * @return a string representing the different information of a portfolio
   */
  public String displayAll(String date) {
    String composition = displayComposition();
    double value = 0.0;
    Map<String, Double> distribution = new HashMap<>();

    try {
      value = getValue(date);
      distribution = getDistribution(date);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }

    StringBuilder result = new StringBuilder();
    result.append("Composition: ").append(System.lineSeparator()).append(composition)
            .append(System.lineSeparator());
    result.append("Value: ").append(value).append(System.lineSeparator());
    result.append("Distribution: ").append(distribution.toString()).append(System.lineSeparator());

    return result.toString();
  }


  /**
   * Display the composition of the portfolio.
   *
   * @return a string representing the composition of the portfolio
   */
  protected String displayComposition() {
    StringBuilder output = new StringBuilder();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    for (Map.Entry<String, Double> entry : stocks.entrySet()) {
      String stockName = entry.getKey();
      double quantity = entry.getValue();

      output.append(stockName).append(": ").append(quantity).append(System.lineSeparator());
      for (Transaction transaction : transactionsLog) {
        if (transaction.getStockName().equals(stockName)) {
          String date = transaction.getDate().format(formatter);
          String type = transaction.getTransactionType();
          double transactionQuantity = transaction.getQuantity();
          output.append("\t(")
                  .append(date)
                  .append(", ")
                  .append(transactionQuantity)
                  .append(", ")
                  .append(type)
                  .append(")").append(System.lineSeparator());
        }
      }
    }
    return output.toString();
  }



  private boolean checkWeight(Map<String, Integer> weight) {
    int totalWeight = weight.values().stream().mapToInt(Integer::intValue).sum();
    return totalWeight == 100;
  }


  /**
   * if the weight and current value is different, balance them.
   * @param weight the weight the user given
   * @param date the date of the value
   */
  public void balanceValue(Map<String, Integer> weight, String date) {
    // Get total portfolio value on the specified date.
    double totalValue = this.getValue(date);

    if (!checkWeight(weight)) {
      throw new IllegalArgumentException("The total weight must sum to 100.");
    }


    Map<String, Double> stocks = new HashMap<>(getStocks());


    for (Map.Entry<String, Integer> entry : weight.entrySet()) {
      String stockName = entry.getKey();
      int weightPercentage = entry.getValue();
      if (!stocks.containsKey(stockName)) {
        throw new IllegalArgumentException("You do not have " + stockName
                + " stocks in your portfolio.");
      }


      Map<String, String> stockData = StockData.getStockData(stockName, date);
      double closePrice = Double.parseDouble(stockData.get("close"));

      double currentQuantity = stocks.get(stockName);
      double currentValue = closePrice * currentQuantity;

      double targetValue = totalValue * weightPercentage / 100.0;
      double difference = targetValue - currentValue;
      makeEqual(targetValue, currentValue, stockName, date);
    }
  }


  /**
   * make the old value update to a new value.
   * @param newValue the new value
   * @param oldValue the old value
   * @param stockName the name of the stock
   * @param date the date user want the price to be
   */
  private void makeEqual(double newValue, double oldValue, String stockName, String date) {
    double difference = newValue - oldValue;
    if (difference < 0) {
      // difference *= -1;
      sellLack(stockName, -difference, date);
    } else if (difference > 0) {
      buyLack(stockName, difference, date);
    }
  }


  /**
   * buy the stocks lack.
   * @param stockName the name of the stock
   * @param difference the difference
   * @param date the date the price
   */
  private void buyLack(String stockName, double difference, String date) {
    Map<String, String> stockData = StockData.getStockData(stockName, date);
    double closePrice = Double.parseDouble(stockData.get("close"));
    double stockNeeded = difference / closePrice;

    double currentQuantity = stocks.getOrDefault(stockName, 0.0);
    stocks.put(stockName, currentQuantity + stockNeeded);

    transactionsLog.add(new Transaction("buy", stockName, stockNeeded,
            LocalDate.parse(date)));
  }

  /**
   * Sell excess stocks to adjust the quantity to match the new desired value.
   *
   * @param stockName the name of the stock
   * @param difference the difference to adjust
   * @param date the date for which to perform the adjustment
   */
  private void sellLack(String stockName, double difference, String date) {
    Map<String, String> stockData = StockData.getStockData(stockName, date);
    double closePrice = Double.parseDouble(stockData.get("close"));
    double stockNeeded = difference / closePrice;

    double currentQuantity = stocks.getOrDefault(stockName, 0.0);
    if (currentQuantity < stockNeeded) {
      throw new IllegalArgumentException("Insufficient quantity of " + stockName
              + " stocks to sell.");
    }
    stocks.put(stockName, currentQuantity - stockNeeded);

    transactionsLog.add(new Transaction("sell", stockName, stockNeeded,
            LocalDate.parse(date)));
  }


  /**
   * draw a chart show the status of the portfolio.
   * @param startDate the start time of the period.
   * @param endDate the end time of the period.
   * @return a string diagram represent the status of the portfolio
   */
  public String chart(LocalDate startDate, LocalDate endDate) {
    StringBuilder output = new StringBuilder();
    String timestamps = determineTimestamp(startDate, endDate);
    int scale;

    // Determine the appropriate scale based on the time span
    switch (timestamps) {
      case "day":
        scale = getScaleForDays(startDate, endDate);
        break;
      case "week":
        scale = getScaleForWeeks(startDate, endDate);
        break;
      case "month":
        scale = getScaleForMonths(startDate, endDate);
        break;
      case "three months":
        scale = getScaleForThreeMonths(startDate, endDate);
        break;
      case "year":
        scale = getScaleForYears(startDate, endDate);
        break;
      default:
        throw new IllegalArgumentException("Invalid timestamp resolution");
    }

    // Header for the chart
    output.append("Performance of portfolio from ")
            .append(startDate)
            .append(" to ")
            .append(endDate)
            .append("\n\n");

    LocalDate currentDate = startDate;
    double lastValue = 0;

    while (!currentDate.isAfter(endDate)) {
      // Check if the current date is a non-trading day
      boolean nonTradingDay = isNonTradingDay(currentDate);

      // Get the portfolio value for the current date
      double value = getValue(currentDate.toString());

      // If it's a non-trading day, use the last available value
      if (nonTradingDay && lastValue != 0) {
        value = lastValue;
      } else if (!nonTradingDay) {
        lastValue = value;
      }

      // Calculate the number of stars based on the value and scale
      int starCount = (int) Math.round(value / scale);

      // Append the date and stars to the output
      output.append(currentDate)
              .append(": ")
              .append("*".repeat(starCount))
              .append("\n");

      // Increment the date based on the timestamp granularity
      switch (timestamps) {
        case "day":
          currentDate = currentDate.plusDays(1);
          break;
        case "week":
          currentDate = currentDate.plusWeeks(1);
          break;
        case "month":
          currentDate = currentDate.plusMonths(1);
          break;
        case "three months":
          currentDate = currentDate.plusMonths(3);
          break;
        case "year":
          currentDate = currentDate.plusYears(1);
          break;
        default:
          throw new IllegalArgumentException("Invalid timestamp resolution");
      }
    }

    // Add the scale information at the end of the chart
    output.append("\nScale: * = ")
            .append(scale)
            .append(" units");

    return output.toString();
  }




  private int getScale(LocalDate startDate, LocalDate endDate,
                       TemporalAdjuster endOfPeriodAdjuster,
                       TemporalUnit nextPeriodUnit) {
    double largestValue = 0;
    LocalDate currentDate = startDate;

    // Iterate through each period from start date to end date
    while (!currentDate.isAfter(endDate)) {
      // Skip non-trading days
      if (!isNonTradingDay(currentDate)) {
        // Get the end of the current period
        LocalDate endOfPeriod = currentDate.with(endOfPeriodAdjuster);
        // Ensure endOfPeriod does not go past the end date
        if (endOfPeriod.isAfter(endDate)) {
          endOfPeriod = endDate;
        }

        // Get the portfolio value for the end of the period
        double value = getValue(endOfPeriod.toString());
        if (value > largestValue) {
          largestValue = value;
        }
      }

      // Move to the next period
      currentDate = currentDate.plus(1, nextPeriodUnit);
    }

    // Determine the power of 10 that is less than or equal to the largest value
    int scalePower = (int) Math.floor(Math.log10(largestValue));
    // Determine the scale based on this power of 10
    int scale = (int) Math.pow(10, scalePower - 1);
    // Ensure the scale is at least 1 to avoid issues with very small values
    return Math.max(scale, 1);
  }


  private int getScaleForDays(LocalDate startDate, LocalDate endDate) {
    return getScale(startDate, endDate,
            TemporalAdjusters.ofDateAdjuster(d -> d),
            ChronoUnit.DAYS);
  }

  private int getScaleForWeeks(LocalDate startDate, LocalDate endDate) {
    // Adjust start date if it falls on a weekend
    if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
      startDate = startDate.minusDays(1); // Move to Friday
    } else if (startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
      startDate = startDate.minusDays(2); // Move to Friday
    }
    return getScale(startDate, endDate,
            TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY),
            ChronoUnit.WEEKS);
  }

  private int getScaleForMonths(LocalDate startDate, LocalDate endDate) {
    return getScale(startDate.withDayOfMonth(1), endDate,
            TemporalAdjusters.lastDayOfMonth(),
            ChronoUnit.MONTHS);
  }

  private int getScaleForThreeMonths(LocalDate startDate, LocalDate endDate) {
    return getScale(startDate.withDayOfMonth(1), endDate,
      date -> ((LocalDate) date).plusMonths(3)
                    .with(TemporalAdjusters.lastDayOfMonth()),
            ChronoUnit.MONTHS);
  }

  private int getScaleForYears(LocalDate startDate, LocalDate endDate) {
    return getScale(startDate.withDayOfYear(1), endDate,
            TemporalAdjusters.lastDayOfYear(),
            ChronoUnit.YEARS);
  }



  private String determineTimestamp(LocalDate startDate, LocalDate endDate) {
    long difference = ChronoUnit.DAYS.between(startDate, endDate);
    if (difference <= 0) {
      throw new IllegalArgumentException("please enter a valid date");
    } else if (difference < 5) {
      return "day";
    } else if (difference <= 30) {
      return "day";
    } else if (difference <= 60) {
      return "week";
    } else if (difference <= 360) {
      return "month";
    } else if (difference <= 3600) {
      return "three months";
    } else {
      return "year";
    }
  }

  // Mock method to check if the date is a non-trading day
  private boolean isNonTradingDay(LocalDate date) {
    // Check if the date is a weekend
    if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
      return true;
    }

    // Define known U.S. stock market holidays
    Set<LocalDate> holidays = new HashSet<>();
    int year = date.getYear();

    // New Year's Day (observed on the closest weekday if it falls on a weekend)
    holidays.add(LocalDate.of(year, Month.JANUARY, 1)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)));
    holidays.add(LocalDate.of(year, Month.JANUARY, 1)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

    // Martin Luther King Jr. Day (third Monday in January)
    holidays.add(LocalDate.of(year, Month.JANUARY, 1)
            .with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY)));

    // Presidents' Day (third Monday in February)
    holidays.add(LocalDate.of(year, Month.FEBRUARY, 1)
            .with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY)));

    // Memorial Day (last Monday in May)
    holidays.add(LocalDate.of(year, Month.MAY, 1)
            .with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY)));

    // Independence Day (observed on the closest weekday if it falls on a weekend)
    holidays.add(LocalDate.of(year, Month.JULY, 4)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)));
    holidays.add(LocalDate.of(year, Month.JULY, 4)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

    // Labor Day (first Monday in September)
    holidays.add(LocalDate.of(year, Month.SEPTEMBER, 1)
            .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)));

    // Thanksgiving Day (fourth Thursday in November)
    holidays.add(LocalDate.of(year, Month.NOVEMBER, 1)
            .with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.THURSDAY)));

    // Christmas Day (observed on the closest weekday if it falls on a weekend)
    holidays.add(LocalDate.of(year, Month.DECEMBER, 25)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)));
    holidays.add(LocalDate.of(year, Month.DECEMBER, 25)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

    // Check if the date is a known holiday
    return holidays.contains(date);
  }



}

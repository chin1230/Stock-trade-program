package stock;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This class represents the controller of an interactive stock application.
 */
public class StockController implements IController {
  private final StocksApp stocksApp;
  private final Appendable appendable;
  private final Readable readable;

  /**
   * public constructor.
   *
   * @param appendable - output.
   * @param readable   - input.
   * @param stocksApp  - representing a stockApp.
   */
  public StockController(Appendable appendable, Readable readable, StocksApp stocksApp) {
    if (appendable == null || readable == null || stocksApp == null) {
      throw new IllegalArgumentException("StocksApp, Readable or appendable is null.");
    }
    this.appendable = appendable;
    this.readable = readable;
    this.stocksApp = stocksApp;
  }

  /**
   * start the program's view.
   *
   * @param view represent the view
   */
  public void start(IView view) {
    view.displayMenu();
    run(view);
  }

  /**
   * run the program.
   *
   * @param view the view needed
   */
  protected void run(IView view) {
    Scanner sc = new Scanner(readable);
    boolean quit = false;

    while (!quit) {
      view.displayMessage("What would you like to do? " +
              "Please type in a number or quit.\n");
      String input = view.getUserInput();
      if (input.equals("quit") || input.equals("q")) {
        quit = true;
      } else {
        processCommand(input, sc, view);
      }
    }
    view.displayMessage("Thank you for using this program!");
  }

  /**
   * process the command.
   *
   * @param input - user type in instruction.
   * @param sc    - scanner.
   * @param view  - the view.
   */
  protected void processCommand(String input, Scanner sc, IView view) {
    switch (input) {
      case "1": // display stock
        showStockHelper(sc, view);
        view.displayMenu();
        break;
      case "2": // display portfolio
        showPortfolioHelper(sc, view);
        view.displayMenu();
        break;
      case "3": // buy stock.
        buyStockHelper(sc, view);
        view.displayMenu();
        break;
      case "4": // check gain or loss
        checkGainOrLossHelper(sc, view);
        view.displayMenu();
        break;
      case "5": // check the average of a stock in a certain time.
        checkAverageHelper(sc, view);
        view.displayMenu();
        break;
      case "6": //if a stock have a crossover over a period of time
        checkCrossoverHelper(sc, view);
        view.displayMenu();
        break;
      case "7": //create a portfolio
        createPortfolioHelper(sc, view);
        view.displayMenu();
        break;
      case "8": //see the value of a portfolio
        checkPortfolioValueHelper(sc, view);
        view.displayMenu();
        break;
      case "9": //add stock to a portfolio
        addStocksHelper(sc, view);
        view.displayMenu();
        break;
      case "10": //remove stock from a portfolio
        removeStocksHelper(sc, view);
        view.displayMenu();
        break;
      case "11": // Find composition, value and distribution of value of a por on a specified date.
        displayAllHelper(sc, view);
        view.displayMenu();
        break;
      case "12":
        reBalanceHelper(sc, view);
        view.displayMenu();
        break;
      case "13":
        chartHelper(sc, view);
        view.displayMenu();
        break;


      default:
        view.displayMessage("Undefined instruction: " + input);
        view.displayMenu();
        break;
    }
  }

  private void checkGainOrLossHelper(Scanner sc, IView view) {
    view.displayMessage("Enter ONE stock ticker (ex: AAPL, GOOG): ");
    String stockName = sc.nextLine();

    view.displayMessage("Enter the start time you want: ");

    String startingTime = this.getDate(sc, view);

    view.displayMessage("Enter the end time you want: ");

    String endingTime = this.getDate(sc, view);

    long gainOrLoss = stocksApp.seeGainOrLoss(stockName, startingTime, endingTime);
    view.displayMessage("Gain or Loss: " + gainOrLoss);
  }

  private void checkAverageHelper(Scanner sc, IView view) {
    view.displayMessage("Enter ONE stock ticker (ex: AAPL, GOOG): ");
    String stockName = sc.nextLine();

    String specificTime = this.getDate(sc, view);

    view.displayMessage("Enter number of days (cannot be negative or fractional): ");
    int days = sc.nextInt();
    sc.nextLine(); // consume newline

    double movingAverage = stocksApp.seeMovingAverage(stockName, specificTime, days);
    view.displayMessage("Moving Average: " + movingAverage);
  }

  private void checkCrossoverHelper(Scanner sc, IView view) {
    view.displayMessage("Enter stock ticker (ex: AAPL, GOOG): ");
    String stockName = sc.nextLine();

    view.displayMessage("Enter starting time you want: ");

    String startingTime = this.getDate(sc, view);

    view.displayMessage("Enter ending time you want: ");

    String endingTime = this.getDate(sc, view);

    view.displayMessage("Enter number of days (cannot be negative or fractional): ");
    int days = sc.nextInt();
    sc.nextLine(); // consume newline

    List<String> crossovers = stocksApp.crossOver(stockName, startingTime, endingTime, days);
    view.displayMessage("Crossovers: " + crossovers);
  }

  private void createPortfolioHelper(Scanner sc, IView view) {
    view.displayMessage("Enter portfolio name: ");
    String portfolioName = sc.nextLine();

    view.displayMessage("How many types of stocks in total you want to add? ");
    int quantity = sc.nextInt();
    sc.nextLine();  // Consume the leftover newline

    // List to hold transactions
    List<Transaction> transactions = new ArrayList<>();

    Map<String, Double> stocks = new HashMap<>();

    for (int i = 0; i < quantity; i++) {
      view.displayMessage("Enter ONE stock name, quantity and the date (yyyy-MM-dd) " +
              "you want to buy it "
              + "(comma-separated - ex: AAPL,20,2024-06-12): ");
      String stockInput = sc.nextLine();
      String[] stockData = stockInput.split(",");

      if (stockData.length != 3) {
        view.displayMessage("Invalid input format. Please try again.");
        i--;
        continue;
      }

      String stockName = stockData[0];
      double quantityBought = Double.parseDouble(stockData[1]);
      String date = stockData[2];
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate buyDate = LocalDate.parse(date, formatter);

      // Add to stocks map
      stocks.put(stockName, stocks.getOrDefault(stockName, 0.0) + quantityBought);

      // Create a transaction
      Transaction transaction = new Transaction("buy", stockName, quantityBought, buyDate);
      transactions.add(transaction);
    }
    // Create the portfolio with transactions
    stocksApp.createPortfolio(portfolioName, stocks, transactions);
    view.displayMessage("Portfolio created: " + portfolioName);
  }



  private void checkPortfolioValueHelper(Scanner sc, IView view) {
    view.displayMessage("Enter portfolio name: ");
    String portfolioName = sc.nextLine();

    view.displayMessage("You will now enter the date you want.");

    LocalDate specificDate = LocalDate.parse(this.getDate(sc, view));

    double value = this.stocksApp.getValue(portfolioName, specificDate.toString());
    view.displayMessage("Portfolio value on " + specificDate + ": " + value);
  }

  private void buyStockHelper(Scanner sc, IView view) {
    view.displayMessage("Enter one stock ticker (ex: AAPL, GOOG): ");
    String stockName = sc.nextLine();

    view.displayMessage("Enter specific quantity (cannot be fractional or negative): ");
    int specificQuantity = sc.nextInt();
    sc.nextLine(); // consume newline

    this.stocksApp.buyStock(stockName, specificQuantity);
    view.displayMessage("Buy successful! You have bought "
            + specificQuantity + "stocks of " + stockName);
  }

  private void showStockHelper(Scanner sc, IView view) {
    String stocks = this.stocksApp.displayStock();
    view.displayMessage("The stocks you have are: " + stocks);
  }

  private void showPortfolioHelper(Scanner sc, IView view) {
    String portfolios = this.stocksApp.displayPortfolio();
    view.displayMessage("Existing portfolios: " + portfolios);
  }

  private void addStocksHelper(Scanner sc, IView view) {
    view.displayMessage("The name of the portfolio is: ");
    String portfolioName = sc.nextLine();

    view.displayMessage("Enter ONE stock ticker (e.g. AAPL, GOOG): ");
    String stockName = sc.nextLine();

    view.displayMessage("The amount you want to add: ");
    double quantity = sc.nextInt();
    sc.nextLine();

    view.displayMessage("Enter specific date (yyyy-MM-dd): ");
    LocalDate specificDate = LocalDate.parse(this.getDate(sc, view));
    // LocalDate specificDate = LocalDate.parse(sc.nextLine());

    // Create a transaction
    Transaction transaction = new Transaction("buy", stockName, quantity, specificDate);

    // Add the transaction to the portfolio
    PortfolioTypes portfolio = PortfolioXMLHandler.loadPortfolioFromXML(portfolioName);
    if (portfolio != null) {
      portfolio.addTransaction(transaction);

      Map<String, Double> stocks = new HashMap<>();
      stocks.put(stockName, quantity);

      this.stocksApp.addStockToPortfolio(portfolioName, stocks, specificDate.toString());
      view.displayMessage("You have successfully added stocks!");

      PortfolioXMLHandler.saveOrUpdatePortfolioToXML(portfolioName, portfolio);
      view.displayMessage("You have successfully added transaction log!");
    } else {
      view.displayMessage("Portfolio not found: " + portfolioName);
    }

  }

  private void removeStocksHelper(Scanner sc, IView view) {
    view.displayMessage("The name of the portfolio is: ");
    String portfolioName = sc.nextLine();

    view.displayMessage("Enter ONE stock ticker that you want to remove "
            + "(ex: AAPL): ");
    String stockName = sc.nextLine();

    view.displayMessage("Amount you want to remove: ");
    double quantity = sc.nextInt();
    sc.nextLine(); // consume newline

    view.displayMessage("You will now to enter the date you want.");
    String specificDate = this.getDate(sc, view);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate specificDate1 = LocalDate.parse(specificDate, formatter);

    // LocalDate specificDate = LocalDate.parse(sc.nextLine());

    // Create a transaction
    Transaction transaction = new Transaction("sell", stockName, quantity, specificDate1);

    // Add the transaction to the portfolio
    PortfolioTypes portfolio = PortfolioXMLHandler.loadPortfolioFromXML(portfolioName);
    if (portfolio != null) {
      portfolio.addTransaction(transaction);

      Map<String, Double> stocks = new HashMap<>();
      stocks.put(stockName, quantity);

      this.stocksApp.removeStockFromPortfolio(portfolioName, stocks, specificDate);
      view.displayMessage("You have successfully removed stocks!");

      PortfolioXMLHandler.saveOrUpdatePortfolioToXML(portfolioName, portfolio);
      view.displayMessage("You have successfully added transaction log!");
    } else {
      view.displayMessage("Portfolio not found: " + portfolioName);
    }


  }


  private void displayAllHelper(Scanner sc, IView view) {
    view.displayMessage("Enter the name of the portfolio: ");
    String portfolioName = sc.nextLine();

    view.displayMessage("You will now to enter the date you want.");

    String specificDate = this.getDate(sc, view);

    String all = this.stocksApp.displayAll(portfolioName, specificDate);
    view.displayMessage("These are the information of the portfolio in a given day"
            + System.lineSeparator() + all);
  }

  private void reBalanceHelper(Scanner sc, IView view) {
    view.displayMessage("Enter the name of the portfolio: ");
    String portfolioName = sc.nextLine();

    view.displayMessage("You will now to enter the date you want.");
    String specificDate = this.getDate(sc, view);

    String portfolioInformation = this.stocksApp.displayCertainPortfolio(portfolioName);
    view.displayMessage("The stocks in this portfolios are: \n" + portfolioInformation);

    view.displayMessage("Enter the stock and weight you want: (eg: GOOG,25,AAPL,30. " +
            "The weight can only be integer.)");
    String[] stockData = sc.nextLine().split(",");

    Map<String, Integer> weight = new HashMap<>();
    for (int i = 0; i < stockData.length; i += 2) {
      weight.put(stockData[i], Integer.parseInt(stockData[i + 1]));
    }

    this.stocksApp.reBalance(portfolioName, weight, specificDate);
    view.displayMessage("You have successfully rebalanced stocks!");
  }

  private void chartHelper(Scanner sc, IView view) {
    view.displayMessage("Enter the name of the portfolio: ");
    String portfolioName = sc.nextLine();
    view.displayMessage("You will now to enter the START date you want.");
    LocalDate startDate = LocalDate.parse(this.getDate(sc, view));
    view.displayMessage("You will now to enter the END date you want.");
    LocalDate endDate = LocalDate.parse(this.getDate(sc, view));

    view.displayMessage(this.stocksApp.chart(portfolioName, startDate, endDate));
  }

  private String getDate(Scanner sc, IView view) {
    view.displayMessage("Enter the year you want: (eg: 2024)");
    String year = sc.nextLine();

    view.displayMessage("Enter the month you want: (eg: 03)");
    String month = sc.nextLine();

    view.displayMessage("Enter the day you want: (eg: 03)");
    String day = sc.nextLine();
    return year + "-" + month + "-" + day;
  }


  /**
   * get the appendable.
   * @return the appendable
   */
  private Appendable getAppendable() {
    return appendable;
  }


}

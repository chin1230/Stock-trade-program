package stock;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the controller of an interactive stock application.
 */
public class GuiController implements IController {
  private final StocksApp stocksApp;
  private final Appendable appendable;
  private IView view;

  private String currentAction;

  /**
   * Public constructor.
   *
   * @param appendable - output.
   * @param stocksApp  - representing a stockApp.
   */
  public GuiController(Appendable appendable, StocksApp stocksApp) {
    if (appendable == null || stocksApp == null) {
      throw new IllegalArgumentException("StocksApp or appendable is null.");
    }
    this.appendable = appendable;
    this.stocksApp = stocksApp;
  }

  /**
   * Starts the program's view.
   *
   * @param view represent the view
   */
  public void start(IView view) {
    this.view = view;
    view.displayMenu();
  }

  /**
   * Sets the current action to be performed.
   *
   * @param action the action to be set
   */
  public void setCurrentAction(String action) {
    this.currentAction = action;
  }

  /**
   * Handles the selection from the menu and sets the corresponding action.
   *
   * @param command the command selected from the menu
   */
  public void handleMenuSelection(String command) {
    switch (command) {
      case "1. Display portfolios":
        setCurrentAction("displayPortfolios");
        processInput(null);
        break;
      case "2. Create a portfolio":
        setCurrentAction("createPortfolio");
        displayCreatePortfolioDialog();
        break;
      case "3. Add stock to portfolio":
        setCurrentAction("addStockToPortfolio");
        displayAddStockDialog();
        break;
      case "4. Sell stock from portfolio":
        setCurrentAction("removeStockFromPortfolio");
        displayRemoveStockDialog();
        break;
      case "5. Display composition":
        setCurrentAction("displayPortfolioDetails");
        displayCompositionDialog();
        break;
      default:
        view.displayMessage("Invalid option");
        break;
    }
  }

  /**
   * Processes the input based on the current action.
   *
   * @param input the input to be processed
   */
  public void processInput(String input) {
    if (currentAction == null) {
      view.displayMessage("No action defined for this prompt.");
      return;
    }

    if (currentAction.equals("displayPortfolios")) {
      displayPortfolios();
    } else {
      view.displayMessage("No action defined for this prompt.");
    }
  }

  /**
   * Displays a dialog for creating a new portfolio.
   */
  private void displayCreatePortfolioDialog() {
    JTextField portfolioNameField = new JTextField();
    JTextField numberOfStocksField = new JTextField();

    Object[] fields = {
      "Portfolio Name:", portfolioNameField,
      "Number of Stocks:", numberOfStocksField
    };

    int result = JOptionPane.showConfirmDialog(null, fields,
            "Create Portfolio", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
      String portfolioName = portfolioNameField.getText();
      String numberOfStocks = numberOfStocksField.getText();
      int numStocks = Integer.parseInt(numberOfStocks);
      displayStockDetailsDialog(portfolioName, numStocks);
    }
  }

  /**
   * Displays a dialog for entering stock details for a new portfolio.
   *
   * @param portfolioName the name of the portfolio
   * @param numStocks     the number of stocks to be added to the portfolio
   */
  private void displayStockDetailsDialog(String portfolioName, int numStocks) {
    JPanel panel = new JPanel(new GridLayout(numStocks, 4, 10, 10));

    JTextField[] stockNames = new JTextField[numStocks];
    JTextField[] quantities = new JTextField[numStocks];
    JTextField[] dates = new JTextField[numStocks];

    for (int i = 0; i < numStocks; i++) {
      stockNames[i] = new JTextField();
      quantities[i] = new JTextField();
      dates[i] = new JTextField();
      panel.add(new JLabel("Stock Name:"));
      panel.add(stockNames[i]);
      panel.add(new JLabel("Quantity:"));
      panel.add(quantities[i]);
      panel.add(new JLabel("Date (yyyy-MM-dd):"));
      panel.add(dates[i]);
    }

    int result = JOptionPane.showConfirmDialog(null, panel,
            "Enter Stock Details", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
      for (int i = 0; i < numStocks; i++) {
        String stockName = stockNames[i].getText();
        String quantity = quantities[i].getText();
        String date = dates[i].getText();
        processCreatePortfolioStockInput(portfolioName, stockName, quantity, date,
                i == 0);
      }
      view.displayMessage("You have successfully created the portfolio with "
              + numStocks + " stocks.");
    }
  }

  /**
   * Displays a dialog for adding a stock to an existing portfolio.
   */
  private void displayAddStockDialog() {
    JTextField portfolioNameField = new JTextField();
    JTextField stockNameField = new JTextField();
    JTextField quantityField = new JTextField();
    JTextField dateField = new JTextField();

    Object[] fields = {
      "Portfolio Name:", portfolioNameField,
      "Stock Name:", stockNameField,
      "Quantity:", quantityField,
      "Date (yyyy-MM-dd):", dateField
    };

    int result = JOptionPane.showConfirmDialog(null, fields,
            "Add Stock to Portfolio", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
      String portfolioName = portfolioNameField.getText();
      String stockName = stockNameField.getText();
      String quantity = quantityField.getText();
      String date = dateField.getText();
      processAddStockInput(portfolioName, stockName, quantity, date);
      view.displayMessage("You have successfully added the stock to the portfolio.");
    }
  }

  /**
   * Displays a dialog for removing a stock from an existing portfolio.
   */
  private void displayRemoveStockDialog() {
    JTextField portfolioNameField = new JTextField();
    JTextField stockNameField = new JTextField();
    JTextField quantityField = new JTextField();
    JTextField dateField = new JTextField();

    Object[] fields = {
      "Portfolio Name:", portfolioNameField,
      "Stock Name:", stockNameField,
      "Quantity:", quantityField,
      "Date (yyyy-MM-dd):", dateField
    };

    int result = JOptionPane.showConfirmDialog(null, fields,
            "Remove Stock from Portfolio", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
      String portfolioName = portfolioNameField.getText();
      String stockName = stockNameField.getText();
      String quantity = quantityField.getText();
      String date = dateField.getText();
      processRemoveStockInput(portfolioName, stockName, quantity, date);
      view.displayMessage("You have successfully removed the stock from the portfolio.");
    }
  }

  /**
   * Displays a dialog for displaying the composition of a portfolio.
   */
  private void displayCompositionDialog() {
    JTextField portfolioNameField = new JTextField();
    JTextField dateField = new JTextField();

    Object[] fields = {
      "Portfolio Name:", portfolioNameField,
      "Date (yyyy-MM-dd):", dateField
    };

    int result = JOptionPane.showConfirmDialog(null, fields,
            "Display Portfolio Composition", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
      String portfolioName = portfolioNameField.getText();
      String date = dateField.getText();
      processDisplayCompositionInput(portfolioName, date);
      view.displayMessage("You have successfully displayed the portfolio composition.");
    }
  }

  /**
   * Processes the input for creating a portfolio with multiple stocks.
   *
   * @param portfolioName the name of the portfolio
   * @param stockName     the name of the stock
   * @param quantity      the quantity of the stock
   * @param date          the date of the transaction
   * @param isFirstStock  whether this is the first stock being added
   */
  public void processCreatePortfolioStockInput(String portfolioName, String stockName,
                                               String quantity, String date,
                                               boolean isFirstStock) {
    double quantityBought = Double.parseDouble(quantity);
    LocalDate buyDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    Map<String, Double> stock = new HashMap<>();
    stock.put(stockName, quantityBought);

    if (isFirstStock) {
      List<Transaction> transactions = new ArrayList<>();
      transactions.add(new Transaction("buy", stockName, quantityBought, buyDate));
      this.stocksApp.createPortfolio(portfolioName, stock, transactions);
    }
    else {
      this.stocksApp.addStockToPortfolio(portfolioName, stock, buyDate.toString());
    }
  }


  /**
   * Processes the input for adding a stock to an existing portfolio.
   *
   * @param portfolioName the name of the portfolio
   * @param stockName     the name of the stock
   * @param quantity      the quantity of the stock
   * @param date          the date of the transaction
   */
  public void processAddStockInput(String portfolioName, String stockName, String quantity,
                                   String date) {
    double quantityToAdd = Double.parseDouble(quantity);
    LocalDate transactionDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    Map<String, Double> stock = new HashMap<>();
    stock.put(stockName, quantityToAdd);

    this.stocksApp.addStockToPortfolio(portfolioName, stock, transactionDate.toString());
  }

  /**
   * Processes the input for removing a stock from an existing portfolio.
   *
   * @param portfolioName the name of the portfolio
   * @param stockName     the name of the stock
   * @param quantity      the quantity of the stock
   * @param date          the date of the transaction
   */
  public void processRemoveStockInput(String portfolioName, String stockName, String quantity,
                                      String date) {
    double quantityToRemove = Double.parseDouble(quantity);

    int intQuantity = Integer.parseInt(quantity);

    LocalDate transactionDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    Map<String, Double> stock = new HashMap<>();
    stock.put(stockName, quantityToRemove);
    // Create a transaction
    Transaction transaction = new Transaction("sell", stockName, intQuantity, transactionDate);
    // Add the transaction to the portfolio
    PortfolioTypes portfolio = PortfolioXMLHandler.loadPortfolioFromXML(portfolioName);
    if (portfolio != null) {
      portfolio.addTransaction(transaction);

      Map<String, Double> stocks = new HashMap<>();
      stocks.put(stockName, quantityToRemove);

      this.stocksApp.removeStockFromPortfolio(portfolioName, stocks, transactionDate.toString());
    }
  }

  /**
   * Processes the input for displaying the composition of a portfolio.
   *
   * @param portfolioName the name of the portfolio
   * @param date          the date for which the composition is to be displayed
   */
  public void processDisplayCompositionInput(String portfolioName, String date) {
    LocalDate specificDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String composition = this.stocksApp.displayAll(portfolioName, specificDate.toString());
    view.displayMessage("Portfolio composition on " + date + ":\n" + composition);
  }

  /**
   * Displays the existing portfolios.
   */
  public void displayPortfolios() {
    String portfolios = this.stocksApp.displayPortfolio();
    view.displayMessage("Existing portfolios: " + portfolios);
  }

  /**
   * Changes the appendable object.
   *
   * @return the updated appendable object
   */
  private Appendable changeAppendable() {
    return this.appendable;
  }
}

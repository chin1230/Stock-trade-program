package stock;

import java.time.LocalDate;

/**
 * This class represents a transaction in the stock management system.
 * A transaction can be either a buy or sell operation involving a specific stock.
 */
public class Transaction {
  private String transactionType; // "buy" or "sell"
  private String stockName;
  private double quantity;
  private LocalDate date;

  /**
   * Constructs a Transaction object with the specified details.
   *
   * @param transactionType the type of the transaction ("buy" or "sell")
   * @param stockName the name of the stock involved in the transaction
   * @param quantity the quantity of stock involved in the transaction
   * @param date the date of the transaction
   */
  public Transaction(String transactionType, String stockName, double quantity, LocalDate date) {
    this.transactionType = transactionType;
    this.stockName = stockName;
    this.quantity = quantity;
    this.date = date;
  }

  /**
   * Gets the type of the transaction.
   *
   * @return the transaction type ("buy" or "sell")
   */
  public String getTransactionType() {
    return transactionType;
  }

  /**
   * Gets the name of the stock involved in the transaction.
   *
   * @return the stock name
   */
  public String getStockName() {
    return stockName;
  }

  /**
   * Gets the quantity of stock involved in the transaction.
   *
   * @return the quantity of stock
   */
  public double getQuantity() {
    return quantity;
  }

  /**
   * Gets the date of the transaction.
   *
   * @return the transaction date
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * Sets the type of the transaction.
   *
   * @param transactionType the transaction type ("buy" or "sell")
   */
  public void setTransactionType(String transactionType) {
    this.transactionType = transactionType;
  }

  /**
   * Sets the name of the stock involved in the transaction.
   *
   * @param stockName the stock name
   */
  public void setStockName(String stockName) {
    this.stockName = stockName;
  }

  /**
   * Sets the quantity of stock involved in the transaction.
   *
   * @param quantity the quantity of stock
   */
  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  /**
   * Sets the date of the transaction.
   *
   * @param date the transaction date
   */
  public void setDate(LocalDate date) {
    this.date = date;
  }

  /**
   * Returns a string representation of the transaction.
   *
   * @return a string representation of the transaction
   */
  @Override
  public String toString() {
    return "Transaction{" + transactionType + quantity + " of "
            + stockName + " stocks on " + date + "}";
  }
}

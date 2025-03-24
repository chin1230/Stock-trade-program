import org.junit.Before;
import org.junit.Test;
import stock.Transaction;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * test for transaction.
 */
public class TransactionTest {

  private Transaction transaction;

  @Before
  public void setUp() {
    transaction = new Transaction("buy", "AAPL", 10.0, LocalDate.of(2023, 6, 1));
  }

  @Test
  public void testGetTransactionType() {
    assertEquals("buy", transaction.getTransactionType());
  }

  @Test
  public void testGetStockName() {
    assertEquals("AAPL", transaction.getStockName());
  }

  @Test
  public void testGetQuantity() {
    assertEquals(10.0, transaction.getQuantity(), 0.001);
  }

  @Test
  public void testGetDate() {
    assertEquals(LocalDate.of(2023, 6, 1), transaction.getDate());
  }

  @Test
  public void testSetTransactionType() {
    transaction.setTransactionType("sell");
    assertEquals("sell", transaction.getTransactionType());
  }

  @Test
  public void testSetStockName() {
    transaction.setStockName("GOOG");
    assertEquals("GOOG", transaction.getStockName());
  }

  @Test
  public void testSetQuantity() {
    transaction.setQuantity(20.0);
    assertEquals(20.0, transaction.getQuantity(), 0.001);
  }

  @Test
  public void testSetDate() {
    transaction.setDate(LocalDate.of(2023, 6, 2));
    assertEquals(LocalDate.of(2023, 6, 2), transaction.getDate());
  }

  @Test
  public void testToString() {
    String expectedString = "Transaction{buy10.0 of AAPL stocks on 2023-06-01}";
    assertEquals(expectedString, transaction.toString());
  }
}

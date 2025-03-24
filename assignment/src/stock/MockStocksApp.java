package stock;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create mock class for testing.
 */
public class MockStocksApp implements StocksApp {
  private Map<String, PortfolioTypes> portfolios = new HashMap<>();


  @Override
  public void buyStock(String name, double quantity) {
    this.portfolios = portfolios;
  }

  @Override
  public long seeGainOrLoss(String stockName, String startingDate, String endingDate) {
    return 0;
  }

  @Override
  public double seeMovingAverage(String stockName, String specificDate, int days) {
    return 0;
  }

  @Override
  public List<String> crossOver(String stockName, String startingDate, String endDate, int days) {
    return List.of();
  }

  @Override
  public void createPortfolio(String portfolioName, Map<String, Double> stocks,
                              List<Transaction> transactions) {
    PortfolioTypes portfolio = new Portfolio();
    portfolios.put(portfolioName, portfolio);
  }

  @Override
  public void addStockToPortfolio(String portfolioName, Map<String, Double> stocks,
                                  String transactionDate) {
    PortfolioTypes portfolio = portfolios.get(portfolioName);
    if (portfolio != null) {
      portfolio.addStock("AAPL", transactionDate, 1);
    }
  }

  @Override
  public void removeStockFromPortfolio(String portfolioName, Map<String, Double> stocks,
                                       String transactionDate) {
    PortfolioTypes portfolio = portfolios.get(portfolioName);
    if (portfolio != null) {
      for (Map.Entry<String, Double> entry : stocks.entrySet()) {
        String stockName = entry.getKey();
        int quantity = entry.getValue().intValue();
        portfolio.removeStock(stockName, transactionDate, quantity);
      }
    }
  }

  @Override
  public double getValue(String name, String date) {
    return 0;
  }

  @Override
  public Map<String, Double> getDistribution(String portfolioName, String date) {
    return Map.of();
  }

  @Override
  public String displayAll(String portfolioName, String date) {
    PortfolioTypes portfolio = portfolios.get(portfolioName);
    if (portfolio != null) {
      return portfolio.displayAll(date);
    }
    return "Portfolio not found";
  }

  @Override
  public String displayStock() {
    return "";
  }

  @Override
  public String displayPortfolio() {
    return String.join(", ", portfolios.keySet());
  }

  @Override
  public String displayCertainPortfolio(String portfolioName) {
    return "";
  }

  @Override
  public void reBalance(String porName, Map<String, Integer> weight, String date) {
    this.portfolios = portfolios;
  }

  @Override
  public String chart(String portfolioName, LocalDate startDate, LocalDate endDate) {
    return "";
  }

  @Override
  public void saveTransactions(String portfolioName, List<Transaction> transactions) {
    this.portfolios = portfolios;
  }

  public Map<String, PortfolioTypes> getPortfolios() {
    return portfolios;
  }
}

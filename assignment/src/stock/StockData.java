package stock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * store the stock data from the API.
 */
public class StockData implements IData {
  private static final Set<String> validTickersCache = new HashSet<>();
  static String apiKey = "4UG9PSAUSGKVJJHD";
  static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Determine if the ticker is valid.
   *
   * @param ticker - stock ticker.
   * @return true if the ticker is valid, false otherwise.
   */
  public static boolean isValidStockTicker(String ticker) {
    if (validTickersCache.contains(ticker)) {
      return true;
    }

    File file = new File(ticker + ".csv");
    if (file.exists()) {
      return true;
    }

    try {
      URL url = new URL(
              "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords="
                      + ticker + "&apikey=" + apiKey);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setConnectTimeout(100); // Set timeout
      conn.setReadTimeout(100);

      int status = conn.getResponseCode();
      if (status != 200) {
        return false; // API call failed, ticker is invalid
      }

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(
              conn.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          // If the response contains the ticker symbol, it is valid
          if (line.contains("\"1. symbol\": \"" + ticker + "\"")) {
            validTickersCache.add(ticker); // Cache the valid ticker
            return true;
          }
        }
      }
    } catch (IOException e) {
      // Handle case where API call fails
      return false;
    }
    return false; // Ticker is not valid if not found in response
  }

  /**
   * fetch the data from the API and store it as a csv files.
   *
   * @param stockSymbol - stock ticker.
   */
  public static void fetchDataAndSaveToCSV(String stockSymbol) {
    if (!isValidStockTicker(stockSymbol)) {
      throw new IllegalArgumentException("Invalid stock ticker: " + stockSymbol);
    }

    URL url;
    try {
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_DAILY"
              + "&outputsize=full"
              + "&symbol=" + stockSymbol + "&apikey=" + apiKey + "&datatype=csv");

    } catch (MalformedURLException e) {
      throw new RuntimeException("the AlphaVantage API has either changed or "
              + "no longer works", e);
    }

    try (InputStream in = url.openStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         BufferedWriter writer = new BufferedWriter(
                 new FileWriter(stockSymbol + ".csv"))) {

      String line;
      while ((line = reader.readLine()) != null) {
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to fetch or save data for " + stockSymbol, e);
    }
  }

  /**
   * read data from the csv or fetch from the api.
   *
   * @param stockSymbol - stock ticker.
   * @param date        - specific date we want stock data of.
   * @return a Map.
   */
  public static Map<String, String> getStockData(String stockSymbol, String date) {
    // check if csv files of that stock exists yet.
    // if not, then fetch data from api and save it as a new csv file.
    File file = new File(stockSymbol + ".csv");
    if (!file.exists()) {
      fetchDataAndSaveToCSV(stockSymbol);
    }

    Map<String, String> stockValues = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      boolean header = true;

      while ((line = reader.readLine()) != null) {
        if (header) {
          header = false;
          continue;
        }

        String[] data = line.split(",");
        if (data[0].equals(date)) {
          stockValues.put("date", data[0]);
          stockValues.put("open", data[1]);
          stockValues.put("high", data[2]);
          stockValues.put("low", data[3]);
          stockValues.put("close", data[4]);
          stockValues.put("volume", data[5]);
          break;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read data for " + stockSymbol, e);
    }
    return stockValues;
  }

  /**
   * get data in period.
   *
   * @param stockSymbol - stock ticker.
   * @param startDate   - starting date of the period.
   * @param endDate     - ending date of the period.
   * @return a map represent the stock during a period
   */
  public static Map<LocalDate, Map<String, String>> getStockDataInPeriod(
          String stockSymbol, String startDate, String endDate) {

    // check if csv file for this stock exists.
    File file = new File(stockSymbol + ".csv");
    if (!file.exists()) {
      fetchDataAndSaveToCSV(stockSymbol);
    }

    // initialize map.
    Map<LocalDate, Map<String, String>> periodStockValues = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

      String line;
      boolean header = true;
      LocalDate start = LocalDate.parse(startDate, formatter);
      LocalDate end = LocalDate.parse(endDate, formatter);

      while ((line = reader.readLine()) != null) {
        if (header) {
          header = false;
          continue;
        }

        String[] data = line.split(",");
        LocalDate date = LocalDate.parse(data[0], formatter);

        if (!date.isBefore(start) && !date.isAfter(end)) {
          Map<String, String> stockValues = new HashMap<>();
          stockValues.put("open", data[1]);
          stockValues.put("high", data[2]);
          stockValues.put("low", data[3]);
          stockValues.put("close", data[4]);
          stockValues.put("volume", data[5]);
          periodStockValues.put(date, stockValues);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read data for " + stockSymbol, e);
    }
    return periodStockValues;
  }

  /**
   * check if the data is up-to-date.
   *
   * @param stockSymbol - stock ticker.
   * @return a boolean indicating if the csv file is up-to-date for specific stock.
   */
  public static boolean isDataUpToDate(String stockSymbol) {
    File file = new File(stockSymbol + ".csv");
    if (!file.exists()) {
      return false;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      boolean header = true;

      while ((line = reader.readLine()) != null) {
        if (header) {
          header = false;
          continue;
        }

        String[] data = line.split(",");
        LocalDate lastDate = LocalDate.parse(data[0], formatter);
        if (!lastDate.isBefore(LocalDate.now().minusDays(1))) {
          return true;
        }
        break;
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read data for " + stockSymbol, e);
    }
    return false;
  }

  /**
   * Check if stock data is available for a given date.
   *
   * @param stockSymbol The stock ticker symbol.
   * @param date The date to check.
   * @return True if data is available, false otherwise.
   */
  public static boolean isDataAvailable(String stockSymbol, String date) {
    Map<String, String> stockData = getStockData(stockSymbol, date);
    return stockData != null && !stockData.isEmpty();
  }

  /**
   * Find the nearest available date for stock data.
   *
   * @param stockSymbol The stock ticker symbol.
   * @param date The date to start checking from.
   * @return The nearest available date as a string.
   */
  public static String findNearestAvailableDate(String stockSymbol, String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate localDate = LocalDate.parse(date, formatter);

    // Check up to 7 days before and after the given date
    for (int i = 0; i <= 7; i++) {
      LocalDate previousDate = localDate.minusDays(i);
      LocalDate nextDate = localDate.plusDays(i);

      if (isDataAvailable(stockSymbol, previousDate.toString())) {
        return previousDate.toString();
      }

      if (isDataAvailable(stockSymbol, nextDate.toString())) {
        return nextDate.toString();
      }
    }

    // If no data is found within the range, return null or handle accordingly
    return null;
  }

  /**
   * Fetch stock data for a given date, with a fallback to the nearest available date.
   *
   * @param stockSymbol The stock ticker symbol.
   * @param date The date to fetch data for.
   * @return The stock data map for the given date or nearest available date.
   */
  public static Map<String, String> getStockDataWithFallback(String stockSymbol, String date) {
    if (isDataAvailable(stockSymbol, date)) {
      return getStockData(stockSymbol, date);
    } else {
      String nearestDate = findNearestAvailableDate(stockSymbol, date);
      // if the nearest date is found, suggest it to the user and fetch data for that date.
      return nearestDate != null ? getStockData(stockSymbol, nearestDate) : null;
    }
  }

}

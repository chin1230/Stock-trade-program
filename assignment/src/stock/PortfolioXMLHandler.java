package stock;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static stock.PortfolioManager.logger;


/**
 * This class provides functionality to save and load portfolio data to and from XML files.
 */
public class PortfolioXMLHandler {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Saves the given portfolio to an XML file with the given portfolio name.
   *
   * @param portfolioName the name of the portfolio to save.
   * @param portfolio the portfolio object containing the data to save.
   */
  public static void savePortfolioToXML(String portfolioName, PortfolioTypes portfolio) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.newDocument();

      Element rootElement = doc.createElement("Portfolio");
      doc.appendChild(rootElement);

      Element stocksElement = doc.createElement("Stocks");
      //new
      rootElement.appendChild(stocksElement);

      for (Map.Entry<String, Double> stockEntry : portfolio.getStocks().entrySet()) {
        Element stock = doc.createElement("Stock");

        Element name = doc.createElement("Name");
        name.appendChild(doc.createTextNode(stockEntry.getKey()));
        stock.appendChild(name);

        Element quantityElement = doc.createElement("Quantity");
        quantityElement.appendChild(doc.createTextNode(stockEntry.getValue().toString()));
        stock.appendChild(quantityElement);

        stocksElement.appendChild(stock);
      }

      // Add transaction log
      Element transactionsElement = doc.createElement("Transactions");
      rootElement.appendChild(transactionsElement);

      for (Transaction transaction : portfolio.getTransactions()) {
        Element transactionElement = doc.createElement("Transaction");

        Element typeElement = doc.createElement("Type");
        typeElement.appendChild(doc.createTextNode(transaction.getTransactionType()));
        transactionElement.appendChild(typeElement);

        Element stockNameElement = doc.createElement("StockName");
        stockNameElement.appendChild(doc.createTextNode(transaction.getStockName()));
        transactionElement.appendChild(stockNameElement);

        Element quantityElement = doc.createElement("Quantity");
        quantityElement.appendChild(doc.createTextNode(String.valueOf(transaction.getQuantity())));
        transactionElement.appendChild(quantityElement);

        Element dateElement = doc.createElement("Date");
        dateElement.appendChild(doc.createTextNode(transaction.getDate()
                .format(DateTimeFormatter.ISO_DATE)));
        transactionElement.appendChild(dateElement);

        transactionsElement.appendChild(transactionElement);
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      StreamResult result = new StreamResult(portfolioName + ".xml");
      transformer.transform(source, result);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error saving portfolio to XML", e);
    }
  }

  /**
   * Loads a portfolio from an XML file with the given portfolio name.
   *
   * @param portfolioName the name of the portfolio to load.
   * @return the portfolio object containing the loaded data, or null if an error occurs.
   */
  public static PortfolioTypes loadPortfolioFromXML(String portfolioName) {
    Map<String, Double> stocks = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();

    try {
      File file = new File(portfolioName + ".xml");
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(file);
      doc.getDocumentElement().normalize();

      // Load stocks
      NodeList stockList = doc.getElementsByTagName("Stock");
      for (int i = 0; i < stockList.getLength(); i++) {
        Node stockNode = stockList.item(i);
        if (stockNode.getNodeType() == Node.ELEMENT_NODE) {
          Element stockElement = (Element) stockNode;
          String stockName = stockElement.getElementsByTagName("Name").item(0)
                  .getTextContent();
          double quantity = Double.parseDouble(stockElement.getElementsByTagName("Quantity")
                  .item(0).getTextContent());
          stocks.put(stockName, quantity);

        }
      }

      NodeList transactionList = doc.getElementsByTagName("Transaction");
      for (int i = 0; i < transactionList.getLength(); i++) {
        Node transactionNode = transactionList.item(i);
        if (transactionNode.getNodeType() == Node.ELEMENT_NODE) {
          Element transactionElement = (Element) transactionNode;
          String type = transactionElement.getElementsByTagName("Type").item(0)
                  .getTextContent();
          String stockName = transactionElement.getElementsByTagName("StockName").item(0)
                  .getTextContent();
          double quantity = Double.parseDouble(transactionElement.getElementsByTagName("Quantity")
                  .item(0).getTextContent());
          LocalDate date = LocalDate.parse(transactionElement.getElementsByTagName("Date")
                  .item(0).getTextContent(), DATE_FORMATTER);
          transactions.add(new Transaction(type, stockName, quantity, date));
        }
      }

      return new Portfolio(stocks, transactions);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error while loading portfolio from XML", e);
      return null;
    }
  }

  /**
   * Saves or updates the given portfolio to an XML file with the given portfolio name.
   *
   * @param portfolioName the name of the portfolio to save or update.
   * @param portfolio the portfolio object containing the data to save or update.
   */
  public static void saveOrUpdatePortfolioToXML(String portfolioName, PortfolioTypes portfolio) {
    try {
      File xmlFile = new File(portfolioName + ".xml");
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc;

      if (xmlFile.exists()) {
        doc = builder.parse(xmlFile);
      } else {
        doc = builder.newDocument();
        Element rootElement = doc.createElement("Portfolio");
        doc.appendChild(rootElement);
      }

      Element rootElement = doc.getDocumentElement();

      // Clear existing Stocks and Transactions elements
      NodeList stocksList = rootElement.getElementsByTagName("Stocks");
      if (stocksList.getLength() > 0) {
        rootElement.removeChild(stocksList.item(0));
      }
      Element stocksElement = doc.createElement("Stocks");
      rootElement.appendChild(stocksElement);

      NodeList transactionsList = rootElement.getElementsByTagName("Transactions");
      if (transactionsList.getLength() > 0) {
        rootElement.removeChild(transactionsList.item(0));
      }
      Element transactionsElement = doc.createElement("Transactions");
      rootElement.appendChild(transactionsElement);

      // Add updated stock quantities to XML
      for (Map.Entry<String, Double> entry : portfolio.getStocks().entrySet()) {
        Element stock = doc.createElement("Stock");

        Element name = doc.createElement("Name");
        name.appendChild(doc.createTextNode(entry.getKey()));
        stock.appendChild(name);

        Element quantity = doc.createElement("Quantity");
        quantity.appendChild(doc.createTextNode(entry.getValue().toString()));
        stock.appendChild(quantity);

        stocksElement.appendChild(stock);
      }

      // Add transactions to XML
      Set<Transaction> uniqueTransactions = new HashSet<>(portfolio.getTransactions());
      for (Transaction transaction : portfolio.getTransactions()) {
        Element transactionElement = doc.createElement("Transaction");

        Element typeElement = doc.createElement("Type");
        typeElement.appendChild(doc.createTextNode(transaction.getTransactionType()));
        transactionElement.appendChild(typeElement);

        Element stockNameElement = doc.createElement("StockName");
        stockNameElement.appendChild(doc.createTextNode(transaction.getStockName()));
        transactionElement.appendChild(stockNameElement);

        Element quantityElement = doc.createElement("Quantity");
        quantityElement.appendChild(doc.createTextNode(String.valueOf(transaction.getQuantity())));
        transactionElement.appendChild(quantityElement);

        Element dateElement = doc.createElement("Date");
        dateElement.appendChild(doc.createTextNode(transaction.getDate().toString()));
        transactionElement.appendChild(dateElement);

        transactionsElement.appendChild(transactionElement);
      }

      // Write the updated document to XML file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(xmlFile);
      transformer.transform(source, result);

    } catch (Exception e) {
      e.printStackTrace();
      logger.log(Level.SEVERE, "Error saving or updating portfolio to XML", e);
    }
  }




}

package stock;


import java.io.InputStreamReader;

/**
 * The main entry point for the Stock Management System application.
 * This class initializes the StocksApp, the GUI controller,
 * and the GUI view, and starts the application.
 */
public class StockProgram {

  /**
   * The main method which serves as the entry point for the application.
   *
   * @param args command-line arguments (not used).
   */
  public static void main(String[] args) {
    // Check if there are command-line arguments
    if (args.length == 0) {
      // No arguments provided, launch GUI
      launchGUI();
    } else if (args.length == 1 && args[0].equals("-text")) {
      launchTextInterface();
    } else {
      // Invalid arguments
      System.err.println("Invalid command-line arguments.");
      System.err.println("Usage:");
      System.err.println("  java -jar Program.jar : Launch GUI");
      System.err.println("  java -jar Program.jar -text : Launch text-based interface");
      System.exit(1);
    }
  }

  private static void launchGUI() {
    StocksApp app = new Account();
    GuiView view = new GuiView();
    GuiController controller = new GuiController(System.out, app);

    view.setStockController(controller);
    controller.start(view);
  }

  private static void launchTextInterface() {
    StocksApp app = new Account();
    Readable rd = new InputStreamReader(System.in);
    Appendable ap = System.out;

    StockView view = new StockView();
    StockController controller = new StockController(ap, rd, app);
    
    view.setStockController(controller);
    controller.start(view);
  }
}


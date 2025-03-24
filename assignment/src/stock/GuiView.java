package stock;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * This class represents the graphical user interface (GUI) for the stock management system.
 */
public class GuiView extends JFrame implements IView {
  private GuiController stockController;

  /**
   * Constructs a new GuiView.
   */
  public GuiView() {
    setTitle("Stock Management System");
    setSize(800, 400); // Adjusted size to better fit the grid
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // Initialize UI components
    initMenuPanel();
  }

  /**
   * Initializes the menu panel with buttons for various actions.
   */
  private void initMenuPanel() {
    JPanel menuPanel = new JPanel();
    menuPanel.setLayout(new GridLayout(2, 3, 10, 10)); // 2x3 grid with gaps

    JLabel menuLabel = new JLabel("Choose an option:", SwingConstants.CENTER);
    menuLabel.setFont(new Font("Arial", Font.BOLD, 18));
    add(menuLabel, BorderLayout.NORTH);

    String[] options = {
      "Display portfolios",
      "Create a portfolio",
      "Add stock to portfolio",
      "Sell stock from portfolio",
      "Display composition"
    };

    for (int i = 0; i < options.length; i++) {
      JButton button = new JButton((i + 1) + ". " + options[i]);
      button.setFont(new Font("Arial", Font.PLAIN, 14)); // Adjust font size for readability
      button.setPreferredSize(new Dimension(200, 60)); // Ensure buttons have uniform size
      button.addActionListener(new MenuButtonListener());
      menuPanel.add(button);
    }

    // Fill the remaining grid cells with empty panels to complete the 2x2 layout
    for (int i = options.length; i < 4; i++) {
      menuPanel.add(new JPanel());
    }

    add(menuPanel, BorderLayout.CENTER);
  }

  /**
   * Sets the stock controller.
   *
   * @param stockController the stock controller to set
   */
  public void setStockController(GuiController stockController) {
    this.stockController = stockController;
  }

  /**
   * Displays the menu.
   */
  @Override
  public void displayMenu() {
    SwingUtilities.invokeLater(() -> setVisible(true));
  }

  /**
   * Sets a prompt message.
   *
   * @param prompt the prompt message to set
   */
  @Override
  public void setPrompt(String prompt) {
    // Not used in this implementation
  }

  /**
   * Gets user input.
   *
   * @return an empty string as this implementation does not get user input directly
   */
  @Override
  public String getUserInput() {
    return "";
  }

  /**
   * Displays a message to the user.
   *
   * @param message the message to display
   */
  public void displayMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

  /**
   * Action listener for menu buttons.
   */
  private class MenuButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      JButton source = (JButton) e.getSource();
      String command = source.getText();

      if (stockController != null) {
        stockController.handleMenuSelection(command);
      } else {
        displayMessage("StockController is not set.");
      }
    }
  }
}

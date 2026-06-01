import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel containerPanel;
    private DashboardScreen dashboardScreen;

    public Main() {
        setTitle("The Luminous Grand Hotel - Management System");
        setSize(1200, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        // Instantiate individual screen components
        LoginScreen loginScreen = new LoginScreen(this);
        dashboardScreen = new DashboardScreen();

        // Register panels into Card Controller
        containerPanel.add(loginScreen, "LOGIN_STAGE");
        containerPanel.add(dashboardScreen, "DASHBOARD_STAGE");

        add(containerPanel);
        cardLayout.show(containerPanel, "LOGIN_STAGE"); // Default to landing portal
    }

    // Callback used by LoginScreen to switch screens upon verification
    public void navigateToDashboard() {
        cardLayout.show(containerPanel, "DASHBOARD_STAGE");
        dashboardScreen.loadRuntimeData(); // Populates database elements on view swap
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
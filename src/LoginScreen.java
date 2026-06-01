import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoginScreen extends JPanel {

    private final Main controller;

    // --- ✨ Forced Warm Gold-Beige Tint ---
    private final Color COLOR_CARD_BG = new Color(222, 192, 153);    // Warm Amber-Tan / Darker Sand
    private final Color COLOR_BUTTON_BG = new Color(153, 91, 18);    // Rich Burnished Bronze for Depth
    private final Color COLOR_INPUT_BG = new Color(201, 171, 131);   // Deep Contrast Inset Tan for Fields
    private final Color COLOR_TEXT_DARK = new Color(46, 32, 23);     // Espresso Black for Maximum Text Sharpness
    private final Color COLOR_TEXT_MUTED = new Color(110, 89, 71);   // Soft Muted Soil Brown for Subtitles

    public LoginScreen(Main controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());

        // Custom JPanel class for a SOLID light card with ROUNDED CORNERS and a DOUBLE BLACK BORDER
        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 30; // Corner radius

                // 1. Draw the solid background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // 2. Draw the Outer Black Line (Thickness: 2px)
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, arc, arc);

                // 3. Draw the Inner Black Line offset by 3 pixels (Thickness: 1.5px)
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(4, 4, w - 8, h - 8, arc - 4, arc - 4);

                g2.dispose();
            }
        };

        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(COLOR_CARD_BG);
        loginCard.setOpaque(false); // Set to false so the sharp default background edges disappear!
        loginCard.setBorder(new EmptyBorder(40, 45, 40, 45));
        loginCard.setPreferredSize(new Dimension(380, 440));

        // Typography Headers
        JLabel lblHotelName = new JLabel("THE LUMINOUS HOTEL") {{
            setFont(new Font("Georgia", Font.BOLD, 18));
            setForeground(COLOR_BUTTON_BG);
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }};
        JLabel lblTerminal = new JLabel("Where Luxury Meets Perfection") {{
            setFont(new Font("Franklin Gothic Demi Cond", Font.ITALIC, 12));
            setForeground(COLOR_TEXT_MUTED);
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }};

        // Credentials Input Fields (Centered Alignment)
        JLabel lblUser = new JLabel("Username:") {{
            setForeground(COLOR_TEXT_DARK);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }};
        JTextField txtUser = new JTextField();
        styleLoginFields(txtUser);

        // Password Input Field
        JLabel lblPass = new JLabel("Passcode:") {{
            setForeground(COLOR_TEXT_DARK);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }};
        JPasswordField txtPass = new JPasswordField();
        styleLoginFields(txtPass);

        // Simplified Login Button
        JButton btnLogin = new JButton("LOGIN");
        styleButton(btnLogin, COLOR_BUTTON_BG);
        btnLogin.setForeground(COLOR_CARD_BG);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Authentication Event Trigger
        btnLogin.addActionListener(e -> {
            String username = txtUser.getText();
            String password = new String(txtPass.getPassword());

            if (username.equals("Ifrah") && password.equals("1234")) {
                controller.navigateToDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Invalid Security Credentials.", "Auth Failure", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Assemble Login Card Components sequentially down the center
        loginCard.add(lblHotelName); loginCard.add(Box.createRigidArea(new Dimension(0, 5)));
        loginCard.add(lblTerminal); loginCard.add(Box.createRigidArea(new Dimension(0, 40)));
        loginCard.add(lblUser); loginCard.add(Box.createRigidArea(new Dimension(0, 5)));
        loginCard.add(txtUser); loginCard.add(Box.createRigidArea(new Dimension(0, 15)));
        loginCard.add(lblPass); loginCard.add(Box.createRigidArea(new Dimension(0, 5)));
        loginCard.add(txtPass); loginCard.add(Box.createRigidArea(new Dimension(0, 35)));
        loginCard.add(btnLogin);

        add(loginCard);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            File imgFile = new File("login_bg.jpg");
            if (imgFile.exists()) {
                BufferedImage bg = ImageIO.read(imgFile);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(new Color(35, 25, 20));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        } catch (IOException e) {
            g.setColor(new Color(35, 25, 20));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void styleLoginFields(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(280, 35));
        field.setBackground(COLOR_INPUT_BG);
        field.setOpaque(true);
        field.setForeground(COLOR_TEXT_DARK);
        field.setCaretColor(COLOR_BUTTON_BG);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setHorizontalAlignment(JTextField.CENTER);

        // ✨ Added: Single black border outline paired with interior padding
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));

        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(280, 42));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
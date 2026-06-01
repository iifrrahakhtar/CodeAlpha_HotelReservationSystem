import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DashboardScreen extends JPanel {

    private static final String DB_URL = "jdbc:sqlite:hotel_reservation.db";

    private DefaultTableModel roomTableModel;
    private DefaultTableModel bookingTableModel;
    private JTextField txtGuestName, txtFatherName, txtAddress, txtRoomNo, txtCancelID;
    private JSpinner spinnerNights;
    private JTextArea txtInvoice;

    private JPanel leftContentCardContainer;
    private CardLayout leftContentCardLayout;

    // --- ✨ Premium Warm Tan & Gold-Beige Palette ---
    private final Color COLOR_BG = new Color(122, 70, 10);         // Muted Sand Tan Base Backing
    private final Color COLOR_PRIMARY = new Color(222, 192, 153);    // Warm Amber-Tan for Structured Container Cards
    private final Color COLOR_ACCENT = new Color(122, 70, 10);       // Rich Roasted Bronze/Auburn for Vibrant Header Headers
    private final Color COLOR_INPUT_BG = new Color(237, 219, 194);   // Balanced Inset Light Cream-Tan for Input Panels
    private final Color COLOR_TEXT_MAIN = new Color(46, 32, 23);     // Espresso Black for Maximum Text Sharpness
    private final Color COLOR_TEXT_MUTED = new Color(110, 89, 71);   // Soft Muted Soil Brown for Subtitles / Secondary Text

    public DashboardScreen() {
        initDatabase();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(COLOR_PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(80, 75));
        headerPanel.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel lblTitle = new JLabel("THE LUMINOUS GRAND HOTEL TERMINAL") {{
            setFont(new Font("Georgia", Font.BOLD, 18));
            setForeground(COLOR_ACCENT);
        }};
        JLabel lblSub = new JLabel("Rooms, Bookings, & Corporate Administration Engine") {{
            setFont(new Font("Segoe UI", Font.ITALIC, 11));
            setForeground(COLOR_TEXT_MUTED);
        }};
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblSub, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- CENTRAL WORKSPACE DIVISION ---
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // --- LEFT WORKSPACE ASSEMBLY (Balanced Layout Grid Partition) ---
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        leftPanel.setBackground(COLOR_BG);

        leftContentCardLayout = new CardLayout();
        leftContentCardContainer = new JPanel(leftContentCardLayout);
        leftContentCardContainer.setBackground(COLOR_BG);

        JPanel pnlCategoriesGrid = new JPanel(new GridLayout(3, 1, 0, 12));
        pnlCategoriesGrid.setBackground(COLOR_BG);

        // Fixed View Rooms card container layout with premium rounded corners
        JPanel pnlRoomTableWrapper = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        pnlRoomTableWrapper.setOpaque(false);
        pnlRoomTableWrapper.setBorder(new EmptyBorder(12, 18, 12, 18));

        leftContentCardContainer.add(pnlCategoriesGrid, "CATEGORIES_VIEW");
        leftContentCardContainer.add(pnlRoomTableWrapper, "ROOMS_VIEW");
        leftPanel.add(leftContentCardContainer);

        // Setup Top Navigation inside Rooms panel
        JPanel pnlTableTopNavigation = new JPanel(new BorderLayout());
        pnlTableTopNavigation.setOpaque(false);
        JLabel lblSelectedCategoryTitle = new JLabel("Category: Selection") {{ setFont(new Font("Segoe UI", Font.BOLD, 13)); setForeground(COLOR_ACCENT); }};
        JButton btnBackToCategories = new JButton("◀ Back to Categories");
        styleButton(btnBackToCategories, COLOR_INPUT_BG, COLOR_TEXT_MAIN);
        btnBackToCategories.setBorder(new RoundedCornerBorder(Color.BLACK, 1, 10)); // Fixed: Rounded back button border
        btnBackToCategories.setPreferredSize(new Dimension(140, 30));
        pnlTableTopNavigation.add(lblSelectedCategoryTitle, BorderLayout.WEST);
        pnlTableTopNavigation.add(btnBackToCategories, BorderLayout.EAST);
        pnlRoomTableWrapper.add(pnlTableTopNavigation, BorderLayout.NORTH);

        roomTableModel = new DefaultTableModel(new String[]{"Room No", "Category", "Nightly Rate", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblRooms = new JTable(roomTableModel);
        styleTable(tblRooms);
        JScrollPane scrollRooms = new JScrollPane(tblRooms);
        scrollRooms.setBorder(new RoundedCornerBorder(Color.BLACK, 1, 10)); // Fixed: Rounded table viewport border
        scrollRooms.getViewport().setBackground(COLOR_INPUT_BG);
        pnlRoomTableWrapper.add(scrollRooms, BorderLayout.CENTER);

        btnBackToCategories.addActionListener(e -> {
            buildCategorySelectionGrid(pnlCategoriesGrid);
            leftContentCardLayout.show(leftContentCardContainer, "CATEGORIES_VIEW");
        });

        // --- RE-ENGINEERED INPUT SECTIONS ---
        JPanel pnlFormLayoutGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlFormLayoutGrid.setBackground(COLOR_BG);

        // 1. Booking Panel Form Setup
        JPanel cardReserve = createFormCard("RESERVE A ROOM & CHECK-IN");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        txtGuestName = addGridFormField(cardReserve, "Guest Primary Name:", gbc, 1);
        txtFatherName = addGridFormField(cardReserve, "Father / Guardian Name:", gbc, 3);
        txtAddress = addGridFormField(cardReserve, "Permanent Address Log:", gbc, 5);

        // Split Layout Sub-panel
        JPanel pnlSplitRow = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlSplitRow.setOpaque(false);

        JPanel pnlRoomSub = new JPanel(new BorderLayout(0, 2)); pnlRoomSub.setOpaque(false);
        pnlRoomSub.add(new JLabel("Room Number Allocation:") {{ setFont(new Font("Segoe UI", Font.BOLD, 11)); setForeground(COLOR_TEXT_MUTED); }}, BorderLayout.NORTH);
        txtRoomNo = new JTextField() {{
            setPreferredSize(new Dimension(0, 24)); setBackground(COLOR_INPUT_BG); setForeground(COLOR_TEXT_MAIN);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCaretColor(COLOR_ACCENT); setHorizontalAlignment(JTextField.CENTER);
            setBorder(new RoundedCornerBorder(Color.BLACK, 1, 10));
        }};
        pnlRoomSub.add(txtRoomNo, BorderLayout.CENTER);
        pnlSplitRow.add(pnlRoomSub);

        JPanel pnlNightsSub = new JPanel(new BorderLayout(0, 2)); pnlNightsSub.setOpaque(false);
        pnlNightsSub.add(new JLabel("Nights Stay Duration:") {{ setFont(new Font("Segoe UI", Font.BOLD, 11)); setForeground(COLOR_TEXT_MUTED); }}, BorderLayout.NORTH);
        spinnerNights = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        spinnerNights.getEditor().getComponent(0).setBackground(COLOR_INPUT_BG);
        spinnerNights.getEditor().getComponent(0).setForeground(COLOR_TEXT_MAIN);
        spinnerNights.getEditor().getComponent(0).setFont(new Font("Segoe UI", Font.BOLD, 12));
        ((JFormattedTextField) spinnerNights.getEditor().getComponent(0)).setHorizontalAlignment(JTextField.CENTER);
        spinnerNights.setPreferredSize(new Dimension(0, 24));
        spinnerNights.setBorder(new RoundedCornerBorder(Color.BLACK, 1, 10));
        pnlNightsSub.add(spinnerNights, BorderLayout.CENTER);
        pnlSplitRow.add(pnlNightsSub);

        gbc.gridy = 7;
        gbc.insets = new Insets(4, 0, 8, 0);
        cardReserve.add(pnlSplitRow, gbc);

        JButton btnBook = new JButton("CONFIRM & PROCESS PAYMENT");
        styleButton(btnBook, COLOR_ACCENT, COLOR_PRIMARY);
        btnBook.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        btnBook.addActionListener(e -> processBooking());
        gbc.gridy = 8;
        gbc.insets = new Insets(4, 0, 2, 0);
        cardReserve.add(btnBook, gbc);

        gbc.gridy = 9;
        gbc.weighty = 1.0;
        cardReserve.add(Box.createGlue(), gbc);
        pnlFormLayoutGrid.add(cardReserve);

        // 2. Cancellation Panel Form Setup
        JPanel cardCancel = createFormCard("RESERVATION CANCELLATION & VACATE");
        GridBagConstraints gbcCancel = new GridBagConstraints();
        gbcCancel.fill = GridBagConstraints.HORIZONTAL;
        gbcCancel.weightx = 1.0;
        gbcCancel.gridx = 0;

        gbcCancel.gridy = 1;
        gbcCancel.weighty = 0.25;
        cardCancel.add(Box.createGlue(), gbcCancel);

        gbcCancel.weighty = 0.0;
        txtCancelID = addGridFormField(cardCancel, "Booking Reference ID:", gbcCancel, 2);

        JButton btnCancel = new JButton("VACATE ROOM");
        styleButton(btnCancel, new Color(153, 41, 18), COLOR_PRIMARY);
        btnCancel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        btnCancel.addActionListener(e -> processCancellation());
        gbcCancel.gridy = 4;
        gbcCancel.insets = new Insets(10, 0, 0, 0);
        cardCancel.add(btnCancel, gbcCancel);

        gbcCancel.gridy = 5;
        gbcCancel.weighty = 1.0;
        cardCancel.add(Box.createGlue(), gbcCancel);

        pnlFormLayoutGrid.add(cardCancel);
        leftPanel.add(pnlFormLayoutGrid);
        mainPanel.add(leftPanel);

        // --- RIGHT WORKSPACE ASSEMBLY ---
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        rightPanel.setBackground(COLOR_BG);

        // --- Ledger Panel Wrapper ---
        JPanel pnlLedger = new JPanel(new BorderLayout(0, 10));
        pnlLedger.setBackground(COLOR_BG);
        pnlLedger.add(new JLabel("ACTIVE RESERVATION LOG LEDGER") {{
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
        }}, BorderLayout.NORTH);

        String[] ledgerColumns = {"ID", "Guest Holder", "Father/Guardian", "Permanent Address Log", "Room", "Check-In Date/Time", "Expected Check-Out", "Nights", "Settled Cost"};
        bookingTableModel = new DefaultTableModel(ledgerColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblBookings = new JTable(bookingTableModel);
        styleTable(tblBookings);
        tblBookings.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollBookings = new JScrollPane(tblBookings, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollBookings.setBorder(BorderFactory.createEmptyBorder());
        scrollBookings.getViewport().setBackground(COLOR_INPUT_BG);
        pnlLedger.add(scrollBookings, BorderLayout.CENTER);
        rightPanel.add(pnlLedger);

        // --- Invoice Panel Wrapper ---
        JPanel pnlInvoiceWrapper = new JPanel(new BorderLayout(0, 5));
        pnlInvoiceWrapper.setBackground(COLOR_BG);
        pnlInvoiceWrapper.add(new JLabel("REAL-TIME BILLING INVOICE RECEIPT") {{
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
        }}, BorderLayout.NORTH);

        txtInvoice = new JTextArea(" No active operation processed items yet.");
        txtInvoice.setFont(new Font("Consolas", Font.BOLD, 12));
        txtInvoice.setEditable(false);
        txtInvoice.setBackground(COLOR_PRIMARY);
        txtInvoice.setForeground(COLOR_TEXT_MAIN);
        txtInvoice.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollInvoice = new JScrollPane(txtInvoice);
        scrollInvoice.setBorder(BorderFactory.createEmptyBorder());
        pnlInvoiceWrapper.add(scrollInvoice, BorderLayout.CENTER);
        rightPanel.add(pnlInvoiceWrapper);

        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);

        buildCategorySelectionGrid(pnlCategoriesGrid);
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS rooms (room_no TEXT PRIMARY KEY, category TEXT, price_per_day REAL, is_available INTEGER);");
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms;");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO rooms VALUES ('101', 'Standard', 80.00, 1), ('102', 'Standard', 80.00, 1), ('103', 'Standard', 80.00, 1)," +
                        "('201', 'Deluxe', 150.00, 1), ('202', 'Deluxe', 150.00, 1), ('203', 'Deluxe', 150.00, 1)," +
                        "('301', 'Suite', 350.00, 1), ('302', 'Suite', 350.00, 1);");
            }

            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "guest_name TEXT, " +
                    "father_name TEXT, " +
                    "address TEXT, " +
                    "room_no TEXT, " +
                    "checkin_date TEXT, " +
                    "checkout_date TEXT, " +
                    "nights_stayed INTEGER, " +
                    "total_paid REAL);");

            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rsCol = meta.getColumns(null, null, "reservations", "checkin_date")) {
                if (!rsCol.next()) {
                    stmt.execute("ALTER TABLE reservations ADD COLUMN checkin_date TEXT;");
                    stmt.execute("ALTER TABLE reservations ADD COLUMN checkout_date TEXT;");
                    stmt.execute("ALTER TABLE reservations ADD COLUMN nights_stayed INTEGER;");
                }
            }
        } catch (SQLException e) { /**/ }
    }

    public void loadRuntimeData() {
        leftContentCardLayout.show(leftContentCardContainer, "CATEGORIES_VIEW");
        buildCategorySelectionGrid((JPanel) leftContentCardContainer.getComponent(0));
        refreshBookingTable();
    }

    private void buildCategorySelectionGrid(JPanel container) {
        container.removeAll();

        String[] categories = {"Standard", "Deluxe", "Suite"};
        String[] specs = {
                "• Single Premium Bed &bull; Ergonomic Desk Space<br>• High-Speed Wi-Fi &bull; Compact En-Suite Bath",
                "• Double Premium Bed &bull; Smart TV Media Hub<br>• Ambient Cozy Lighting &bull; Private Terrace Balcony",
                "• King Master Bed &bull; Luxury Lounge Living Area<br>• Integrated Kitchenette &bull; Attached Spa Hot Bath"
        };

        for (int i = 0; i < categories.length; i++) {
            final String categoryName = categories[i];
            String specificationText = specs[i];

            JPanel card = new JPanel(new BorderLayout(15, 0)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.dispose();
                }
            };
            card.setBackground(COLOR_PRIMARY);
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(8, 15, 8, 15));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel pnlLabelsGroup = new JPanel(new GridLayout(2, 1, 0, 2));
            pnlLabelsGroup.setOpaque(false);

            JLabel lblName = new JLabel(categoryName.toUpperCase() + " ROOM TIER") {{
                setFont(new Font("Georgia", Font.BOLD, 14));
                setForeground(COLOR_ACCENT);
            }};
            JLabel lblSpecs = new JLabel("<html>" + specificationText + "</html>") {{
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                setForeground(COLOR_TEXT_MAIN);
            }};

            pnlLabelsGroup.add(lblName);
            pnlLabelsGroup.add(lblSpecs);
            card.add(pnlLabelsGroup, BorderLayout.CENTER);

            JButton btnExplore = new JButton("VIEW ROOMS ➔");
            styleButton(btnExplore, COLOR_INPUT_BG, COLOR_TEXT_MAIN);
            btnExplore.setBorder(new RoundedCornerBorder(Color.BLACK, 1, 10)); // Added smooth border to action button
            btnExplore.setPreferredSize(new Dimension(130, 32));

            JPanel pnlButtonWrapper = new JPanel(new GridBagLayout());
            pnlButtonWrapper.setOpaque(false);
            pnlButtonWrapper.add(btnExplore);
            card.add(pnlButtonWrapper, BorderLayout.EAST);

            java.awt.event.MouseAdapter clickHandler = new java.awt.event.MouseAdapter() {
                @Override public void mousePressed(java.awt.event.MouseEvent e) {
                    drillDownToRoomCategoryView(categoryName);
                }
            };
            card.addMouseListener(clickHandler);
            btnExplore.addActionListener(e -> drillDownToRoomCategoryView(categoryName));

            container.add(card);
        }
        container.revalidate();
        container.repaint();
    }

    private void drillDownToRoomCategoryView(String category) {
        JPanel pnlRoomsView = (JPanel) leftContentCardContainer.getComponent(1);
        JPanel topNav = (JPanel) pnlRoomsView.getComponent(0);
        JLabel lblTitle = (JLabel) topNav.getComponent(0);
        lblTitle.setText("CATEGORY SPECIFIC ALLOCATION MAP: " + category.toUpperCase());

        roomTableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM rooms WHERE category = ?;")) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roomTableModel.addRow(new Object[]{
                            rs.getString("room_no"), rs.getString("category"),
                            String.format("$%.2f", rs.getDouble("price_per_day")),
                            rs.getInt("is_available") == 1 ? "AVAILABLE" : "OCCUPIED"
                    });
                }
            }
        } catch (SQLException e) { /**/ }

        leftContentCardLayout.show(leftContentCardContainer, "ROOMS_VIEW");
    }

    private void refreshBookingTable() {
        bookingTableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reservations;")) {
            while (rs.next()) {
                bookingTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("guest_name"),
                        rs.getString("father_name"),
                        rs.getString("address"),
                        rs.getString("room_no"),
                        rs.getString("checkin_date"),
                        rs.getString("checkout_date"),
                        rs.getInt("nights_stayed"),
                        String.format("$%.2f", rs.getDouble("total_paid"))
                });
            }
        } catch (SQLException e) { /**/ }
    }

    private void processBooking() {
        String guestName = txtGuestName.getText().trim();
        String fatherName = txtFatherName.getText().trim();
        String address = txtAddress.getText().trim();
        String roomNo = txtRoomNo.getText().trim();
        int nights = (int) spinnerNights.getValue();

        if (guestName.isEmpty() || fatherName.isEmpty() || address.isEmpty() || roomNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Operation Halted: All identification parameters required.", "Incomplete Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] options = {"Cash", "Credit Card", "Corporate Billing Account", "Crypto/Digital Wallet"};
        String selection = (String) JOptionPane.showInputDialog(
                this,
                "Select Transaction Payment Settlement Instrument Method:",
                "Payment Processing Engine",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selection == null) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement psCheck = conn.prepareStatement("SELECT is_available, price_per_day FROM rooms WHERE room_no = ?;");
            psCheck.setString(1, roomNo);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next() || rs.getInt("is_available") == 0) {
                JOptionPane.showMessageDialog(this, "Allocation Failure: Target room occupied or non-existent.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double rate = rs.getDouble("price_per_day");
            double total = rate * nights;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String checkInStr = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, nights);
            String checkOutStr = sdf.format(cal.getTime());

            conn.setAutoCommit(false);

            PreparedStatement psRoomUpdate = conn.prepareStatement("UPDATE rooms SET is_available = 0 WHERE room_no = ?;");
            psRoomUpdate.setString(1, roomNo);
            psRoomUpdate.executeUpdate();

            PreparedStatement psReserve = conn.prepareStatement(
                    "INSERT INTO reservations(guest_name, father_name, address, room_no, checkin_date, checkout_date, nights_stayed, total_paid) VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
            psReserve.setString(1, guestName);
            psReserve.setString(2, fatherName);
            psReserve.setString(3, address);
            psReserve.setString(4, roomNo);
            psReserve.setString(5, checkInStr);
            psReserve.setString(6, checkOutStr);
            psReserve.setInt(7, nights);
            psReserve.setDouble(8, total);
            psReserve.executeUpdate();
            conn.commit();

            txtInvoice.setText("=== LEGAL BOOKING SUCCESSFUL ===\n\n" +
                    "Guest: " + guestName.toUpperCase() + "\n" +
                    "Father Name: " + fatherName.toUpperCase() + "\n" +
                    "Address: " + address + "\n" +
                    "Room Unit: " + roomNo + "\n" +
                    "Stay Duration: " + nights + " Nights\n" +
                    "Check-In: " + checkInStr + "\n" +
                    "Expected Check-Out: " + checkOutStr + "\n\n" +
                    "Settlement Instrument: " + selection.toUpperCase() + "\n" +
                    "Total Settled Base Cost: $" + String.format("%.2f", total));

            txtGuestName.setText(""); txtFatherName.setText(""); txtAddress.setText(""); txtRoomNo.setText(""); spinnerNights.setValue(1);
            refreshBookingTable();
            leftContentCardLayout.show(leftContentCardContainer, "CATEGORIES_VIEW");
            buildCategorySelectionGrid((JPanel) leftContentCardContainer.getComponent(0));
        } catch (SQLException e) { /**/ }
    }

    private void processCancellation() {
        String bookingIDStr = txtCancelID.getText().trim();
        if (bookingIDStr.isEmpty()) return;
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement psBookCheck = conn.prepareStatement("SELECT room_no FROM reservations WHERE id = ?;");
            psBookCheck.setInt(1, Integer.parseInt(bookingIDStr));
            ResultSet rs = psBookCheck.executeQuery();
            if (!rs.next()) return;

            String targetedRoom = rs.getString("room_no");
            conn.setAutoCommit(false);

            PreparedStatement psDelBooking = conn.prepareStatement("DELETE FROM reservations WHERE id = ?;");
            psDelBooking.setInt(1, Integer.parseInt(bookingIDStr));
            psDelBooking.executeUpdate();

            PreparedStatement psReleaseRoom = conn.prepareStatement("UPDATE rooms SET is_available = 1 WHERE room_no = ?;");
            psReleaseRoom.setString(1, targetedRoom);
            psReleaseRoom.executeUpdate();
            conn.commit();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentVacateTime = sdf.format(Calendar.getInstance().getTime());

            txtInvoice.setText("RESERVATION RELEASE SYSTEM\n\n" +
                    "Registration Key #" + bookingIDStr + " Purged.\n" +
                    "Room " + targetedRoom + " updated status: VACANT.\n" +
                    "Official Checkout Time: " + currentVacateTime);
            txtCancelID.setText("");
            refreshBookingTable();
            buildCategorySelectionGrid((JPanel) leftContentCardContainer.getComponent(0));
        } catch (SQLException e) { /**/ }
    }

    private JPanel createFormCard(String title) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        card.setBackground(COLOR_PRIMARY);
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 18, 12, 18));

        GridBagLayout gbl = new GridBagLayout();
        card.setLayout(gbl);

        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.gridx = 0; gbcTitle.gridy = 0; gbcTitle.weightx = 1.0;
        gbcTitle.fill = GridBagConstraints.HORIZONTAL;
        gbcTitle.anchor = GridBagConstraints.NORTH;
        gbcTitle.insets = new Insets(0, 0, 12, 0);

        JLabel lblCardTitle = new JLabel(title) {{ setFont(new Font("Segoe UI", Font.BOLD, 11)); setForeground(COLOR_ACCENT); }};
        card.add(lblCardTitle, gbcTitle);

        return card;
    }

    private JTextField addGridFormField(JPanel panel, String fieldLabel, GridBagConstraints gbc, int startRow) {
        JLabel label = new JLabel(fieldLabel) {{ setFont(new Font("Segoe UI", Font.BOLD, 11)); setForeground(COLOR_TEXT_MUTED); }} ;
        gbc.gridy = startRow;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(2, 0, 2, 0);
        panel.add(label, gbc);

        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, 24));
        tf.setBackground(COLOR_INPUT_BG);
        tf.setForeground(COLOR_TEXT_MAIN);
        tf.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tf.setCaretColor(COLOR_ACCENT);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setBorder(new RoundedCornerBorder(Color.BLACK, 1, 10));

        gbc.gridy = startRow + 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(tf, gbc);

        return tf;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setBackground(COLOR_INPUT_BG);
        table.setForeground(COLOR_TEXT_MAIN);
        table.setGridColor(COLOR_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(COLOR_ACCENT);

        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(COLOR_ACCENT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(r);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- Custom Inner Class helper to implement premium round input borders ---
    private static class RoundedCornerBorder extends AbstractBorder {
        private final Color lineColor;
        private final int thickness;
        private final int cornerRadius;

        public RoundedCornerBorder(Color lineColor, int thickness, int cornerRadius) {
            this.lineColor = lineColor;
            this.thickness = thickness;
            this.cornerRadius = cornerRadius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness / 2, y + thickness / 2, width - thickness, height - thickness, cornerRadius, cornerRadius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, cornerRadius / 2, thickness + 2, cornerRadius / 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = cornerRadius / 2;
            insets.top = thickness + 2;
            insets.right = cornerRadius / 2;
            insets.bottom = thickness + 2;
            return insets;
        }
    }
}
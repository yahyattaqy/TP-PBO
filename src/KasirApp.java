import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;


public class KasirApp extends JFrame {
    private Transaksi transaksi;
    private JPanel panelItemContainer;
    private JPanel productPanel;
    private JLabel labelTotal;
    private JLabel labelTotalItem, labelTotalHargaAsli, labelTotalDiskon;
    private JTextField txtDiskonTransaksi;
    private JTextField txtNominalBayar;
    private JLabel labelKembalian;
    private double persenDiskon = 0;
    private boolean sudahDibayar = false;
    private static final DecimalFormat currencyFormat = new DecimalFormat("#,##0");

    public KasirApp() {
        transaksi = new Transaksi();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Point of Sale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 249, 250));
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1400, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        JLabel titleLabel = new JLabel("Point of Sale");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 30, 30));
        
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerButtons.setBackground(Color.WHITE);
        
        JButton btnRiwayat = createHeaderButton("History", new Color(100, 100, 100));
        JButton btnKelola = createHeaderButton("Manage", new Color(100, 100, 100));
        
        btnRiwayat.addActionListener(e -> tampilkanRiwayat());
        btnKelola.addActionListener(e -> kelolaBarang());
        
        headerButtons.add(btnRiwayat);
        headerButtons.add(btnKelola);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(headerButtons, BorderLayout.EAST);

        // Main Container - 2 Column Layout
        JPanel mainContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContainer.setBackground(new Color(248, 249, 250));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // LEFT PANEL - Product Grid
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setBackground(new Color(248, 249, 250));
        
        JLabel productsLabel = new JLabel("Products");
        productsLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        productsLabel.setForeground(new Color(30, 30, 30));
        
        // Product Grid
        productPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        productPanel.setBackground(new Color(248, 249, 250));
        
        // Add product buttons
        for (Barang barang : DaftarBarang.getDaftarBarang()) {
            JButton btnProduct = createProductButton(barang);
            productPanel.add(btnProduct);
        }
        
        JScrollPane scrollProducts = new JScrollPane(productPanel);
        scrollProducts.setBorder(null);
        scrollProducts.setBackground(new Color(248, 249, 250));
        scrollProducts.getVerticalScrollBar().setUnitIncrement(16);
        scrollProducts.getViewport().setBackground(new Color(248, 249, 250));
        
        leftPanel.add(productsLabel, BorderLayout.NORTH);
        leftPanel.add(scrollProducts, BorderLayout.CENTER);

        // RIGHT PANEL - Cart & Payment
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(new Color(248, 249, 250));
        
        // Cart Panel
        JPanel cartPanel = new JPanel(new BorderLayout(0, 10));
        cartPanel.setBackground(Color.WHITE);
        cartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        cartLabel.setForeground(new Color(30, 30, 30));
        
        panelItemContainer = new JPanel();
        panelItemContainer.setLayout(new BoxLayout(panelItemContainer, BoxLayout.Y_AXIS));
        panelItemContainer.setBackground(Color.WHITE);
        
        JScrollPane scrollCart = new JScrollPane(panelItemContainer);
        scrollCart.setBorder(null);
        scrollCart.setBackground(Color.WHITE);
        scrollCart.getVerticalScrollBar().setUnitIncrement(16);
        scrollCart.setMinimumSize(new Dimension(600, 200));
        scrollCart.setPreferredSize(new Dimension(600, 350));
        scrollCart.getViewport().setBackground(Color.WHITE);
        
        cartPanel.add(cartLabel, BorderLayout.NORTH);
        cartPanel.add(scrollCart, BorderLayout.CENTER);

        // Summary & Payment Panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Summary items
        labelTotalItem = createSummaryLabel("0 items", false);
        labelTotalHargaAsli = createSummaryLabel("Rp 0", false);
        labelTotalDiskon = createSummaryLabel("Rp 0", false);
        
        summaryPanel.add(createSummaryRow("Items", labelTotalItem));
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(createSummaryRow("Subtotal", labelTotalHargaAsli));
        summaryPanel.add(Box.createVerticalStrut(10));
        
        // Discount input
        JPanel discountRow = new JPanel(new BorderLayout(10, 0));
        discountRow.setBackground(Color.WHITE);
        discountRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel lblDiscount = new JLabel("Discount (%)");
        lblDiscount.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        lblDiscount.setForeground(new Color(100, 100, 100));
        
        txtDiskonTransaksi = new JTextField("0");
        txtDiskonTransaksi.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        txtDiskonTransaksi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtDiskonTransaksi.setPreferredSize(new Dimension(80, 35));
        txtDiskonTransaksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try {
                    String input = txtDiskonTransaksi.getText().trim();
                    if (!input.isEmpty()) {
                        double diskon = Double.parseDouble(input);
                        if (diskon < 0) diskon = 0;
                        if (diskon > 100) diskon = 100;
                        persenDiskon = diskon;
                    } else {
                        persenDiskon = 0;
                    }
                    updateTotal();
                    hitungKembalian();
                } catch (NumberFormatException ex) {
                    persenDiskon = 0;
                }
            }
        });
        
        discountRow.add(lblDiscount, BorderLayout.WEST);
        discountRow.add(txtDiskonTransaksi, BorderLayout.EAST);
        summaryPanel.add(discountRow);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(createSummaryRow("Discount", labelTotalDiskon));
        summaryPanel.add(Box.createVerticalStrut(15));
        
        // Total
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(Color.WHITE);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        totalRow.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)));
        totalRow.add(Box.createVerticalStrut(15), BorderLayout.NORTH);
        
        JLabel lblTotal = new JLabel("Total");
        lblTotal.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        lblTotal.setForeground(new Color(30, 30, 30));
        
        labelTotal = new JLabel("Rp 0");
        labelTotal.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        labelTotal.setForeground(new Color(0, 122, 255));
        
        JPanel totalRowInner = new JPanel(new BorderLayout());
        totalRowInner.setBackground(Color.WHITE);
        totalRowInner.add(lblTotal, BorderLayout.WEST);
        totalRowInner.add(labelTotal, BorderLayout.EAST);
        totalRow.add(totalRowInner, BorderLayout.CENTER);
        summaryPanel.add(totalRow);
        summaryPanel.add(Box.createVerticalStrut(20));
        
        // Payment input
        JLabel lblPay = new JLabel("Payment Amount");
        lblPay.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        lblPay.setForeground(new Color(100, 100, 100));
        lblPay.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtNominalBayar = new JTextField();
        txtNominalBayar.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        txtNominalBayar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        txtNominalBayar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtNominalBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                hitungKembalian();
            }
        });
        
        summaryPanel.add(lblPay);
        summaryPanel.add(Box.createVerticalStrut(8));
        summaryPanel.add(txtNominalBayar);
        summaryPanel.add(Box.createVerticalStrut(10));
        
        // Change
        labelKembalian = createSummaryLabel("Rp 0", true);
        summaryPanel.add(createSummaryRow("Change", labelKembalian));
        summaryPanel.add(Box.createVerticalStrut(20));
        
        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JButton btnPay = new JButton("Pay Now");
        btnPay.setFont(new Font("SF Pro Text", Font.BOLD, 15));
        btnPay.setForeground(Color.WHITE);
        btnPay.setBackground(new Color(0, 122, 255));
        btnPay.setBorderPainted(false);
        btnPay.setFocusPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.addActionListener(e -> onBayar());
        
        JButton btnComplete = new JButton("Complete");
        btnComplete.setFont(new Font("SF Pro Text", Font.BOLD, 15));
        btnComplete.setForeground(Color.WHITE);
        btnComplete.setBackground(new Color(52, 199, 89));
        btnComplete.setBorderPainted(false);
        btnComplete.setFocusPainted(false);
        btnComplete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComplete.addActionListener(e -> onSelesai());
        
        actionPanel.add(btnPay);
        actionPanel.add(btnComplete);
        summaryPanel.add(actionPanel);

        rightPanel.add(cartPanel, BorderLayout.CENTER);
        rightPanel.add(summaryPanel, BorderLayout.SOUTH);

        mainContainer.add(leftPanel);
        mainContainer.add(rightPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);

        setVisible(true);
    }
    
    private JButton createHeaderButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 35));
        return btn;
    }
    
    private JButton createProductButton(Barang barang) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 10));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 120));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(barang.getNama());
        nameLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        nameLabel.setForeground(new Color(30, 30, 30));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel("Rp " + currencyFormat.format((long)barang.getHarga()));
        priceLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        priceLabel.setForeground(new Color(0, 122, 255));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel stockLabel = new JLabel("Stock: " + barang.getStok());
        stockLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 12));
        stockLabel.setForeground(barang.getStok() > 10 ? new Color(52, 199, 89) : 
                                 barang.getStok() > 0 ? new Color(255, 149, 0) : 
                                 new Color(255, 59, 48));
        stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(stockLabel);
        
        btn.add(infoPanel, BorderLayout.CENTER);
        
        btn.addActionListener(e -> {
            if (barang.getStok() > 0) {
                tambahBarangKeKeranjang(barang);
            } else {
                JOptionPane.showMessageDialog(this, 
                    barang.getNama() + " is out of stock!", 
                    "Out of Stock", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        return btn;
    }
    
    private JPanel createSummaryRow(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        lblLabel.setForeground(new Color(100, 100, 100));
        
        row.add(lblLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }
    
    private JLabel createSummaryLabel(String text, boolean highlight) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SF Pro Text", highlight ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(highlight ? new Color(52, 199, 89) : new Color(30, 30, 30));
        return label;
    }
    
    private void tambahBarangKeKeranjang(Barang barangAsli) {
        String input = JOptionPane.showInputDialog(this, 
            "Enter quantity for " + barangAsli.getNama() + "\nAvailable stock: " + barangAsli.getStok(), 
            "1");
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            int jumlah = Integer.parseInt(input.trim());
            
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (barangAsli.getStok() < jumlah) {
                JOptionPane.showMessageDialog(this, "Insufficient stock!\nAvailable: " + barangAsli.getStok(), "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Barang barang = new Barang(barangAsli.getKode(), barangAsli.getNama(), barangAsli.getHarga(), barangAsli.getStok());
            TransaksiItem item = new TransaksiItem(barang, jumlah);
            transaksi.tambahItem(item);
            // Stok akan dikurangi saat pembayaran selesai
            
            tambahItemKePanel(item);
            updateTotal();
            refreshProductButtons();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshProductButtons() {
        productPanel.removeAll();
        
        for (Barang barang : DaftarBarang.getDaftarBarang()) {
            productPanel.add(createProductButton(barang));
        }
        
        productPanel.revalidate();
        productPanel.repaint();
        DaftarBarang.simpanKeFile();
    }

    private void tambahItemKePanel(TransaksiItem item) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setMaximumSize(new Dimension(540, 65));
        itemPanel.setPreferredSize(new Dimension(540, 65));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Item info with fixed width
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setPreferredSize(new Dimension(280, 45));
        
        JLabel nameLabel = new JLabel(item.getBarang().getNama() + " x" + item.getJumlah());
        nameLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        nameLabel.setForeground(new Color(30, 30, 30));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel("Rp " + currencyFormat.format((long)item.hitungSubtotal()));
        priceLabel.setFont(new Font("SF Pro Text", Font.BOLD, 13));
        priceLabel.setForeground(new Color(0, 122, 255));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnEdit = new JButton("Edit");
        btnEdit.setFont(new Font("SF Pro Text", Font.PLAIN, 11));
        btnEdit.setForeground(new Color(0, 122, 255));
        btnEdit.setBackground(Color.WHITE);
        btnEdit.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 122, 255), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> editItemDariPanel(item, itemPanel, nameLabel, priceLabel));
        
        JButton btnDelete = new JButton("Remove");
        btnDelete.setFont(new Font("SF Pro Text", Font.PLAIN, 11));
        btnDelete.setForeground(new Color(255, 59, 48));
        btnDelete.setBackground(Color.WHITE);
        btnDelete.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 59, 48), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> hapusItemDariPanel(item, itemPanel));
        
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        
        itemPanel.add(infoPanel, BorderLayout.CENTER);
        itemPanel.add(buttonPanel, BorderLayout.EAST);
        
        panelItemContainer.add(itemPanel);
        panelItemContainer.revalidate();
        panelItemContainer.repaint();
    }
    
    private void editItemDariPanel(TransaksiItem itemLama, JPanel itemPanel, JLabel nameLabel, JLabel priceLabel) {
        Barang barangItem = itemLama.getBarang();
        int jumlahLama = itemLama.getJumlah();
        
        Barang barangAsli = null;
        for (Barang b : DaftarBarang.getDaftarBarang()) {
            if (b.getKode().equals(barangItem.getKode())) {
                barangAsli = b;
                break;
            }
        }
        
        if (barangAsli == null) return;
        
        String input = JOptionPane.showInputDialog(this, 
            "Edit quantity for: " + barangItem.getNama() + "\n" +
            "Current quantity: " + jumlahLama + "\n" +
            "Available stock: " + barangAsli.getStok() + "\n\n" +
            "Enter new quantity:",
            jumlahLama);
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            int jumlahBaru = Integer.parseInt(input.trim());
            
            if (jumlahBaru <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (jumlahBaru == jumlahLama) return;
            
            // Cek stok untuk penambahan quantity
            if (jumlahBaru > jumlahLama) {
                int selisih = jumlahBaru - jumlahLama;
                if (barangAsli.getStok() < selisih) {
                    JOptionPane.showMessageDialog(this, 
                        "Insufficient stock!\nRequired: " + selisih + "\nAvailable: " + barangAsli.getStok(),
                        "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            int index = transaksi.getDaftarItem().indexOf(itemLama);
            if (index >= 0) {
                TransaksiItem itemBaru = new TransaksiItem(barangItem, jumlahBaru);
                transaksi.getDaftarItem().set(index, itemBaru);
                
                nameLabel.setText(barangItem.getNama() + " x" + jumlahBaru);
                priceLabel.setText("Rp " + currencyFormat.format((long)itemBaru.hitungSubtotal()));
                
                updateTotal();
                refreshProductButtons();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusItemDariPanel(TransaksiItem item, JPanel itemPanel) {
        // Hapus dari transaksi
        transaksi.getDaftarItem().remove(item);
        
        // Hapus panel
        panelItemContainer.remove(itemPanel);
        panelItemContainer.revalidate();
        panelItemContainer.repaint();
        
        updateTotal();
        refreshProductButtons();
    }

    private void hitungKembalian() {
        try {
            double totalAsli = transaksi.hitungTotal();
            double diskon = totalAsli * (persenDiskon / 100.0);
            double total = totalAsli - diskon;
            String nominalText = txtNominalBayar.getText().trim();
            
            if (nominalText.isEmpty()) {
                labelKembalian.setText("Rp 0");
                labelKembalian.setForeground(new Color(200, 0, 0));
                return;
            }
            
            double nominalBayar = Double.parseDouble(nominalText);
            double kembalian = nominalBayar - total;
            
            if (kembalian < 0) {
                labelKembalian.setText("Insufficient: Rp " + currencyFormat.format((long)Math.abs(kembalian)));
                labelKembalian.setForeground(new Color(255, 59, 48));
            } else {
                labelKembalian.setText("Rp " + currencyFormat.format((long)kembalian));
                labelKembalian.setForeground(new Color(52, 199, 89));
            }
        } catch (NumberFormatException ex) {
            labelKembalian.setText("Invalid amount");
            labelKembalian.setForeground(new Color(255, 59, 48));
        }
    }

    private void onBayar() {
        try {
            if (transaksi.getDaftarItem().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String nominalText = txtNominalBayar.getText().trim();
            if (nominalText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan nominal bayar terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double totalAsli = transaksi.hitungTotal();
            double diskon = totalAsli * (persenDiskon / 100.0);
            double total = totalAsli - diskon;
            double nominalBayar = Double.parseDouble(nominalText);
            
            if (nominalBayar < total) {
                JOptionPane.showMessageDialog(this, "Uang tidak cukup! Kurang: Rp " + currencyFormat.format((long)(total - nominalBayar)), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double kembalian = nominalBayar - total;
            
            String struk = "======== STRUK PEMBELIAN ========\n";
            struk += "Tanggal: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n";
            struk += "================================\n";
            for (int i = 0; i < transaksi.getDaftarItem().size(); i++) {
                TransaksiItem item = transaksi.getDaftarItem().get(i);
                struk += item.getBarang().getNama() + " x" + item.getJumlah() + " = Rp " + currencyFormat.format((long)item.hitungSubtotal()) + "\n";
            }
            struk += "================================\n";
            struk += "Subtotal: Rp " + currencyFormat.format((long)totalAsli) + "\n";
            if (persenDiskon > 0) {
                struk += "Diskon (" + persenDiskon + "%): - Rp " + currencyFormat.format((long)diskon) + "\n";
            }
            struk += "Total Harga: Rp " + currencyFormat.format((long)total) + "\n";
            struk += "Nominal Bayar: Rp " + currencyFormat.format((long)nominalBayar) + "\n";
            struk += "Kembalian: Rp " + currencyFormat.format((long)kembalian) + "\n";
            struk += "================================\n";
            struk += "Terima Kasih!\n";
            
            // Kurangi stok setelah pembayaran berhasil
            for (TransaksiItem item : transaksi.getDaftarItem()) {
                for (Barang b : DaftarBarang.getDaftarBarang()) {
                    if (b.getKode().equals(item.getBarang().getKode())) {
                        b.kurangiStok(item.getJumlah());
                        break;
                    }
                }
            }
            
            // Simpan ke riwayat transaksi
            TransaksiSelesai transaksiSelesai = new TransaksiSelesai(
                transaksi.getDaftarItem(), 
                totalAsli, 
                persenDiskon, 
                diskon, 
                total, 
                nominalBayar, 
                kembalian
            );
            RiwayatTransaksi.tambahTransaksi(transaksiSelesai);
            
            // Set flag bahwa sudah dibayar
            sudahDibayar = true;
            
            // Update tampilan stok
            refreshProductButtons();
            
            // Simpan struk ke file txt
            simpanStrukKeFile(struk);
            
            JOptionPane.showMessageDialog(this, struk, "✓ Pembayaran Berhasil", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format nominal tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void simpanStrukKeFile(String struk) {
        try {
            // Buat folder struk jika belum ada
            File folder = new File("struk");
            if (!folder.exists()) {
                folder.mkdir();
            }
            
            // Generate nama file dengan timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "struk/struk_" + timestamp + ".txt";
            
            // Tulis struk ke file
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(struk);
            writer.close();
            
            System.out.println("Struk berhasil disimpan ke: " + fileName);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan struk: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Struk tidak dapat disimpan ke file!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onSelesai() {
        if (!sudahDibayar) {
            JOptionPane.showMessageDialog(this, "Transaction not paid yet!\nPlease process payment first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Complete transaction and clear cart?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DaftarBarang.simpanKeFile();
            
            transaksi = new Transaksi();
            panelItemContainer.removeAll();
            panelItemContainer.revalidate();
            panelItemContainer.repaint();
            persenDiskon = 0;
            sudahDibayar = false;
            txtDiskonTransaksi.setText("0");
            updateTotal();
            txtNominalBayar.setText("");
            labelKembalian.setText("Rp 0");
            labelKembalian.setForeground(new Color(52, 199, 89));
            JOptionPane.showMessageDialog(this, "Transaction completed!\nData saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateTotal() {
        double total = transaksi.hitungTotal();
        // Terapkan diskon transaksi
        double diskon = total * (persenDiskon / 100.0);
        double totalSetelahDiskon = total - diskon;
        labelTotal.setText("Rp " + currencyFormat.format((long)totalSetelahDiskon));
        updateDetailTransaksi();
    }

    private void updateDetailTransaksi() {
        int totalItems = 0;
        double totalHargaAsli = 0;
        
        // Hitung total item dan harga
        for (TransaksiItem item : transaksi.getDaftarItem()) {
            totalItems += item.getJumlah();
            totalHargaAsli += item.getBarang().getHarga() * item.getJumlah();
        }
        
        // Hitung diskon dari persenDiskon
        double totalDiskon = totalHargaAsli * (persenDiskon / 100.0);
        
        // Update label
        labelTotalItem.setText(totalItems + " items");
        labelTotalHargaAsli.setText("Rp " + currencyFormat.format((long)totalHargaAsli));
        
        if (totalDiskon > 0) {
            labelTotalDiskon.setText("- Rp " + currencyFormat.format((long)totalDiskon));
            labelTotalDiskon.setForeground(new Color(255, 59, 48));
        } else {
            labelTotalDiskon.setText("Rp 0");
            labelTotalDiskon.setForeground(new Color(100, 100, 100));
        }
    }

    private void simpanData() {
        if (DaftarBarang.simpanKeFile()) {
            JOptionPane.showMessageDialog(this, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanRiwayat() {
        JFrame frameRiwayat = new JFrame("Riwayat Transaksi");
        frameRiwayat.setSize(900, 600);
        frameRiwayat.setLocationRelativeTo(this);
        frameRiwayat.setLayout(new BorderLayout(10, 10));
        
        // Panel atas dengan info
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBackground(new Color(230, 240, 255));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblInfo = new JLabel("Total Transaksi: " + RiwayatTransaksi.getJumlahTransaksi() + " transaksi");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblInfo);
        
        // List untuk menampilkan riwayat
        DefaultListModel<String> modelRiwayat = new DefaultListModel<>();
        JList<String> listRiwayat = new JList<>(modelRiwayat);
        listRiwayat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listRiwayat.setFont(new Font("Courier New", Font.PLAIN, 12));
        listRiwayat.setFixedCellHeight(30);
        
        // Isi data riwayat
        for (int i = 0; i < RiwayatTransaksi.getJumlahTransaksi(); i++) {
            TransaksiSelesai ts = RiwayatTransaksi.getTransaksi(i);
            String display = String.format("#%03d | %s | %d item | Total: Rp %s", 
                i + 1, 
                ts.getTanggalFormatted(),
                ts.getTotalItem(),
                currencyFormat.format((long)ts.getTotalAkhir()));
            modelRiwayat.addElement(display);
        }
        
        JScrollPane scrollPane = new JScrollPane(listRiwayat);
        
        // Panel bawah dengan tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelTombol.setBackground(new Color(245, 245, 245));
        
        JButton btnDetail = new JButton("📄 Lihat Detail");
        btnDetail.setPreferredSize(new Dimension(140, 35));
        btnDetail.addActionListener(e -> {
            int selectedIndex = listRiwayat.getSelectedIndex();
            if (selectedIndex >= 0) {
                tampilkanDetailTransaksi(RiwayatTransaksi.getTransaksi(selectedIndex));
            } else {
                JOptionPane.showMessageDialog(frameRiwayat, "Pilih transaksi terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton btnTutup = new JButton("✕ Tutup");
        btnTutup.setPreferredSize(new Dimension(100, 35));
        btnTutup.addActionListener(e -> frameRiwayat.dispose());
        
        panelTombol.add(btnDetail);
        panelTombol.add(btnTutup);
        
        frameRiwayat.add(panelInfo, BorderLayout.NORTH);
        frameRiwayat.add(scrollPane, BorderLayout.CENTER);
        frameRiwayat.add(panelTombol, BorderLayout.SOUTH);
        
        frameRiwayat.setVisible(true);
    }
    
    private void tampilkanDetailTransaksi(TransaksiSelesai ts) {
        if (ts == null) return;
        
        StringBuilder detail = new StringBuilder();
        detail.append("======== DETAIL TRANSAKSI ========\n");
        detail.append("Tanggal: ").append(ts.getTanggalFormatted()).append("\n");
        detail.append("==================================\n");
        detail.append("Item yang dibeli:\n");
        
        for (TransaksiItem item : ts.getItems()) {
            detail.append(String.format("  %s x%d = Rp %s\n", 
                item.getBarang().getNama(),
                item.getJumlah(),
                currencyFormat.format((long)item.hitungSubtotal())));
        }
        
        detail.append("==================================\n");
        detail.append(String.format("Subtotal: Rp %s\n", currencyFormat.format((long)ts.getSubtotal())));
        
        if (ts.getPersenDiskon() > 0) {
            detail.append(String.format("Diskon (%.1f%%): - Rp %s\n", 
                ts.getPersenDiskon(), 
                currencyFormat.format((long)ts.getTotalDiskon())));
        }
        
        detail.append(String.format("Total: Rp %s\n", currencyFormat.format((long)ts.getTotalAkhir())));
        detail.append(String.format("Bayar: Rp %s\n", currencyFormat.format((long)ts.getNominalBayar())));
        detail.append(String.format("Kembalian: Rp %s\n", currencyFormat.format((long)ts.getKembalian())));
        detail.append("==================================\n");
        
        JTextArea textArea = new JTextArea(detail.toString());
        textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Detail Transaksi", JOptionPane.PLAIN_MESSAGE);
    }

    private void kelolaBarang() {
        JFrame frameKelola = new JFrame("Kelola Barang");
        frameKelola.setSize(900, 600);
        frameKelola.setLocationRelativeTo(this);
        frameKelola.setLayout(new BorderLayout(10, 10));
        
        // Panel atas - Tabel barang
        String[] columnNames = {"Kode", "Nama", "Harga", "Stok"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Load data ke tabel
        refreshTableBarang(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Barang"));
        
        // Panel bawah - Form input
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Form Data Barang"));
        panelForm.setBackground(new Color(245, 250, 255));
        
        JLabel lblKode = new JLabel("Kode Barang:");
        JTextField txtKode = new JTextField();
        JLabel lblNama = new JLabel("Nama Barang:");
        JTextField txtNama = new JTextField();
        JLabel lblHarga = new JLabel("Harga:");
        JTextField txtHarga = new JTextField();
        JLabel lblStok = new JLabel("Stok:");
        JTextField txtStok = new JTextField();
        
        panelForm.add(lblKode);
        panelForm.add(txtKode);
        panelForm.add(lblNama);
        panelForm.add(txtNama);
        panelForm.add(lblHarga);
        panelForm.add(txtHarga);
        panelForm.add(lblStok);
        panelForm.add(txtStok);
        
        // Panel tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelTombol.setBackground(new Color(245, 250, 255));
        
        JButton btnTambah = new JButton("➕ Tambah");
        btnTambah.setPreferredSize(new Dimension(100, 35));
        btnTambah.setBackground(new Color(76, 175, 80));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        
        JButton btnEdit = new JButton("✏️ Edit");
        btnEdit.setPreferredSize(new Dimension(100, 35));
        btnEdit.setBackground(new Color(33, 150, 243));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        
        JButton btnHapus = new JButton("🗑️ Hapus");
        btnHapus.setPreferredSize(new Dimension(100, 35));
        btnHapus.setBackground(new Color(244, 67, 54));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFocusPainted(false);
        
        JButton btnClear = new JButton("🔄 Clear");
        btnClear.setPreferredSize(new Dimension(100, 35));
        
        JButton btnTutup = new JButton("✕ Tutup");
        btnTutup.setPreferredSize(new Dimension(100, 35));
        
        panelTombol.add(btnTambah);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);
        panelTombol.add(btnClear);
        panelTombol.add(btnTutup);
        
        panelForm.add(new JLabel(""));
        panelForm.add(panelTombol);
        
        // Event handlers
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtKode.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtKode.setEditable(false);
                    txtNama.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtHarga.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtStok.setText(tableModel.getValueAt(selectedRow, 3).toString());
                }
            }
        });
        
        btnTambah.addActionListener(e -> {
            try {
                String kode = txtKode.getText().trim();
                String nama = txtNama.getText().trim();
                double harga = Double.parseDouble(txtHarga.getText().trim());
                int stok = Integer.parseInt(txtStok.getText().trim());
                
                if (kode.isEmpty() || nama.isEmpty()) {
                    JOptionPane.showMessageDialog(frameKelola, "Kode dan Nama tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Barang barangBaru = new Barang(kode, nama, harga, stok);
                if (DaftarBarang.tambahBarang(barangBaru)) {
                    JOptionPane.showMessageDialog(frameKelola, "Barang berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshTableBarang(tableModel);
                    clearFormBarang(txtKode, txtNama, txtHarga, txtStok);
                } else {
                    JOptionPane.showMessageDialog(frameKelola, "Kode barang sudah ada!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frameKelola, "Harga dan Stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frameKelola, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnEdit.addActionListener(e -> {
            try {
                String kode = txtKode.getText().trim();
                String nama = txtNama.getText().trim();
                double harga = Double.parseDouble(txtHarga.getText().trim());
                int stok = Integer.parseInt(txtStok.getText().trim());
                
                if (kode.isEmpty()) {
                    JOptionPane.showMessageDialog(frameKelola, "Pilih barang dari tabel terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (DaftarBarang.updateBarang(kode, nama, harga, stok)) {
                    JOptionPane.showMessageDialog(frameKelola, "Barang berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshTableBarang(tableModel);
                    clearFormBarang(txtKode, txtNama, txtHarga, txtStok);
                } else {
                    JOptionPane.showMessageDialog(frameKelola, "Barang tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frameKelola, "Harga dan Stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frameKelola, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnHapus.addActionListener(e -> {
            String kode = txtKode.getText().trim();
            if (kode.isEmpty()) {
                JOptionPane.showMessageDialog(frameKelola, "Pilih barang dari tabel terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(frameKelola, 
                "Apakah Anda yakin ingin menghapus barang dengan kode " + kode + "?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (DaftarBarang.hapusBarang(kode)) {
                    JOptionPane.showMessageDialog(frameKelola, "Barang berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshTableBarang(tableModel);
                    clearFormBarang(txtKode, txtNama, txtHarga, txtStok);
                } else {
                    JOptionPane.showMessageDialog(frameKelola, "Barang tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnClear.addActionListener(e -> clearFormBarang(txtKode, txtNama, txtHarga, txtStok));
        
        btnTutup.addActionListener(e -> frameKelola.dispose());
        
        frameKelola.add(scrollPane, BorderLayout.CENTER);
        frameKelola.add(panelForm, BorderLayout.SOUTH);
        frameKelola.setVisible(true);
    }
    
    private void refreshTableBarang(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (Barang b : DaftarBarang.getDaftarBarang()) {
            Object[] row = {
                b.getKode(),
                b.getNama(),
                "Rp " + currencyFormat.format((long)b.getHarga()),
                b.getStok()
            };
            tableModel.addRow(row);
        }
    }
    
    private void clearFormBarang(JTextField txtKode, JTextField txtNama, JTextField txtHarga, JTextField txtStok) {
        txtKode.setText("");
        txtKode.setEditable(true);
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KasirApp());
    }
}

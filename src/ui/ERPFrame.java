package ui;

import dao.PersonelDAO;
import model.Personel;
import model.SalaryRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ERPFrame extends JFrame {
    private final PersonelDAO dao = new PersonelDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public ERPFrame() {
        setTitle("ERP Uygulaması - PostgreSQL");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("PERSONEL YÖNETİM SİSTEMİ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Ad", "Soyad", "Pozisyon", "Maaş", "Giriş Tarihi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnYenile = new JButton("🔄 Yenile");
        JButton btnEkle = new JButton("➕ Yeni Personel");
        JButton btnDuzenle = new JButton("✏️ Düzenle");
        JButton btnSil = new JButton("🗑️ Sil");
        JButton btnMaasToplam = new JButton("💰 Maaş Toplamı");
        JButton btnOzetGrafik = new JButton("📊 Özet & Grafik");
        JButton btnExport = new JButton("📤 CSV Dışa Aktar");

        btnMaasToplam.setPreferredSize(new Dimension(150, 35));
        btnMaasToplam.setBackground(new Color(155, 89, 182));
        btnMaasToplam.setForeground(Color.WHITE);

        btnYenile.setPreferredSize(new Dimension(120, 35));
        btnEkle.setPreferredSize(new Dimension(150, 35));
        btnDuzenle.setPreferredSize(new Dimension(120, 35));
        btnSil.setPreferredSize(new Dimension(100, 35));
        btnOzetGrafik.setPreferredSize(new Dimension(150, 35));
        btnExport.setPreferredSize(new Dimension(150, 35));

        btnEkle.setBackground(new Color(24, 158, 89));
        btnEkle.setForeground(Color.WHITE);
        btnDuzenle.setBackground(new Color(52, 152, 219));
        btnDuzenle.setForeground(Color.WHITE);
        btnSil.setBackground(new Color(231, 76, 60));
        btnSil.setForeground(Color.WHITE);
        btnOzetGrafik.setBackground(new Color(52, 152, 219));
        btnOzetGrafik.setForeground(Color.WHITE);
        btnExport.setBackground(new Color(44, 62, 80));
        btnExport.setForeground(Color.WHITE);

        buttonPanel.add(btnYenile);
        buttonPanel.add(btnEkle);
        buttonPanel.add(btnDuzenle);
        buttonPanel.add(btnSil);
        buttonPanel.add(btnOzetGrafik);
        buttonPanel.add(btnMaasToplam);
        buttonPanel.add(btnExport);

        add(buttonPanel, BorderLayout.SOUTH);

        btnYenile.addActionListener(e -> loadData());
        btnEkle.addActionListener(e -> ekle());
        btnDuzenle.addActionListener(e -> duzenle());
        btnSil.addActionListener(e -> sil());
        btnMaasToplam.addActionListener(e -> maasToplamHesapla());

        btnExport.addActionListener(e -> {
            java.util.List<model.SalaryRecord> records;
            try {
                records = buildSalaryRecords();
            } catch (Exception ex) {
                records = null;
            }

            java.awt.Component parent = (java.awt.Component) this;

            if (records == null || records.isEmpty()) {
                export.ReportExporter.exportTableToCSV(parent, table);
            } else {
                export.ReportExporter.exportSalaryRecordsToCSV(parent, records);
            }
        });


        btnOzetGrafik.addActionListener(e -> {
            List<SalaryRecord> salaryRecords;
            try {
                salaryRecords = buildSalaryRecords();
                if (salaryRecords == null || salaryRecords.isEmpty()) {
                    salaryRecords = ERPFrameExtensions.exampleData();
                }
            } catch (Exception ex) {
                salaryRecords = ERPFrameExtensions.exampleData();
            }

            JFrame reportFrame = new JFrame("Raporlar ve Grafik");
            reportFrame.setSize(900, 700);
            reportFrame.setLocationRelativeTo(this);
            reportFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            ERPFrameExtensions.attachPanels(reportFrame, salaryRecords);
            reportFrame.setVisible(true);
        });

        loadData();
    }

    private List<SalaryRecord> buildSalaryRecords() {
        return dao.getAll().stream().map(p -> {
            double gross = p.getMaas() != null ? p.getMaas().doubleValue() : 0.0;
            LocalDate date = p.getGirisTarihi() != null ? p.getGirisTarihi() : LocalDate.now();
            return new SalaryRecord(
                    String.valueOf(p.getId()),
                    (p.getAd() != null ? p.getAd() : "") + " " + (p.getSoyad() != null ? p.getSoyad() : ""),
                    p.getPozisyon() != null ? p.getPozisyon() : "Bilinmiyor",
                    gross,
                    0.0,
                    0.0,
                    gross,
                    date
            );
        }).collect(Collectors.toList());
    }

    private void maasToplamHesapla() {
        double toplam = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String maasStr = (String) tableModel.getValueAt(i, 4);
            if (maasStr != null && !maasStr.isEmpty()) {
                maasStr = maasStr.replace("₺", "").trim().replace(",", ".");
                try {
                    toplam += Double.parseDouble(maasStr);
                } catch (NumberFormatException ex) {
                    System.err.println("Maaş okunamadı: " + maasStr);
                }
            }
        }

        JOptionPane.showMessageDialog(this,
                String.format("Toplam Maaş: %.2f ₺", toplam),
                "Maaş Toplamı",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        dao.getAll().forEach(p -> {
            Object[] row = {
                    p.getId(),
                    p.getAd(),
                    p.getSoyad(),
                    p.getPozisyon(),
                    p.getMaas() != null ? String.format("%.2f ₺", p.getMaas()) : "",
                    p.getGirisTarihi() != null ? p.getGirisTarihi().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : ""
            };
            tableModel.addRow(row);
        });
        System.out.println("✓ Personel listesi yüklendi. Toplam: " + tableModel.getRowCount());
    }

    private void ekle() {
        PersonelForm form = new PersonelForm(null);
        form.setVisible(true);

        if (form.isSaved()) {
            Personel p = form.getPersonel();
            if (dao.insert(p)) {
                JOptionPane.showMessageDialog(this,
                        "Personel başarıyla eklendi!",
                        "Başarılı",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Personel eklenirken hata oluştu!",
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void duzenle() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Lütfen düzenlemek için bir personel seçin!",
                    "Uyarı",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Personel personel = dao.getById(id);

        if (personel == null) {
            JOptionPane.showMessageDialog(this,
                    "Personel bulunamadı!",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        PersonelForm form = new PersonelForm(personel);
        form.setVisible(true);

        if (form.isSaved()) {
            if (dao.update(form.getPersonel())) {
                JOptionPane.showMessageDialog(this,
                        "Personel başarıyla güncellendi!",
                        "Başarılı",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Personel güncellenirken hata oluştu!",
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sil() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Lütfen silmek için bir personel seçin!",
                    "Uyarı",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String ad = (String) tableModel.getValueAt(selectedRow, 1);
        String soyad = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                ad + " " + soyad + " isimli personeli silmek istediğinize emin misiniz?",
                "Silme Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(this,
                        "Personel başarıyla silindi!",
                        "Başarılı",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Personel silinirken hata oluştu!",
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            ERPFrame frame = new ERPFrame();
            frame.setVisible(true);
        });
    }
}

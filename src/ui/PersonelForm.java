package ui;

import model.Personel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PersonelForm extends JDialog {
    private final JTextField txtAd = new JTextField(20);
    private final JTextField txtSoyad = new JTextField(20);
    private final JTextField txtPozisyon = new JTextField(20);
    private final JTextField txtMaas = new JTextField(10);
    private final JTextField txtGiris = new JTextField(10);
    private boolean saved = false;
    private Personel personel;

    public PersonelForm(Personel p) {
        setModal(true);
        setTitle(p == null ? "Yeni Personel Ekle" : "Personel Düzenle");
        setSize(450, 280);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Form paneli
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Ad:"));
        formPanel.add(txtAd);
        formPanel.add(new JLabel("Soyad:"));
        formPanel.add(txtSoyad);
        formPanel.add(new JLabel("Pozisyon:"));
        formPanel.add(txtPozisyon);
        formPanel.add(new JLabel("Maaş:"));
        formPanel.add(txtMaas);
        formPanel.add(new JLabel("Giriş Tarihi (YYYY-MM-DD):"));
        formPanel.add(txtGiris);

        add(formPanel, BorderLayout.CENTER);

        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnSave = new JButton("💾 Kaydet");
        JButton btnCancel = new JButton("❌ İptal");

        btnSave.setPreferredSize(new Dimension(100, 35));
        btnCancel.setPreferredSize(new Dimension(100, 35));

        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(149, 165, 166));
        btnCancel.setForeground(Color.WHITE);

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);

        // Mevcut personel varsa alanları doldur
        if (p != null) {
            this.personel = p;
            txtAd.setText(p.getAd());
            txtSoyad.setText(p.getSoyad());
            txtPozisyon.setText(p.getPozisyon());
            txtMaas.setText(p.getMaas() == null ? "" : p.getMaas().toString());
            txtGiris.setText(p.getGirisTarihi() == null ? "" : p.getGirisTarihi().toString());
        } else {
            // Yeni personel için bugünün tarihini varsayılan yap
            txtGiris.setText(LocalDate.now().toString());
        }

        // Kaydet butonu
        btnSave.addActionListener(e -> {
            if (txtAd.getText().isBlank() || txtSoyad.getText().isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Ad ve Soyad alanları zorunludur!",
                        "Uyarı",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (personel == null) {
                personel = new Personel();
            }

            personel.setAd(txtAd.getText().trim());
            personel.setSoyad(txtSoyad.getText().trim());
            personel.setPozisyon(txtPozisyon.getText().trim());

            try {
                personel.setMaas(txtMaas.getText().isBlank() ? null : new BigDecimal(txtMaas.getText().trim()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Maaş alanı geçerli bir sayı olmalıdır!",
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                personel.setGirisTarihi(txtGiris.getText().isBlank() ? LocalDate.now() : LocalDate.parse(txtGiris.getText().trim()));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Tarih formatı YYYY-MM-DD olmalıdır!\nÖrnek: 2024-01-15",
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            saved = true;
            setVisible(false);
        });

        // İptal butonu
        btnCancel.addActionListener(e -> {
            saved = false;
            setVisible(false);
        });


    }

    public boolean isSaved() {
        return saved;
    }

    public Personel getPersonel() {
        return personel;
    }
}
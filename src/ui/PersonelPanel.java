package ui;

import model.Personel;
import service.PersonelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PersonelPanel extends JPanel {
    private final PersonelService service = new PersonelService();
    private final JTable table;



    public PersonelPanel() {
        setLayout(new BorderLayout());
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        doldur();
    }

    private void doldur() {
        String[] kolonlar = {"ID", "Ad", "Soyad", "Pozisyon", "Maaş"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0);
        List<Personel> liste = service.listele();

        for (Personel p : liste) {
            model.addRow(new Object[]{
                    p.getId(), p.getAd(), p.getSoyad(), p.getPozisyon(), p.getMaas()
            });
        }
        table.setModel(model);
    }

}

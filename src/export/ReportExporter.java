package export;

import model.SalaryRecord;
import model.DepartmentSummary;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReportExporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    // --- 1. SalaryRecord listesini CSV olarak dışa aktarır ---
    public static void exportSalariesCsv(File file, List<SalaryRecord> records) throws IOException {
        if (file == null) throw new IllegalArgumentException("file is null");
        try (BufferedWriter w = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            w.write("id,name,department,gross,tax,insurance,net,date");
            w.newLine();
            for (SalaryRecord r : records) {
                String id = csvField(r.getId());
                String name = csvField(r.getName());
                String dept = csvField(r.getDepartment());
                String gross = numberField(r.getGross());
                String tax = numberField(r.getTax());
                String insurance = numberField(r.getInsurance());
                String net = numberField(r.getNet());
                String date = r.getDate() != null ? csvField(r.getDate().format(DATE_FORMAT)) : csvField("");
                w.write(String.join(",", id, name, dept, gross, tax, insurance, net, date));
                w.newLine();
            }
        }
    }

    // --- 2. Departman özetlerini dışa aktarır (şimdilik opsiyonel) ---
    public static void exportDepartmentCsv(File file, List<DepartmentSummary> summaries) throws IOException {
        if (file == null) throw new IllegalArgumentException("file is null");
        try (BufferedWriter w = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            w.write("department,total,average,count");
            w.newLine();
            for (DepartmentSummary s : summaries) {
                String dept = csvField(s.getDepartment());
                String total = numberField(s.getTotal());
                String avg = numberField(s.getAverage());
                String count = csvField(String.valueOf(s.getCount()));
                w.write(String.join(",", dept, total, avg, count));
                w.newLine();
            }
        }
    }

    // --- 3. JTable'daki verileri CSV olarak dışa aktarır ---
    public static void exportTableToCSV(java.awt.Component parent, JTable table) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("CSV olarak kaydet");
        chooser.setSelectedFile(new File("personel_listesi.csv"));
        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                TableModel model = table.getModel();
                int colCount = model.getColumnCount();

                // Başlık
                for (int i = 0; i < colCount; i++) {
                    writer.write(csvField(model.getColumnName(i)));
                    if (i < colCount - 1) writer.write(",");
                }
                writer.newLine();

                // Satırlar
                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 0; c < colCount; c++) {
                        Object value = model.getValueAt(r, c);
                        writer.write(csvField(value));
                        if (c < colCount - 1) writer.write(",");
                    }
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(parent, "CSV dosyası kaydedildi:\n" + file.getAbsolutePath(),
                        "Dışa Aktarma Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "CSV dışa aktarma hatası:\n" + ex.getMessage(),
                        "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- 4. SalaryRecord listesini CSV olarak dışa aktarır (GUI destekli) ---
    public static void exportSalaryRecordsToCSV(java.awt.Component parent, List<SalaryRecord> records) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Maaş kayıtlarını CSV olarak kaydet");
        chooser.setSelectedFile(new File("salary_records.csv"));
        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                exportSalariesCsv(file, records);
                JOptionPane.showMessageDialog(parent, "Maaş kayıtları CSV olarak kaydedildi:\n" + file.getAbsolutePath(),
                        "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "CSV dışa aktarma hatası:\n" + ex.getMessage(),
                        "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Yardımcı fonksiyonlar ---
    private static String csvField(Object v) {
        if (v == null) return "\"\"";
        String s = String.valueOf(v);
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + escaped + "\"" : escaped;
    }

    private static String numberField(Number n) {
        if (n == null) return "\"\"";
        String formatted = String.format(Locale.US, "%.2f", n.doubleValue());
        return csvField(formatted);
    }
}

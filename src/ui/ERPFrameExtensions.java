package ui;

import model.SalaryRecord;
import model.DepartmentSummary;
import export.ReportExporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ERPFrameExtensions {
    public static void attachPanels(JFrame frame, List<SalaryRecord> records) {
        DepartmentSummaryPanel summaryPanel = new DepartmentSummaryPanel();
        SalaryChartPanel chartPanel = new SalaryChartPanel();

        summaryPanel.updateData(records);
        chartPanel.updateData(records);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, summaryPanel, chartPanel);
        split.setResizeWeight(0.5);

        JButton exportSalaries = new JButton("CSV Dışa Aktar (Maaşlar)");
        exportSalaries.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    ReportExporter.exportSalariesCsv(fc.getSelectedFile(), records);
                    JOptionPane.showMessageDialog(frame, "Dışa aktarma tamamlandı.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Hata: " + ex.getMessage());
                }
            }
        });

        JButton exportSummary = new JButton("CSV Dışa Aktar (Özet)");
        exportSummary.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    List<DepartmentSummary> sums = records.stream()
                            .collect(Collectors.groupingBy(SalaryRecord::getDepartment))
                            .entrySet().stream()
                            .map(en -> {
                                double total = en.getValue().stream().mapToDouble(SalaryRecord::getGross).sum();
                                long cnt = en.getValue().size();
                                return new DepartmentSummary(en.getKey(), total, cnt>0?total/cnt:0, cnt);
                            }).collect(Collectors.toList());
                    ReportExporter.exportDepartmentCsv(fc.getSelectedFile(), sums);
                    JOptionPane.showMessageDialog(frame, "Dışa aktarma tamamlandı.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Hata: " + ex.getMessage());
                }
            }
        });

        JPanel topButtons = new JPanel();
        topButtons.add(exportSalaries);
        topButtons.add(exportSummary);

        frame.getContentPane().add(topButtons, BorderLayout.NORTH);
        frame.getContentPane().add(split, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    public static List<SalaryRecord> exampleData() {
        return List.of(
                new SalaryRecord("1","Ali","Muhasebe",10000,2000,500,7500, LocalDate.of(2025,1,10)),
                new SalaryRecord("2","Ayşe","Muhendislik",14000,3000,700,10300, LocalDate.of(2025,1,15)),
                new SalaryRecord("3","Mehmet","Muhendislik",12000,2500,600,9200, LocalDate.of(2025,2,5))
        );
    }
}

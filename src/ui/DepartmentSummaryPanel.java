// language: java
package ui;

import model.SalaryRecord;
import model.DepartmentSummary;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DepartmentSummaryPanel extends JPanel {
    private final SummaryTableModel model = new SummaryTableModel();

    public DepartmentSummaryPanel() {
        setLayout(new BorderLayout());
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void updateData(List<SalaryRecord> records) {
        Map<String, List<SalaryRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(SalaryRecord::getDepartment));
        List<DepartmentSummary> summaries = grouped.entrySet().stream()
                .map(e -> {
                    String dept = e.getKey();
                    double total = e.getValue().stream().mapToDouble(SalaryRecord::getGross).sum();
                    long count = e.getValue().size();
                    double avg = count > 0 ? total / count : 0;
                    return new DepartmentSummary(dept, total, avg, count);
                })
                .sorted((a,b) -> a.getDepartment().compareToIgnoreCase(b.getDepartment()))
                .collect(Collectors.toList());
        model.setData(summaries);
    }

    private static class SummaryTableModel extends AbstractTableModel {
        private List<DepartmentSummary> data = List.of();
        private final String[] cols = {"Departman", "Toplam (Brüt)", "Ortalama", "Kişi Sayısı"};

        public void setData(List<DepartmentSummary> list) {
            this.data = list;
            fireTableDataChanged();
        }

        public int getRowCount() { return data.size(); }
        public int getColumnCount() { return cols.length; }
        public String getColumnName(int col) { return cols[col]; }

        public Object getValueAt(int row, int col) {
            DepartmentSummary s = data.get(row);
            return switch (col) {
                case 0 -> s.getDepartment();
                case 1 -> s.getTotal();
                case 2 -> s.getAverage();
                case 3 -> s.getCount();
                default -> null;
            };
        }
    }
}

package ui;

import model.SalaryRecord;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SalaryChartPanel extends JPanel {
    private Map<YearMonth, Double> monthly = new TreeMap<>();

    public SalaryChartPanel() {
        setPreferredSize(new Dimension(600, 300));
    }

    public void updateData(List<SalaryRecord> records) {
        Map<YearMonth, Double> map = records.stream()
                .collect(Collectors.groupingBy(r -> YearMonth.from(r.getDate()),
                        Collectors.summingDouble(SalaryRecord::getGross)));
        monthly = new TreeMap<>(map);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (monthly.isEmpty()) {
            g0.drawString("Grafik için veri yok", 10, 20);
            return;
        }
        Graphics2D g = (Graphics2D) g0.create();
        int w = getWidth(), h = getHeight();
        int left = 60, right = 20, top = 20, bottom = 40;
        int gw = w - left - right, gh = h - top - bottom;

        double max = monthly.values().stream().mapToDouble(d -> d).max().orElse(1);
        double min = 0;

        g.setColor(Color.WHITE);
        g.fillRect(0,0,w,h);
        g.setColor(Color.BLACK);
        g.drawLine(left, top, left, top+gh);
        g.drawLine(left, top+gh, left+gw, top+gh);

        for (int i=0;i<=5;i++) {
            int y = top + gh - (int)((gh * i) / 5.0);
            double val = min + (max - min) * i / 5.0;
            g.drawLine(left-5, y, left, y);
            g.drawString(String.format("%.0f", val), 5, y+4);
        }

        int n = monthly.size();
        int idx = 0;
        int prevX=0, prevY=0;
        g.setColor(Color.BLUE);
        for (Map.Entry<YearMonth, Double> e : monthly.entrySet()) {
            int x = left + (int)((double)idx / Math.max(1, n-1) * gw);
            int y = top + gh - (int)(((e.getValue() - min) / (max - min + 1e-9)) * gh);
            g.fillOval(x-3, y-3, 6, 6);
            if (idx>0) {
                g.drawLine(prevX, prevY, x, y);
            }
            prevX = x; prevY = y;
            g.drawString(e.getKey().toString(), x-20, top+gh+15);
            idx++;
        }
        g.dispose();
    }
}

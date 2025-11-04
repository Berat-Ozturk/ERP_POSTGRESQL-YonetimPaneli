// language: java
package model;

import java.time.LocalDate;

public class SalaryRecord {
    private final String id;
    private final String name;
    private final String department;
    private final double gross;
    private final double tax;
    private final double insurance;
    private final double net;
    private final LocalDate date;

    public SalaryRecord(String id, String name, String department,
                        double gross, double tax, double insurance,
                        double net, LocalDate date) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.gross = gross;
        this.tax = tax;
        this.insurance = insurance;
        this.net = net;
        this.date = date;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getGross() { return gross; }
    public double getTax() { return tax; }
    public double getInsurance() { return insurance; }
    public double getNet() { return net; }
    public LocalDate getDate() { return date; }
}

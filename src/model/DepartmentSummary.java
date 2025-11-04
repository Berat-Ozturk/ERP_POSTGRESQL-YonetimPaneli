// language: java
package model;

public class DepartmentSummary {
    private final String department;
    private final double total;
    private final double average;
    private final long count;

    public DepartmentSummary(String department, double total, double average, long count) {
        this.department = department;
        this.total = total;
        this.average = average;
        this.count = count;
    }

    public String getDepartment() { return department; }
    public double getTotal() { return total; }
    public double getAverage() { return average; }
    public long getCount() { return count; }
}

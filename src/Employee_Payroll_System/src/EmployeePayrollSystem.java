//package payroll;

import java.sql.*;
import java.util.Scanner;

public class EmployeePayrollSystem {
    static final String URL = "jdbc:mysql://localhost:3306/payroll_db";
    static final String USER = "root";
    static final String PASSWORD = "Root@123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully.");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Scanner sc = new Scanner(System.in)) {
                System.out.println("✅ Connected to database.");

                while (true) {
                    System.out.println("\n--- Employee Payroll System ---");
                    System.out.println("1. Add Employee");
                    System.out.println("2. View Employees");
                    System.out.println("3. Generate Payroll");
                    System.out.println("4. View Payroll Records");
                    System.out.println("5. Exit");
                    System.out.print("Choice: ");

                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1:
                            addEmployee(conn, sc);
                            break;
                        case 2:
                            viewEmployees(conn);
                            break;
                        case 3:
                            generatePayroll(conn, sc);
                            break;
                        case 4:
                            viewPayroll(conn);
                            break;
                        case 5:
                            System.out.println("Exiting...");
                            return;
                        default:
                            System.out.println("Invalid choice!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addEmployee(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Position: ");
        String position = sc.nextLine();
        System.out.print("Salary: ");
        double salary = sc.nextDouble();

        String sql = "INSERT INTO employees (name, position, salary) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, position);
            ps.setDouble(3, salary);
            ps.executeUpdate();
            System.out.println("✅ Employee added.");
        }
    }

    static void viewEmployees(Connection conn) throws SQLException {
        String sql = "SELECT * FROM employees";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Employee List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                                   rs.getString("name") + " | " +
                                   rs.getString("position") + " | " +
                                   rs.getDouble("salary"));
            }
        }
    }

    static void generatePayroll(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Employee ID: ");
        int empId = sc.nextInt();
        sc.nextLine();
        System.out.print("Pay Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Amount: ");
        double amount = sc.nextDouble();

        String sql = "INSERT INTO payroll (employee_id, pay_date, amount) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setString(2, date);
            ps.setDouble(3, amount);
            ps.executeUpdate();
            System.out.println("✅ Payroll generated.");
        }
    }

    static void viewPayroll(Connection conn) throws SQLException {
        String sql = "SELECT p.id, e.name, p.pay_date, p.amount " +
                     "FROM payroll p JOIN employees e ON p.employee_id = e.id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Payroll Records ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                                   rs.getString("name") + " | " +
                                   rs.getDate("pay_date") + " | " +
                                   rs.getDouble("amount"));
            }
        }
    }
}

import java.sql.*;
import java.util.Scanner;

public class ExamResultManagementSystem {
    static final String URL = "jdbc:mysql://localhost:3306/examdb";
    static final String USER = "root"; // change to your MySQL username
    static final String PASSWORD = "Root@123"; // change to your MySQL password

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully.");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Scanner sc = new Scanner(System.in)) {

                System.out.println("✅ Connected to database.");

                while (true) {
                    System.out.println("\n--- Exam Result Management System ---");
                    System.out.println("1. Add Student");
                    System.out.println("2. Add Result");
                    System.out.println("3. View Students");
                    System.out.println("4. View Results");
                    System.out.println("5. Exit");
                    System.out.print("Choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1:
                            addStudent(conn, sc);
                            break;
                        case 2:
                            addResult(conn, sc);
                            break;
                        case 3:
                            viewStudents(conn);
                            break;
                        case 4:
                            viewResults(conn);
                            break;
                        case 5:
                            System.out.println("Exiting... Goodbye!");
                            return;
                        default:
                            System.out.println("❌ Invalid choice!");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addStudent(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Course: ");
        String course = sc.nextLine();

        String sql = "INSERT INTO students (name, course) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, course);
            pstmt.executeUpdate();
            System.out.println("✅ Student added successfully.");
        }
    }

    private static void addResult(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Student ID: ");
        int studentId = sc.nextInt();
        sc.nextLine();
        System.out.print("Subject: ");
        String subject = sc.nextLine();
        System.out.print("Marks: ");
        int marks = sc.nextInt();

        String sql = "INSERT INTO results (student_id, subject, marks) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, subject);
            pstmt.setInt(3, marks);
            pstmt.executeUpdate();
            System.out.println("✅ Result added successfully.");
        }
    }

    private static void viewStudents(Connection conn) throws SQLException {
        String sql = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Students ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("name") + " | " + rs.getString("course"));
            }
        }
    }

    private static void viewResults(Connection conn) throws SQLException {
        String sql = "SELECT r.id, s.name, s.course, r.subject, r.marks " +
                     "FROM results r JOIN students s ON r.student_id = s.id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Results ---");
            while (rs.next()) {
                System.out.println("Result ID: " + rs.getInt("id") + " | Name: " + rs.getString("name") +
                        " | Course: " + rs.getString("course") + " | Subject: " + rs.getString("subject") +
                        " | Marks: " + rs.getInt("marks"));
            }
        }
    }
}


import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    static final String URL = "jdbc:mysql://localhost:3306/hospitaldb";
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
                    System.out.println("\n--- Hospital Management Menu ---");
                    System.out.println("1. Add Patient");
                    System.out.println("2. Add Doctor");
                    System.out.println("3. Book Appointment");
                    System.out.println("4. View Patients");
                    System.out.println("5. View Doctors");
                    System.out.println("6. View Appointments");
                    System.out.println("7. Exit");
                    System.out.print("Choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1 -> addPatient(conn, sc);
                        case 2 -> addDoctor(conn, sc);
                        case 3 -> bookAppointment(conn, sc);
                        case 4 -> viewPatients(conn);
                        case 5 -> viewDoctors(conn);
                        case 6 -> viewAppointments(conn);
                        case 7 -> { System.out.println("👋 Exiting..."); return; }
                        default -> System.out.println("❌ Invalid choice.");
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addPatient(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Gender: ");
        String gender = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)");
        ps.setString(1, name);
        ps.setInt(2, age);
        ps.setString(3, gender);
        ps.executeUpdate();
        System.out.println("✅ Patient added.");
    }

    static void addDoctor(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Specialty: ");
        String specialty = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO doctors(name, specialty) VALUES (?, ?)");
        ps.setString(1, name);
        ps.setString(2, specialty);
        ps.executeUpdate();
        System.out.println("✅ Doctor added.");
    }

    static void bookAppointment(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Patient ID: ");
        int patientId = sc.nextInt();
        System.out.print("Doctor ID: ");
        int doctorId = sc.nextInt();
        sc.nextLine();
        System.out.print("Appointment Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)");
        ps.setInt(1, patientId);
        ps.setInt(2, doctorId);
        ps.setDate(3, Date.valueOf(date));
        ps.executeUpdate();
        System.out.println("✅ Appointment booked.");
    }

    static void viewPatients(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM patients");
        System.out.println("\n--- Patients ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %d | %s%n",
                    rs.getInt("id"), rs.getString("name"), rs.getInt("age"), rs.getString("gender"));
        }
    }

    static void viewDoctors(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM doctors");
        System.out.println("\n--- Doctors ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %s%n",
                    rs.getInt("id"), rs.getString("name"), rs.getString("specialty"));
        }
    }

    static void viewAppointments(Connection conn) throws SQLException {
        String query = """
                SELECT a.id, p.name AS patient, d.name AS doctor, a.appointment_date
                FROM appointments a
                JOIN patients p ON a.patient_id = p.id
                JOIN doctors d ON a.doctor_id = d.id
                """;
        ResultSet rs = conn.createStatement().executeQuery(query);
        System.out.println("\n--- Appointments ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %s | %s%n",
                    rs.getInt("id"), rs.getString("patient"), rs.getString("doctor"), rs.getDate("appointment_date"));
        }
    }
}

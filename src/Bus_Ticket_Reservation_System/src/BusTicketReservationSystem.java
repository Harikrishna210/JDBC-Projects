import java.sql.*;
import java.util.Scanner;

public class BusTicketReservationSystem {
    static final String URL = "jdbc:mysql://localhost:3306/busdb";
    static final String USER = "root"; // your MySQL username
    static final String PASSWORD = "Root@123"; // your MySQL password

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully.");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Scanner sc = new Scanner(System.in)) {

                System.out.println("✅ Connected to the database.");

                while (true) {
                    System.out.println("\n--- Bus Ticket Reservation ---");
                    System.out.println("1. View Buses");
                    System.out.println("2. Book Ticket");
                    System.out.println("3. View Bookings");
                    System.out.println("4. Exit");
                    System.out.print("Choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1:
                            viewBuses(conn);
                            break;
                        case 2:
                            bookTicket(conn, sc);
                            break;
                        case 3:
                            viewBookings(conn);
                            break;
                        case 4:
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

    private static void viewBuses(Connection conn) throws SQLException {
        String sql = "SELECT * FROM buses";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nAvailable Buses:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("bus_name") + " | "
                        + rs.getString("source") + " -> " + rs.getString("destination")
                        + " | Seats Available: " + rs.getInt("seats_available"));
            }
        }
    }

    private static void bookTicket(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Bus ID: ");
        int busId = sc.nextInt();
        sc.nextLine();
        System.out.print("Passenger Name: ");
        String name = sc.nextLine();
        System.out.print("Number of Seats: ");
        int seats = sc.nextInt();

        // Check seat availability
        String checkSql = "SELECT seats_available FROM buses WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setInt(1, busId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int available = rs.getInt("seats_available");
                if (seats <= available) {
                    // Book ticket
                    String insertBooking = "INSERT INTO bookings (bus_id, passenger_name, seats_booked) VALUES (?, ?, ?)";
                    try (PreparedStatement bookStmt = conn.prepareStatement(insertBooking)) {
                        bookStmt.setInt(1, busId);
                        bookStmt.setString(2, name);
                        bookStmt.setInt(3, seats);
                        bookStmt.executeUpdate();
                    }

                    // Update seats
                    String updateSeats = "UPDATE buses SET seats_available = seats_available - ? WHERE id=?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSeats)) {
                        updateStmt.setInt(1, seats);
                        updateStmt.setInt(2, busId);
                        updateStmt.executeUpdate();
                    }

                    System.out.println("✅ Booking confirmed for " + name);
                } else {
                    System.out.println("❌ Not enough seats available!");
                }
            } else {
                System.out.println("❌ Bus not found!");
            }
        }
    }

    private static void viewBookings(Connection conn) throws SQLException {
        String sql = "SELECT b.id, b.passenger_name, bu.bus_name, bu.source, bu.destination, b.seats_booked " +
                     "FROM bookings b JOIN buses bu ON b.bus_id = bu.id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nBookings:");
            while (rs.next()) {
                System.out.println("Booking ID: " + rs.getInt("id") + " | Passenger: " + rs.getString("passenger_name") +
                        " | Bus: " + rs.getString("bus_name") + " | Route: " + rs.getString("source") + " -> " + rs.getString("destination") +
                        " | Seats: " + rs.getInt("seats_booked"));
            }
        }
    }
}

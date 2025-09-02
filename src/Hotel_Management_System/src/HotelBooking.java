import java.sql.*;
import java.util.Scanner;

public class HotelBooking {
    static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
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
                    System.out.println("\n--- Hotel Booking System ---");
                    System.out.println("1. View Available Rooms");
                    System.out.println("2. Book Room");
                    System.out.println("3. View Bookings");
                    System.out.println("4. Exit");
                    System.out.print("Choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1:
                            viewRooms(conn);
                            break;
                        case 2:
                            bookRoom(conn, sc);
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

    private static void viewRooms(Connection conn) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE available = TRUE";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Available Rooms ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        " | Room No: " + rs.getInt("room_number") +
                        " | Type: " + rs.getString("type") +
                        " | Price per Day: ₹" + rs.getDouble("price"));
            }
        }
    }

    private static void bookRoom(Connection conn, Scanner sc) throws SQLException {
        viewRooms(conn);
        System.out.print("Enter Room ID to book: ");
        int roomId = sc.nextInt();
        System.out.print("Enter your name: ");
        sc.nextLine();
        String name = sc.nextLine();
        System.out.print("Number of days: ");
        int days = sc.nextInt();

        String checkRoom = "SELECT price, available FROM rooms WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkRoom)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (!rs.getBoolean("available")) {
                    System.out.println("❌ Room is already booked.");
                    return;
                }
                double pricePerDay = rs.getDouble("price");
                double total = pricePerDay * days;

                String insertBooking = "INSERT INTO bookings (customer_name, room_id, days, total_price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertBooking)) {
                    insertStmt.setString(1, name);
                    insertStmt.setInt(2, roomId);
                    insertStmt.setInt(3, days);
                    insertStmt.setDouble(4, total);
                    insertStmt.executeUpdate();
                }

                String updateRoom = "UPDATE rooms SET available = FALSE WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateRoom)) {
                    updateStmt.setInt(1, roomId);
                    updateStmt.executeUpdate();
                }

                System.out.println("✅ Room booked successfully! Total Price: ₹" + total);
            } else {
                System.out.println("❌ Invalid Room ID.");
            }
        }
    }

    private static void viewBookings(Connection conn) throws SQLException {
        String sql = "SELECT b.id, b.customer_name, r.room_number, r.type, b.days, b.total_price " +
                     "FROM bookings b JOIN rooms r ON b.room_id = r.id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- All Bookings ---");
            while (rs.next()) {
                System.out.println("Booking ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("customer_name") +
                        " | Room No: " + rs.getInt("room_number") +
                        " | Type: " + rs.getString("type") +
                        " | Days: " + rs.getInt("days") +
                        " | Total: ₹" + rs.getDouble("total_price"));
            }
        }
    }
}

import java.sql.*;
import java.util.Scanner;

public class HotelBookingSystem {
    static final String URL = "jdbc:mysql://localhost:3306/hoteldb";
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
                    System.out.println("\n--- Hotel Booking Menu ---");
                    System.out.println("1. Add Guest");
                    System.out.println("2. Add Room");
                    System.out.println("3. Book Room");
                    System.out.println("4. View Guests");
                    System.out.println("5. View Rooms");
                    System.out.println("6. View Bookings");
                    System.out.println("7. Exit");
                    System.out.print("Choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1 -> addGuest(conn, sc);
                        case 2 -> addRoom(conn, sc);
                        case 3 -> bookRoom(conn, sc);
                        case 4 -> viewGuests(conn);
                        case 5 -> viewRooms(conn);
                        case 6 -> viewBookings(conn);
                        case 7 -> { System.out.println("👋 Exiting..."); return; }
                        default -> System.out.println("❌ Invalid choice.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addGuest(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Phone: ");
        String phone = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO guests(name, phone) VALUES (?, ?)");
        ps.setString(1, name);
        ps.setString(2, phone);
        ps.executeUpdate();
        System.out.println("✅ Guest added.");
    }

    static void addRoom(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Room Number: ");
        String roomNumber = sc.nextLine();
        System.out.print("Type: ");
        String type = sc.nextLine();
        System.out.print("Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO rooms(room_number, type, price) VALUES (?, ?, ?)");
        ps.setString(1, roomNumber);
        ps.setString(2, type);
        ps.setDouble(3, price);
        ps.executeUpdate();
        System.out.println("✅ Room added.");
    }

    static void bookRoom(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Guest ID: ");
        int guestId = sc.nextInt();
        System.out.print("Room ID: ");
        int roomId = sc.nextInt();
        sc.nextLine();
        System.out.print("Check-in Date (YYYY-MM-DD): ");
        String checkIn = sc.nextLine();
        System.out.print("Check-out Date (YYYY-MM-DD): ");
        String checkOut = sc.nextLine();

        // Check if room is available
        PreparedStatement checkRoom = conn.prepareStatement("SELECT available FROM rooms WHERE id = ?");
        checkRoom.setInt(1, roomId);
        ResultSet rs = checkRoom.executeQuery();
        if (rs.next() && !rs.getBoolean("available")) {
            System.out.println("❌ Room is not available.");
            return;
        }

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO bookings(guest_id, room_id, check_in, check_out) VALUES (?, ?, ?, ?)");
        ps.setInt(1, guestId);
        ps.setInt(2, roomId);
        ps.setDate(3, Date.valueOf(checkIn));
        ps.setDate(4, Date.valueOf(checkOut));
        ps.executeUpdate();

        PreparedStatement updateRoom = conn.prepareStatement("UPDATE rooms SET available = FALSE WHERE id = ?");
        updateRoom.setInt(1, roomId);
        updateRoom.executeUpdate();

        System.out.println("✅ Room booked successfully.");
    }

    static void viewGuests(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM guests");
        System.out.println("\n--- Guests ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %s%n", rs.getInt("id"), rs.getString("name"), rs.getString("phone"));
        }
    }

    static void viewRooms(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM rooms");
        System.out.println("\n--- Rooms ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %s | %.2f | %s%n",
                    rs.getInt("id"), rs.getString("room_number"), rs.getString("type"),
                    rs.getDouble("price"), rs.getBoolean("available") ? "Available" : "Booked");
        }
    }

    static void viewBookings(Connection conn) throws SQLException {
        String query = """
                SELECT b.id, g.name AS guest, r.room_number, b.check_in, b.check_out
                FROM bookings b
                JOIN guests g ON b.guest_id = g.id
                JOIN rooms r ON b.room_id = r.id
                """;
        ResultSet rs = conn.createStatement().executeQuery(query);
        System.out.println("\n--- Bookings ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %s | %s | %s%n",
                    rs.getInt("id"), rs.getString("guest"), rs.getString("room_number"),
                    rs.getDate("check_in"), rs.getDate("check_out"));
        }
    }
}

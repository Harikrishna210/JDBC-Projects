import java.sql.*;
import java.util.Scanner;

public class CinemaTicketBookingSystem {
    static final String URL = "jdbc:mysql://localhost:3306/cinemadb";
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
                    System.out.println("\n--- Cinema Ticket Booking System ---");
                    System.out.println("1. View Movies");
                    System.out.println("2. Book Ticket");
                    System.out.println("3. View Bookings");
                    System.out.println("4. Exit");
                    System.out.print("Choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1:
                            viewMovies(conn);
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

    private static void viewMovies(Connection conn) throws SQLException {
        String sql = "SELECT * FROM movies";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Available Movies ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("title") +
                        " | Show Time: " + rs.getString("show_time") +
                        " | Price: ₹" + rs.getDouble("price"));
            }
        }
    }

    private static void bookTicket(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        viewMovies(conn);
        System.out.print("Enter Movie ID: ");
        int movieId = sc.nextInt();
        System.out.print("Number of Seats: ");
        int seats = sc.nextInt();

        // Fetch movie price
        String priceQuery = "SELECT price FROM movies WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(priceQuery)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double price = rs.getDouble("price");
                double total = price * seats;

                String insertBooking = "INSERT INTO bookings (customer_name, movie_id, seats, total_price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertBooking)) {
                    insertStmt.setString(1, name);
                    insertStmt.setInt(2, movieId);
                    insertStmt.setInt(3, seats);
                    insertStmt.setDouble(4, total);
                    insertStmt.executeUpdate();
                    System.out.println("✅ Booking successful! Total Price: ₹" + total);
                }
            } else {
                System.out.println("❌ Invalid Movie ID.");
            }
        }
    }

    private static void viewBookings(Connection conn) throws SQLException {
        String sql = "SELECT b.id, b.customer_name, m.title, m.show_time, b.seats, b.total_price " +
                     "FROM bookings b JOIN movies m ON b.movie_id = m.id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- All Bookings ---");
            while (rs.next()) {
                System.out.println("Booking ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("customer_name") +
                        " | Movie: " + rs.getString("title") +
                        " | Show Time: " + rs.getString("show_time") +
                        " | Seats: " + rs.getInt("seats") +
                        " | Total: ₹" + rs.getDouble("total_price"));
            }
        }
    }
}

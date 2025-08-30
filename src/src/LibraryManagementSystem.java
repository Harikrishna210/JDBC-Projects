import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class LibraryManagementSystem {
    static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    static final String USER = "root";
    static final String PASSWORD = "Root@123"; // change this

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully.");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Scanner sc = new Scanner(System.in)) {

                System.out.println("✅ Connected to the database successfully.");

                while (true) {
                    System.out.println("\n--- Library Menu ---");
                    System.out.println("1. Add Book");
                    System.out.println("2. Add Member");
                    System.out.println("3. Borrow Book");
                    System.out.println("4. Return Book");
                    System.out.println("5. View Books");
                    System.out.println("6. Exit");
                    System.out.print("Choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 1) addBook(conn, sc);
                    else if (choice == 2) addMember(conn, sc);
                    else if (choice == 3) borrowBook(conn, sc);
                    else if (choice == 4) returnBook(conn, sc);
                    else if (choice == 5) viewBooks(conn);
                    else if (choice == 6) {
                        System.out.println("👋 Exiting...");
                        break;
                    } else System.out.println("❌ Invalid choice");
                }

            } catch (SQLException e) {
                System.err.println("❌ Database connection failed. Please check URL, username, and password.");
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Please add mysql-connector-j-8.4.0.jar to the Build Path.");
            e.printStackTrace();
        }
    }

    static void addBook(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Title: ");
        String title = sc.nextLine();
        System.out.print("Author: ");
        String author = sc.nextLine();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO books(title, author, available) VALUES(?, ?, true)");
        ps.setString(1, title);
        ps.setString(2, author);
        ps.executeUpdate();
        System.out.println("Book added.");
    }

    static void addMember(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Name: ");
        String name = sc.nextLine();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO members(name) VALUES(?)");
        ps.setString(1, name);
        ps.executeUpdate();
        System.out.println("Member added.");
    }

    static void borrowBook(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Book ID: ");
        int bookId = sc.nextInt();
        System.out.print("Member ID: ");
        int memberId = sc.nextInt();
        sc.nextLine();

        PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE id=?");
        check.setInt(1, bookId);
        ResultSet rs = check.executeQuery();
        if (rs.next() && rs.getBoolean(1)) {
            PreparedStatement borrow = conn.prepareStatement(
                "INSERT INTO borrowings(book_id, member_id, borrow_date) VALUES(?, ?, ?)");
            borrow.setInt(1, bookId);
            borrow.setInt(2, memberId);
            borrow.setDate(3, Date.valueOf(LocalDate.now()));
            borrow.executeUpdate();

            PreparedStatement updateBook = conn.prepareStatement("UPDATE books SET available=false WHERE id=?");
            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();
            System.out.println("Book borrowed.");
        } else {
            System.out.println("Book not available.");
        }
    }

    static void returnBook(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Borrowing ID: ");
        int borrowId = sc.nextInt();
        sc.nextLine();

        PreparedStatement getBook = conn.prepareStatement("SELECT book_id FROM borrowings WHERE id=?");
        getBook.setInt(1, borrowId);
        ResultSet rs = getBook.executeQuery();
        if (rs.next()) {
            int bookId = rs.getInt(1);

            PreparedStatement updateBorrow = conn.prepareStatement("UPDATE borrowings SET return_date=? WHERE id=?");
            updateBorrow.setDate(1, Date.valueOf(LocalDate.now()));
            updateBorrow.setInt(2, borrowId);
            updateBorrow.executeUpdate();

            PreparedStatement updateBook = conn.prepareStatement("UPDATE books SET available=true WHERE id=?");
            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            System.out.println("Book returned.");
        } else {
            System.out.println("Invalid borrowing ID.");
        }
    }

    static void viewBooks(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM books");
        System.out.println("\n--- Books ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %s | %s%n",
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getBoolean("available") ? "Available" : "Borrowed");
        }
    }
}

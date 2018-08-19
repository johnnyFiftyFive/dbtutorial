package org.j55.dbtutorial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author johnnyFiftyFive
 */
public class DbApp {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/BOOKSTORE";
        String username = "root";
        String password = "dummy_passord";

        System.out.println("Connecting database...");

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            System.out.println("Database connected!");

            insertBook(new Book("Harry Potter i Serwer Tajemnic", "J.K. Rowling", 2021), connection);
            insertBook(new Book("Czolgi i myszki", "G.G. Rozaupr", 1918), connection);
            insertBook(new Book("Nigdy wiecej taniej whisky", "Ernest Hemingway", 1936), connection);

            List<Book> books = selectAllBooks(connection);
            System.out.println("Liczba książek: " + books.size());
            System.out.println("Lista pobranych ksiazek: ");
            for (Book book : books) {
                System.out.println(book);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Błąd przy zamykaniu połączenia do bazy: " + e);
                }
            }
        }
    }

    private static List<Book> selectAllBooks(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select ID, TITLE, AUTHOR, ISSUE_YEAR from BOOK");
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        List<Book> bookList = new ArrayList<>();
        while (resultSet.next()) {
            long id = resultSet.getLong("ID");
            String title = resultSet.getString("TITLE");
            String author = resultSet.getString("AUTHOR");
            int year = resultSet.getInt("ISSUE_YEAR");
            bookList.add(new Book(id, title, author, year));
        }
        resultSet.close();
        return bookList;
    }

    private static void insertBook(Book book, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("insert into BOOK(TITLE, AUTHOR, ISSUE_YEAR) values (?, ?, ?)");
        statement.setString(1, book.getTitle());
        statement.setString(2, book.getAuthor());
        statement.setInt(3, book.getYear());

        statement.executeUpdate();
        connection.commit();
    }
}

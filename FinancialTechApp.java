import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FinancialTechApp {

    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/financial_db";
    private static final String USERNAME = "sagar1024";
    private static final String PASSWORD = "sagar1024";

    // JDBC variables for opening, closing, and managing the database connection
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            // Opening a connection
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();

            // Creating a financial transaction
            createFinancialTransaction(1234567890, "Deposit", 1000.0);

            // Retrieving and displaying account balance
            double accountBalance = getAccountBalance(1234567890);
            System.out.println("Account Balance: $" + accountBalance);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Closing the connection
            try {
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createFinancialTransaction(long accountNumber, String transactionType, double amount)
            throws SQLException {
        // Getting the current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String transactionDate = dateFormat.format(new Date());

        // Inserting the financial transaction into the database
        String insertQuery = "INSERT INTO transactions (account_number, transaction_type, amount, transaction_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, transactionType);
            preparedStatement.setDouble(3, amount);
            preparedStatement.setString(4, transactionDate);
            preparedStatement.executeUpdate();
        }
    }

    private static double getAccountBalance(long accountNumber) throws SQLException {
        // Retrievign the total balance for the specified account
        String selectQuery = "SELECT SUM(amount) AS total_balance FROM transactions WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setLong(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("total_balance");
            }
        }
        return 0.0;
    }
}

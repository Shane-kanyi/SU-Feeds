import javax.swing.SwingUtilities;
import com.lastname.sufeeds.SuFeedsGUI;
import com.lastname.sufeeds.DatabaseManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Database connection details
        String url = "jdbc:postgresql://localhost:5432/db_first_last_studentNumber";
        String user = "postgres";
        String password = "2807";

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");

            // Close the connection
            connection.close();
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed.");
            e.printStackTrace();
        }

        DatabaseManager dbManager = new DatabaseManager();

        SwingUtilities.invokeLater(() -> {
            SuFeedsGUI gui = new SuFeedsGUI(dbManager);
            gui.setVisible(true);
        });
    }
}
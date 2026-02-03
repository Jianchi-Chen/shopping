import java.sql.*;

public class CreateDatabase {
    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection conn = DriverManager.getConnection(url, "postgres", "dcncloud");
        Statement stmt = conn.createStatement();
        
        try {
            stmt.execute("CREATE DATABASE shopping");
            System.out.println("Database 'shopping' created successfully!");
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Database 'shopping' already exists");
            } else {
                throw e;
            }
        }
        
        stmt.close();
        conn.close();
    }
}

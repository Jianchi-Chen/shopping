import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InitDatabase {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "dcncloud";
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            
            // 检查数据库是否存在
            stmt.execute("SELECT 1 FROM pg_database WHERE datname = 'shopping'");
            if (stmt.getResultSet().next()) {
                System.out.println("Database 'shopping' already exists.");
            } else {
                stmt.execute("CREATE DATABASE shopping");
                System.out.println("Database 'shopping' created successfully.");
            }
            
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

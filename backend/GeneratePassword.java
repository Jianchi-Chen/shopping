import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "admin123";
        String hashed = encoder.encode(password);
        
        System.out.println("密码: " + password);
        System.out.println("BCrypt 哈希: " + hashed);
        System.out.println("\n将以下内容复制到 init.sql:");
        System.out.println("'" + hashed + "'");
        
        // 验证
        System.out.println("\n验证: " + encoder.matches(password, hashed));
    }
}

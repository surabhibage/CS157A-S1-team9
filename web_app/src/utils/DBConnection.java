package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/libsystem";
            String user = "root";
            String password = "admin123";

            Connection con = DriverManager.getConnection(url, user, password);

            System.out.println("✅ DB Connected!");

            return con;

        } catch (Exception e) {
            System.out.println("❌ DB ERROR:");
            e.printStackTrace();   // 🔥 THIS IS IMPORTANT
            return null;
        }
    }
}
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve form data
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String ageStr = request.getParameter("age");
        String role = request.getParameter("role");

        // Simple parsing for age, defaulting to 0 if left blank
        int age = 0;
        if (ageStr != null && !ageStr.trim().isEmpty()) {
            age = Integer.parseInt(ageStr);
        }

        // Generate a simple unique ID (In production, use AUTO_INCREMENT in MySQL instead)
        int userId = (int)(Math.random() * 100000);

        String dbUser = "root";
        String dbPassword = "your_mysql_password";

        Connection con = null;
        PreparedStatement pst = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/LibraryManagementSys?autoReconnect=true&useSSL=false";
            con = DriverManager.getConnection(url, dbUser, dbPassword);

            // Prepare the SQL INSERT statement
            String sql = "INSERT INTO Users (user_id, username, password_hash, first_name, last_name, phone_number, address, age, role) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setString(2, username);
            pst.setString(3, password); // Note: Hash this in a real application!
            pst.setString(4, firstName);
            pst.setString(5, lastName);
            pst.setString(6, phone);
            pst.setString(7, address);
            pst.setInt(8, age);
            pst.setString(9, role);

            // Execute the insert
            pst.executeUpdate();

            // If successful, send them to the login page to sign in
            response.sendRedirect("login.jsp?registered=true");

        } catch (SQLIntegrityConstraintViolationException e) {
            // This catches the error if the username already exists in the database
            response.sendRedirect("register.jsp?error=duplicate");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=exception");
        } finally {
            try { if (pst != null) pst.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
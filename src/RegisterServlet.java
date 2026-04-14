//package src;
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

        // 1. Retrieve form data
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        String dbUser = "root";
        String dbPassword = "ne83De-JVui";
        String url = "jdbc:mysql://localhost:3306/Team9LibSys?autoReconnect=true&useSSL=false";

        Connection con = null;
        PreparedStatement pst = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, dbUser, dbPassword);

            // 2. Updated SQL (Removed User_ID because of AUTO_INCREMENT)
            // Column names match your CREATE TABLE exactly
            String sql = "INSERT INTO Users (Username, Password, First_Name, Last_Name, Phone, Address) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            pst = con.prepareStatement(sql);

            // 3. Mapping parameters (Note the index shift)
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, firstName);
            pst.setString(4, lastName);
            pst.setString(5, phone);
            pst.setString(6, address);

            // 4. Execute the insert
            pst.executeUpdate();

            // Success redirect
            response.sendRedirect("login.jsp?registered=true");

        } catch (SQLIntegrityConstraintViolationException e) {
            // Catches duplicate usernames
            response.sendRedirect("register.jsp?error=duplicate");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=exception");
        } finally {
            try { if (pst != null) pst.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (con != null) con.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
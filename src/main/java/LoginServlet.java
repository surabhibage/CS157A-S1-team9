//package web_app.src;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Get the parameters from the HTML form
        String userParam = request.getParameter("username");
        String passParam = request.getParameter("password");

        String dbUser = "root";
        String dbPassword = "root";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // 2. Load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 3. Establish the Connection to the 'Class' database
            String url = "jdbc:mysql://localhost:3306/Team9LibSys?autoReconnect=true&useSSL=false";
            con = DriverManager.getConnection(url, dbUser, dbPassword);

            // 4. Base SQL Query using PreparedStatement to prevent SQL injection
            String sql = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, userParam);
            pst.setString(2, passParam); // In a real app, hash passParam before comparing

            rs = pst.executeQuery();

            // 5. Check if a user was found
            if (rs.next()) {
                // Login successful! Create a session.
                HttpSession session = request.getSession();
                session.setAttribute("username", rs.getString("username"));
                //session.setAttribute("role", rs.getString("role")); // e.g., 'Borrower' or 'Admin'

                // Redirect to the home page
                response.sendRedirect("home.jsp");
            } else {
                // Login failed. Redirect back to login with an error flag.
                response.sendRedirect("login.jsp?error=invalid");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=exception");
        } finally {
            // 6. Clean up database resources
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (pst != null) pst.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
// package src;
import db.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
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

    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      con = DatabaseConnection.getConnection();

      // 4. Base SQL Query using PreparedStatement to prevent SQL injection
      String sql = "SELECT * FROM Users WHERE Username = ?";
      pst = con.prepareStatement(sql);
      pst.setString(1, userParam);

      rs = pst.executeQuery();

      // 5. Check if a user was found
      if (rs.next()) {
        String hashedPass = rs.getString("Password");
        if (org.mindrot.jbcrypt.BCrypt.checkpw(passParam, hashedPass)) {
          // Login successful! Create a session.
          HttpSession session = request.getSession();
          session.setAttribute("username", rs.getString("username"));
          int userId = rs.getInt("User_ID");
          session.setAttribute("userId", userId);

          String adminCheckSql = "SELECT * FROM Admin WHERE User_ID = ?";
          try (PreparedStatement adminPst = con.prepareStatement(adminCheckSql)) {
            adminPst.setInt(1, userId);
            try (ResultSet adminRs = adminPst.executeQuery()) {
              if (adminRs.next()) {
                session.setAttribute("role", "Admin");
              } else {
                session.setAttribute("role", "Borrower");
              }
            }
          }

          // Redirect to the home page
          response.sendRedirect("home.jsp");
        } else {
          // Password did not match
          response.sendRedirect("login.jsp?error=invalid");
        }
      } else {
        // User not found. Redirect back to login with an error flag.
        response.sendRedirect("login.jsp?error=invalid");
      }

    } catch (Exception e) {
      e.printStackTrace();
      response.sendRedirect("login.jsp?error=exception");
    } finally {
      // 6. Clean up database resources
      try {
        if (rs != null) rs.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if (pst != null) pst.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if (con != null) con.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

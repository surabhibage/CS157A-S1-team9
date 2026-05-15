// package src;
import db.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
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

    Connection con = null;
    PreparedStatement pst = null;

    try {
      con = DatabaseConnection.getConnection();

      // 2. Updated SQL (Removed User_ID because of AUTO_INCREMENT)
      // Column names match your CREATE TABLE exactly
      String sql =
          "INSERT INTO Users (Username, Password, First_Name, Last_Name, Phone, Address) "
              + "VALUES (?, ?, ?, ?, ?, ?)";

      pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

      // 3. Mapping parameters (Note the index shift)
      pst.setString(1, username);
      pst.setString(2, org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt()));
      pst.setString(3, firstName);
      pst.setString(4, lastName);
      pst.setString(5, phone);
      pst.setString(6, address);

      // 4. Execute the insert
      pst.executeUpdate();

      int userId = -1;
      try (java.sql.ResultSet generatedKeys = pst.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          userId = generatedKeys.getInt(1);
        }
      }

      String role = request.getParameter("role");
      if ("Admin".equals(role)) {
        String adminSql = "INSERT INTO Admin (User_ID, EmployeeID, Permissions) VALUES (?, ?, ?)";
        try (PreparedStatement adminPst = con.prepareStatement(adminSql)) {
          adminPst.setInt(1, userId);
          adminPst.setInt(2, (int) (Math.random() * 10000));
          adminPst.setString(3, "All");
          adminPst.executeUpdate();
        }
      } else {
        String borrowerSql =
            "INSERT INTO Borrower (User_ID, CardNum, maxBorrowed, numBorrowed) VALUES (?, ?, ?, ?)";
        try (PreparedStatement borrowerPst = con.prepareStatement(borrowerSql)) {
          borrowerPst.setInt(1, userId);
          borrowerPst.setInt(2, (int) (Math.random() * 100000)); // Random Card Number
          borrowerPst.setInt(3, 5); // Default max borrowed
          borrowerPst.setInt(4, 0); // Default num borrowed
          borrowerPst.executeUpdate();
        }
      }

      // Success redirect
      response.sendRedirect("login.jsp?registered=true");

    } catch (SQLIntegrityConstraintViolationException e) {
      // Catches duplicate usernames
      response.sendRedirect("register.jsp?error=duplicate");
    } catch (Exception e) {
      e.printStackTrace();
      response.sendRedirect("register.jsp?error=exception");
    } finally {
      try {
        if (pst != null) pst.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        if (con != null) con.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}

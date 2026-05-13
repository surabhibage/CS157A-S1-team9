import db.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateContactServlet")
public class UpdateContactServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      response.sendRedirect("login.jsp");
      return;
    }

    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");
    String phone = request.getParameter("phone");
    String address = request.getParameter("address");

    try (Connection con = DatabaseConnection.getConnection()) {
      StringBuilder sql = new StringBuilder("UPDATE Users SET ");
      boolean first = true;
      if (firstName != null && !firstName.isEmpty()) {
        sql.append("First_Name = ?");
        first = false;
      }
      if (lastName != null && !lastName.isEmpty()) {
        if (!first)
          sql.append(", ");
        sql.append("Last_Name = ?");
        first = false;
      }
      if (phone != null && !phone.isEmpty()) {
        if (!first)
          sql.append(", ");
        sql.append("Phone = ?");
        first = false;
      }
      if (address != null && !address.isEmpty()) {
        if (!first)
          sql.append(", ");
        sql.append("Address = ?");
        first = false;
      }

      if (first) {
        response.sendRedirect("settings.jsp"); // Nothing to update
        return;
      }
      sql.append(" WHERE User_ID = ?");

      try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
        int idx = 1;
        if (firstName != null && !firstName.isEmpty()) {
          pst.setString(idx++, firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
          pst.setString(idx++, lastName);
        }
        if (phone != null && !phone.isEmpty()) {
          pst.setString(idx++, phone);
        }
        if (address != null && !address.isEmpty()) {
          pst.setString(idx++, address);
        }
        pst.setInt(idx, userId);
        pst.executeUpdate();
      }
      response.sendRedirect("settings.jsp?msg=contact_updated");
    } catch (Exception e) {
      e.printStackTrace();
      response.sendRedirect("settings.jsp?error=contact_exception");
    }
  }
}

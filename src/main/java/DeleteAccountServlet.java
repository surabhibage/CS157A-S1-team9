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

@WebServlet("/DeleteAccountServlet")
public class DeleteAccountServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      response.sendRedirect("login.jsp");
      return;
    }

    Connection con = null;
    try {
      con = DatabaseConnection.getConnection();
      con.setAutoCommit(false);

      // Delete dependencies in correct order to avoid foreign key constraints
      String[] sqls = {
          "DELETE FROM Borrows WHERE User_ID = ?",
          "DELETE FROM Requests WHERE User_ID = ?",
          "DELETE FROM Registers WHERE User_ID = ?",
          "DELETE FROM Approves WHERE User_ID = ?",
          "DELETE FROM Admin_Modifies WHERE User_ID = ?",
          "DELETE FROM Edits WHERE User_ID = ?",
          "DELETE FROM Borrower WHERE User_ID = ?",
          "DELETE FROM Admin WHERE User_ID = ?",
          "DELETE FROM Users WHERE User_ID = ?"
      };

      for (String sql : sqls) {
          try (PreparedStatement pst = con.prepareStatement(sql)) {
              pst.setInt(1, userId);
              pst.executeUpdate();
          }
      }

      con.commit();
      session.invalidate();
      response.sendRedirect("login.jsp?msg=account_deleted");

    } catch (Exception e) {
      if (con != null) {
          try { con.rollback(); } catch (Exception ex) {}
      }
      e.printStackTrace();
      response.sendRedirect("settings.jsp?error=delete_exception");
    } finally {
      if (con != null) {
          try { con.close(); } catch (Exception ex) {}
      }
    }
  }
}

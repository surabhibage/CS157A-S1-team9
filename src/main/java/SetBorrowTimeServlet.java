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

@WebServlet("/SetBorrowTimeServlet")
public class SetBorrowTimeServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer userId = (Integer) session.getAttribute("userId");
    String role = (String) session.getAttribute("role");

    if (userId == null || !"Admin".equals(role)) {
      response.sendRedirect("login.jsp");
      return;
    }

    String borrowTimeStr = request.getParameter("borrowTime");
    int borrowTime = 14;
    try {
      borrowTime = Integer.parseInt(borrowTimeStr);
    } catch (NumberFormatException e) {
      response.sendRedirect("settings.jsp?error=borrow_exception");
      return;
    }

    try (Connection con = DatabaseConnection.getConnection();
        PreparedStatement pst = con.prepareStatement("UPDATE Admin SET BorrowTime = ? WHERE User_ID = ?")) {
      pst.setInt(1, borrowTime);
      pst.setInt(2, userId);
      pst.executeUpdate();
      response.sendRedirect("settings.jsp?msg=borrow_updated");
    } catch (Exception e) {
      e.printStackTrace();
      response.sendRedirect("settings.jsp?error=borrow_exception");
    }
  }
}

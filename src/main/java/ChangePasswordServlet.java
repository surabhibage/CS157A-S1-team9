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
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
      response.sendRedirect("login.jsp");
      return;
    }

    String newPassword = request.getParameter("newPassword");
    String confirmPassword = request.getParameter("confirmPassword");

    if (!newPassword.equals(confirmPassword)) {
      response.sendRedirect("settings.jsp?error=pwd_mismatch");
      return;
    }

    String hashedPass = BCrypt.hashpw(newPassword, BCrypt.gensalt());

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement pst = con.prepareStatement("UPDATE Users SET Password = ? WHERE User_ID = ?")) {
      pst.setString(1, hashedPass);
      pst.setInt(2, userId);
      pst.executeUpdate();
      response.sendRedirect("settings.jsp?msg=pwd_updated");
    } catch (Exception e) {
      e.printStackTrace();
      response.sendRedirect("settings.jsp?error=pwd_exception");
    }
  }
}

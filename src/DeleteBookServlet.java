import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteBookServlet")
public class DeleteBookServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookId = request.getParameter("bookId");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Team9LibSys", "root", "ne83De-JVui");

            // SQL to delete the specific book
            String sql = "DELETE FROM Book WHERE Book_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookId);

            pstmt.executeUpdate();
            conn.close();

            // Redirect back to home.jsp to see the updated list
            response.sendRedirect("home.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home.jsp?error=DeleteFailed");
        }
    }
}
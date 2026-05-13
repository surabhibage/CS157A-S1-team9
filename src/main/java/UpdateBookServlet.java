import db.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateBookServlet")
public class UpdateBookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (!"Admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        String bookId = request.getParameter("bookId");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String genre = request.getParameter("genre");
        String publisher = request.getParameter("publisher");
        String pubDateStr = request.getParameter("publicationDate");
        String synopsis = request.getParameter("synopsis");

        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE Book SET Title = ?, Author = ?, Genre = ?, Publisher = ?, Publication_Date = ?, Synopsis = ? WHERE Book_ID = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, title);
                pst.setString(2, author);
                pst.setString(3, genre);
                pst.setString(4, publisher);
                
                if (pubDateStr != null && !pubDateStr.isEmpty()) {
                    pst.setDate(5, Date.valueOf(pubDateStr));
                } else {
                    pst.setNull(5, java.sql.Types.DATE);
                }
                
                pst.setString(6, synopsis);
                pst.setInt(7, Integer.parseInt(bookId));
                
                pst.executeUpdate();
            }
            response.sendRedirect("book.jsp?id=" + bookId + "&msg=updated");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("edit-book.jsp?id=" + bookId + "&error=update_failed");
        }
    }
}

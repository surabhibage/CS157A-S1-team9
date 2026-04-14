//package src;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddBookServlet")
public class AddBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extracting parameters from the form
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String publisher = request.getParameter("publisher");
        String pubDateStr = request.getParameter("pubDate");
        String genre = request.getParameter("genre");
        String synopsis = request.getParameter("synopsis");

        // These values come from your original configuration [cite: 9]
        String url = "jdbc:mysql://localhost:3306/Team9LibSys?useSSL=false";
        String user = "root";
        String password = "ne83De-JVui";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            String sql = "INSERT INTO Book (Book_ID, Title, Author, Publisher, Publication_Date, Genre, Synopsis) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, bookId);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, publisher);
            pstmt.setDate(5, Date.valueOf(pubDateStr));
            pstmt.setString(6, genre);
            pstmt.setString(7, synopsis);

            pstmt.executeUpdate();
            conn.close(); // Properly closing the connection [cite: 11]

            response.sendRedirect("home.jsp");

        } catch (Exception e) {
            e.printStackTrace(); // Logs error to the console [cite: 12]
            response.getWriter().println("Database Error: " + e.getMessage());
        }
    }
}
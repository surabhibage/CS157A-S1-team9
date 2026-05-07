import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import db.DatabaseConnection;

@WebServlet("/RequestHoldServlet")
public class RequestHoldServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String bookId = request.getParameter("bookId");
        String role = (String) session.getAttribute("role");

        // 1. Security Check: Is the user logged in?
        if (userId == null || !"Borrower".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            
            //check if hold already exists for book
            String checkHoldSql = "SELECT COUNT(*) FROM Requests r " +
                    "JOIN Reserves res ON r.Hold_ID = res.Hold_ID " +
                    "WHERE r.User_ID = ? AND res.Book_ID = ?";
            
            try (PreparedStatement checkPst = con.prepareStatement(checkHoldSql)) {
                checkPst.setInt(1, userId);
                checkPst.setString(2, bookId);
                ResultSet rs = checkPst.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    // User already has a hold on this book!
                    response.sendRedirect("home.jsp?error=already_on_hold");
                    return; 
                }
            }
            
            con.setAutoCommit(false);
            
            // 2. Prepare Dates
            Date today = new Date(System.currentTimeMillis());
            
            // Set expiration to 7 days from now
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date expiration = new Date(cal.getTimeInMillis());
            
            int holdId = (int) (Math.random() * 1000000);

            // 3. Insert Hold Record
            String sql = "INSERT INTO Holds (Hold_ID, Request_Date, Expiration_Date, Pickup_Location) " +
                         "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pst = con.prepareStatement(sql)) {
            	pst.setInt(1, holdId);
                pst.setDate(2, today);
                pst.setDate(3, expiration);
                pst.setString(4, "Main Library Desk"); // Default location
                pst.executeUpdate();
            }
            
            // 4. Insert Requests Record
            int cardNum = 0;
            try (PreparedStatement pst = con.prepareStatement("SELECT CardNum FROM Borrower WHERE User_ID = ?")) {
                pst.setInt(1, userId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) cardNum = rs.getInt("CardNum");
                }
            }
            
            String requestSql = "INSERT INTO Requests (User_ID, Hold_ID, CardNum) VALUES (?, ?, ?)";
            
            try (PreparedStatement req = con.prepareStatement(requestSql)) {
            	req.setInt(1, userId);
                req.setInt(2, holdId);
                req.setInt(3, cardNum);
                req.executeUpdate();
            }
            
            // 5. Insert Reserves Record
            String reserveSql = "INSERT INTO Reserves (Hold_ID, Book_ID) VALUES (?, ?)";
            
            try (PreparedStatement res = con.prepareStatement(reserveSql)) {
            	res.setInt(1, holdId);
                res.setString(2, bookId);
                res.executeUpdate();
            }
            
            con.commit();
            response.sendRedirect("home.jsp?hold=success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home.jsp?error=exception");
        } finally {
            try { if (con != null) con.close(); } catch (Exception ex) {}
        }
    }
}
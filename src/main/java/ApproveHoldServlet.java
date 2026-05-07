import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import db.DatabaseConnection;

@WebServlet("/ApproveHoldServlet")
public class ApproveHoldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        Integer employeeId = (Integer) session.getAttribute("userId");
        String holdIdStr = request.getParameter("holdId");
        String userIdStr = request.getParameter("userId"); //not needed / not used?

        if (!"Admin".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        if (employeeId == null || holdIdStr == null || userIdStr == null) {
            response.sendRedirect("adminDashboard.jsp?error=missing_data");
            return;
        }
        
        Connection con = null;
        try {
        	con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);
        	
            // Insert into the Approves table as per your schema
            String sql = "INSERT INTO Approves (Hold_ID, User_ID, Employee_ID) VALUES (?, ?, ?)";
            
            try (PreparedStatement pst = con.prepareStatement(sql)) {
            	pst.setInt(1, Integer.parseInt(holdIdStr));
                pst.setInt(2, employeeId);
                pst.setInt(3, employeeId);
                pst.executeUpdate();
            }
            con.commit();
            
            response.sendRedirect("adminDashboard.jsp?holdApproved=true");
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            response.sendRedirect("adminDashboard.jsp?error=exception");
        } finally {
            try { if (con != null) con.close(); } catch (Exception ex) {}
        }
    }
}
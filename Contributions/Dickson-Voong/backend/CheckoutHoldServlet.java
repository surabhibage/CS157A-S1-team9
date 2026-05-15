import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import db.DatabaseConnection;

@WebServlet("/CheckoutHoldServlet")
public class CheckoutHoldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String holdIdStr = request.getParameter("holdId");
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");

        if (!"Admin".equals(role) && !"Librarian".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            int holdId = Integer.parseInt(holdIdStr);

            // 1. Get the User_ID and Book_ID from the existing hold
            int userId = 0;
            String bookId = "";
            String findDataSql = "SELECT r.User_ID, res.Book_ID FROM Requests r " +
                                 "JOIN Reserves res ON r.Hold_ID = res.Hold_ID WHERE r.Hold_ID = ?";
            try (PreparedStatement pst = con.prepareStatement(findDataSql)) {
                pst.setInt(1, holdId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("User_ID");
                    bookId = rs.getString("Book_ID");
                }
            }

            // 2. Find an available Copy_ID for this book
            String copySql = "SELECT i.Copy_ID FROM Inventory i JOIN Has h ON i.Copy_ID = h.Copy_ID " +
                             "WHERE h.Book_ID = ? AND i.Status = 'Available' LIMIT 1";
            int copyId = -1;
            try (PreparedStatement pst = con.prepareStatement(copySql)) {
                pst.setInt(1, Integer.parseInt(bookId));
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        copyId = rs.getInt("Copy_ID");
                    }
                }
            }

            if (copyId == -1) {
                response.sendRedirect("adminDashboard.jsp?error=no_copies");
                return;
            }

            // 3. Create the Loan record
            String loanSql = "INSERT INTO Loans (Loan_ID, Borrow_Date, Due_Date, Date_Returned, Renewal_Count) " +
                    "VALUES (?, ?, ?, NULL, 0)";
		   int loanId = (int)(Math.random() * 1000000); // Simple ID generation
		   
		   Calendar cal = Calendar.getInstance();
		   Date borrowDate = new Date(cal.getTimeInMillis());
		   cal.add(Calendar.DAY_OF_YEAR, 14); // 14 days due date
		   Date dueDate = new Date(cal.getTimeInMillis());

		   try (PreparedStatement pst = con.prepareStatement(loanSql)) {
		       pst.setInt(1, loanId);
		       pst.setDate(2, borrowDate);
		       pst.setDate(3, dueDate);
		       pst.executeUpdate();
		   }
            
            // Insert into Borrows (Link User to Loan)
		   String borrowsSql = "INSERT INTO Borrows (User_ID, Loan_ID, CardNum) VALUES (?, ?, ?)";
           // Get CardNum first
           int cardNum = 0;
           try (PreparedStatement pst = con.prepareStatement("SELECT CardNum FROM Borrower WHERE User_ID = ?")) {
               pst.setInt(1, userId);
               try (ResultSet rs = pst.executeQuery()) {
                   if (rs.next()) cardNum = rs.getInt("CardNum");
               }
           }
           try (PreparedStatement pst = con.prepareStatement(borrowsSql)) {
               pst.setInt(1, userId);
               pst.setInt(2, loanId);
               pst.setInt(3, cardNum);
               pst.executeUpdate();
           }
            
            // Insert into LoansOut (Link Copy to Loan)
           String loansOutSql = "INSERT INTO LoansOut (Loan_ID, Copy_ID) VALUES (?, ?)";
           try (PreparedStatement pst = con.prepareStatement(loansOutSql)) {
               pst.setInt(1, loanId);
               pst.setInt(2, copyId);
               pst.executeUpdate();
           }

            // 4. Update Inventory Status
           String updateInvSql = "UPDATE Inventory SET Status = 'Borrowed' WHERE Copy_ID = ?";
           try (PreparedStatement pst = con.prepareStatement(updateInvSql)) {
               pst.setInt(1, copyId);
               pst.executeUpdate();
           }
           
           // 5. Update Borrower count
           String updateBorrowerSql = "UPDATE Borrower SET numBorrowed = numBorrowed + 1 WHERE User_ID = ?";
           try (PreparedStatement pst = con.prepareStatement(updateBorrowerSql)) {
               pst.setInt(1, userId);
               pst.executeUpdate();
           }

            // 6. Cleanup the Hold
           String removeApprovesSql = "DELETE FROM Approves WHERE Hold_ID = ?";
           try (PreparedStatement pst = con.prepareStatement(removeApprovesSql)) {
        	   pst.setInt(1,  holdId);
        	   pst.executeUpdate();
           }
           
           String removeRequestsSql = "DELETE FROM Requests WHERE Hold_ID = ?";
           try (PreparedStatement pst = con.prepareStatement(removeRequestsSql)) {
        	   pst.setInt(1,  holdId);
        	   pst.executeUpdate();
           }
           
           String removeReservesSql = "DELETE FROM Reserves WHERE Hold_ID = ?";
           try (PreparedStatement pst = con.prepareStatement(removeReservesSql)) {
        	   pst.setInt(1,  holdId);
        	   pst.executeUpdate();
           }
           
           String removeHoldsSql = "DELETE FROM Holds WHERE Hold_ID = ?";
           try (PreparedStatement pst = con.prepareStatement(removeHoldsSql)) {
        	   pst.setInt(1,  holdId);
        	   pst.executeUpdate();
           }

            con.commit();
            response.sendRedirect("adminDashboard.jsp?checkout=success");

        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            response.sendRedirect("adminDashboard.jsp?error=checkout_failed");
        } finally {
            if (con != null) try { con.close(); } catch (SQLException ex) {}
        }
    }
}
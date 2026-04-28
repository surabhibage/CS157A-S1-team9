import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import db.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ReturnBookServlet")
public class ReturnBookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        String copyIdParam = request.getParameter("copyId");

        if (!"Admin".equals(role)) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (copyIdParam == null) {
            response.sendRedirect("adminDashboard.jsp?error=missing_id");
            return;
        }

        int copyId = Integer.parseInt(copyIdParam);

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            // 1. Find the active loan for this copy
            String loanSql = "SELECT lo.Loan_ID, b.User_ID FROM LoansOut lo " +
                             "JOIN Borrows b ON lo.Loan_ID = b.Loan_ID " +
                             "JOIN Loans l ON lo.Loan_ID = l.Loan_ID " +
                             "WHERE lo.Copy_ID = ? AND l.Date_Returned IS NULL";
            int loanId = -1;
            int userId = -1;
            try (PreparedStatement pst = con.prepareStatement(loanSql)) {
                pst.setInt(1, copyId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        loanId = rs.getInt("Loan_ID");
                        userId = rs.getInt("User_ID");
                    }
                }
            }

            if (loanId == -1) {
                response.sendRedirect("adminDashboard.jsp?error=no_active_loan");
                return;
            }

            // 2. Update Loans table (set return date)
            String updateLoanSql = "UPDATE Loans SET Date_Returned = CURRENT_DATE WHERE Loan_ID = ?";
            try (PreparedStatement pst = con.prepareStatement(updateLoanSql)) {
                pst.setInt(1, loanId);
                pst.executeUpdate();
            }

            // 3. Update Inventory (set status to Available)
            String updateInvSql = "UPDATE Inventory SET Status = 'Available' WHERE Copy_ID = ?";
            try (PreparedStatement pst = con.prepareStatement(updateInvSql)) {
                pst.setInt(1, copyId);
                pst.executeUpdate();
            }

            // 4. Update Borrower (decrement numBorrowed)
            String updateBorrowerSql = "UPDATE Borrower SET numBorrowed = GREATEST(0, numBorrowed - 1) WHERE User_ID = ?";
            try (PreparedStatement pst = con.prepareStatement(updateBorrowerSql)) {
                pst.setInt(1, userId);
                pst.executeUpdate();
            }

            con.commit();
            response.sendRedirect("adminDashboard.jsp?return=success");

        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            response.sendRedirect("adminDashboard.jsp?error=exception");
        } finally {
            try { if (con != null) con.close(); } catch (Exception ex) {}
        }
    }
}

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

@WebServlet("/RenewBookServlet")
public class RenewBookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String loanIdParam = request.getParameter("loanId");

        if (userId == null || loanIdParam == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int loanId = Integer.parseInt(loanIdParam);

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();

            // 1. Get Loan details (Renewal_Count and Book_ID)
            String loanDetailsSql = "SELECT l.Renewal_Count, h.Book_ID FROM Loans l " +
                                    "JOIN LoansOut lo ON l.Loan_ID = lo.Loan_ID " +
                                    "JOIN Has h ON lo.Copy_ID = h.Copy_ID " +
                                    "WHERE l.Loan_ID = ?";
            int renewalCount = 0;
            int bookId = -1;
            try (PreparedStatement pst = con.prepareStatement(loanDetailsSql)) {
                pst.setInt(1, loanId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        renewalCount = rs.getInt("Renewal_Count");
                        bookId = rs.getInt("Book_ID");
                    }
                }
            }

            // 2. Check Max Renewals Limit (e.g., 3)
            if (renewalCount >= 3) {
                response.sendRedirect("home.jsp?error=max_renewals_reached");
                return;
            }

            // 3. Check for ACTIVE holds (not expired)
            String holdSql = "SELECT COUNT(*) FROM Reserves r JOIN Holds h ON r.Hold_ID = h.Hold_ID " +
                             "WHERE r.Book_ID = ? AND h.Expiration_Date >= CURRENT_DATE";
            try (PreparedStatement pst = con.prepareStatement(holdSql)) {
                pst.setInt(1, bookId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        response.sendRedirect("home.jsp?error=on_hold");
                        return;
                    }
                }
            }

            // 4. Update the Due Date and Increment Count
            String updateSql = "UPDATE Loans SET Due_Date = DATE_ADD(Due_Date, INTERVAL 14 DAY), Renewal_Count = Renewal_Count + 1 WHERE Loan_ID = ?";
            try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                pst.setInt(1, loanId);
                int rows = pst.executeUpdate();
                if (rows > 0) {
                    response.sendRedirect("home.jsp?renew=success");
                } else {
                    response.sendRedirect("home.jsp?error=not_found");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home.jsp?error=exception");
        } finally {
            try { if (con != null) con.close(); } catch (Exception ex) {}
        }
    }
}

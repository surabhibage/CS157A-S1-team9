import db.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/BorrowBookServlet")
public class BorrowBookServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession();
    Integer userId = (Integer) session.getAttribute("userId");
    String role = (String) session.getAttribute("role");
    String bookId = request.getParameter("bookId");

    if (userId == null || !"Borrower".equals(role)) {
      response.sendRedirect("login.jsp");
      return;
    }

    Connection con = null;
    try {
      con = DatabaseConnection.getConnection();
      con.setAutoCommit(false);

      // 1. Check if borrower has reached max limit
      String limitSql = "SELECT numBorrowed, maxBorrowed FROM Borrower WHERE User_ID = ?";
      int numBorrowed = 0;
      int maxBorrowed = 0;
      try (PreparedStatement pst = con.prepareStatement(limitSql)) {
        pst.setInt(1, userId);
        try (ResultSet rs = pst.executeQuery()) {
          if (rs.next()) {
            numBorrowed = rs.getInt("numBorrowed");
            maxBorrowed = rs.getInt("maxBorrowed");
          }
        }
      }

      if (numBorrowed >= maxBorrowed) {
        response.sendRedirect("home.jsp?error=limit_reached");
        return;
      }

      // check for active holds
      String holdSql =
          "SELECT COUNT(*) FROM Reserves r JOIN Holds h ON r.Hold_ID = h.Hold_ID "
              + "WHERE r.Book_ID = ? AND h.Expiration_Date >= CURRENT_DATE";
      try (PreparedStatement pst = con.prepareStatement(holdSql)) {
        pst.setInt(1, Integer.parseInt(bookId));
        try (ResultSet rs = pst.executeQuery()) {
          if (rs.next() && rs.getInt(1) > 0) {
            response.sendRedirect("home.jsp?error=on_hold");
            return;
          }
        }
      }

      // 2. Find an available copy
      String copySql =
          "SELECT i.Copy_ID FROM Inventory i JOIN Has h ON i.Copy_ID = h.Copy_ID "
              + "WHERE h.Book_ID = ? AND i.Status = 'Available' LIMIT 1";
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
        response.sendRedirect("home.jsp?error=no_copies");
        return;
      }

      // 3. Create Loan record
      String loanSql =
          "INSERT INTO Loans (Loan_ID, Borrow_Date, Due_Date, Date_Returned, Renewal_Count) "
              + "VALUES (?, ?, ?, NULL, 0)";
      int loanId = (int) (Math.random() * 1000000); // Simple ID generation

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

      // 4. Link User and Loan (Borrows)
      String borrowsSql = "INSERT INTO Borrows (User_ID, Loan_ID, CardNum) VALUES (?, ?, ?)";
      // Get CardNum first
      int cardNum = 0;
      try (PreparedStatement pst =
          con.prepareStatement("SELECT CardNum FROM Borrower WHERE User_ID = ?")) {
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

      // 5. Link Loan and Copy (LoansOut)
      String loansOutSql = "INSERT INTO LoansOut (Loan_ID, Copy_ID) VALUES (?, ?)";
      try (PreparedStatement pst = con.prepareStatement(loansOutSql)) {
        pst.setInt(1, loanId);
        pst.setInt(2, copyId);
        pst.executeUpdate();
      }

      // 6. Update Inventory Status
      String updateInvSql = "UPDATE Inventory SET Status = 'Borrowed' WHERE Copy_ID = ?";
      try (PreparedStatement pst = con.prepareStatement(updateInvSql)) {
        pst.setInt(1, copyId);
        pst.executeUpdate();
      }

      // 7. Update Borrower count
      String updateBorrowerSql =
          "UPDATE Borrower SET numBorrowed = numBorrowed + 1 WHERE User_ID = ?";
      try (PreparedStatement pst = con.prepareStatement(updateBorrowerSql)) {
        pst.setInt(1, userId);
        pst.executeUpdate();
      }

      con.commit();
      response.sendRedirect("home.jsp?borrow=success");

    } catch (Exception e) {
      if (con != null)
        try {
          con.rollback();
        } catch (Exception ex) {
        }
      e.printStackTrace();
      response.sendRedirect("home.jsp?error=exception");
    } finally {
      try {
        if (con != null) con.close();
      } catch (Exception ex) {
      }
    }
  }
}

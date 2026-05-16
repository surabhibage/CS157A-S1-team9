import db.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
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

    String bookIdStr = request.getParameter("bookId");
    if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
        response.sendRedirect("home.jsp?error=DeleteFailed");
        return;
    }

    try {
      int bookId = Integer.parseInt(bookIdStr);
      Connection conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false); // Start transaction

      try {
          // 1. Get all Copy_IDs associated with this Book
          java.util.List<Integer> copyIds = new java.util.ArrayList<>();
          String getCopiesSql = "SELECT Copy_ID FROM Has WHERE Book_ID = ?";
          try (PreparedStatement ps = conn.prepareStatement(getCopiesSql)) {
              ps.setInt(1, bookId);
              try (java.sql.ResultSet rs = ps.executeQuery()) {
                  while (rs.next()) {
                      copyIds.add(rs.getInt("Copy_ID"));
                  }
              }
          }

          // 2. Delete from Reserves (Holds linked to this Book)
          String deleteReserves = "DELETE FROM Reserves WHERE Book_ID = ?";
          try (PreparedStatement ps = conn.prepareStatement(deleteReserves)) {
              ps.setInt(1, bookId);
              ps.executeUpdate();
          }

          // 3. Delete from Has (Link between Book and Inventory)
          String deleteHas = "DELETE FROM Has WHERE Book_ID = ?";
          try (PreparedStatement ps = conn.prepareStatement(deleteHas)) {
              ps.setInt(1, bookId);
              ps.executeUpdate();
          }

          // 4. Delete dependent records for each Copy_ID
          if (!copyIds.isEmpty()) {
              String deleteLoansOut = "DELETE FROM LoansOut WHERE Copy_ID = ?";
              String deleteEdits = "DELETE FROM Edits WHERE Copy_ID = ?";
              String deleteInventory = "DELETE FROM Inventory WHERE Copy_ID = ?";
              
              try (PreparedStatement psLoansOut = conn.prepareStatement(deleteLoansOut);
                   PreparedStatement psEdits = conn.prepareStatement(deleteEdits);
                   PreparedStatement psInv = conn.prepareStatement(deleteInventory)) {
                   
                  for (int copyId : copyIds) {
                      // Delete LoansOut
                      psLoansOut.setInt(1, copyId);
                      psLoansOut.executeUpdate();
                      
                      // Delete Edits
                      psEdits.setInt(1, copyId);
                      psEdits.executeUpdate();
                      
                      // Delete Inventory
                      psInv.setInt(1, copyId);
                      psInv.executeUpdate();
                  }
              }
          }

          // 5. Finally, delete the Book itself
          String deleteBook = "DELETE FROM Book WHERE Book_ID = ?";
          try (PreparedStatement ps = conn.prepareStatement(deleteBook)) {
              ps.setInt(1, bookId);
              ps.executeUpdate();
          }

          conn.commit(); // Commit transaction
          response.sendRedirect("home.jsp");

      } catch (Exception e) {
          conn.rollback(); // Rollback on error
          throw e;
      } finally {
          conn.setAutoCommit(true);
          conn.close();
      }

    } catch (Exception e) {
      e.printStackTrace();
      response.sendRedirect("home.jsp?error=DeleteFailed");
    }
  }
}

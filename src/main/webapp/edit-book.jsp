<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="db.DatabaseConnection" %>
<%
    if (!"Admin".equals(session.getAttribute("role"))) {
        response.sendRedirect("login.jsp?error=unauthorized");
        return;
    }
    String bookId = request.getParameter("id");
    if (bookId == null || bookId.trim().isEmpty()) {
        response.sendRedirect("home.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Book - Library Inventory</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9; }
        header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px; }
        .dashboard-container { display: flex; gap: 20px; justify-content: center; }
        .main-content { flex: 1; max-width: 600px; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .btn { padding: 10px 15px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; }
        .btn:hover { background-color: #0056b3; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group textarea, .form-group select { padding: 8px; width: 100%; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        .form-group textarea { height: 100px; }
    </style>
</head>
<body>

<header>
    <h2>Edit Book</h2>
    <nav>
        <a href="book.jsp?id=<%= bookId %>" class="btn">Back to Details</a>
    </nav>
</header>

<div class="dashboard-container">
    <div class="main-content">
        <%
            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM Book WHERE Book_ID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(bookId));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
        %>
                    <form action="UpdateBookServlet" method="post">
                        <input type="hidden" name="bookId" value="<%= bookId %>">
                        
                        <div class="form-group">
                            <label>Title:</label>
                            <input type="text" name="title" value="<%= rs.getString("Title") %>" required>
                        </div>
                        
                        <div class="form-group">
                            <label>Author:</label>
                            <input type="text" name="author" value="<%= rs.getString("Author") %>" required>
                        </div>
                        
                        <div class="form-group">
                            <label>Genre:</label>
                            <input type="text" name="genre" value="<%= rs.getString("Genre") %>">
                        </div>
                        
                        <div class="form-group">
                            <label>Publisher:</label>
                            <input type="text" name="publisher" value="<%= rs.getString("Publisher") %>">
                        </div>
                        
                        <div class="form-group">
                            <label>Publication Date:</label>
                            <input type="date" name="publicationDate" value="<%= rs.getDate("Publication_Date") %>">
                        </div>
                        
                        <div class="form-group">
                            <label>Synopsis:</label>
                            <textarea name="synopsis"><%= rs.getString("Synopsis") %></textarea>
                        </div>
                        
                        <button type="submit" class="btn">Update Book</button>
                    </form>
        <%
                } else {
                    out.print("<p class='error'>Book not found.</p>");
                }
            } catch (Exception e) {
                out.print("<p class='error'>Error: " + e.getMessage() + "</p>");
            } finally {
                if (conn != null) try { conn.close(); } catch (SQLException e) {}
            }
        %>
    </div>
</div>

</body>
</html>

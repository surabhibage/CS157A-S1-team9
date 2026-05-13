<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="db.DatabaseConnection" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
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
    <title>Book Details</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9; }
        header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px; }
        .dashboard-container { display: flex; gap: 20px; justify-content: center; }
        .main-content { flex: 1; max-width: 800px; background: white; padding: 40px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .btn { padding: 10px 15px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; display: inline-block; }
        .btn:hover { background-color: #0056b3; }
        .book-detail { margin-bottom: 15px; line-height: 1.6; font-size: 16px; }
        .book-detail strong { display: inline-block; width: 150px; color: #333; }
        .book-synopsis { background-color: #f8f9fa; padding: 20px; border-radius: 6px; border-left: 4px solid #007bff; margin-top: 10px; color: #555; }
        hr { border: 0; border-top: 1px solid #eee; margin: 30px 0; }
    </style>
</head>
<body>

<header>
    <h2>Book Details</h2>
    <nav>
        <% if ("Admin".equals(session.getAttribute("role"))) { %>
            <a href="edit-book.jsp?id=<%= bookId %>" class="btn" style="background-color: #ffc107; color: black; margin-right: 10px;">Edit Book</a>
        <% } %>
        <a href="home.jsp" class="btn">Back to Home</a>
    </nav>
</header>

<div class="dashboard-container">
    <div class="main-content">
        <% if ("updated".equals(request.getParameter("msg"))) { %>
            <div style="background-color: #d4edda; color: #155724; padding: 10px; margin-bottom: 20px; border-radius: 4px; border: 1px solid #c3e6cb;">
                Book details updated successfully!
            </div>
        <% } %>
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
                    <h2 style="margin-top: 0; color: #0056b3;"><%= rs.getString("Title") %></h2>
                    <hr>
                    <div class="book-detail"><strong>Author:</strong> <%= rs.getString("Author") %></div>
                    <div class="book-detail"><strong>Genre:</strong> <%= rs.getString("Genre") %></div>
                    <div class="book-detail"><strong>Publication Date:</strong> <%= rs.getDate("Publication_Date") %></div>
                    <div class="book-detail"><strong>Publisher:</strong> <%= rs.getString("Publisher") %></div>
                    <div class="book-detail" style="margin-top: 30px;">
                        <strong>Synopsis:</strong>
                        <div class="book-synopsis">
                            <%= rs.getString("Synopsis") %>
                        </div>
                    </div>
        <%
                } else {
                    out.print("<p style='color: red;'>Book not found in the database.</p>");
                }
            } catch (Exception e) {
                out.print("<p style='color: red;'>Error retrieving book details: " + e.getMessage() + "</p>");
            } finally {
                if (conn != null) {
                    try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
                }
            }
        %>
    </div>
</div>

</body>
</html>

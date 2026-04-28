<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="db.DatabaseConnection" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home - Library Inventory System</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ccc; padding-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #0056b3; color: white; text-transform: capitalize; }
        .btn { padding: 5px 10px; background-color: #28a745; color: white; text-decoration: none; border-radius: 3px; }
        .btn-delete { background-color: #dc3545; color: white; border: none; padding: 6px 12px; cursor: pointer; border-radius: 3px; }
        .btn-delete:hover { background-color: #c82333; }
    </style>
    <script>
        function confirmDelete(bookId) {
            if (confirm("Are you sure you want to delete Book ID: " + bookId + "?")) {
                window.location.href = "DeleteBookServlet?bookId=" + bookId;
            }
        }
    </script>
</head>
<body>

<header>
    <h1>Library Inventory</h1>
    <nav>
        <% if ("Admin".equals(session.getAttribute("role"))) { %>
        <a href="adminDashboard.jsp" class="btn" style="background-color: #17a2b8;">Admin Dashboard</a> |
        <a href="add-book.jsp" class="btn" style="background-color: #007bff;">Add New Book</a> |
        <% } %>
        <a href="settings.jsp">Account Settings</a> |
        <a href="LogoutServlet">Log Out</a>
    </nav>
</header>

<%
    String role = (String) session.getAttribute("role");
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<h2>All Inventory Details</h2>

<form method="GET" action="home.jsp" style="margin-bottom: 20px;">
    <input type="text" name="query" placeholder="Search by Title, Author, or Genre..." 
           value="<%= request.getParameter("query") != null ? request.getParameter("query") : "" %>" 
           style="padding: 8px; width: 300px; border-radius: 4px; border: 1px solid #ccc;">
    <input type="submit" value="Search" class="btn" style="background-color: #007bff; border: none; padding: 9px 15px;">
    <a href="home.jsp" class="btn" style="background-color: #6c757d; margin-left: 10px;">Clear</a>
</form>

<% if (request.getParameter("borrow") != null) { %>
    <div style="padding: 10px; margin-bottom: 20px; border-radius: 4px; background-color: #d4edda; color: #155724;">Book borrowed successfully!</div>
<% } %>
<% if (request.getParameter("renew") != null) { %>
    <div style="padding: 10px; margin-bottom: 20px; border-radius: 4px; background-color: #d4edda; color: #155724;">Book renewed successfully!</div>
<% } %>
<% if (request.getParameter("error") != null) { 
    String errorMsg = request.getParameter("error");
    String displayMsg = errorMsg;
    if ("max_renewals_reached".equals(errorMsg)) displayMsg = "Maximum renewal limit reached (3 renewals max).";
    if ("on_hold".equals(errorMsg)) displayMsg = "Renewal failed: This book is on hold for another user.";
%>
    <div style="padding: 10px; margin-bottom: 20px; border-radius: 4px; background-color: #f8d7da; color: #721c24;">Error: <%= displayMsg %></div>
<% } %>

<table>
    <thead>
        <tr>
            <th>Book ID</th>
            <th>Title</th>
            <th>Author</th>
            <th>Genre</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <%
            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                String searchQuery = request.getParameter("query");
                String sql = "SELECT b.Book_ID, b.Title, b.Author, b.Genre, " +
                             "(SELECT COUNT(*) FROM Inventory i JOIN Has h ON i.Copy_ID = h.Copy_ID WHERE h.Book_ID = b.Book_ID AND i.Status = 'Available') as AvailableCopies " +
                             "FROM Book b";
                
                PreparedStatement pstmt;
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    sql += " WHERE b.Title LIKE ? OR b.Author LIKE ? OR b.Genre LIKE ?";
                    pstmt = conn.prepareStatement(sql);
                    String searchPattern = "%" + searchQuery.trim() + "%";
                    pstmt.setString(1, searchPattern);
                    pstmt.setString(2, searchPattern);
                    pstmt.setString(3, searchPattern);
                } else {
                    pstmt = conn.prepareStatement(sql);
                }
                
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    String currentId = rs.getString("Book_ID");
                    int available = rs.getInt("AvailableCopies");
        %>
        <tr>
            <td><%= rs.getString("Book_ID") %></td>
            <td><%= rs.getString("Title") %></td>
            <td><%= rs.getString("Author") %></td>
            <td><%= rs.getString("Genre") %></td>
            <td><%= available > 0 ? "Available (" + available + ")" : "Out of Stock" %></td>
            <td>
                <% if ("Admin".equals(role)) { %>
                    <button class="btn-delete" onclick="confirmDelete('<%= currentId %>')">Delete</button>
                <% } else if ("Borrower".equals(role) && available > 0) { %>
                    <a href="BorrowBookServlet?bookId=<%= currentId %>" class="btn">Borrow</a>
                <% } %>
            </td>
        </tr>
        <%
                }
            } catch(Exception e) {
                out.println("<tr><td colspan='6'>Error: " + e.getMessage() + "</td></tr>");
            } finally {
                // Keep connection open for the second table if needed, or close and reopen.
                // For simplicity in JSP, we'll close it at the very end.
            }
        %>
    </tbody>
</table>

<% if ("Borrower".equals(role)) { %>
    <hr style="margin-top: 40px;">
    <h2>My Borrowed Books</h2>
    <table>
        <thead>
            <tr>
                <th>Title</th>
                <th>Due Date</th>
                <th>Renewals</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                try {
                    String myBooksSql = "SELECT b.Title, l.Due_Date, l.Renewal_Count, l.Loan_ID " +
                                        "FROM Loans l " +
                                        "JOIN Borrows br ON l.Loan_ID = br.Loan_ID " +
                                        "JOIN LoansOut lo ON l.Loan_ID = lo.Loan_ID " +
                                        "JOIN Has h ON lo.Copy_ID = h.Copy_ID " +
                                        "JOIN Book b ON h.Book_ID = b.Book_ID " +
                                        "WHERE br.User_ID = ? AND l.Date_Returned IS NULL";
                    PreparedStatement myPstmt = conn.prepareStatement(myBooksSql);
                    myPstmt.setInt(1, userId);
                    ResultSet myRs = myPstmt.executeQuery();
                    while(myRs.next()) {
            %>
            <tr>
                <td><%= myRs.getString("Title") %></td>
                <td><%= myRs.getDate("Due_Date") %></td>
                <td><%= myRs.getInt("Renewal_Count") %></td>
                <td>
                    <a href="RenewBookServlet?loanId=<%= myRs.getInt("Loan_ID") %>" class="btn" style="background-color: #ffc107; color: black;">Renew</a>
                </td>
            </tr>
            <%
                    }
                } catch(Exception e) {
                    out.println("<tr><td colspan='4'>Error: " + e.getMessage() + "</td></tr>");
                }
            %>
        </tbody>
    </table>
<% } %>

<%
    // Final cleanup
    if (conn != null) {
        try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
%>

</body>
</html>
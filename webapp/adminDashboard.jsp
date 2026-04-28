<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="db.DatabaseConnection" %>
<%
    // Security Check: Ensure only Admins/Librarians can view this page
    String role = (String) session.getAttribute("role");
    if (role == null || (!role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("Librarian"))) {
        response.sendRedirect("login.jsp?error=unauthorized");
        return; 
    }
    String username = (String) session.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Library Inventory</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9; }
        header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px; }
        .dashboard-container { display: flex; gap: 20px; }
        .sidebar { flex: 1; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); height: fit-content; }
        .main-content { flex: 3; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #0056b3; color: white; }
        .btn { padding: 10px 15px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; }
        .btn:hover { background-color: #0056b3; }
        .alert { padding: 10px; margin-bottom: 20px; border-radius: 4px; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
    </style>
</head>
<body>

<header>
    <h1>Admin Dashboard</h1>
    <nav>
        <a href="home.jsp">View Inventory</a> |
        <a href="add-book.jsp">Add New Book</a> |
        <a href="LogoutServlet">Log Out</a>
    </nav>
</header>

<% if (request.getParameter("return") != null) { %>
    <div class="alert success">Book returned successfully!</div>
<% } %>
<% if (request.getParameter("error") != null) { %>
    <div class="alert error">Error: <%= request.getParameter("error") %></div>
<% } %>

<div class="dashboard-container">
    <div class="sidebar">
        <h3>Quick Actions</h3>
        <ul style="list-style: none; padding: 0;">
            <li style="margin-bottom: 10px;"><a href="add-book.jsp" class="btn" style="display: block; text-align: center;">Add New Book</a></li>
        </ul>
        
        <hr>
        
        <h3>Check-in Book</h3>
        <form action="ReturnBookServlet" method="GET">
            <label for="copyId">Copy ID:</label><br>
            <input type="text" id="copyId" name="copyId" style="width: 100%; padding: 8px; margin: 8px 0;" required>
            <input type="submit" value="Check-in Book" class="btn" style="width: 100%; background-color: #28a745;">
        </form>
    </div>

    <div class="main-content">
        <h2>Currently Borrowed Books</h2>
        <table>
            <thead>
                <tr>
                    <th>Copy ID</th>
                    <th>Title</th>
                    <th>Borrower</th>
                    <th>Due Date</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    try {
                        Connection conn = DatabaseConnection.getConnection();
                        String sql = "SELECT lo.Copy_ID, b.Title, u.Username, l.Due_Date " +
                                     "FROM Loans l " +
                                     "JOIN Borrows br ON l.Loan_ID = br.Loan_ID " +
                                     "JOIN Users u ON br.User_ID = u.User_ID " +
                                     "JOIN LoansOut lo ON l.Loan_ID = lo.Loan_ID " +
                                     "JOIN Has h ON lo.Copy_ID = h.Copy_ID " +
                                     "JOIN Book b ON h.Book_ID = b.Book_ID " +
                                     "WHERE l.Date_Returned IS NULL";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);
                        while(rs.next()) {
                %>
                <tr>
                    <td><%= rs.getInt("Copy_ID") %></td>
                    <td><%= rs.getString("Title") %></td>
                    <td><%= rs.getString("Username") %></td>
                    <td><%= rs.getDate("Due_Date") %></td>
                    <td>
                        <a href="ReturnBookServlet?copyId=<%= rs.getInt("Copy_ID") %>" class="btn" style="background-color: #28a745; padding: 5px 10px; font-size: 0.8em;">Check-in</a>
                    </td>
                </tr>
                <%
                        }
                        conn.close();
                    } catch(Exception e) {
                        out.println("<tr><td colspan='5'>Error loading loans: " + e.getMessage() + "</td></tr>");
                    }
                %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
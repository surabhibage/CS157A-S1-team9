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
        <a href="add-book.jsp" class="btn" style="background-color: #007bff;">Add New Book</a> |
        <% } %>
        <a href="settings.jsp">Account Settings</a> |
        <a href="LogoutServlet">Log Out</a>
    </nav>
</header>

<h2>All Inventory Details</h2>

<form method="GET" action="home.jsp" style="margin-bottom: 20px;">
    <input type="text" name="query" placeholder="Search by Title, Author, or Genre..." 
           value="<%= request.getParameter("query") != null ? request.getParameter("query") : "" %>" 
           style="padding: 8px; width: 300px; border-radius: 4px; border: 1px solid #ccc;">
    <input type="submit" value="Search" class="btn" style="background-color: #007bff; border: none; padding: 9px 15px;">
    <a href="home.jsp" class="btn" style="background-color: #6c757d; margin-left: 10px;">Clear</a>
</form>

<table>
    <thead>
        <tr>
            <%
                try {
                    Connection conn = DatabaseConnection.getConnection();

                    String searchQuery = request.getParameter("query");
                    ResultSet rs;
                    
                    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                        String sql = "SELECT * FROM Book WHERE Title LIKE ? OR Author LIKE ? OR Genre LIKE ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        String searchPattern = "%" + searchQuery.trim() + "%";
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);
                        rs = pstmt.executeQuery();
                    } else {
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery("SELECT * FROM Book");
                    }
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();

                    // 2. Automatically generate headers based on DB columns
                    for (int i = 1; i <= columnCount; i++) {
                        out.print("<th>" + rsmd.getColumnName(i).replace("_", " ") + "</th>");
                    }
            %>
            <% if ("Admin".equals(session.getAttribute("role"))) { %>
            <th>Actions</th> 
            <% } %>
        </tr>
    </thead>
    <tbody>
        <%
                    while(rs.next()) {
                        String currentId = rs.getString("Book_ID"); // Assuming PK is Book_ID
        %>
        <tr>
            <%
                // 3. Automatically generate data cells for every column
                for (int i = 1; i <= columnCount; i++) {
            %>
                <td><%= rs.getString(i) == null ? "" : rs.getString(i) %></td>
            <% } %>

            <% if ("Admin".equals(session.getAttribute("role"))) { %>
            <td>
                <button class="btn-delete" onclick="confirmDelete('<%= currentId %>')">
                    Delete
                </button>
            </td>
            <% } %>
        </tr>
        <%
                    }
                    conn.close();
                } catch(Exception e) {
                    out.println("<tr><td colspan='10'>Error: " + e.getMessage() + "</td></tr>");
                }
        %>
    </tbody>
</table>

</body>
</html>
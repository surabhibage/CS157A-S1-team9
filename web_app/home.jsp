<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
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
        th { background-color: #0056b3; color: white; }
        .btn { padding: 5px 10px; background-color: #28a745; color: white; text-decoration: none; border-radius: 3px; }
    </style>
</head>
<body>

<header>
    <h1>Library Inventory</h1>
    <nav>
        <a href="settings.jsp">Account Settings</a> |
        <a href="LogoutServlet">Log Out</a>
    </nav>
</header>

<h2>Available Books</h2>

<table>
    <tr>
        <th>Title</th>
        <th>Author</th>
        <th>Availability</th>
        <th>Action</th>
    </tr>

    <%
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryManagementSys?autoReconnect=true&useSSL=false", "root", "password");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT book_id, title, author, availability_status FROM Books"); // TODO: edit statement to match

            while(rs.next()) {
    %>
    <tr>
        <td><%= rs.getString("title") %></td>
        <td><%= rs.getString("author") %></td>
        <td><%= rs.getString("availability_status") %></td>
        <td><a href="bookDetails.jsp?id=<%= rs.getInt("book_id") %>" class="btn">View Details</a></td>
    </tr>
    <%
            }
            conn.close();
        } catch(Exception e) {
            out.println("Error connecting to database.");
        }
    %>
</table>

</body>
</html>
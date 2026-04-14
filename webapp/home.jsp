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
        <th>Genre</th>
    </tr>

    <%
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Team9LibSys?autoReconnect=true&useSSL=false", "root", "ne83De-JVui");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Book_ID, Title, Author, Genre FROM Book");

            while(rs.next()) {
    %>
    <tr>
        <td><%= rs.getString("Title") %></td>
        <td><%= rs.getString("Author") %></td>
        <td><%= rs.getString("Genre") %></td>
    </tr>
    <%
            }
            conn.close();
        } catch(Exception e) {
            out.println("<tr><td colspan='3'>Database Error: " + e.getMessage() + "</td></tr>");
            e.printStackTrace(); // This prints the full error to your IntelliJ console
        }
    %>
</table>

</body>
</html>
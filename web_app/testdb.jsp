<%@ page import="java.sql.*"%>
<%@ page import="utils.DBConnection"%>

<!DOCTYPE html>
<html>
<head>
    <title>DB Connection Test</title>
</head>

<body>

<h2>DB Connection Test</h2>

<%
    Connection con = null;

    try {
        con = DBConnection.getConnection();

        if (con != null) {
            out.println("<p style='color:green;'>✅ Connected to database successfully!</p>");
        } else {
            out.println("<p style='color:red;'>❌ Connection failed</p>");
        }

    } catch(Exception e) {
        out.println("<p style='color:red;'>❌ Error: " + e.getMessage() + "</p>");
    } finally {
        try {
            if (con != null) {
                con.close();   // ✅ safe close (no crash)
            }
        } catch(Exception e) {
            out.println("<p style='color:red;'>❌ Close Error: " + e.getMessage() + "</p>");
        }
    }
%>

</body>
</html>
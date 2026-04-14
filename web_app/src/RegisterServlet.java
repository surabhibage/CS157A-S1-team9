// //package web_app.src;
// import java.io.IOException;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.SQLIntegrityConstraintViolationException;
// import javax.servlet.ServletException;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// @WebServlet("/RegisterServlet")
// public class RegisterServlet extends HttpServlet {
//     private static final long serialVersionUID = 1L;

//     protected void doPost(HttpServletRequest request, HttpServletResponse response)
//             throws ServletException, IOException {

//         // Retrieve form data
//         String username = request.getParameter("username");
//         String password = request.getParameter("password");
//         String firstName = request.getParameter("firstName");
//         String lastName = request.getParameter("lastName");
//         String phone = request.getParameter("phone");
//         String address = request.getParameter("address");
//         String ageStr = request.getParameter("age");
//         String role = request.getParameter("role");

//         // Simple parsing for age, defaulting to 0 if left blank
//         int age = 0;
//         if (ageStr != null && !ageStr.trim().isEmpty()) {
//             age = Integer.parseInt(ageStr);
//         }

//         // Generate a simple unique ID (In production, use AUTO_INCREMENT in MySQL instead)
//         int userId = (int)(Math.random() * 100000);

//         String dbUser = "root";
//         String dbPassword = "your_mysql_password";

//         Connection con = null;
//         PreparedStatement pst = null;

//         try {
//             Class.forName("com.mysql.cj.jdbc.Driver");
//             String url = "jdbc:mysql://localhost:3306/LibraryManagementSys?autoReconnect=true&useSSL=false";
//             con = DriverManager.getConnection(url, dbUser, dbPassword);

//             // Prepare the SQL INSERT statement
//             String sql = "INSERT INTO Users (user_id, username, password_hash, first_name, last_name, phone_number, address, age, role) "
//                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

//             pst = con.prepareStatement(sql);
//             pst.setInt(1, userId);
//             pst.setString(2, username);
//             pst.setString(3, password); // Note: Hash this in a real application!
//             pst.setString(4, firstName);
//             pst.setString(5, lastName);
//             pst.setString(6, phone);
//             pst.setString(7, address);
//             pst.setInt(8, age);
//             pst.setString(9, role);

//             // Execute the insert
//             pst.executeUpdate();

//             // If successful, send them to the login page to sign in
//             response.sendRedirect("login.jsp?registered=true");

//         } catch (SQLIntegrityConstraintViolationException e) {
//             // This catches the error if the username already exists in the database
//             response.sendRedirect("register.jsp?error=duplicate");
//         } catch (Exception e) {
//             e.printStackTrace();
//             response.sendRedirect("register.jsp?error=exception");
//         } finally {
//             try { if (pst != null) pst.close(); } catch (Exception e) { e.printStackTrace(); }
//             try { if (con != null) con.close(); } catch (Exception e) { e.printStackTrace(); }
//         }
//     }
// }
import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String role = request.getParameter("role");

        String dbUser = "root";
        String dbPassword = "your_mysql_password";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/LibraryManagementSys?useSSL=false",
                dbUser,
                dbPassword
            );

            // 🔴 Generate new User_ID
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(User_ID) FROM Users");

            int newUserId = 1;
            if (rs.next()) {
                newUserId = rs.getInt(1) + 1;
            }

            // 🔴 Insert into Users
            String userSql = "INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(userSql);

            pst.setInt(1, newUserId);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, fname);
            pst.setString(5, lname);
            pst.setString(6, null);
            pst.setString(7, null);

            pst.executeUpdate();

            // 🔴 Insert into role table
            if (role.equals("Borrower")) {
                String bSql = "INSERT INTO Borrower VALUES (?, ?, ?, ?)";
                PreparedStatement pst2 = con.prepareStatement(bSql);

                pst2.setInt(1, newUserId);
                pst2.setInt(2, 1000 + newUserId);
                pst2.setInt(3, 5);
                pst2.setInt(4, 0);

                pst2.executeUpdate();
            } else {
                String aSql = "INSERT INTO Admin VALUES (?, ?, ?)";
                PreparedStatement pst3 = con.prepareStatement(aSql);

                pst3.setInt(1, newUserId);
                pst3.setInt(2, 5000 + newUserId);
                pst3.setString(3, "FULL");

                pst3.executeUpdate();
            }

            response.sendRedirect("login.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=true");
        }
    }
}
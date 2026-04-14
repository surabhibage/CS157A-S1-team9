<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Account - Library Inventory</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }
        .register-container { background: white; padding: 20px; border-radius: 8px; max-width: 400px; margin: auto; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        input[type="text"], input[type="password"], input[type="number"] { width: 100%; padding: 10px; margin: 8px 0; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        select { width: 100%; padding: 10px; margin: 8px 0; border: 1px solid #ccc; border-radius: 4px; }
        input[type="submit"] { background-color: #28a745; color: white; padding: 10px; border: none; width: 100%; border-radius: 4px; cursor: pointer; margin-top: 10px;}
        .error-message { color: red; font-size: 0.9em; text-align: center; margin-bottom: 10px; }
        .help-text { font-size: 0.8em; color: #666; margin-bottom: 10px; display: block; }
    </style>
</head>
<body>

<div class="register-container">
    <h2>Create an Account</h2>

    <%
        String error = request.getParameter("error");
        if ("duplicate".equals(error)) {
            out.println("<div class='error-message'>That username is already taken. Please choose another.</div>");
        } else if ("exception".equals(error)) {
            out.println("<div class='error-message'>An error occurred while creating your account.</div>");
        }
    %>

    <form action="RegisterServlet" method="POST">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required>

        <label for="password">Password:</label>
        <input type="password" id="password" name="password"
               pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,}"
               title="Must contain at least one number, one uppercase and lowercase letter, one special character, and at least 8 or more characters" required>
        <span class="help-text">Must include a capital letter, number, and special character.</span>

        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" name="firstName" required>

        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" name="lastName" required>

        <label for="phone">Phone Number:</label>
        <input type="text" id="phone" name="phone">

        <label for="address">Address:</label>
        <input type="text" id="address" name="address">

        <label for="age">Age:</label>
        <input type="number" id="age" name="age" min="5" max="120">

        <label for="role">Account Type:</label>
        <select id="role" name="role">
            <option value="Borrower">Borrower</option>
            <option value="Admin">Librarian (Admin)</option>
        </select>

        <input type="submit" value="Register Account">
    </form>

    <p style="text-align: center; font-size: 0.9em;">
        Already have an account? <a href="login.jsp">Log In</a>
    </p>
</div>

</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page session="true" %>
        <% if (session.getAttribute("userId")==null) { response.sendRedirect("login.jsp"); return; } String
            role=(String) session.getAttribute("role"); %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <title>Account Settings</title>
                <style>
                    body { font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9; }
                    header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px; }
                    .dashboard-container { display: flex; gap: 20px; justify-content: center; }
                    .main-content { flex: 1; max-width: 600px; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
                    .btn { padding: 10px 15px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; }
                    .btn:hover { background-color: #0056b3; }
                    .btn-danger { background-color: #dc3545; }
                    .btn-danger:hover { background-color: #c82333; }
                    .alert { padding: 10px; margin-bottom: 20px; border-radius: 4px; }
                    .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
                    .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
                    .form-group { margin-bottom: 15px; }
                    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
                    .form-group input { padding: 8px; width: 100%; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
                    hr { border: 0; border-top: 1px solid #eee; margin: 30px 0; }
                </style>
            </head>

            <body>
                <header>
                    <h2>Account Settings</h2>
                    <nav>
                        <a href="home.jsp" class="btn">Back to Home</a>
                    </nav>
                </header>

                <div class="dashboard-container">
                    <div class="main-content">
                        <% String error=request.getParameter("error"); 
                           if ("pwd_mismatch".equals(error)) { out.print("<div class='alert error'>Passwords do not match.</div>"); }
                           if ("pwd_exception".equals(error)) { out.print("<div class='alert error'>Error updating password.</div>"); }
                           if ("contact_exception".equals(error)) { out.print("<div class='alert error'>Error updating contact info.</div>"); }
                           if ("borrow_exception".equals(error)) { out.print("<div class='alert error'>Error updating borrow time.</div>"); }
                           if ("delete_exception".equals(error)) { out.print("<div class='alert error'>Error deleting account. You may have outstanding loans.</div>"); }

                           String msg = request.getParameter("msg");
                           if ("pwd_updated".equals(msg)) { out.print("<div class='alert success'>Password updated successfully.</div>"); }
                           if ("contact_updated".equals(msg)) { out.print("<div class='alert success'>Contact info updated successfully.</div>"); }
                           if ("borrow_updated".equals(msg)) { out.print("<div class='alert success'>Borrow time updated successfully.</div>"); }
                        %>

                        <h3>Change Password</h3>
                        <form action="ChangePasswordServlet" method="post">
                            <div class="form-group">
                                <label>New Password:</label>
                                <input type="password" name="newPassword" required>
                            </div>
                            <div class="form-group">
                                <label>Confirm Password:</label>
                                <input type="password" name="confirmPassword" required>
                            </div>
                            <button type="submit" class="btn">Update Password</button>
                        </form>

                        <hr>

                        <h3>Update Contact Information</h3>
                        <form action="UpdateContactServlet" method="post">
                            <div class="form-group">
                                <label>First Name:</label>
                                <input type="text" name="firstName">
                            </div>
                            <div class="form-group">
                                <label>Last Name:</label>
                                <input type="text" name="lastName">
                            </div>
                            <div class="form-group">
                                <label>Phone:</label>
                                <input type="text" name="phone">
                            </div>
                            <div class="form-group">
                                <label>Address:</label>
                                <input type="text" name="address">
                            </div>
                            <button type="submit" class="btn">Update Contact Info</button>
                        </form>

                        <hr>

                        <% if ("Admin".equals(role)) { %>
                            <h3>Set Default Borrow Time</h3>
                            <form action="SetBorrowTimeServlet" method="post">
                                <div class="form-group">
                                    <label>Borrow Time (Days):</label>
                                    <input type="number" name="borrowTime" min="1" max="365" required>
                                </div>
                                <button type="submit" class="btn">Update Borrow Time</button>
                            </form>
                            <hr>
                        <% } %>

                        <h3>Delete Account</h3>
                        <form action="DeleteAccountServlet" method="post"
                            onsubmit="return confirm('Are you sure you want to delete your account? This action cannot be undone.');">
                            <button type="submit" class="btn btn-danger">Delete Account</button>
                        </form>
                    </div>
                </div>
            </body>

            </html>
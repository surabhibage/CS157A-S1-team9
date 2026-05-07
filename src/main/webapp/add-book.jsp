<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add New Book - Library System</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 40px; }
        .container { max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        h2 { color: #0056b3; border-bottom: 2px solid #eee; padding-bottom: 10px; }
        label { display: block; margin-top: 15px; font-weight: bold; color: #333; }
        input, textarea { width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box; }
        .btn-group { margin-top: 25px; display: flex; gap: 10px; }
        .btn-save { background-color: #28a745; color: white; border: none; padding: 12px 20px; cursor: pointer; border-radius: 5px; flex: 2; }
        .btn-cancel { background-color: #6c757d; color: white; text-decoration: none; padding: 12px 20px; text-align: center; border-radius: 5px; flex: 1; }
        .btn-save:hover { background-color: #218838; }
    </style>
</head>
<body>

<div class="container">
    <h2>Add New Book to Inventory</h2>
    <form action="AddBookServlet" method="POST">

        <label for="bookId">Book ID (ISBN):</label>
        <input type="number" id="bookId" name="bookId" placeholder="e.g. 123456" required>

        <label for="title">Book Title:</label>
        <input type="text" id="title" name="title" required>

        <label for="author">Author:</label>
        <input type="text" id="author" name="author" required>

        <label for="publisher">Publisher:</label>
        <input type="text" id="publisher" name="publisher">

        <label for="pubDate">Publication Date:</label>
        <input type="date" id="pubDate" name="pubDate" required>

        <label for="genre">Genre:</label>
        <input type="text" id="genre" name="genre">

        <label for="synopsis">Synopsis:</label>
        <textarea id="synopsis" name="synopsis" rows="5" placeholder="Brief description of the book..."></textarea>

        <div class="btn-group">
            <button type="submit" class="btn-save">Save Book</button>
            <a href="home.jsp" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>

</body>
</html>
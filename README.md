Refer to 'casey' branch for web app code due on April 14th, 2026 

Explaination of files and connections: 
JSP handles the frontend of the application, this is what the user can see. The setup folder DB_Creation.sql consists of all the code which creates the tables of the library database. DB_Fill.sql file populates the tables with values. The src folder contains all the java files. These files are then triggered by buttons and forms when clicked and interacted with in the frontend - through the jsp files. The function for each of the java files are as follows: 
1. LoginServlet.java: Handles user login logic
2. RegisterServlet.java: Handles the creation of new users (who do not have a login)
3. AddBookServlet.java: Admin function of adding a new book
4. DeleteBookServlet.java: Admin function of deleting a book

The webapp folder consisting of the jsp files handle the frontend UI
Each jsp page name handles the different functional requirements. For instance, login.jsp handles the frontend for the login functional requirement. 

The WEB-INF folder class the complied class files of the java files. The connection is as follows: Login page -> Home page -> choosing necessary action 

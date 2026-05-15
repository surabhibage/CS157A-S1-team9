# Database Set Up:
In MySQL Workbench:

Open and run `setup/DB_Creation.sql` and `setup/DB_Fill.sql`.


# To Run Library Management System:

Go to `src/java/db/DatabaseConnection.java`

Update the username and password to match your MySQL database.

**In the terminal:**
```
mvn clean install
```

Copy `.war` file from target folder into `~\apache-tomcat-9.0.118\webapps`

Rename to file to `library.war`

**Start tomcat server**

Access at `http://localhost:8080/library/`

# Core Features

## Security & Authentication
- **Secure Password Hashing**: Utilizes **jBCrypt** to hash and salt passwords before they persist in the database, ensuring user data protection.
- **Role-Based Access Control**: Securely separates features between **Admin** and **Borrower** roles using session-based authentication.
- **Registration**: Allows new users to determine their role (admin/borrower) and join the library system.

## Home & Discovery
- **Search**: Find books by title, author, or genre.
- **Inventory Overview**: View a real-time list of all books and their current availability status.
- **Detailed Metadata**: Click any book title to view a dedicated **Book Details** page containing the publisher, publication date, and synopsis.

## Account Management
- **Contact Updates**: Users can update their first name, last name, phone number, and physical address.
- **Password Management**: Securely change existing passwords within the account settings.
- **Account Deletion**: Users can permanently delete their accounts; the system automatically handles the cleanup of related database records (requests, borrows, etc.).

## Admin Features
- **Library Management**: Add new books, delete outdated entries, or edit existing book metadata directly from the book details view.
- **System Configuration**: Admins can set and update the **Default Borrow Time** (in days) which dynamically calculates due dates for all new loans.
- **Dashboard**: Access a high-level view of users, roles, loans, and holds.

## Borrower Features
- **Circulation Services**: Borrow available books, return copies, and renew existing loans (limited to 3 renewals).
- **Hold System**: Place holds on books that are currently out of stock.
- **Activity Tracking**: View a personal list of "My Borrowed Books" with calculated due dates and renewal counts.

-- Create base entities first (no foreign key dependencies)
CREATE TABLE Library (
    Library_ID INT PRIMARY KEY,
    Name VARCHAR(255),
    Address VARCHAR(255),
    Phone_Number VARCHAR(20)
);

CREATE TABLE Users (
    User_ID INT PRIMARY KEY,
    Username VARCHAR(100),
    Password VARCHAR(255),
    First_Name VARCHAR(100),
    Last_Name VARCHAR(100),
    Phone VARCHAR(20),
    Address VARCHAR(255)
);

CREATE TABLE Book (
    Book_ID INT PRIMARY KEY,
    Title VARCHAR(255),
    Author VARCHAR(255),
    Publisher VARCHAR(255),
    Publication_Date DATE,
    Genre VARCHAR(100),
    Synopsis TEXT
);

CREATE TABLE Loans (
    Loan_ID INT PRIMARY KEY,
    Borrow_Date DATE,
    Due_Date DATE,
    Date_Returned DATE,
    Renewal_Count INT
);

CREATE TABLE Holds (
    Hold_ID INT PRIMARY KEY,
    Request_Date DATE,
    Expiration_Date DATE,
    Pickup_Location VARCHAR(255)
);

CREATE TABLE Settings (
    Setting_ID INT PRIMARY KEY,
    Max_Renewals INT,
    Default_Borrow_Val INT
);

-- Borrower and Admin inherit User_ID from Users
CREATE TABLE Borrower (
    User_ID INT PRIMARY KEY,
    CardNum INT,
    maxBorrowed INT,
    numBorrowed INT,
    FOREIGN KEY (User_ID) REFERENCES Users(User_ID)
);

CREATE TABLE Admin (
    User_ID INT PRIMARY KEY,
    EmployeeID INT,
    Permissions VARCHAR(255),
    FOREIGN KEY (User_ID) REFERENCES Users(User_ID)
);

-- Inventory depends on Library
CREATE TABLE Inventory (
    Copy_ID INT PRIMARY KEY,
    Status VARCHAR(50),
    Library_ID INT,
    FOREIGN KEY (Library_ID) REFERENCES Library(Library_ID)
);

-- Relationships
CREATE TABLE Has (
    Book_ID INT,
    Copy_ID INT,
    PRIMARY KEY (Book_ID, Copy_ID),
    FOREIGN KEY (Book_ID) REFERENCES Book(Book_ID),
    FOREIGN KEY (Copy_ID) REFERENCES Inventory(Copy_ID)
);

CREATE TABLE Reserves (
    Hold_ID INT,
    Book_ID INT,
    PRIMARY KEY (Hold_ID, Book_ID),
    FOREIGN KEY (Hold_ID) REFERENCES Holds(Hold_ID),
    FOREIGN KEY (Book_ID) REFERENCES Book(Book_ID)
);

CREATE TABLE Registers (
    User_ID INT,
    Library_ID INT,
    PRIMARY KEY (User_ID, Library_ID),
    FOREIGN KEY (User_ID) REFERENCES Users(User_ID),
    FOREIGN KEY (Library_ID) REFERENCES Library(Library_ID)
);

CREATE TABLE Borrows (
    User_ID INT,
    Loan_ID INT,
    CardNum INT, -- Redundant, but included per your schema
    PRIMARY KEY (User_ID, Loan_ID),
    FOREIGN KEY (User_ID) REFERENCES Borrower(User_ID),
    FOREIGN KEY (Loan_ID) REFERENCES Loans(Loan_ID)
);

CREATE TABLE LoansOut (
    Loan_ID INT,
    Copy_ID INT,
    PRIMARY KEY (Loan_ID, Copy_ID),
    FOREIGN KEY (Loan_ID) REFERENCES Loans(Loan_ID),
    FOREIGN KEY (Copy_ID) REFERENCES Inventory(Copy_ID)
);

CREATE TABLE Requests (
    User_ID INT,
    Hold_ID INT,
    CardNum INT, -- Redundant, but included per your schema
    PRIMARY KEY (User_ID, Hold_ID),
    FOREIGN KEY (User_ID) REFERENCES Borrower(User_ID),
    FOREIGN KEY (Hold_ID) REFERENCES Holds(Hold_ID)
);

CREATE TABLE Approves (
    Hold_ID INT,
    User_ID INT,
    Employee_ID INT, -- Redundant, but included per your schema
    PRIMARY KEY (Hold_ID, User_ID),
    FOREIGN KEY (Hold_ID) REFERENCES Holds(Hold_ID),
    FOREIGN KEY (User_ID) REFERENCES Admin(User_ID)
);

CREATE TABLE Admin_Modifies (
    Setting_ID INT,
    User_ID INT,
    Employee_ID INT, -- Redundant, but included per your schema
    PRIMARY KEY (Setting_ID, User_ID),
    FOREIGN KEY (Setting_ID) REFERENCES Settings(Setting_ID),
    FOREIGN KEY (User_ID) REFERENCES Admin(User_ID)
);

CREATE TABLE Edits (
    Copy_ID INT,
    User_ID INT,
    Employee_ID INT, -- Redundant, but included per your schema
    Type VARCHAR(100),
    PRIMARY KEY (Copy_ID, User_ID),
    FOREIGN KEY (Copy_ID) REFERENCES Inventory(Copy_ID),
    FOREIGN KEY (User_ID) REFERENCES Admin(User_ID)
);
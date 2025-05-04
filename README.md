# SecureVault 🔐

A **Secure Password Vault** built using **Java**, **Swing UI**, **JDBC** and **MySQL**.

This project allows users to securely store, manage, and retrieve their passwords for various services. All passwords are encrypted for maximum security.

---

## 🚀 Features

- User Registration (Signup) with secure password hashing + salt
- User Login Authentication
- Password Vault Dashboard
- Add, Edit, View, Delete Password Entries
- Password Encryption using AES
- Auto Logout on inactivity for enhanced security
- Responsive GUI with Java Swing
- MySQL Database integration
- Proper exception handling and validation
- Lightweight and Secure Java application

---

## 🛠️ Technologies Used

- Java 23
- Swing (GUI)
- JDBC (Database Connectivity)
- MySQL Database
- AES Encryption
- Git & GitHub

---

## ⚙️ How to Run

1. Clone this repository:

```bash
git clone https://github.com/your-username/SecureVault.git
```

2. Set up the MySQL Database:
   - Open MySQL Command Line Client.
   - Create the database by running:

  ```sql
  CREATE DATABASE securevault;
  ```

3. Create the necessary tables:

   - Use the securevault database:

```sql
USE securevault;
```



4. Configure Database Credentials:

  - Open ```swift
          src/com/securevault/utils/DBConnection.java
         ```

  - Update your MySQL username and password here:

  ```java
  private final String DB_USER = "your_mysql_username";
  private final String DB_PASS = "your_mysql_password";
  ```

5. Compile the project:

   ```bash
   javac -cp ".;mysql-connector-j-9.3.0.jar" src/com/securevault/*.java src/com/securevault/controller/*.java src/com/securevault/model/*.java src/com/securevault/service/*.java src/com/securevault/utils/*.java src/com/securevault/exceptions/*.java src/com/securevault/ui/*.java
   ```
   
6. Run the project:

   ```bash
   java -cp ".;mysql-connector-j-9.3.0.jar;src" com.securevault.App
   ```
   
## 📚 Project Status

- ✅ Fully Functional
- ✅ Database Connected
- 🚧 SecureVault is presently a desktop application. Development is in progress to transform it into a fully functional web application.
- 🔄 Future Scope:
     - Build a SecureVault Web Version (Spring Boot + React/HTML) to make it accessible online via Vercel, Render, or Cloud servers.

  
---

<https://drive.google.com/file/d/1XB24LiAIoDueU9diImkYjV699FApjQfB/view?usp=sharing>

 ##  🙌 Thank you for visiting!
 - Feel free to fork, contribute, or raise issues!
 - Contributions are always welcome!



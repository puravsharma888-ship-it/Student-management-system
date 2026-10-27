# Student Management System - Demo University (Tomcat 11 / Java 17)

Java web application (JSP, Jakarta Servlets, JDBC, MySQL) for managing students, courses, fees and complaints.
Branded for a fictitious "Demo University".

## Features
- Login / logout with sessions; passwords stored as PBKDF2-HMAC-SHA256 hashes
- Role-based dashboards: admin, manager, teacher, student
- Admin: register students (server-side validated), course setup, grievance handling
- Student: course registration, profile, fees, complaints
- Teacher: own profile + courses with enrolment counts
- Manager: overview of students, teachers, courses, registrations and complaints (read-only)

## Requirements
- JDK 17 or newer, Eclipse IDE with Maven, Apache Tomcat 11, MySQL Server + MySQL Workbench
- Internet access on the first Maven build so dependencies can download

## 1. Configure MySQL
1. Start MySQL Server and open MySQL Workbench.
2. Open and run the whole of `src/db/studentManagementSystem.sql` (it creates every table and demo data; it is safe to run again).
3. Check the connection settings in `src/main/java/com/studentmgmtsys/dao/Env.java`
   (default: `root` / `12345678` on `localhost:3306`). Change `DB_PASSWORD` if your MySQL password differs.

Demo logins (password for all four is `123`, local demo only):
`admin`, `manager`, `teacher01`, `stud23101`
If login fails, run `src/db/repair-login.sql`, then check the Tomcat console for database errors.

## 2. Import into Eclipse
File > Import > Maven > Existing Maven Projects > select the folder with `pom.xml`;
then Maven > Update Project; set Java 17 and the Tomcat v11.0 runtime.

## 3. Build and deploy
Run As > Maven build, goal `clean package` (creates `target/StudentManagementSystem.war`),
add the project to the Tomcat 11 server in the Servers view, start it and open
`http://localhost:8080/StudentManagementSystem/` (use your own port/context path if different).

## Notes
- Existing plain-text passwords (from an older database) are upgraded to hashes automatically on first login.
- New student passwords are hashed when the administrator registers the student.
- Not yet run against a live MySQL/Tomcat instance by the packager: test every role after deploying.

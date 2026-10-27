-- Demo University - Student Management System
-- Safe to run more than once: nothing is dropped. Run the whole script in MySQL Workbench.
CREATE DATABASE IF NOT EXISTS studentManagementSystem;
USE studentManagementSystem;

-- Login accounts. Passwords are stored as PBKDF2-HMAC-SHA256 hashes (see PasswordUtil.java).
CREATE TABLE IF NOT EXISTS login (
    user_id  INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(40)  NOT NULL,
    status   VARCHAR(40)  DEFAULT 'active'
);

-- Demo logins: admin / manager / stud23101 / teacher01, password for all = 123 (local demo only)
INSERT INTO login (username, password, role, status) VALUES
('admin',     'pbkdf2$65536$DhXn2N2nxlGkiDHMGlsxnw==$FUmVXjQp5KLAxQ828MsQicSDr1J9XtUG1z1Bh0thFlU=', 'admin',   'active'),
('manager',   'pbkdf2$65536$dbvFevibxHFqUHpAW1S/Bg==$LqxhgcGN0Bw+2w8olwWkl06CafSguPc+x+jbqm6tIDA=', 'manager', 'active'),
('stud23101', 'pbkdf2$65536$mGTtvj6GyngbfratPxZeNQ==$wmeS8iNQ9sH5tZQu0FL7GmsbylR6V3KDYgXjcufmSb8=', 'student', 'active'),
('teacher01', 'pbkdf2$65536$2QwxEhcTRycVqj1eAQoi2g==$jSmtJu7wLrUwJd4EUBKkOI0yUEpqrTpNRG2e20AOttE=', 'teacher', 'active')
ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role), status = VALUES(status);

CREATE TABLE IF NOT EXISTS students (
    student_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    registration_no VARCHAR(30) NOT NULL,
    department      VARCHAR(50) NOT NULL,
    semester        VARCHAR(10),
    admission_year  VARCHAR(10),
    status          VARCHAR(20) DEFAULT 'active',
    FOREIGN KEY (user_id) REFERENCES login(user_id)
);

CREATE TABLE IF NOT EXISTS studentProfile (
    profile_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL,
    phone         VARCHAR(15),
    address       TEXT,
    dob           DATE,
    education     VARCHAR(100),
    profile_pic   VARCHAR(255),
    father_name   VARCHAR(100),
    father_mobile VARCHAR(15),
    mother_name   VARCHAR(100),
    mother_mobile VARCHAR(15),
    FOREIGN KEY (user_id) REFERENCES login(user_id)
);

CREATE TABLE IF NOT EXISTS teacherProfile (
    profile_id  INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    phone       VARCHAR(15),
    department  VARCHAR(50),
    experience  VARCHAR(100),
    education   VARCHAR(100),
    profile_pic VARCHAR(255),
    status      VARCHAR(20) DEFAULT 'active',
    FOREIGN KEY (user_id) REFERENCES login(user_id)
);

CREATE TABLE IF NOT EXISTS all_courses (
    course_id   INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20)  NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    branch      VARCHAR(50)  NOT NULL,
    semester    INT NOT NULL,
    credits     INT NOT NULL,
    status      VARCHAR(20) DEFAULT 'active'
);

CREATE TABLE IF NOT EXISTS registered_course (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    course_id     INT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_course (user_id, course_id),
    FOREIGN KEY (user_id)   REFERENCES login(user_id),
    FOREIGN KEY (course_id) REFERENCES all_courses(course_id)
);

-- Column "semister" keeps the spelling used by FeesDAO.java
CREATE TABLE IF NOT EXISTS fees (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    stud_id   INT NOT NULL,
    transport BOOLEAN DEFAULT FALSE,
    semister  BOOLEAN DEFAULT FALSE,
    hostel    BOOLEAN DEFAULT FALSE,
    misc      BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (stud_id) REFERENCES login(user_id)
);

CREATE TABLE IF NOT EXISTS complain (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(150) NOT NULL,
    description  TEXT,
    user_id      INT NOT NULL,
    status       VARCHAR(20) DEFAULT 'pending',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES login(user_id)
);

-- ---------- Demo data (inserted only when missing) ----------
INSERT INTO students (user_id, registration_no, department, semester, admission_year, status)
SELECT l.user_id, '2307002', 'BCA', '5', '2023', 'active' FROM login l
WHERE l.username = 'stud23101' AND NOT EXISTS (SELECT 1 FROM students s WHERE s.user_id = l.user_id);

INSERT INTO studentProfile (user_id, full_name, email, phone, address, dob, education,
                            father_name, father_mobile, mother_name, mother_mobile)
SELECT l.user_id, 'Rahul Kumar', 'rahul@example.com', '9876543210', 'Delhi', '2004-05-10', '12th',
       'Suresh Kumar', '9876500001', 'Anita Kumar', '9876500002' FROM login l
WHERE l.username = 'stud23101' AND NOT EXISTS (SELECT 1 FROM studentProfile p WHERE p.user_id = l.user_id);

INSERT INTO teacherProfile (user_id, full_name, email, phone, department, experience, education)
SELECT l.user_id, 'Priya Sharma', 'priya@example.com', '9998887770', 'Computer Science', '5 years', 'M.Tech' FROM login l
WHERE l.username = 'teacher01' AND NOT EXISTS (SELECT 1 FROM teacherProfile t WHERE t.user_id = l.user_id);

INSERT INTO all_courses (course_code, course_name, branch, semester, credits, status) VALUES
('DU501', 'Internet and Web Technology', 'BCA', 5, 4, 'active'),
('DU502', 'Computer Communication Networks', 'BCA', 5, 4, 'active'),
('DU503', 'Python Programming', 'BCA', 5, 4, 'active')
ON DUPLICATE KEY UPDATE course_name = VALUES(course_name);

INSERT INTO fees (stud_id, transport, semister, hostel, misc)
SELECT l.user_id, FALSE, FALSE, FALSE, FALSE FROM login l
WHERE l.username = 'stud23101' AND NOT EXISTS (SELECT 1 FROM fees f WHERE f.stud_id = l.user_id);

SELECT user_id, username, role, status FROM login ORDER BY user_id;

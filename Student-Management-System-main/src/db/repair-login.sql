-- Login repair script (nothing is dropped). Resets the four demo accounts to password 123.
CREATE DATABASE IF NOT EXISTS studentManagementSystem;
USE studentManagementSystem;

CREATE TABLE IF NOT EXISTS login (
    user_id  INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(40)  NOT NULL,
    status   VARCHAR(40)  DEFAULT 'active'
);

INSERT INTO login (username, password, role, status) VALUES
('admin',     'pbkdf2$65536$DhXn2N2nxlGkiDHMGlsxnw==$FUmVXjQp5KLAxQ828MsQicSDr1J9XtUG1z1Bh0thFlU=', 'admin',   'active'),
('manager',   'pbkdf2$65536$dbvFevibxHFqUHpAW1S/Bg==$LqxhgcGN0Bw+2w8olwWkl06CafSguPc+x+jbqm6tIDA=', 'manager', 'active'),
('stud23101', 'pbkdf2$65536$mGTtvj6GyngbfratPxZeNQ==$wmeS8iNQ9sH5tZQu0FL7GmsbylR6V3KDYgXjcufmSb8=', 'student', 'active'),
('teacher01', 'pbkdf2$65536$2QwxEhcTRycVqj1eAQoi2g==$jSmtJu7wLrUwJd4EUBKkOI0yUEpqrTpNRG2e20AOttE=', 'teacher', 'active')
ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role), status = VALUES(status);

SELECT user_id, username, role, status FROM login ORDER BY user_id;

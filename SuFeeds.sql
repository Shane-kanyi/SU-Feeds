-- Active: 1745313197699@@127.0.0.1@5432@db_first_last_studentnumber
-- Create Student Table
CREATE TABLE IF NOT EXISTS tbl_students (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    password VARCHAR(100)
);

-- Create Class Table
CREATE TABLE IF NOT EXISTS tbl_classes (
    class_id VARCHAR(10) PRIMARY KEY,  
    student_id INT,
    class_name VARCHAR(100),
    semester VARCHAR(10), 
    FOREIGN KEY (student_id) REFERENCES tbl_students(id)
);

-- Create Topic Table
CREATE TABLE IF NOT EXISTS tbl_topics (
    id SERIAL PRIMARY KEY,
    class_id VARCHAR(10),
    topic_name VARCHAR(100),
    week INT,
    student_id INT,
    FOREIGN KEY (class_id) REFERENCES tbl_classes(class_id),
    FOREIGN KEY (student_id) REFERENCES tbl_students(id)
);

-- Create Feedback Table
CREATE TABLE IF NOT EXISTS tbl_feedback (
    id SERIAL PRIMARY KEY,
    topic_id INT,
	student_id INT,
    feedback_content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES tbl_topics(id),
	FOREIGN KEY (student_id) REFERENCES tbl_students(id)
);

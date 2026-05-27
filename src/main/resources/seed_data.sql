-- Xóa dữ liệu cũ (nếu có) để tránh lỗi trùng lặp khi chạy lại
-- Chú ý thứ tự xóa: bảng con xóa trước, bảng cha xóa sau
TRUNCATE TABLE reviews, notifications, lesson_progress, enrollments, lessons, courses, users RESTART IDENTITY CASCADE;

-- ==========================================
-- 1. THÊM DỮ LIỆU BẢNG USERS
-- Mật khẩu mặc định cho tất cả user là 'password' (đã được băm bằng BCrypt)
-- ==========================================
INSERT INTO users (username, password_hash, email, full_name, role, is_active, created_at, updated_at) VALUES
('admin01', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'admin@example.com', 'Quản Trị Viên Một', 'ADMIN', true, NOW(), NOW()),
('teacher01', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'teacher1@example.com', 'Giảng Viên Một', 'TEACHER', true, NOW(), NOW()),
('teacher02', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'teacher2@example.com', 'Giảng Viên Hai', 'TEACHER', true, NOW(), NOW()),
('student01', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'student1@example.com', 'Học Viên Một', 'STUDENT', true, NOW(), NOW()),
('student02', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'student2@example.com', 'Học Viên Hai', 'STUDENT', true, NOW(), NOW()),
('student03', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'student3@example.com', 'Học Viên Ba', 'STUDENT', true, NOW(), NOW()),
('student04', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'student4@example.com', 'Học Viên Tư', 'STUDENT', true, NOW(), NOW()),
('student05', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'student5_inactive@example.com', 'Học Viên Bị Khóa', 'STUDENT', false, NOW(), NOW()), -- Dành cho test filter status inactive
('teacher03', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'teacher3@example.com', 'Giảng Viên Ba', 'TEACHER', true, NOW(), NOW()),
('admin02', '$2a$10$wYOMPq.0E86h8J5/oX.oJ.LzB01nK6Z30n9.53kX3k2/qL3/j5/yO', 'admin2@example.com', 'Quản Trị Viên Hai', 'ADMIN', true, NOW(), NOW());

-- ==========================================
-- 2. THÊM DỮ LIỆU BẢNG COURSES
-- teacher_id tham chiếu tới users (ID 2, 3, 9 là các TEACHER)
-- ==========================================
INSERT INTO courses (title, description, teacher_id, price, duration_hours, status, created_at, updated_at) VALUES
('Khóa học Spring Boot Cơ Bản', 'Làm quen với Spring Boot 3 và RESTful API.', 2, 299000.00, 20, 'PUBLISHED', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
('Khóa học Hibernate Nâng Cao', 'Đi sâu vào JPA và tối ưu hóa truy vấn.', 2, 450000.00, 35, 'DRAFT', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
('Frontend với ReactJS', 'Xây dựng giao diện tương tác với React và Tailwind.', 3, 300000.00, 25, 'PUBLISHED', NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
('Lập trình Java Web từ A-Z', 'Xây dựng ứng dụng Web hoàn chỉnh bằng Servlet, JSP và kết nối DB.', 2, 500000.00, 60, 'PUBLISHED', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days'),
('Cấu trúc dữ liệu và Giải thuật', 'Nắm vững các cấu trúc dữ liệu và thuật toán cơ bản phục vụ phỏng vấn.', 9, 199000.00, 30, 'PUBLISHED', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
('Cơ sở dữ liệu PostgreSQL', 'Thiết kế, tối ưu hóa và quản trị CSDL PostgreSQL chuyên sâu.', 9, 250000.00, 15, 'ARCHIVED', NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days'),
('Python cho Khoa học Dữ liệu', 'Học Python cơ bản và các thư viện phân tích dữ liệu: Numpy, Pandas.', 3, 350000.00, 40, 'PUBLISHED', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
('Docker & Kubernetes cho DevOps', 'Triển khai và quản trị container hóa ứng dụng microservices.', 2, 600000.00, 18, 'DRAFT', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day');

-- ==========================================
-- 3. THÊM DỮ LIỆU BẢNG LESSONS
-- course_id tham chiếu tới courses (ID 1 đến 8)
-- ==========================================
INSERT INTO lessons (course_id, title, content_url, text_content, order_index, is_published, created_at, updated_at) VALUES
-- Khóa 1: Spring Boot Cơ Bản (Có 5 bài học)
(1, 'Bài 1: Giới thiệu Spring Boot', 'https://video.example.com/spring-1', 'Nội dung bài 1: Giới thiệu kiến trúc framework, so sánh với Spring MVC truyền thống.', 1, true, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days'),
(1, 'Bài 2: Dependency Injection', 'https://video.example.com/spring-2', 'Nội dung bài 2: Giải thích DI và IoC Container, cách sử dụng @Autowired, @Component.', 2, true, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days'),
(1, 'Bài 3: Tạo REST API', NULL, 'Nội dung bài 3: Sử dụng @RestController, @GetMapping, @PostMapping và thiết lập HTTP status.', 3, true, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days'),
(1, 'Bài 4: Spring Data JPA', 'https://video.example.com/spring-4', 'Nội dung bài 4: Kết nối cơ sở dữ liệu PostgreSQL, định nghĩa các Repository, sử dụng Query Method.', 4, true, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'),
(1, 'Bài 5: Spring Security & JWT', 'https://video.example.com/spring-5', 'Nội dung bài 5: Phân quyền API, cấu hình SecurityFilterChain, đăng nhập và sinh JWT token.', 5, true, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'),

-- Khóa 2: Hibernate Nâng Cao (Có 2 bài học, chưa publish vì khóa học là DRAFT)
(2, 'Bài 1: Entity Lifecycle', 'https://video.example.com/hibernate-1', 'Vòng đời của một Entity trong Hibernate: Transient, Managed, Detached, Removed.', 1, false, NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days'),
(2, 'Bài 2: Association Mapping', 'https://video.example.com/hibernate-2', 'Chi tiết cách thiết lập quan hệ @OneToMany, @ManyToOne, @ManyToMany và Cascade.', 2, false, NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days'),

-- Khóa 3: Frontend với ReactJS (Có 3 bài học)
(3, 'Bài 1: React Component & JSX', 'https://video.example.com/react-1', 'Khái niệm về Component, cú pháp JSX, cách nhúng mã Javascript vào HTML.', 1, true, NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days'),
(3, 'Bài 2: State & Props', 'https://video.example.com/react-2', 'Truyền dữ liệu giữa các component qua Props, quản lý trạng thái local bằng State.', 2, true, NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days'),
(3, 'Bài 3: React Hooks', 'https://video.example.com/react-3', 'Tìm hiểu vòng đời component và cách sử dụng các hook phổ biến: useState, useEffect.', 3, true, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),

-- Khóa 4: Java Web (Có 2 bài học)
(4, 'Bài 1: Tổng quan về Servlet & JSP', 'https://video.example.com/javaweb-1', 'Cấu trúc thư mục ứng dụng web Java, vòng đời servlet, xử lý request và response.', 1, true, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),
(4, 'Bài 2: MVC Model 2 Architecture', 'https://video.example.com/javaweb-2', 'Xây dựng ứng dụng theo mô hình MVC tách biệt Controller, View và Model.', 2, true, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),

-- Khóa 5: Cấu trúc dữ liệu và Giải thuật (Có 2 bài học)
(5, 'Bài 1: Độ phức tạp thuật toán (Time Complexity)', 'https://video.example.com/dsa-1', 'Khái niệm Big O notation, cách tối ưu hóa các vòng lặp chồng nhau.', 1, true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
(5, 'Bài 2: Các cấu trúc dữ liệu tuyến tính', 'https://video.example.com/dsa-2', 'Triển khai và sử dụng Stack, Queue, Array, và Linked List trong Java.', 2, true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),

-- Khóa 7: Python cho Khoa học Dữ liệu (Có 1 bài học)
(7, 'Bài 1: Cú pháp cơ bản và kiểu dữ liệu', 'https://video.example.com/python-1', 'Biến, câu lệnh rẽ nhánh if-else, vòng lặp for/while, và các kiểu List, Dict trong Python.', 1, true, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days');

-- ==========================================
-- 4. THÊM DỮ LIỆU BẢNG ENROLLMENTS
-- student_id tham chiếu tới users (ID 4, 5, 6, 7 là các STUDENT)
-- ==========================================
INSERT INTO enrollments (student_id, course_id, enrollment_date, status, progress_percentage) VALUES
(4, 1, NOW() - INTERVAL '10 days', 'ENROLLED', 60.00),  -- student01 đăng ký khóa Spring Boot (đã xong 3/5 bài)
(5, 1, NOW() - INTERVAL '12 days', 'COMPLETED', 100.00), -- student02 đăng ký và đã hoàn thành khóa Spring Boot (5/5 bài)
(4, 3, NOW() - INTERVAL '5 days', 'ENROLLED', 33.33),   -- student01 đăng ký khóa ReactJS (đã xong 1/3 bài)
(6, 1, NOW() - INTERVAL '14 days', 'COMPLETED', 100.00), -- student03 đăng ký và đã hoàn thành khóa Spring Boot (5/5 bài)
(6, 4, NOW() - INTERVAL '6 days', 'ENROLLED', 50.00),   -- student03 đăng ký khóa Java Web (đã xong 1/2 bài)
(7, 5, NOW() - INTERVAL '3 days', 'ENROLLED', 0.00),     -- student04 đăng ký khóa DSA (đang học, 0/2 bài)
(7, 1, NOW() - INTERVAL '8 days', 'ENROLLED', 20.00),    -- student04 đăng ký khóa Spring Boot (đã xong 1/5 bài)
(7, 3, NOW() - INTERVAL '9 days', 'COMPLETED', 100.00);  -- student04 đăng ký khóa ReactJS (đã hoàn thành 3/3 bài)

-- Cập nhật ngày hoàn thành (completion_date) cho các enrollment có trạng thái COMPLETED
UPDATE enrollments SET completion_date = NOW() - INTERVAL '2 days' WHERE enrollment_id = 2;
UPDATE enrollments SET completion_date = NOW() - INTERVAL '4 days' WHERE enrollment_id = 4;
UPDATE enrollments SET completion_date = NOW() - INTERVAL '1 day' WHERE enrollment_id = 8;

-- ==========================================
-- 5. THÊM DỮ LIỆU BẢNG LESSON_PROGRESS
-- enrollment_id tham chiếu tới enrollments (1 đến 8)
-- lesson_id tham chiếu tới lessons (1 đến 15)
-- ==========================================
INSERT INTO lesson_progress (enrollment_id, lesson_id, is_completed, completed_at, last_accessed_at) VALUES
-- student01 (enrollment 1) học khóa 1 (gồm lesson 1, 2, 3, 4, 5) -> hoàn thành 3 bài đầu
(1, 1, true, NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days'),
(1, 2, true, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),
(1, 3, true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(1, 4, false, NULL, NOW() - INTERVAL '2 days'),
(1, 5, false, NULL, NOW() - INTERVAL '2 days'),

-- student02 (enrollment 2) học khóa 1 -> hoàn thành cả 5 bài
(2, 1, true, NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days'),
(2, 2, true, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
(2, 3, true, NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days'),
(2, 4, true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(2, 5, true, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),

-- student01 (enrollment 3) học khóa 3 (gồm lesson 8, 9, 10) -> hoàn thành 1 bài đầu
(3, 8, true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
(3, 9, false, NULL, NOW() - INTERVAL '2 days'),
(3, 10, false, NULL, NOW() - INTERVAL '1 day'),

-- student03 (enrollment 4) học khóa 1 -> hoàn thành cả 5 bài
(4, 1, true, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'),
(4, 2, true, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
(4, 3, true, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
(4, 4, true, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),
(4, 5, true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),

-- student03 (enrollment 5) học khóa 4 (gồm lesson 11, 12) -> hoàn thành 1 bài đầu
(5, 11, true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(5, 12, false, NULL, NOW() - INTERVAL '3 days'),

-- student04 (enrollment 6) học khóa 5 (gồm lesson 13, 14) -> chưa hoàn thành bài nào
(6, 13, false, NULL, NOW() - INTERVAL '2 days'),
(6, 14, false, NULL, NOW() - INTERVAL '1 day'),

-- student04 (enrollment 7) học khóa 1 -> hoàn thành 1 bài đầu
(7, 1, true, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),
(7, 2, false, NULL, NOW() - INTERVAL '5 days'),
(7, 3, false, NULL, NOW() - INTERVAL '3 days'),
(7, 4, false, NULL, NOW() - INTERVAL '3 days'),
(7, 5, false, NULL, NOW() - INTERVAL '2 days'),

-- student04 (enrollment 8) học khóa 3 -> hoàn thành cả 3 bài
(8, 8, true, NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days'),
(8, 9, true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(8, 10, true, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day');

-- ==========================================
-- 6. THÊM DỮ LIỆU BẢNG NOTIFICATIONS
-- user_id tham chiếu tới users (ID 4, 5, 6 là các STUDENT, ID 2 là TEACHER)
-- ==========================================
INSERT INTO notifications (user_id, message, type, target_url, is_read, created_at) VALUES
(4, 'Chào mừng bạn đã gia nhập hệ thống học tập trực tuyến CourseMS.', 'SYSTEM_ALERT', '/api/courses', true, NOW() - INTERVAL '10 days'),
(4, 'Bạn đã đăng ký thành công khóa học Spring Boot Cơ Bản. Hãy bắt đầu bài học đầu tiên ngay!', 'ENROLLMENT_CONFIRMED', '/api/courses/1', true, NOW() - INTERVAL '10 days'),
(2, 'Yêu cầu xuất bản khóa học Spring Boot Cơ Bản của bạn đã được phê duyệt.', 'NEW_COURSE', '/api/courses/1', false, NOW() - INTERVAL '15 days'),
(5, 'Chúc mừng bạn đã hoàn thành xuất sắc khóa học Spring Boot Cơ Bản! Hãy kiểm tra chứng chỉ trong hồ sơ.', 'ENROLLMENT_CONFIRMED', '/api/enrollments/2', false, NOW() - INTERVAL '2 days'),
(6, 'Giảng viên vừa thêm bài học mới trong khóa Lập trình Java Web từ A-Z.', 'LESSON_UPDATED', '/api/courses/4', false, NOW() - INTERVAL '3 days'),
(4, 'Bạn chưa tham gia học khóa Frontend với ReactJS trong vòng 3 ngày qua. Hãy sắp xếp thời gian nhé!', 'SYSTEM_ALERT', '/api/courses/3', false, NOW() - INTERVAL '1 day');

-- ==========================================
-- 7. THÊM DỮ LIỆU BẢNG REVIEWS
-- student_id tham chiếu tới users (các STUDENT 4, 5, 6, 7)
-- course_id tham chiếu tới courses (1 và 3)
-- ==========================================
INSERT INTO reviews (student_id, course_id, rating, comment, created_at, updated_at) VALUES
(5, 1, 5, 'Khóa học rất hay và thực tế! Giảng viên nhiệt tình, bài tập phong phú.', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(4, 1, 4, 'Nội dung rất tốt, tuy nhiên bài số 5 về Security cấu hình hơi phức tạp một chút.', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
(6, 1, 5, 'Quá tuyệt vời! Nhờ khóa học này mà mình đã vượt qua kỳ phỏng vấn thực tập sinh Spring Boot.', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(4, 3, 4, 'ReactJS dạy rất dễ tiếp cận cho người mới, giao diện bài giảng đẹp mắt.', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
(7, 3, 5, 'Một trong những khóa học React chất lượng nhất mình từng học. Giải thích Hooks rất trực quan.', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
(6, 4, 3, 'Kiến thức Servlet/JSP rất chi tiết nhưng video bài giảng hơi dài và lý thuyết nhiều.', NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours');

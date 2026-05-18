# Course Management System API

Hệ thống RESTful API quản lý khóa học trực tuyến — xây dựng bằng Java Spring Boot với phân quyền JWT.

##  Mục lục
- [Giới thiệu](#giới-thiệu)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Cơ sở dữ liệu](#cơ-sở-dữ-liệu)
- [Cài đặt & Chạy dự án](#cài-đặt--chạy-dự-án)
- [Xác thực & Phân quyền](#xác-thực--phân-quyền)
- [Danh sách API](#danh-sách-api)
- [Chuẩn Response](#chuẩn-response)
- [Test Case](#test-case)
- [Clean Code Guidelines](#clean-code-guidelines)

---

## Giới thiệu
Hệ thống API Quản lý Khóa học cung cấp đầy đủ các chức năng:
- **Xác thực:** Đăng nhập, đăng xuất, xác thực JWT.
- **Sinh viên:** Xem, tìm kiếm, đăng ký khóa học và theo dõi tiến độ học.
- **Giảng viên:** Thêm, sửa, xóa bài học trong khóa học mình phụ trách.
- **Admin:** Toàn quyền quản lý khóa học, người dùng và phân quyền.

Tổng số API: 44 endpoints | Bắt buộc: 60 điểm | Không bắt buộc: 40 điểm

---

## Công nghệ sử dụng

| Thành phần | Công nghệ |
| :--- | :--- |
| Ngôn ngữ | Java 17+ |
| Framework | Spring Boot 3.x |
| ORM | Spring Data JPA + Hibernate |
| Cơ sở dữ liệu | PostgreSQL |
| Bảo mật | Spring Security + JWT |
| Build tool | Gradle |
| Test | Postman |
| Triển khai | Tomcat Server |

---

## Cấu trúc dự án

```text
src/main/java/com/example/coursems/
├── config/           # JWT, Security, Exception Handler
├── controller/       # API endpoints
├── dto/              # Request/Response models
├── entity/           # JPA Entity: User, Course, Lesson, Enrollment
├── repository/       # Spring Data JPA Repositories
├── service/          # Business logic (interface + impl)
├── mapper/           # Map DTO <-> Entity
└── util/             # Helper classes (Validation, Constants)
```

---

## Cơ sở dữ liệu
Hệ thống gồm 7 bảng chính:

### Users
| Trường | Kiểu | Ràng buộc |
| :--- | :--- | :--- |
| `user_id` | INT | PK, AUTO_INCREMENT |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL |
| `password_hash` | VARCHAR(255) | NOT NULL |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL |
| `full_name` | VARCHAR(100) | NOT NULL |
| `role` | ENUM | 'ADMIN', 'TEACHER', 'STUDENT' (DEFAULT 'STUDENT') |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT TRUE |
| `created_at` / `updated_at` | DATETIME | AUTO |

### Courses
| Trường | Kiểu | Ràng buộc |
| :--- | :--- | :--- |
| `course_id` | INT | PK, AUTO_INCREMENT |
| `title` | VARCHAR(255) | NOT NULL |
| `description` | TEXT | NULLABLE |
| `teacher_id` | INT | FK -> Users |
| `price` | DECIMAL(10,2) | DEFAULT 0.00 |
| `duration_hours` | INT | NULLABLE |
| `status` | ENUM | 'DRAFT', 'PUBLISHED', 'ARCHIVED' (DEFAULT 'DRAFT') |

### Lessons
| Trường | Kiểu | Ràng buộc |
| :--- | :--- | :--- |
| `lesson_id` | INT | PK, AUTO_INCREMENT |
| `course_id` | INT | FK -> Courses |
| `title` | VARCHAR(255) | NOT NULL |
| `content_url` | VARCHAR(500) | NULLABLE |
| `text_content` | TEXT | NULLABLE |
| `order_index` | INT | NOT NULL |
| `is_published` | BOOLEAN | DEFAULT FALSE |

### Enrollments
| Trường | Kiểu | Ràng buộc |
| :--- | :--- | :--- |
| `enrollment_id` | INT | PK, AUTO_INCREMENT |
| `student_id` | INT | FK -> Users |
| `course_id` | INT | FK -> Courses |
| `enrollment_date` | DATETIME | DEFAULT NOW() |
| `status` | ENUM | 'ENROLLED', 'COMPLETED', 'DROPPED' (DEFAULT 'ENROLLED') |
| `progress_percentage` | DECIMAL(5,2) | DEFAULT 0.00 |

*Lưu ý: Bảng Enrollments có thêm ràng buộc `UNIQUE(student_id, course_id)`.*

### LessonProgress
| Trường | Kiểu | Ràng buộc |
| :--- | :--- | :--- |
| `progress_id` | INT | PK, AUTO_INCREMENT |
| `enrollment_id` | INT | FK -> Enrollments, NOT NULL |
| `lesson_id` | INT | FK -> Lessons, NOT NULL |
| `is_completed` | BOOLEAN | DEFAULT FALSE, NOT NULL |
| `completed_at` | DATETIME | NULLABLE |
| `last_accessed_at` | DATETIME | AUTO, NOT NULL |

*Lưu ý: Bảng LessonProgress có thêm ràng buộc `UNIQUE(enrollment_id, lesson_id)`.*

### Notifications
| Trường | Kiểu | Ràng buộc |
| :--- | :--- | :--- |
| `notification_id` | INT | PK, AUTO_INCREMENT |
| `user_id` | INT | FK -> Users, NOT NULL |
| `message` | TEXT | NOT NULL |
| `type` | ENUM | NULLABLE |
| `target_url` | VARCHAR(500) | NULLABLE |
| `is_read` | BOOLEAN | DEFAULT FALSE, NOT NULL |
| `created_at` | DATETIME | AUTO, NOT NULL |

### Reviews
| Trường | Kiểu | Ràng buộc |
| :--- | :--- | :--- |
| `review_id` | INT | PK, AUTO_INCREMENT |
| `course_id` | INT | FK -> Courses, NOT NULL |
| `student_id` | INT | FK -> Users, NOT NULL |
| `rating` | INT | CHECK (rating >= 1 AND rating <= 5), NOT NULL |
| `comment` | TEXT | NULLABLE |
| `created_at` / `updated_at` | DATETIME | AUTO, NOT NULL |

*Lưu ý: Bảng Reviews có thêm ràng buộc `UNIQUE(course_id, student_id)`.*

---

## Cài đặt & Chạy dự án

### Yêu cầu
- Java 17+
- PostgreSQL 14+
- Gradle 8+

### Các bước cài đặt

```bash
# 1. Clone repository
git clone https://github.com/<your-username>/course-management-system.git
cd course-management-system

# 2. Cấu hình database
cp src/main/resources/application.example.yml src/main/resources/application.yml
# Chỉnh sửa thông tin DB trong application.yml

# 3. Build và chạy
./gradlew bootRun
```

### Biến môi trường
| Biến | Mô tả | Mặc định |
| :--- | :--- | :--- |
| `DB_HOST` | Host PostgreSQL | `localhost` |
| `DB_PORT` | Port PostgreSQL | `5432` |
| `DB_NAME` | Tên database | `course_db` |
| `DB_USERNAME` | Username | `postgres` |
| `DB_PASSWORD` | Password | *(bắt buộc đặt)* |
| `JWT_SECRET` | Secret key JWT | *(bắt buộc đặt)* |
| `JWT_EXPIRATION` | Thời hạn token (ms) | `86400000` |

---

## Xác thực & Phân quyền
Hệ thống sử dụng JWT Bearer Token. Sau khi đăng nhập, thêm token vào header:
`Authorization: Bearer <your_token>`

### Vai trò người dùng
| Role | Quyền |
| :--- | :--- |
| **ADMIN** | Toàn quyền: quản lý khóa học, người dùng, phân quyền |
| **TEACHER** | Tạo và chỉnh sửa bài học trong khóa học mình phụ trách |
| **STUDENT** | Xem, đăng ký khóa học và theo dõi tiến độ |
| **PUBLIC** | Không cần đăng nhập |
| **AUTH** | Đã đăng nhập (tất cả role) |

---

## Danh sách API

### Authentication
| # | Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| 1 | POST | `/api/auth/login` | PUBLIC | Đăng nhập, nhận JWT token |
| 2 | POST | `/api/auth/verify` | AUTH | Xác thực token |
| 3 | GET | `/api/auth/me` | AUTH | Lấy thông tin người dùng hiện tại |
| 4 | POST | `/api/auth/logout` | AUTH | Đăng xuất, invalidate token (optional) |

### Users
| # | Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| 5 | GET | `/api/users` | ADMIN | Lấy danh sách tất cả người dùng (hỗ trợ lọc `?status={active/inactive}`) |
| 6 | GET | `/api/users/{user_id}` | ADMIN | Lấy thông tin chi tiết người dùng |
| 7 | POST | `/api/users` | ADMIN | Tạo tài khoản mới |
| 8 | PUT | `/api/users/{user_id}` | OWNER_OR_ADMIN | Cập nhật thông tin cá nhân |
| 9 | PUT | `/api/users/{user_id}/role` | ADMIN | Cập nhật vai trò người dùng |
| 10 | PUT | `/api/users/{user_id}/status` | ADMIN | Kích hoạt / vô hiệu hóa tài khoản |
| 11 | PUT | `/api/users/{user_id}/password` | OWNER_OR_ADMIN | Đổi mật khẩu |
| 12 | DELETE | `/api/users/{user_id}` | ADMIN | Xóa người dùng |

### Courses
| # | Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| 13 | GET | `/api/courses` | AUTH | Lấy danh sách khóa học |
| 14 | GET | `/api/courses?search={keyword}` | AUTH | Tìm kiếm khóa học |
| 15 | GET | `/api/courses?teacher_id={id}` | AUTH | Lọc khóa học theo giảng viên |
| 15b | GET | `/api/courses?status={status}` | AUTH | Lọc khóa học theo trạng thái (ADMIN thấy tất cả, STUDENT/TEACHER chỉ thấy PUBLISHED) |
| 16 | GET | `/api/courses/{course_id}` | AUTH | Chi tiết khóa học |
| 17 | POST | `/api/courses` | ADMIN | Tạo khóa học mới (DRAFT) |
| 18 | PUT | `/api/courses/{course_id}` | ADMIN | Cập nhật thông tin khóa học |
| 19 | PUT | `/api/courses/{course_id}/status` | ADMIN | Cập nhật trạng thái (DRAFT/PUBLISHED/ARCHIVED) |
| 20 | DELETE | `/api/courses/{course_id}` | ADMIN | Xóa khóa học |

### Lessons
| # | Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| 21 | GET | `/api/courses/{course_id}/lessons` | AUTH | Danh sách bài học (chỉ PUBLISHED) |
| 22 | GET | `/api/lessons/{lesson_id}` | AUTH | Chi tiết bài học (chỉ PUBLISHED) |
| 22b | GET | `/api/lessons/{lesson_id}/content_preview` | AUTH | Lấy nội dung xem trước của bài học (ví dụ: đoạn trích ngắn) |
| 23 | POST | `/api/courses/{course_id}/lessons` | TEACHER_OR_ADMIN | Thêm bài học mới |
| 24 | PUT | `/api/lessons/{lesson_id}` | TEACHER_OR_ADMIN | Cập nhật bài học |
| 25 | PUT | `/api/lessons/{lesson_id}/publish` | TEACHER_OR_ADMIN | Cập nhật trạng thái hiển thị |
| 26 | DELETE | `/api/lessons/{lesson_id}` | TEACHER_OR_ADMIN | Xóa bài học |

### Enrollments
| # | Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| 27 | GET | `/api/enrollments` | STUDENT | Danh sách khóa học đã đăng ký |
| 28 | POST | `/api/enrollments` | STUDENT | Đăng ký khóa học |
| 29 | GET | `/api/enrollments/{enrollment_id}` | STUDENT | Chi tiết đăng ký & tiến độ |
| 30 | PUT | `/api/enrollments/{enrollment_id}/complete_lesson/{lesson_id}` | STUDENT | Đánh dấu bài học hoàn thành |

### Notifications (optional)
| # | Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| 31 | GET | `/api/notifications` | AUTH | Danh sách thông báo |
| 32 | PUT | `/api/notifications/{id}/read` | AUTH | Đánh dấu đã đọc |
| 33 | POST | `/api/notifications` | ADMIN | Tạo thông báo |
| 34 | DELETE | `/api/notifications/{id}` | ADMIN | Xóa thông báo |

### Reports (optional)
| # | Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| 35 | GET | `/api/reports/top_courses` | ADMIN | Khóa học phổ biến nhất |
| 36 | GET | `/api/reports/student_progress/{student_id}` | ADMIN | Tiến độ học của sinh viên |
| 37 | GET | `/api/reports/teacher_courses_overview/{teacher_id}` | ADMIN | Tổng quan khóa học của giảng viên |

### Reviews (optional)
| # | Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- | :--- |
| 38 | GET | `/api/courses/{course_id}/reviews` | AUTH | Danh sách đánh giá |
| 39 | POST | `/api/courses/{course_id}/reviews` | STUDENT | Gửi đánh giá |
| 40 | PUT | `/api/reviews/{review_id}` | OWNER_OR_ADMIN | Cập nhật đánh giá |
| 41 | DELETE | `/api/reviews/{review_id}` | OWNER_OR_ADMIN | Xóa đánh giá |

---

## Chuẩn Response
Tất cả API trả về theo format thống nhất:

```json
{
  "success": true,
  "message": "Thao tác thành công",
  "data": {},
  "errors": null,
  "timestamp": "2025-07-30T09:45:00"
}
```

Response có phân trang:

```json
{
  "success": true,
  "message": "Lấy danh sách thành công",
  "data": {
    "items": [...],
    "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalPages": 5,
      "totalItems": 50
    }
  },
  "errors": null,
  "timestamp": "2025-07-30T09:45:00"
}
```

### HTTP Status Codes
| Status | Ý nghĩa | Tình huống |
| :--- | :--- | :--- |
| **200 OK** | Thành công | GET, PUT, DELETE thành công |
| **201 Created** | Tạo mới thành công | POST tạo resource mới |
| **400 Bad Request** | Dữ liệu không hợp lệ | Sai format, thiếu trường |
| **401 Unauthorized** | Thiếu hoặc sai token | Chưa đăng nhập |
| **403 Forbidden** | Không đủ quyền | Sai role |
| **404 Not Found** | Không tìm thấy | ID không tồn tại |
| **409 Conflict** | Xung đột | Đăng ký trùng, tên trùng |
| **500 Internal Server Error** | Lỗi hệ thống | Exception chưa xử lý |

---

## Test Case
Mỗi API phải có tối thiểu 3 test case:

| Loại | Mô tả |
| :--- | :--- |
| **Happy path** | Input đúng, kết quả trả về đúng kỳ vọng |
| **Invalid input** | Thiếu trường, sai format, ID không tồn tại |
| **Unauthorized / Role** | Thiếu token (401), sai vai trò (403) |
| **Conflict / Business** | Đăng ký trùng, vi phạm ràng buộc logic |

---

## Clean Code Guidelines
- Tên hàm/biến rõ ràng, theo quy tắc camelCase.
- Tuân thủ kiến trúc phân tầng, tránh trùng lặp code.
- Mỗi service tách rõ interface và implementation.
- Dùng `@Valid`, DTO và Mapper để tách biệt logic entity.
- Không trả Entity trực tiếp ra ngoài — luôn dùng DTO.
- Tách config JWT/Exception thành package riêng.
- Dùng enum thay cho chuỗi cứng (status, role...).
- Tuân thủ chuẩn REST.

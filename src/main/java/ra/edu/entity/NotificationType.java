package ra.edu.entity;

public enum NotificationType {
    NEW_COURSE,           // Khi có khóa học mới được publish
    LESSON_UPDATED,       // Khi một bài học trong khóa học đang tham gia bị thay đổi
    ENROLLMENT_CONFIRMED, // Khi sinh viên đăng ký khóa học thành công
    SYSTEM_ALERT
}

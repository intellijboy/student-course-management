package cn.intellijboy.demo.web.studentcourse;

public record CourseGradeDto(Long id, Long studentId, Long courseId, Integer score) {
}

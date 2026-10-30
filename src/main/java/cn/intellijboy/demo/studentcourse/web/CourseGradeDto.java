package cn.intellijboy.demo.studentcourse.web;

public record CourseGradeDto(Long id, Long studentId, Long courseId, Integer score) {
}

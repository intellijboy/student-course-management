package cn.intellijboy.demo.studentcourse.web.grade;

public record GradeRequest(Long studentId, Long courseId, Integer score) {
}

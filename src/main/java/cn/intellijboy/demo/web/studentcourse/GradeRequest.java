package cn.intellijboy.demo.web.studentcourse;

public record GradeRequest(Long studentId, Long courseId, Integer score) {
}

package cn.intellijboy.demo.studentcourse.web;

public record GradeRequest(Long studentId, Long courseId, Integer score) {
}

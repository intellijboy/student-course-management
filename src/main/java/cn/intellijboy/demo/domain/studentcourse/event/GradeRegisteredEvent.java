package cn.intellijboy.demo.domain.studentcourse.event;

/**
 * 成绩登记领域事件。
 */
public record GradeRegisteredEvent(Long studentId, Long courseId, int score) {
}

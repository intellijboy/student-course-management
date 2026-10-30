package cn.intellijboy.demo.studentcourse.domain.event;

/**
 * 成绩修改领域事件。
 */
public record GradeUpdatedEvent(Long id, Long studentId, Long courseId, int score) {
}

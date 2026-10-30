package cn.intellijboy.demo.domain.studentcourse.event;

/**
 * 成绩修改领域事件。
 */
public record GradeUpdatedEvent(Long id, Long studentId, Long courseId, int score) {
}

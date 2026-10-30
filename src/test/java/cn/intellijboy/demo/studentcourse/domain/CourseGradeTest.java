package cn.intellijboy.demo.studentcourse.domain;

import cn.intellijboy.demo.studentcourse.domain.exception.InvalidScoreException;
import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("课程成绩领域测试")
class CourseGradeTest {

    @Test
    @DisplayName("使用合法分数创建成绩")
    void shouldCreateGradeWithValidScore() {
        CourseGrade grade = new CourseGrade(1L, 10L, 20L, 85);
        assertNotNull(grade);
        assertEquals(85, grade.getScore().value());
    }

    @Test
    @DisplayName("构造成绩时拒绝负数分数")
    void shouldThrowWhenConstructingWithNegativeScore() {
        assertThrows(InvalidScoreException.class, () -> new CourseGrade(1L, 10L, 20L, -1));
    }

    @Test
    @DisplayName("构造成绩时拒绝超过 100 的分数")
    void shouldThrowWhenConstructingWithScoreAboveHundred() {
        assertThrows(InvalidScoreException.class, () -> new CourseGrade(1L, 10L, 20L, 101));
    }

    @Test
    @DisplayName("使用合法值更新成绩分数")
    void shouldUpdateScoreWithValidValue() {
        CourseGrade grade = new CourseGrade(1L, 10L, 20L, 85);
        grade.updateScore(90);
        assertEquals(90, grade.getScore().value());
    }

    @Test
    @DisplayName("更新分数时拒绝越界值")
    void shouldThrowWhenUpdatingScoreToOutOfRangeValue() {
        CourseGrade grade = new CourseGrade(1L, 10L, 20L, 85);
        assertThrows(InvalidScoreException.class, () -> grade.updateScore(-1));
    }
}

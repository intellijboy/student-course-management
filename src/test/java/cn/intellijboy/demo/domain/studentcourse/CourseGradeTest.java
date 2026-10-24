package cn.intellijboy.demo.domain.studentcourse;

import cn.intellijboy.demo.domain.studentcourse.exception.InvalidScoreException;
import cn.intellijboy.demo.domain.studentcourse.model.CourseGrade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseGradeTest {

    @Test
    void shouldCreateGradeWithValidScore() {
        CourseGrade grade = new CourseGrade(1L, 10L, 20L, 85);
        assertNotNull(grade);
        assertEquals(85, grade.getScore());
    }

    @Test
    void shouldThrowWhenConstructingWithNegativeScore() {
        assertThrows(InvalidScoreException.class, () -> new CourseGrade(1L, 10L, 20L, -1));
    }

    @Test
    void shouldThrowWhenConstructingWithScoreAboveHundred() {
        assertThrows(InvalidScoreException.class, () -> new CourseGrade(1L, 10L, 20L, 101));
    }

    @Test
    void shouldUpdateScoreWithValidValue() {
        CourseGrade grade = new CourseGrade(1L, 10L, 20L, 85);
        grade.updateScore(90);
        assertEquals(90, grade.getScore());
    }

    @Test
    void shouldThrowWhenUpdatingScoreToOutOfRangeValue() {
        CourseGrade grade = new CourseGrade(1L, 10L, 20L, 85);
        assertThrows(InvalidScoreException.class, () -> grade.updateScore(-1));
    }
}

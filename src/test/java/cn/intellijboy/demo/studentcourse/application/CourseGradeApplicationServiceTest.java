package cn.intellijboy.demo.studentcourse.application;

import cn.intellijboy.demo.studentcourse.domain.exception.DuplicateGradeException;
import cn.intellijboy.demo.studentcourse.domain.exception.InvalidScoreException;
import cn.intellijboy.demo.studentcourse.domain.model.Course;
import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;
import cn.intellijboy.demo.studentcourse.domain.model.Student;
import cn.intellijboy.demo.studentcourse.domain.repository.CourseRepository;
import cn.intellijboy.demo.studentcourse.domain.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("成绩应用服务测试")
class CourseGradeApplicationServiceTest {

    @Autowired
    private CourseGradeApplicationService applicationService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = studentRepository.save(new Student(null, "成绩服务测试学生", "grade-app-student@example.com"));
        course = courseRepository.save(new Course(null, "成绩服务测试课程", 3));
    }

    @Test
    @DisplayName("登记合法成绩后能按学生与课程查询到该成绩")
    void shouldRegisterValidGradeAndBeVisibleInQueries() {
        CourseGrade grade = applicationService.register(student.getId(), course.getId(), 90);

        assertNotNull(grade.getId());
        assertEquals(90, grade.getScore().value());

        List<CourseGrade> byStudent = applicationService.findByStudent(student.getId());
        assertTrue(byStudent.stream().anyMatch(g -> g.getId().equals(grade.getId())));

        List<CourseGrade> byCourse = applicationService.findByCourse(course.getId());
        assertTrue(byCourse.stream().anyMatch(g -> g.getId().equals(grade.getId())));
    }

    @Test
    @DisplayName("同一学生对同一课程重复登记成绩时被拒绝")
    void shouldRejectDuplicateRegistrationForSameStudentAndCourse() {
        applicationService.register(student.getId(), course.getId(), 85);

        assertThrows(DuplicateGradeException.class,
            () -> applicationService.register(student.getId(), course.getId(), 92));
    }

    @Test
    @DisplayName("登记成绩时拒绝越界分数")
    void shouldRejectOutOfRangeScoreOnRegister() {
        assertThrows(InvalidScoreException.class,
            () -> applicationService.register(student.getId(), course.getId(), 101));
    }

    @Test
    @DisplayName("更新成绩分数后查询结果同步变更")
    void shouldUpdateGradeScore() {
        CourseGrade grade = applicationService.register(student.getId(), course.getId(), 60);

        CourseGrade updated = applicationService.update(grade.getId(), 95);

        assertEquals(95, updated.getScore().value());
    }

    @Test
    @DisplayName("更新成绩时拒绝越界分数")
    void shouldRejectOutOfRangeScoreOnUpdate() {
        CourseGrade grade = applicationService.register(student.getId(), course.getId(), 70);

        assertThrows(InvalidScoreException.class,
            () -> applicationService.update(grade.getId(), 150));
    }

    @Test
    @DisplayName("更新不存在的成绩时抛出 IllegalArgumentException")
    void shouldThrowWhenUpdatingNonExistentGrade() {
        assertThrows(IllegalArgumentException.class,
            () -> applicationService.update(999999L, 80));
    }

    @Test
    @DisplayName("删除成绩后按学生查询结果为空")
    void shouldDeleteGradeAndHideFromQueries() {
        CourseGrade grade = applicationService.register(student.getId(), course.getId(), 88);

        applicationService.delete(grade.getId());

        assertTrue(applicationService.findByStudent(student.getId()).isEmpty());
    }
}

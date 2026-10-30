package cn.intellijboy.demo.studentcourse.infrastructure;

import cn.intellijboy.demo.studentcourse.domain.model.Course;
import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;
import cn.intellijboy.demo.studentcourse.domain.model.Student;
import cn.intellijboy.demo.studentcourse.domain.repository.CourseGradeRepository;
import cn.intellijboy.demo.studentcourse.domain.repository.CourseRepository;
import cn.intellijboy.demo.studentcourse.domain.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("学生课程仓储集成测试")
class RepositoryIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseGradeRepository courseGradeRepository;

    @Test
    @DisplayName("保存学生后可按 id 查询到该学生")
    void shouldSaveAndFindStudent() {
        Student saved = studentRepository.save(new Student(null, "张三", "zhangsan@example.com"));

        assertNotNull(saved.getId());
        Optional<Student> found = studentRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("张三", found.get().getName());
        assertEquals("zhangsan@example.com", found.get().getEmail().value());
    }

    @Test
    @DisplayName("保存课程后可按 id 查询到该课程")
    void shouldSaveAndFindCourse() {
        Course saved = courseRepository.save(new Course(null, "数学", 4));

        assertNotNull(saved.getId());
        Optional<Course> found = courseRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("数学", found.get().getName());
        assertEquals(4, found.get().getCredits());
    }

    @Test
    @DisplayName("保存成绩后可按学生 id 与课程 id 查询到该成绩")
    void shouldSaveGradeAndFindByStudentIdAndCourseId() {
        CourseGrade saved = courseGradeRepository.save(new CourseGrade(null, 100L, 200L, 88));

        assertNotNull(saved.getId());
        Optional<CourseGrade> found = courseGradeRepository.findByStudentIdAndCourseId(100L, 200L);

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(88, found.get().getScore().value());
    }

    @Test
    @DisplayName("同一学生同一课程重复保存成绩被数据库拒绝")
    void shouldRejectDuplicateGradeForSameStudentAndCourse() {
        courseGradeRepository.save(new CourseGrade(null, 300L, 400L, 75));

        assertThrows(DataIntegrityViolationException.class,
            () -> courseGradeRepository.save(new CourseGrade(null, 300L, 400L, 90)));
    }

    @Test
    @DisplayName("按 id 删除学生后查询不到该学生")
    void shouldDeleteStudentById() {
        Student saved = studentRepository.save(new Student(null, "李四", "lisi@example.com"));

        studentRepository.deleteById(saved.getId());

        assertTrue(studentRepository.findById(saved.getId()).isEmpty());
    }
}

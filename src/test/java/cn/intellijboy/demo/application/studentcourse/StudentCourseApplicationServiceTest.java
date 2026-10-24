package cn.intellijboy.demo.application.studentcourse;

import cn.intellijboy.demo.domain.studentcourse.exception.NotFoundException;
import cn.intellijboy.demo.domain.studentcourse.model.Course;
import cn.intellijboy.demo.domain.studentcourse.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StudentCourseApplicationServiceTest {

    @Autowired
    private StudentApplicationService studentService;

    @Autowired
    private CourseApplicationService courseService;

    @Test
    void shouldCreateStudentAndFindInListAndById() {
        String email = "app-" + UUID.randomUUID() + "@example.com";
        Student created = studentService.create("王小王", email);

        assertNotNull(created.getId());
        Optional<Student> byId = studentService.findById(created.getId());
        assertTrue(byId.isPresent());
        assertEquals("王小王", byId.get().getName());
        assertEquals(email, byId.get().getEmail());

        List<Student> all = studentService.findAll();
        assertTrue(all.stream().anyMatch(s -> s.getId().equals(created.getId())));

        studentService.delete(created.getId());
    }

    @Test
    void shouldUpdateStudent() {
        String email = "app-update-" + UUID.randomUUID() + "@example.com";
        Student created = studentService.create("更新前", email);

        Student updated = studentService.update(created.getId(), "更新后", "updated@example.com");

        assertEquals("更新后", updated.getName());
        Optional<Student> found = studentService.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("更新后", found.get().getName());
        assertEquals("updated@example.com", found.get().getEmail());

        studentService.delete(created.getId());
    }

    @Test
    void shouldDeleteStudent() {
        String email = "app-delete-" + UUID.randomUUID() + "@example.com";
        Student created = studentService.create("待删除", email);

        studentService.delete(created.getId());

        assertTrue(studentService.findById(created.getId()).isEmpty());
    }

    @Test
    void shouldCreateUpdateAndDeleteCourse() {
        String name = "应用层课程-" + UUID.randomUUID();
        Course created = courseService.create(name, 3);
        assertNotNull(created.getId());

        Course updated = courseService.update(created.getId(), name + "-改", 5);
        assertEquals(name + "-改", updated.getName());
        assertEquals(5, updated.getCredits());

        Optional<Course> found = courseService.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals(name + "-改", found.get().getName());
        assertEquals(5, found.get().getCredits());

        assertTrue(courseService.findAll().stream().anyMatch(c -> c.getId().equals(created.getId())));

        courseService.delete(created.getId());
        assertTrue(courseService.findById(created.getId()).isEmpty());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentStudent() {
        long missingId = 100_000_000L;

        NotFoundException ex = assertThrows(NotFoundException.class,
            () -> studentService.update(missingId, "不存在", "nobody@example.com"));

        assertTrue(ex.getMessage().contains("学生不存在"));
    }

    @Test
    void shouldThrowWhenDeletingNonExistentCourse() {
        long missingId = 100_000_000L;

        NotFoundException ex = assertThrows(NotFoundException.class,
            () -> courseService.delete(missingId));

        assertTrue(ex.getMessage().contains("课程不存在"));
    }
}

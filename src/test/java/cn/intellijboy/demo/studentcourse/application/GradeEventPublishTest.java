package cn.intellijboy.demo.studentcourse.application;

import cn.intellijboy.demo.studentcourse.domain.event.GradeRegisteredEvent;
import cn.intellijboy.demo.studentcourse.domain.event.GradeUpdatedEvent;
import cn.intellijboy.demo.studentcourse.domain.model.Course;
import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;
import cn.intellijboy.demo.studentcourse.domain.model.Student;
import cn.intellijboy.demo.studentcourse.domain.repository.CourseRepository;
import cn.intellijboy.demo.studentcourse.domain.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 成绩领域事件发布验证（仅作示范，不引入跨模块消费）。
 */
@SpringBootTest
class GradeEventPublishTest {

    @Autowired
    private CourseGradeApplicationService applicationService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EventRecorder eventRecorder;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        eventRecorder.reset();
        student = studentRepository.save(new Student(null, "事件测试学生", "event-student@example.com"));
        course = courseRepository.save(new Course(null, "事件测试课程", 3));
    }

    @Test
    void registerPublishesGradeRegisteredEvent() {
        CourseGrade grade = applicationService.register(student.getId(), course.getId(), 88);
        assertNotNull(grade.getId());

        List<Object> events = eventRecorder.events();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof GradeRegisteredEvent);
        GradeRegisteredEvent event = (GradeRegisteredEvent) events.get(0);
        assertEquals(student.getId(), event.studentId());
        assertEquals(course.getId(), event.courseId());
        assertEquals(88, event.score());
    }

    @Test
    void updatePublishesGradeUpdatedEvent() {
        CourseGrade grade = applicationService.register(student.getId(), course.getId(), 60);
        eventRecorder.reset();

        applicationService.update(grade.getId(), 95);
        assertEquals(1, eventRecorder.events().size());
        assertTrue(eventRecorder.events().get(0) instanceof GradeUpdatedEvent);
        GradeUpdatedEvent event = (GradeUpdatedEvent) eventRecorder.events().get(0);
        assertEquals(95, event.score());
        assertEquals(student.getId(), event.studentId());
    }

    @TestConfiguration
    static class EventConfig {
        @Bean
        EventRecorder eventRecorder() {
            return new EventRecorder();
        }
    }

    static class EventRecorder {
        private final List<Object> events = new ArrayList<>();

        @EventListener
        void onGradeRegistered(GradeRegisteredEvent event) {
            events.add(event);
        }

        @EventListener
        void onGradeUpdated(GradeUpdatedEvent event) {
            events.add(event);
        }

        void reset() {
            events.clear();
        }

        List<Object> events() {
            return events;
        }
    }
}

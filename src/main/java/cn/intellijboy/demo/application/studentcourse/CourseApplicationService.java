package cn.intellijboy.demo.application.studentcourse;

import cn.intellijboy.demo.domain.studentcourse.exception.NotFoundException;
import cn.intellijboy.demo.domain.studentcourse.model.Course;
import cn.intellijboy.demo.domain.studentcourse.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseApplicationService {

    private final CourseRepository courseRepository;

    public CourseApplicationService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course create(String name, Integer credits) {
        return courseRepository.save(new Course(null, name, credits));
    }

    @Transactional
    public Course update(Long id, String name, Integer credits) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("课程不存在: id=" + id));
        course.setName(name);
        course.setCredits(credits);
        return courseRepository.save(course);
    }

    @Transactional
    public void delete(Long id) {
        if (courseRepository.findById(id).isEmpty()) {
            throw new NotFoundException("课程不存在: id=" + id);
        }
        courseRepository.deleteById(id);
    }
}

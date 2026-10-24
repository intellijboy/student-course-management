package cn.intellijboy.demo.domain.studentcourse.repository;

import cn.intellijboy.demo.domain.studentcourse.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    List<Course> findAll();

    Optional<Course> findById(Long id);

    Course save(Course course);

    void deleteById(Long id);
}

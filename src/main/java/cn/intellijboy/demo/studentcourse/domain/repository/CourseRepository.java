package cn.intellijboy.demo.studentcourse.domain.repository;

import cn.intellijboy.demo.studentcourse.domain.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    List<Course> findAll();

    Optional<Course> findById(Long id);

    Course save(Course course);

    void deleteById(Long id);
}

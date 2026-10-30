package cn.intellijboy.demo.studentcourse.domain.repository;

import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;

import java.util.List;
import java.util.Optional;

public interface CourseGradeRepository {

    List<CourseGrade> findAll();

    Optional<CourseGrade> findById(Long id);

    CourseGrade save(CourseGrade courseGrade);

    void deleteById(Long id);

    Optional<CourseGrade> findByStudentIdAndCourseId(Long studentId, Long courseId);
}

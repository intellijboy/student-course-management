package cn.intellijboy.demo.application.studentcourse;

import cn.intellijboy.demo.domain.studentcourse.exception.DuplicateGradeException;
import cn.intellijboy.demo.domain.studentcourse.model.CourseGrade;
import cn.intellijboy.demo.domain.studentcourse.repository.CourseGradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseGradeApplicationService {

    private final CourseGradeRepository courseGradeRepository;

    public CourseGradeApplicationService(CourseGradeRepository courseGradeRepository) {
        this.courseGradeRepository = courseGradeRepository;
    }

    @Transactional
    public CourseGrade register(Long studentId, Long courseId, int score) {
        if (courseGradeRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new DuplicateGradeException("该学生已登记过此课程的成绩，不可重复登记");
        }
        return courseGradeRepository.save(new CourseGrade(null, studentId, courseId, score));
    }

    @Transactional
    public CourseGrade update(Long id, int score) {
        CourseGrade grade = courseGradeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("成绩不存在，id=" + id));
        grade.updateScore(score);
        return courseGradeRepository.save(grade);
    }

    @Transactional
    public void delete(Long id) {
        courseGradeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CourseGrade> findByStudent(Long studentId) {
        return courseGradeRepository.findAll().stream()
            .filter(grade -> studentId.equals(grade.getStudentId()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseGrade> findByCourse(Long courseId) {
        return courseGradeRepository.findAll().stream()
            .filter(grade -> courseId.equals(grade.getCourseId()))
            .toList();
    }
}

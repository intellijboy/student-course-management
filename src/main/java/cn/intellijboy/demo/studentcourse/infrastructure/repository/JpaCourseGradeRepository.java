package cn.intellijboy.demo.studentcourse.infrastructure.repository;

import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;
import cn.intellijboy.demo.studentcourse.domain.repository.CourseGradeRepository;
import cn.intellijboy.demo.studentcourse.infrastructure.mapper.CourseGradeMapper;
import cn.intellijboy.demo.studentcourse.infrastructure.persistence.SpringDataCourseGradeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCourseGradeRepository implements CourseGradeRepository {

    private final SpringDataCourseGradeRepository springDataRepository;
    private final CourseGradeMapper mapper;

    public JpaCourseGradeRepository(SpringDataCourseGradeRepository springDataRepository, CourseGradeMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CourseGrade> findAll() {
        return springDataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<CourseGrade> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public CourseGrade save(CourseGrade courseGrade) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(courseGrade)));
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public Optional<CourseGrade> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return springDataRepository.findByStudentIdAndCourseId(studentId, courseId).map(mapper::toDomain);
    }
}

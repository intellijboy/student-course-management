package cn.intellijboy.demo.infrastructure.studentcourse.repository;

import cn.intellijboy.demo.domain.studentcourse.model.CourseGrade;
import cn.intellijboy.demo.domain.studentcourse.repository.CourseGradeRepository;
import cn.intellijboy.demo.infrastructure.studentcourse.mapper.CourseGradeMapper;
import cn.intellijboy.demo.infrastructure.studentcourse.persistence.SpringDataCourseGradeRepository;
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

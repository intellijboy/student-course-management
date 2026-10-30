package cn.intellijboy.demo.studentcourse.infrastructure.repository;

import cn.intellijboy.demo.studentcourse.domain.model.Course;
import cn.intellijboy.demo.studentcourse.domain.repository.CourseRepository;
import cn.intellijboy.demo.studentcourse.infrastructure.mapper.CourseMapper;
import cn.intellijboy.demo.studentcourse.infrastructure.persistence.SpringDataCourseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCourseRepository implements CourseRepository {

    private final SpringDataCourseRepository springDataRepository;
    private final CourseMapper mapper;

    public JpaCourseRepository(SpringDataCourseRepository springDataRepository, CourseMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Course> findAll() {
        return springDataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Course> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Course save(Course course) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(course)));
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
}

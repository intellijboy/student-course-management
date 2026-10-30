package cn.intellijboy.demo.studentcourse.infrastructure.repository;

import cn.intellijboy.demo.studentcourse.domain.model.Student;
import cn.intellijboy.demo.studentcourse.domain.repository.StudentRepository;
import cn.intellijboy.demo.studentcourse.infrastructure.mapper.StudentMapper;
import cn.intellijboy.demo.studentcourse.infrastructure.persistence.SpringDataStudentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaStudentRepository implements StudentRepository {

    private final SpringDataStudentRepository springDataRepository;
    private final StudentMapper mapper;

    public JpaStudentRepository(SpringDataStudentRepository springDataRepository, StudentMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Student> findAll() {
        return springDataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Student> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Student save(Student student) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(student)));
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
}

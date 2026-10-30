package cn.intellijboy.demo.studentcourse.domain.repository;

import cn.intellijboy.demo.studentcourse.domain.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {

    List<Student> findAll();

    Optional<Student> findById(Long id);

    Student save(Student student);

    void deleteById(Long id);
}

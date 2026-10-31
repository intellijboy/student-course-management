/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.studentcourse.repository;

import cn.intellijboy.domain.studentcourse.model.Student;
import cn.intellijboy.domain.studentcourse.repository.StudentRepository;
import cn.intellijboy.infrastructure.studentcourse.mapper.StudentMapper;
import cn.intellijboy.infrastructure.studentcourse.persistence.SpringDataStudentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生仓库的 JPA 实现：通过 Spring Data 的 {@link SpringDataStudentRepository} 查询并保存学生，并由 {@link StudentMapper} 完成领域模型与实体互转。
 */
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

/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.application.studentcourse;

import cn.intellijboy.domain.studentcourse.exception.StudentNotFoundException;
import cn.intellijboy.domain.studentcourse.model.Student;
import cn.intellijboy.domain.studentcourse.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 学生管理应用服务：对外暴露学生查询、创建、更新与删除用例，直接基于仓储操作 Student 聚合。
 */
@Service
public class StudentApplicationService {

    private final StudentRepository studentRepository;

    public StudentApplicationService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional
    public Student create(String name, String email) {
        return studentRepository.save(new Student(null, name, email));
    }

    @Transactional
    public Student update(Long id, String name, String email) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException("学生不存在: id=" + id));
        student.updateName(name);
        student.updateEmail(email);
        return studentRepository.save(student);
    }

    @Transactional
    public void delete(Long id) {
        if (studentRepository.findById(id).isEmpty()) {
            throw new StudentNotFoundException("学生不存在: id=" + id);
        }
        studentRepository.deleteById(id);
    }
}

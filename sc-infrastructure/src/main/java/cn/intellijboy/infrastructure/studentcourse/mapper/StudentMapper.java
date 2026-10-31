/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.studentcourse.mapper;

import cn.intellijboy.domain.studentcourse.model.Student;
import cn.intellijboy.infrastructure.studentcourse.persistence.StudentJpaEntity;
import org.springframework.stereotype.Component;

/**
 * 学生映射器：负责领域模型 {@link Student} 与 JPA 实体 {@link StudentJpaEntity} 之间的双向转换。
 */
@Component
public class StudentMapper {

    public StudentJpaEntity toJpa(Student student) {
        if (student == null) {
            return null;
        }
        StudentJpaEntity entity = new StudentJpaEntity();
        entity.setId(student.getId());
        entity.setName(student.getName());
        entity.setEmail(student.getEmail().value());
        return entity;
    }

    public Student toDomain(StudentJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Student(entity.getId(), entity.getName(), entity.getEmail());
    }
}

/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.studentcourse.mapper;

import cn.intellijboy.domain.studentcourse.model.Course;
import cn.intellijboy.infrastructure.studentcourse.persistence.CourseJpaEntity;
import org.springframework.stereotype.Component;

/**
 * 课程映射器：负责领域模型 {@link Course} 与 JPA 实体 {@link CourseJpaEntity} 之间的双向转换。
 */
@Component
public class CourseMapper {

    public CourseJpaEntity toJpa(Course course) {
        if (course == null) {
            return null;
        }
        CourseJpaEntity entity = new CourseJpaEntity();
        entity.setId(course.getId());
        entity.setName(course.getName());
        entity.setCredits(course.getCredits());
        return entity;
    }

    public Course toDomain(CourseJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Course(entity.getId(), entity.getName(), entity.getCredits());
    }
}

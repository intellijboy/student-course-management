/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.application.studentcourse;

import cn.intellijboy.domain.studentcourse.exception.NotFoundException;
import cn.intellijboy.domain.studentcourse.model.Course;
import cn.intellijboy.domain.studentcourse.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 课程管理应用服务：对外暴露课程查询、创建、更新与删除用例，直接基于仓储操作 Course 聚合。
 */
@Service
public class CourseApplicationService {

    private final CourseRepository courseRepository;

    public CourseApplicationService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course create(String name, Integer credits) {
        return courseRepository.save(new Course(null, name, credits));
    }

    @Transactional
    public Course update(Long id, String name, Integer credits) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("课程不存在: id=" + id));
        course.updateName(name);
        course.updateCredits(credits);
        return courseRepository.save(course);
    }

    @Transactional
    public void delete(Long id) {
        if (courseRepository.findById(id).isEmpty()) {
            throw new NotFoundException("课程不存在: id=" + id);
        }
        courseRepository.deleteById(id);
    }
}

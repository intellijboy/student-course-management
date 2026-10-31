/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.studentcourse.grade;

import cn.intellijboy.application.studentcourse.CourseGradeApplicationService;
import cn.intellijboy.domain.studentcourse.model.CourseGrade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 成绩查询 REST 接口：GET /api/students/{id}/grades 按学生查成绩，GET /api/courses/{id}/grades 按课程查成绩。
 */
@RestController
public class GradeQueryController {

    private final CourseGradeApplicationService courseGradeApplicationService;

    public GradeQueryController(CourseGradeApplicationService courseGradeApplicationService) {
        this.courseGradeApplicationService = courseGradeApplicationService;
    }

    /**
     * 按学生查询其全部课程成绩。
     *
     * @param id 学生 id
     * @return 200 与该学生的成绩列表
     */
    @GetMapping("/api/students/{id}/grades")
    public List<CourseGradeView> findByStudent(@PathVariable Long id) {
        return courseGradeApplicationService.findByStudent(id).stream()
                .map(this::toView)
                .toList();
    }

    /**
     * 按课程查询全部选课学生的成绩。
     *
     * @param id 课程 id
     * @return 200 与该课程的成绩列表
     */
    @GetMapping("/api/courses/{id}/grades")
    public List<CourseGradeView> findByCourse(@PathVariable Long id) {
        return courseGradeApplicationService.findByCourse(id).stream()
                .map(this::toView)
                .toList();
    }

    private CourseGradeView toView(CourseGrade grade) {
        return new CourseGradeView(grade.getId(), grade.getStudentId(), grade.getCourseId(), grade.getScore().value());
    }
}

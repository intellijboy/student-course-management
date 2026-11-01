/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.handler;

import cn.intellijboy.domain.studentcourse.exception.CourseNotFoundException;
import cn.intellijboy.domain.studentcourse.exception.DuplicateGradeException;
import cn.intellijboy.domain.studentcourse.exception.GradeNotFoundException;
import cn.intellijboy.domain.studentcourse.exception.InvalidScoreException;
import cn.intellijboy.domain.studentcourse.exception.StudentNotFoundException;
import cn.intellijboy.interfaces.studentcourse.course.CourseController;
import cn.intellijboy.interfaces.studentcourse.grade.CourseGradeController;
import cn.intellijboy.interfaces.studentcourse.grade.GradeQueryController;
import cn.intellijboy.interfaces.studentcourse.student.StudentController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 学生-课程模块异常处理：仅处理本模块领域异常，作用域限定到本模块 Controller，
 * 优先级高于全局兜底处理器（@Order(1)）。通用参数/兜底异常由 GlobalExceptionHandler 统一处理。
 */
@Order(1)
@RestControllerAdvice(assignableTypes = {CourseController.class, StudentController.class,
    CourseGradeController.class, GradeQueryController.class})
public class StudentCourseExceptionHandler {

    /**
     * 学生/课程/成绩等资源不存在时返回 404。
     */
    @ExceptionHandler({StudentNotFoundException.class, CourseNotFoundException.class,
        GradeNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * 成绩非法（分数越界或重复录入）等业务校验失败时返回 400。
     */
    @ExceptionHandler({InvalidScoreException.class, DuplicateGradeException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", ex.getMessage()));
    }
}

/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.studentcourse;

import cn.intellijboy.domain.studentcourse.exception.DuplicateGradeException;
import cn.intellijboy.domain.studentcourse.exception.InvalidScoreException;
import cn.intellijboy.domain.studentcourse.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 学生-课程模块全局异常处理：将领域与参数异常统一映射为 HTTP 状态码（资源不存在 404、业务/参数校验失败 400）与错误消息。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 学生/课程等资源不存在时返回 404。
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
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

    /**
     * 参数缺失、类型不匹配等非法参数时返回 400。
     */
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> handleIllegalArgument(Exception ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", ex.getMessage()));
    }
}

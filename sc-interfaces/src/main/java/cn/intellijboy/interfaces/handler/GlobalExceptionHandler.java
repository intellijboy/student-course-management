/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

/**
 * 全局异常处理兜底：统一处理跨模块的通用参数异常、Spring MVC 标准异常与未预期异常。
 *
 * <p>职责边界：
 * <ul>
 *   <li>继承 {@link ResponseEntityExceptionHandler}，保证 Spring MVC 标准异常保持正确语义
 *       （路径参数类型不匹配/请求体解析失败 → 400，资源不存在 → 404，方法不支持 → 405 等），
 *       并通过 {@link #handleExceptionInternal} 统一错误体结构为 {@code {message}}；</li>
 *   <li>领域非法参数（{@link IllegalArgumentException}）→ 400；</li>
 *   <li>未预期异常（兜底）→ 500 并记录日志，避免向客户端泄露内部细节。</li>
 * </ul>
 * 各模块的领域异常由各自模块级 {@link org.springframework.web.bind.annotation.RestControllerAdvice}
 * 处理（{@code @Order(1)}，优先级更高），本处理器为最低优先级兜底，不重复声明领域异常类型。
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 统一 Spring MVC 标准异常的错误体结构为 {@code {message}}，保持前端接收结构一致。
     * 对 {@link ProblemDetail} 提取其 detail，避免将冗长的 ProblemDetail 字符串暴露给客户端。
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {
        String message;
        if (body instanceof ProblemDetail pd) {
            message = (pd.getDetail() != null && !pd.getDetail().isBlank())
                    ? pd.getDetail() : pd.getTitle();
        } else {
            message = (body != null) ? body.toString() : ex.getMessage();
        }
        return super.handleExceptionInternal(ex, Map.of("message", message), headers, statusCode, request);
    }

    /**
     * 领域内部非法参数（如值对象 null 校验）等客户端错误时返回 400。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * 未预期异常兜底：返回 500 与统一错误结构，不向客户端泄露内部细节。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        log.error("未预期异常", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "服务器内部错误"));
    }
}

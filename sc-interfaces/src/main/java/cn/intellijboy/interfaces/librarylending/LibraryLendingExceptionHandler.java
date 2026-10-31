/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending;

import cn.intellijboy.domain.librarylending.exception.BookCopyUnavailableException;
import cn.intellijboy.domain.librarylending.exception.BookNotFoundException;
import cn.intellijboy.domain.librarylending.exception.DuplicateLoanException;
import cn.intellijboy.domain.librarylending.exception.DuplicateReservationException;
import cn.intellijboy.domain.librarylending.exception.LoanNotFoundException;
import cn.intellijboy.domain.librarylending.exception.ReaderNotFoundException;
import cn.intellijboy.interfaces.librarylending.book.BookController;
import cn.intellijboy.interfaces.librarylending.loan.LoanController;
import cn.intellijboy.interfaces.librarylending.reservation.ReservationController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 图书借阅模块异常处理：领域异常映射为 HTTP 状态与错误消息。
 */
@RestControllerAdvice(assignableTypes = {BookController.class, LoanController.class, ReservationController.class})
public class LibraryLendingExceptionHandler {

    @ExceptionHandler({ReaderNotFoundException.class, BookNotFoundException.class, LoanNotFoundException.class})
    public ResponseEntity<Map<String, String>> notFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({BookCopyUnavailableException.class, DuplicateLoanException.class,
        DuplicateReservationException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> badRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("message", e.getMessage()));
    }
}

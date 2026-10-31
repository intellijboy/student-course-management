/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending.loan;

/**
 * 借阅请求。
 */
public record LoanRequest(Long readerId, Long bookId) {
}

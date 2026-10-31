/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending.loan;

import cn.intellijboy.domain.librarylending.model.Loan;

import java.time.LocalDate;

/**
 * 借阅记录视图：承载借阅标识、读者/图书/副本、借出/应还/归还日期。
 */
public record LoanView(Long id, Long readerId, Long bookId, Long bookCopyId,
                       LocalDate borrowedAt, LocalDate dueDate, LocalDate returnedAt) {
    public static LoanView of(Loan loan) {
        return new LoanView(loan.getId(), loan.getReaderId().value(), loan.getBookId().value(),
            loan.getBookCopyId().value(), loan.getBorrowedAt(), loan.getDueDate(), loan.getReturnedAt());
    }
}

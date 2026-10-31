/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.web.loan;

import cn.intellijboy.demo.librarylending.domain.model.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 逾期借阅视图：承载借阅标识、读者/副本、应还日期及按策略计算的逾期费用。
 */
public record OverdueView(Long loanId, Long readerId, Long bookCopyId, LocalDate dueDate,
                          BigDecimal fee) {
    public static OverdueView of(Loan loan, BigDecimal fee) {
        return new OverdueView(loan.getId(), loan.getReaderId().value(), loan.getBookCopyId().value(),
            loan.getDueDate(), fee);
    }
}

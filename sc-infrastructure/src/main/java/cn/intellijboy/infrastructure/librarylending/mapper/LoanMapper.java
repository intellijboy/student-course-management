/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.librarylending.mapper;

import cn.intellijboy.domain.librarylending.model.BookCopyId;
import cn.intellijboy.domain.librarylending.model.BookId;
import cn.intellijboy.domain.librarylending.model.Loan;
import cn.intellijboy.domain.librarylending.model.LoanPeriod;
import cn.intellijboy.domain.librarylending.model.ReaderId;
import cn.intellijboy.infrastructure.librarylending.persistence.LoanJpaEntity;
import org.springframework.stereotype.Component;

/**
 * 借阅记录映射器：负责领域模型 {@link Loan} 与 JPA 实体 {@link LoanJpaEntity} 之间的双向转换。
 */
@Component
public class LoanMapper {

    public LoanJpaEntity toJpa(Loan loan) {
        LoanJpaEntity entity = new LoanJpaEntity();
        entity.setId(loan.getId());
        entity.setReaderId(loan.getReaderId().value());
        entity.setBookId(loan.getBookId().value());
        entity.setBookCopyId(loan.getBookCopyId().value());
        entity.setBorrowedAt(loan.getBorrowedAt());
        entity.setDueDate(loan.getDueDate());
        entity.setReturnedAt(loan.getReturnedAt());
        return entity;
    }

    public Loan toDomain(LoanJpaEntity entity) {
        LoanPeriod period = new LoanPeriod(
            (int) java.time.temporal.ChronoUnit.DAYS.between(entity.getBorrowedAt(), entity.getDueDate()));
        Loan loan = new Loan(entity.getId(), new ReaderId(entity.getReaderId()),
            new BookId(entity.getBookId()), new BookCopyId(entity.getBookCopyId()),
            entity.getBorrowedAt(), period);
        if (entity.getReturnedAt() != null) {
            loan.returnBook(entity.getReturnedAt());
        }
        return loan;
    }
}

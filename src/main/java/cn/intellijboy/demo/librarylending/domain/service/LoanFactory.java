package cn.intellijboy.demo.librarylending.domain.service;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.LoanPeriod;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

import java.time.LocalDate;

/**
 * 借阅工厂：创建借阅记录，集中处理创建校验（借期合法性）。
 */
public class LoanFactory {

    /** 创建借阅记录；重复借阅不变式由应用层基于在借记录校验 */
    public Loan createLoan(Long id, ReaderId readerId, BookId bookId, BookCopyId bookCopyId,
                           LocalDate borrowedAt, LoanPeriod period) {
        if (period == null || period.days() <= 0) {
            throw new IllegalArgumentException("借期必须为正");
        }
        return new Loan(id, readerId, bookId, bookCopyId, borrowedAt, period);
    }
}

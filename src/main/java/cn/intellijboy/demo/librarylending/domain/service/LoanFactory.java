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

    /**
     * 创建借阅记录，借期不合法时抛出异常；重复借阅不变式由应用层基于在借记录校验。
     *
     * @param id 借阅记录标识，可为 {@code null}（由基础设施层生成）
     * @param readerId 借阅读者标识，不能为 {@code null}
     * @param bookId 借阅图书标识，不能为 {@code null}
     * @param bookCopyId 借阅馆藏副本标识，不能为 {@code null}
     * @param borrowedAt 借出日期
     * @param period 借阅期限，必须为正天数
     * @return 创建完成的借阅记录
     * @throws IllegalArgumentException 当借期为 {@code null} 或天数小于等于 0 时抛出
     */
    public Loan createLoan(Long id, ReaderId readerId, BookId bookId, BookCopyId bookCopyId,
                           LocalDate borrowedAt, LoanPeriod period) {
        if (period == null || period.days() <= 0) {
            throw new IllegalArgumentException("借期必须为正");
        }
        return new Loan(id, readerId, bookId, bookCopyId, borrowedAt, period);
    }
}

package cn.intellijboy.demo.librarylending.domain.service;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopy;
import cn.intellijboy.demo.librarylending.domain.model.LoanPeriod;
import cn.intellijboy.demo.librarylending.domain.model.OverdueFee;

import java.math.BigDecimal;

/**
 * 借阅策略领域服务：可借判断、借期与逾期费率。
 */
public class BorrowingPolicy {

    /** 默认借期（14 天） */
    public static final LoanPeriod DEFAULT_LOAN_PERIOD = new LoanPeriod(14);

    /** 默认逾期费率（每天 0.5 元） */
    public static final OverdueFee DEFAULT_OVERDUE_FEE = new OverdueFee(BigDecimal.valueOf(0.5));

    /** 图书是否有可借副本 */
    public boolean hasAvailableCopy(Book book) {
        return book.firstAvailableCopy().isPresent();
    }

    /** 副本是否可借 */
    public boolean isBorrowable(BookCopy copy) {
        return copy.isAvailable();
    }

    /** 默认借期 */
    public LoanPeriod loanPeriod() {
        return DEFAULT_LOAN_PERIOD;
    }

    /** 默认逾期费率 */
    public OverdueFee overdueFee() {
        return DEFAULT_OVERDUE_FEE;
    }
}

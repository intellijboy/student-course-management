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

    /**
     * 判断图书是否存在可借副本。
     *
     * @param book 待检查的图书，不能为 {@code null}
     * @return 若该图书至少有一个可借副本则返回 {@code true}，否则返回 {@code false}
     */
    public boolean hasAvailableCopy(Book book) {
        return book.firstAvailableCopy().isPresent();
    }

    /**
     * 判断副本当前是否可借。
     *
     * @param copy 待检查的馆藏副本，不能为 {@code null}
     * @return 若副本处于可借状态则返回 {@code true}，否则返回 {@code false}
     */
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

package cn.intellijboy.demo.librarylending.domain.service;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopy;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 领域服务与工厂单元测试：借阅策略、逾期费率、图书与借阅创建校验。
 */
class BorrowingPolicyAndFactoryTest {

    private final BorrowingPolicy policy = new BorrowingPolicy();
    private final BookFactory bookFactory = new BookFactory();
    private final LoanFactory loanFactory = new LoanFactory();

    @Test
    void hasAvailableCopyTrueWhenAvailable() {
        Book book = bookFactory.createBook(new BookId(1L), "DDD 实战", "作者", 2);
        assertTrue(policy.hasAvailableCopy(book));
    }

    @Test
    void noAvailableCopyWhenAllBorrowed() {
        Book book = bookFactory.createBook(new BookId(1L), "DDD 实战", "作者", 1);
        book.firstAvailableCopy().orElseThrow().borrow();
        assertFalse(policy.hasAvailableCopy(book));
    }

    @Test
    void copyIsBorrowableOnlyWhenAvailable() {
        BookCopy copy = new BookCopy(new BookCopyId(1L));
        assertTrue(policy.isBorrowable(copy));
        copy.borrow();
        assertFalse(policy.isBorrowable(copy));
    }

    @Test
    void defaultOverdueFeeIsPositive() {
        assertTrue(policy.overdueFee().ratePerDay().signum() > 0);
    }

    @Test
    void bookFactoryRejectsNonPositiveCopyCount() {
        assertThrows(IllegalArgumentException.class,
            () -> bookFactory.createBook(new BookId(1L), "DDD 实战", "作者", 0));
    }

    @Test
    void bookFactoryCreatesExpectedCopies() {
        Book book = bookFactory.createBook(new BookId(1L), "DDD 实战", "作者", 3);
        assertEquals(3, book.totalCount());
        assertEquals(3, book.availableCount());
    }

    @Test
    void loanFactoryRejectsNonPositivePeriod() {
        assertThrows(IllegalArgumentException.class,
            () -> loanFactory.createLoan(1L, null, null, null, java.time.LocalDate.now(),
                new cn.intellijboy.demo.librarylending.domain.model.LoanPeriod(0)));
    }
}

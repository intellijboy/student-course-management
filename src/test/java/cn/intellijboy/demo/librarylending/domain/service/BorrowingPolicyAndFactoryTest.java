package cn.intellijboy.demo.librarylending.domain.service;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopy;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 领域服务与工厂单元测试：借阅策略、逾期费率、图书与借阅创建校验。
 */
@DisplayName("借阅策略与工厂测试")
class BorrowingPolicyAndFactoryTest {

    private final BorrowingPolicy policy = new BorrowingPolicy();
    private final BookFactory bookFactory = new BookFactory();
    private final LoanFactory loanFactory = new LoanFactory();

    @Test
    @DisplayName("存在可借副本时借阅策略判定为可借")
    void hasAvailableCopyTrueWhenAvailable() {
        Book book = bookFactory.createBook(new BookId(1L), "DDD 实战", "作者", 2);
        assertTrue(policy.hasAvailableCopy(book));
    }

    @Test
    @DisplayName("全部副本借出时判定无可借副本")
    void noAvailableCopyWhenAllBorrowed() {
        Book book = bookFactory.createBook(new BookId(1L), "DDD 实战", "作者", 1);
        book.firstAvailableCopy().orElseThrow().borrow();
        assertFalse(policy.hasAvailableCopy(book));
    }

    @Test
    @DisplayName("仅可借状态的副本可被借出")
    void copyIsBorrowableOnlyWhenAvailable() {
        BookCopy copy = new BookCopy(new BookCopyId(1L));
        assertTrue(policy.isBorrowable(copy));
        copy.borrow();
        assertFalse(policy.isBorrowable(copy));
    }

    @Test
    @DisplayName("默认逾期费率为正数")
    void defaultOverdueFeeIsPositive() {
        assertTrue(policy.overdueFee().ratePerDay().signum() > 0);
    }

    @Test
    @DisplayName("副本数量非正数时图书工厂创建被拒绝")
    void bookFactoryRejectsNonPositiveCopyCount() {
        assertThrows(IllegalArgumentException.class,
            () -> bookFactory.createBook(new BookId(1L), "DDD 实战", "作者", 0));
    }

    @Test
    @DisplayName("图书工厂按指定数量创建可借副本")
    void bookFactoryCreatesExpectedCopies() {
        Book book = bookFactory.createBook(new BookId(1L), "DDD 实战", "作者", 3);
        assertEquals(3, book.totalCount());
        assertEquals(3, book.availableCount());
    }

    @Test
    @DisplayName("借阅期限非正数时借阅工厂创建被拒绝")
    void loanFactoryRejectsNonPositivePeriod() {
        assertThrows(IllegalArgumentException.class,
            () -> loanFactory.createLoan(1L, null, null, null, java.time.LocalDate.now(),
                new cn.intellijboy.demo.librarylending.domain.model.LoanPeriod(0)));
    }
}

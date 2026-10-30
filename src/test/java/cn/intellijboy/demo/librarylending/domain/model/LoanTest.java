package cn.intellijboy.demo.librarylending.domain.model;

import cn.intellijboy.demo.librarylending.domain.exception.LoanNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Loan 聚合根领域单元测试：应还期限、逾期判定与归还行为。
 */
@DisplayName("借阅记录聚合测试")
class LoanTest {

    private final ReaderId reader = new ReaderId(1L);
    private final BookId book = new BookId(1L);
    private final BookCopyId copy = new BookCopyId(10L);

    private Loan loanAt(LocalDate borrowedAt, LoanPeriod period) {
        return new Loan(1L, reader, book, copy, borrowedAt, period);
    }

    @Test
    @DisplayName("应还日期为借出日期加借阅期限")
    void dueDateIsBorrowedAtPlusPeriod() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        assertEquals(LocalDate.of(2026, 10, 15), loan.getDueDate());
    }

    @Test
    @DisplayName("期限内借阅记录不逾期")
    void notOverdueWithinPeriod() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        assertFalse(loan.isOverdue(LocalDate.of(2026, 10, 15)));
        assertEquals(0, loan.overdueDays(LocalDate.of(2026, 10, 15)));
    }

    @Test
    @DisplayName("超过应还日期后判定逾期并计算逾期天数")
    void overdueAfterDueDate() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        assertTrue(loan.isOverdue(LocalDate.of(2026, 10, 20)));
        assertEquals(5, loan.overdueDays(LocalDate.of(2026, 10, 20)));
    }

    @Test
    @DisplayName("归还后借阅记录不再逾期")
    void returnClearsOverdueStatus() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        loan.returnBook(LocalDate.of(2026, 10, 20));
        assertTrue(loan.isReturned());
        assertFalse(loan.isOverdue(LocalDate.of(2026, 10, 20)));
        assertEquals(0, loan.overdueDays(LocalDate.of(2026, 10, 20)));
    }

    @Test
    @DisplayName("重复归还是同一条借阅记录被拒绝")
    void doubleReturnRejected() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        loan.returnBook(LocalDate.of(2026, 10, 8));
        assertThrows(LoanNotFoundException.class, () -> loan.returnBook(LocalDate.of(2026, 10, 9)));
    }
}

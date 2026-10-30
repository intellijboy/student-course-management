package cn.intellijboy.demo.librarylending.domain.model;

import cn.intellijboy.demo.librarylending.domain.exception.LoanNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Loan 聚合根领域单元测试：应还期限、逾期判定与归还行为。
 */
class LoanTest {

    private final ReaderId reader = new ReaderId(1L);
    private final BookId book = new BookId(1L);
    private final BookCopyId copy = new BookCopyId(10L);

    private Loan loanAt(LocalDate borrowedAt, LoanPeriod period) {
        return new Loan(1L, reader, book, copy, borrowedAt, period);
    }

    @Test
    void dueDateIsBorrowedAtPlusPeriod() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        assertEquals(LocalDate.of(2026, 10, 15), loan.getDueDate());
    }

    @Test
    void notOverdueWithinPeriod() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        assertFalse(loan.isOverdue(LocalDate.of(2026, 10, 15)));
        assertEquals(0, loan.overdueDays(LocalDate.of(2026, 10, 15)));
    }

    @Test
    void overdueAfterDueDate() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        assertTrue(loan.isOverdue(LocalDate.of(2026, 10, 20)));
        assertEquals(5, loan.overdueDays(LocalDate.of(2026, 10, 20)));
    }

    @Test
    void returnClearsOverdueStatus() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        loan.returnBook(LocalDate.of(2026, 10, 20));
        assertTrue(loan.isReturned());
        assertFalse(loan.isOverdue(LocalDate.of(2026, 10, 20)));
        assertEquals(0, loan.overdueDays(LocalDate.of(2026, 10, 20)));
    }

    @Test
    void doubleReturnRejected() {
        Loan loan = loanAt(LocalDate.of(2026, 10, 1), new LoanPeriod(14));
        loan.returnBook(LocalDate.of(2026, 10, 8));
        assertThrows(LoanNotFoundException.class, () -> loan.returnBook(LocalDate.of(2026, 10, 9)));
    }
}

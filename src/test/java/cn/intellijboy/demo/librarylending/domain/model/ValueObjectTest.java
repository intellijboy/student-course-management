package cn.intellijboy.demo.librarylending.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 值对象不变式单元测试：借期非负、费率计算正确、标识不可为空。
 */
class ValueObjectTest {

    @Test
    void readerIdRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ReaderId(null));
    }

    @Test
    void bookIdRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookId(null));
    }

    @Test
    void bookCopyIdRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookCopyId(null));
    }

    @Test
    void loanPeriodRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new LoanPeriod(-1));
    }

    @Test
    void loanPeriodAllowsZero() {
        assertEquals(0, new LoanPeriod(0).days());
    }

    @Test
    void overdueFeeRejectsNullRate() {
        assertThrows(IllegalArgumentException.class, () -> new OverdueFee(null));
    }

    @Test
    void overdueFeeRejectsNegativeRate() {
        assertThrows(IllegalArgumentException.class, () -> new OverdueFee(BigDecimal.valueOf(-0.1)));
    }

    @Test
    void overdueFeeZeroForNonOverdue() {
        OverdueFee fee = new OverdueFee(BigDecimal.valueOf(0.5));
        assertEquals(BigDecimal.ZERO, fee.amountFor(0));
        assertEquals(BigDecimal.ZERO, fee.amountFor(-3));
    }

    @Test
    void overdueFeeCalculatesByDays() {
        OverdueFee fee = new OverdueFee(BigDecimal.valueOf(0.5));
        assertEquals(BigDecimal.valueOf(2.5), fee.amountFor(5));
    }
}

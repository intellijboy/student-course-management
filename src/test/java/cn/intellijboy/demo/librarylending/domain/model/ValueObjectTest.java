package cn.intellijboy.demo.librarylending.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 值对象不变式单元测试：借期非负、费率计算正确、标识不可为空。
 */
@DisplayName("值对象不变式测试")
class ValueObjectTest {

    @Test
    @DisplayName("读者标识拒绝空值")
    void readerIdRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ReaderId(null));
    }

    @Test
    @DisplayName("图书标识拒绝空值")
    void bookIdRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookId(null));
    }

    @Test
    @DisplayName("馆藏副本标识拒绝空值")
    void bookCopyIdRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookCopyId(null));
    }

    @Test
    @DisplayName("借期拒绝负数")
    void loanPeriodRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new LoanPeriod(-1));
    }

    @Test
    @DisplayName("借期允许为零")
    void loanPeriodAllowsZero() {
        assertEquals(0, new LoanPeriod(0).days());
    }

    @Test
    @DisplayName("逾期费率拒绝空值")
    void overdueFeeRejectsNullRate() {
        assertThrows(IllegalArgumentException.class, () -> new OverdueFee(null));
    }

    @Test
    @DisplayName("逾期费率拒绝负数")
    void overdueFeeRejectsNegativeRate() {
        assertThrows(IllegalArgumentException.class, () -> new OverdueFee(BigDecimal.valueOf(-0.1)));
    }

    @Test
    @DisplayName("未逾期时费用为零")
    void overdueFeeZeroForNonOverdue() {
        OverdueFee fee = new OverdueFee(BigDecimal.valueOf(0.5));
        assertEquals(BigDecimal.ZERO, fee.amountFor(0));
        assertEquals(BigDecimal.ZERO, fee.amountFor(-3));
    }

    @Test
    @DisplayName("按逾期天数计算费用")
    void overdueFeeCalculatesByDays() {
        OverdueFee fee = new OverdueFee(BigDecimal.valueOf(0.5));
        assertEquals(BigDecimal.valueOf(2.5), fee.amountFor(5));
    }
}

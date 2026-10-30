package cn.intellijboy.demo.librarylending.domain.model;

import java.math.BigDecimal;

/**
 * 逾期费率值对象：按每天费率计算逾期费用。
 */
public record OverdueFee(BigDecimal ratePerDay) {

    public OverdueFee {
        if (ratePerDay == null || ratePerDay.signum() < 0) {
            throw new IllegalArgumentException("逾期费率不能为空且不能为负");
        }
    }

    /**
     * 按逾期天数计算费用。
     *
     * @param overdueDays 逾期天数（<=0 视为未逾期）
     */
    public BigDecimal amountFor(int overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return ratePerDay.multiply(BigDecimal.valueOf(overdueDays));
    }
}

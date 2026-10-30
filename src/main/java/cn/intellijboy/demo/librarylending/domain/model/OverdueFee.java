package cn.intellijboy.demo.librarylending.domain.model;

import java.math.BigDecimal;

/**
 * 逾期费率值对象：按每天费率计算逾期费用。
 */
public record OverdueFee(BigDecimal ratePerDay) {

    /**
     * @param ratePerDay 每天逾期费率，不能为 null 且不能为负
     */
    public OverdueFee {
        if (ratePerDay == null || ratePerDay.signum() < 0) {
            throw new IllegalArgumentException("逾期费率不能为空且不能为负");
        }
    }

    /**
     * 按逾期天数计算费用：费率乘以逾期天数，未逾期时费用为 0。
     *
     * @param overdueDays 逾期天数（<=0 视为未逾期）
     * @return 计算得到的逾期费用；天数不大于 0 时返回 0
     */
    public BigDecimal amountFor(int overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return ratePerDay.multiply(BigDecimal.valueOf(overdueDays));
    }
}

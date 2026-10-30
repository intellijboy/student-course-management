package cn.intellijboy.demo.librarylending.domain.model;

/**
 * 借期值对象：以天数为单位的不可变借阅期限。
 */
public record LoanPeriod(int days) {

    /**
     * @param days 借期天数，不能为负
     */
    public LoanPeriod {
        if (days < 0) {
            throw new IllegalArgumentException("借期不能为负数");
        }
    }
}

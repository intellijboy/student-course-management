package cn.intellijboy.demo.domain.studentcourse.model;

/**
 * 邮箱值对象：不可变，构造时校验格式。
 */
public record Email(String value) {

    private static final String PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public Email {
        if (value == null || !value.matches(PATTERN)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
    }
}

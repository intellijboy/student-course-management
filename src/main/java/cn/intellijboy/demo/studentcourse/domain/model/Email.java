package cn.intellijboy.demo.studentcourse.domain.model;

/**
 * 邮箱值对象：不可变，构造时校验格式。
 */
public record Email(String value) {

    private static final String PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * 紧凑构造器：校验邮箱格式，保证值对象不变式。
     *
     * @param value 邮箱字符串，必须匹配 {@value #PATTERN} 所定义的格式
     * @throws IllegalArgumentException 当邮箱为 {@code null} 或不符合格式要求时抛出
     */
    public Email {
        if (value == null || !value.matches(PATTERN)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
    }
}

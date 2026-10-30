package cn.intellijboy.demo.studentcourse.domain.exception;

/**
 * 成绩不存在。
 */
public class NotFoundException extends RuntimeException {

    /**
     * 创建异常实例。
     *
     * @param message 异常信息
     */
    public NotFoundException(String message) {
        super(message);
    }
}

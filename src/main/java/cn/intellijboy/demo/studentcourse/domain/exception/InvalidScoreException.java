package cn.intellijboy.demo.studentcourse.domain.exception;

/**
 * 成绩分数不合法。
 */
public class InvalidScoreException extends RuntimeException {

    /**
     * 创建异常实例。
     *
     * @param message 异常信息
     */
    public InvalidScoreException(String message) {
        super(message);
    }
}

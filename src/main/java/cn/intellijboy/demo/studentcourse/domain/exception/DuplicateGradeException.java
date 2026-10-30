package cn.intellijboy.demo.studentcourse.domain.exception;

/**
 * 同一学生对同一课程重复登记成绩。
 */
public class DuplicateGradeException extends RuntimeException {

    /**
     * 创建异常实例。
     *
     * @param message 异常信息
     */
    public DuplicateGradeException(String message) {
        super(message);
    }
}

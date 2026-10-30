package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 借阅记录不存在。
 */
public class LoanNotFoundException extends RuntimeException {

    /**
     * 创建借阅记录不存在异常。
     *
     * @param message 异常描述信息
     */
    public LoanNotFoundException(String message) {
        super(message);
    }
}

package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 同一读者重复借阅同一馆藏副本。
 */
public class DuplicateLoanException extends RuntimeException {

    /**
     * 创建重复借阅异常。
     *
     * @param message 异常描述信息
     */
    public DuplicateLoanException(String message) {
        super(message);
    }
}

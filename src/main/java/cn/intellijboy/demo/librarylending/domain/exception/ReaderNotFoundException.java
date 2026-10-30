package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 读者不存在。
 */
public class ReaderNotFoundException extends RuntimeException {

    /**
     * 创建读者不存在异常。
     *
     * @param message 异常描述信息
     */
    public ReaderNotFoundException(String message) {
        super(message);
    }
}

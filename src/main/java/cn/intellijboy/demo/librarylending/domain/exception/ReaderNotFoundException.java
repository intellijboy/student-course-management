package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 读者不存在。
 */
public class ReaderNotFoundException extends RuntimeException {

    public ReaderNotFoundException(String message) {
        super(message);
    }
}

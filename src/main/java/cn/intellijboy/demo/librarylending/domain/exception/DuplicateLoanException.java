package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 同一读者重复借阅同一馆藏副本。
 */
public class DuplicateLoanException extends RuntimeException {

    public DuplicateLoanException(String message) {
        super(message);
    }
}

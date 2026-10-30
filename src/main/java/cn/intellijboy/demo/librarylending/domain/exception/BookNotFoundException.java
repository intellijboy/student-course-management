package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 图书不存在。
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }
}

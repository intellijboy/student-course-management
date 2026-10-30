package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 馆藏副本不可借（已借出 / 已被预约占用 / 不存在）。
 */
public class BookCopyUnavailableException extends RuntimeException {

    public BookCopyUnavailableException(String message) {
        super(message);
    }
}

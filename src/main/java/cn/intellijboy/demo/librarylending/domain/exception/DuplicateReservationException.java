package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 读者重复预约同一图书。
 */
public class DuplicateReservationException extends RuntimeException {

    public DuplicateReservationException(String message) {
        super(message);
    }
}

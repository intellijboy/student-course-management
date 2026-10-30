package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 借阅记录不存在。
 */
public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(String message) {
        super(message);
    }
}

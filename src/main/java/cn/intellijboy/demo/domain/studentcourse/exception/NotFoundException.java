package cn.intellijboy.demo.domain.studentcourse.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}

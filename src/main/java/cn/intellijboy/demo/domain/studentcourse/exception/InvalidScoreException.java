package cn.intellijboy.demo.domain.studentcourse.exception;

public class InvalidScoreException extends RuntimeException {

    public InvalidScoreException(String message) {
        super(message);
    }
}

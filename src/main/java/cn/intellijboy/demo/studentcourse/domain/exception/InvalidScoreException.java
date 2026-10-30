package cn.intellijboy.demo.studentcourse.domain.exception;

public class InvalidScoreException extends RuntimeException {

    public InvalidScoreException(String message) {
        super(message);
    }
}

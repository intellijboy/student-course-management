package cn.intellijboy.demo.studentcourse.domain.exception;

public class DuplicateGradeException extends RuntimeException {

    public DuplicateGradeException(String message) {
        super(message);
    }
}

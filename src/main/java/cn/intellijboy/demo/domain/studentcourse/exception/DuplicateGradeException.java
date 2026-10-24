package cn.intellijboy.demo.domain.studentcourse.exception;

public class DuplicateGradeException extends RuntimeException {

    public DuplicateGradeException(String message) {
        super(message);
    }
}

package cn.intellijboy.demo.domain.studentcourse.model;

import cn.intellijboy.demo.domain.studentcourse.exception.InvalidScoreException;

public class CourseGrade {

    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 100;

    private Long id;
    private Long studentId;
    private Long courseId;
    private Integer score;

    public CourseGrade() {
    }

    public CourseGrade(Long id, Long studentId, Long courseId, Integer score) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        setScore(score);
    }

    public void updateScore(Integer score) {
        setScore(score);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getScore() {
        return score;
    }

    private void setScore(Integer score) {
        if (score == null || score < MIN_SCORE || score > MAX_SCORE) {
            throw new InvalidScoreException("成绩必须在 " + MIN_SCORE + " 到 " + MAX_SCORE + " 之间");
        }
        this.score = score;
    }
}

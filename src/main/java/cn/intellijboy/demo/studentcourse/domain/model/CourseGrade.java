package cn.intellijboy.demo.studentcourse.domain.model;

/**
 * 课程成绩实体（充血模型）：成绩以 {@link Score} 值对象承载 0-100 不变式。
 */
public class CourseGrade {

    private Long id;
    private Long studentId;
    private Long courseId;
    private Score score;

    public CourseGrade() {
    }

    public CourseGrade(Long id, Long studentId, Long courseId, Integer score) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = new Score(score);
    }

    /** 修改成绩（值对象构造时校验区间） */
    public void updateScore(Integer score) {
        this.score = new Score(score);
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

    public Long getCourseId() {
        return courseId;
    }

    public Score getScore() {
        return score;
    }
}

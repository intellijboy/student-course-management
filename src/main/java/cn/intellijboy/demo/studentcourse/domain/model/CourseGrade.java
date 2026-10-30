package cn.intellijboy.demo.studentcourse.domain.model;

/**
 * 课程成绩实体（充血模型）：成绩以 {@link Score} 值对象承载 0-100 不变式。
 */
public class CourseGrade {

    private Long id;
    private Long studentId;
    private Long courseId;
    private Score score;

    /**
     * 创建一个无初始状态的课程成绩实例（供框架反序列化或 ORM 使用）。
     */
    public CourseGrade() {
    }

    /**
     * 创建课程成绩实体。
     *
     * <p>成绩在 {@link Score} 值对象构造时校验 0-100 区间。
     *
     * @param id 成绩标识（可为 {@code null}，由持久层生成）
     * @param studentId 学生标识
     * @param courseId 课程标识
     * @param score 成绩分数，必须在 0-100 之间（越界时由 Score 值对象构造校验拒绝）
     */
    public CourseGrade(Long id, Long studentId, Long courseId, Integer score) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = new Score(score);
    }

    /**
     * 修改课程成绩。
     *
     * <p>以给定分数构造新的 {@link Score} 值对象，0-100 区间校验在其构造时完成。
     *
     * @param score 新成绩分数，必须在 0-100 之间
     */
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

package cn.intellijboy.demo.studentcourse.domain.model;

/**
 * 课程实体（充血模型）：行为与校验内聚。
 */
public class Course {

    private Long id;
    private String name;
    private Integer credits;

    /**
     * 创建一个无初始状态的课程实例（供框架反序列化或 ORM 使用）。
     */
    public Course() {
    }

    /**
     * 创建课程实体。
     *
     * <p>课程名称校验非空，学分校验为正数。
     *
     * @param id 课程标识（可为 {@code null}，由持久层生成）
     * @param name 课程名称，不能为空白
     * @param credits 学分数，必须为正数
     * @throws IllegalArgumentException 当课程名称为 {@code null} 或空白，或学分为 {@code null} 或非正数时抛出
     */
    public Course(Long id, String name, Integer credits) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        requireCredits(credits);
        this.id = id;
        this.name = name;
        this.credits = credits;
    }

    /**
     * 修改课程名称。
     *
     * @param name 新课程名称，不能为 {@code null} 或空白
     * @throws IllegalArgumentException 当课程名称为 {@code null} 或空白时抛出
     */
    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        this.name = name;
    }

    /**
     * 修改课程学分。
     *
     * @param credits 新学分数，必须为正数
     * @throws IllegalArgumentException 当学分为 {@code null} 或非正数时抛出
     */
    public void updateCredits(Integer credits) {
        requireCredits(credits);
        this.credits = credits;
    }

    /**
     * 校验学分必须为正数。
     *
     * @param credits 待校验的学分数
     * @throws IllegalArgumentException 当学分为 {@code null} 或非正数时抛出
     */
    private void requireCredits(Integer credits) {
        if (credits == null || credits <= 0) {
            throw new IllegalArgumentException("学分必须为正数");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Integer getCredits() {
        return credits;
    }
}

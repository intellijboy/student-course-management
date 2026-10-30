package cn.intellijboy.demo.studentcourse.domain.model;

/**
 * 课程实体（充血模型）：行为与校验内聚。
 */
public class Course {

    private Long id;
    private String name;
    private Integer credits;

    public Course() {
    }

    public Course(Long id, String name, Integer credits) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        requireCredits(credits);
        this.id = id;
        this.name = name;
        this.credits = credits;
    }

    /** 修改课程名（校验非空） */
    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        this.name = name;
    }

    /** 修改学分（校验为正） */
    public void updateCredits(Integer credits) {
        requireCredits(credits);
        this.credits = credits;
    }

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

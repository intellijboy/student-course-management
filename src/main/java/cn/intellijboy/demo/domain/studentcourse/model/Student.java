package cn.intellijboy.demo.domain.studentcourse.model;

/**
 * 学生实体（充血模型）：行为与校验内聚，邮箱以 {@link Email} 值对象表达。
 */
public class Student {

    private Long id;
    private String name;
    private Email email;

    public Student() {
    }

    public Student(Long id, String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("学生姓名不能为空");
        }
        this.id = id;
        this.name = name;
        this.email = new Email(email);
    }

    /** 修改姓名（校验非空） */
    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("学生姓名不能为空");
        }
        this.name = name;
    }

    /** 修改邮箱（值对象构造时校验格式） */
    public void updateEmail(String email) {
        this.email = new Email(email);
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

    public Email getEmail() {
        return email;
    }
}

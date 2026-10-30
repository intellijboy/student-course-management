package cn.intellijboy.demo.studentcourse.domain.model;

/**
 * 学生实体（充血模型）：行为与校验内聚，邮箱以 {@link Email} 值对象表达。
 */
public class Student {

    private Long id;
    private String name;
    private Email email;

    /**
     * 创建一个无初始状态的学生实例（供框架反序列化或 ORM 使用）。
     */
    public Student() {
    }

    /**
     * 创建学生实体。
     *
     * <p>姓名校验非空；邮箱在 {@link Email} 值对象构造时校验格式。
     *
     * @param id 学生标识（可为 {@code null}，由持久层生成）
     * @param name 学生姓名，不能为空白
     * @param email 邮箱字符串，须符合 Email 值对象的格式要求
     * @throws IllegalArgumentException 当姓名为 {@code null} 或空白时抛出
     */
    public Student(Long id, String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("学生姓名不能为空");
        }
        this.id = id;
        this.name = name;
        this.email = new Email(email);
    }

    /**
     * 修改学生姓名。
     *
     * @param name 新姓名，不能为 {@code null} 或空白
     * @throws IllegalArgumentException 当姓名为 {@code null} 或空白时抛出
     */
    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("学生姓名不能为空");
        }
        this.name = name;
    }

    /**
     * 修改学生邮箱。
     *
     * <p>以给定字符串构造新的 {@link Email} 值对象，格式校验在其构造时完成。
     *
     * @param email 新邮箱字符串，须符合 Email 值对象的格式要求
     */
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

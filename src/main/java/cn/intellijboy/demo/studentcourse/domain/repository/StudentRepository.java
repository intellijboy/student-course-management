package cn.intellijboy.demo.studentcourse.domain.repository;

import cn.intellijboy.demo.studentcourse.domain.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * 学生仓储端口：领域层定义，由基础设施层实现。
 */
public interface StudentRepository {

    /**
     * 查询全部学生。
     *
     * @return 全部学生集合；若无数据则返回空集合
     */
    List<Student> findAll();

    /**
     * 按标识查询学生。
     *
     * @param id 学生标识
     * @return 对应的学生；不存在时返回 {@link Optional#empty()}
     */
    Optional<Student> findById(Long id);

    /**
     * 保存学生（新增或更新）。
     *
     * @param student 待保存的学生实体
     * @return 保存后的学生实体（含持久层生成的标识）
     */
    Student save(Student student);

    /**
     * 按标识删除学生。
     *
     * @param id 学生标识
     */
    void deleteById(Long id);
}

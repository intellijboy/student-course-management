package cn.intellijboy.demo.studentcourse.domain.repository;

import cn.intellijboy.demo.studentcourse.domain.model.Course;

import java.util.List;
import java.util.Optional;

/**
 * 课程仓储端口：领域层定义，由基础设施层实现。
 */
public interface CourseRepository {

    /**
     * 查询全部课程。
     *
     * @return 全部课程集合；若无数据则返回空集合
     */
    List<Course> findAll();

    /**
     * 按标识查询课程。
     *
     * @param id 课程标识
     * @return 对应的课程；不存在时返回 {@link Optional#empty()}
     */
    Optional<Course> findById(Long id);

    /**
     * 保存课程（新增或更新）。
     *
     * @param course 待保存的课程实体
     * @return 保存后的课程实体（含持久层生成的标识）
     */
    Course save(Course course);

    /**
     * 按标识删除课程。
     *
     * @param id 课程标识
     */
    void deleteById(Long id);
}

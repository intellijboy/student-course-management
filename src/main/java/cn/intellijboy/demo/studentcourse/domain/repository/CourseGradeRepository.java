package cn.intellijboy.demo.studentcourse.domain.repository;

import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;

import java.util.List;
import java.util.Optional;

/**
 * 课程成绩仓储端口：领域层定义，由基础设施层实现。
 */
public interface CourseGradeRepository {

    /**
     * 查询全部课程成绩。
     *
     * @return 全部课程成绩集合；若无数据则返回空集合
     */
    List<CourseGrade> findAll();

    /**
     * 按标识查询课程成绩。
     *
     * @param id 成绩标识
     * @return 对应的课程成绩；不存在时返回 {@link Optional#empty()}
     */
    Optional<CourseGrade> findById(Long id);

    /**
     * 保存课程成绩（新增或更新）。
     *
     * @param courseGrade 待保存的课程成绩实体
     * @return 保存后的课程成绩实体（含持久层生成的标识）
     */
    CourseGrade save(CourseGrade courseGrade);

    /**
     * 按标识删除课程成绩。
     *
     * @param id 成绩标识
     */
    void deleteById(Long id);

    /**
     * 按学生与课程查询课程成绩。
     *
     * @param studentId 学生标识
     * @param courseId 课程标识
     * @return 对应的课程成绩；不存在时返回 {@link Optional#empty()}
     */
    Optional<CourseGrade> findByStudentIdAndCourseId(Long studentId, Long courseId);
}

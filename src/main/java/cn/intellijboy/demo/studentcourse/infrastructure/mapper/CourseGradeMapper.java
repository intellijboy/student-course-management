package cn.intellijboy.demo.studentcourse.infrastructure.mapper;

import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;
import cn.intellijboy.demo.studentcourse.infrastructure.persistence.CourseGradeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CourseGradeMapper {

    public CourseGradeJpaEntity toJpa(CourseGrade grade) {
        if (grade == null) {
            return null;
        }
        CourseGradeJpaEntity entity = new CourseGradeJpaEntity();
        entity.setId(grade.getId());
        entity.setStudentId(grade.getStudentId());
        entity.setCourseId(grade.getCourseId());
        entity.setScore(grade.getScore().value());
        return entity;
    }

    public CourseGrade toDomain(CourseGradeJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CourseGrade(entity.getId(), entity.getStudentId(), entity.getCourseId(), entity.getScore());
    }
}

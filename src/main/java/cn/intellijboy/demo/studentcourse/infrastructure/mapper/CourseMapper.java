package cn.intellijboy.demo.studentcourse.infrastructure.mapper;

import cn.intellijboy.demo.studentcourse.domain.model.Course;
import cn.intellijboy.demo.studentcourse.infrastructure.persistence.CourseJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseJpaEntity toJpa(Course course) {
        if (course == null) {
            return null;
        }
        CourseJpaEntity entity = new CourseJpaEntity();
        entity.setId(course.getId());
        entity.setName(course.getName());
        entity.setCredits(course.getCredits());
        return entity;
    }

    public Course toDomain(CourseJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Course(entity.getId(), entity.getName(), entity.getCredits());
    }
}

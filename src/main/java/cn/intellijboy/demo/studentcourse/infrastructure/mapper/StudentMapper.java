package cn.intellijboy.demo.studentcourse.infrastructure.mapper;

import cn.intellijboy.demo.studentcourse.domain.model.Student;
import cn.intellijboy.demo.studentcourse.infrastructure.persistence.StudentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentJpaEntity toJpa(Student student) {
        if (student == null) {
            return null;
        }
        StudentJpaEntity entity = new StudentJpaEntity();
        entity.setId(student.getId());
        entity.setName(student.getName());
        entity.setEmail(student.getEmail().value());
        return entity;
    }

    public Student toDomain(StudentJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Student(entity.getId(), entity.getName(), entity.getEmail());
    }
}

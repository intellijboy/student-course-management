package cn.intellijboy.demo.infrastructure.studentcourse.mapper;

import cn.intellijboy.demo.domain.studentcourse.model.Student;
import cn.intellijboy.demo.infrastructure.studentcourse.persistence.StudentJpaEntity;
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
        entity.setEmail(student.getEmail());
        return entity;
    }

    public Student toDomain(StudentJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Student(entity.getId(), entity.getName(), entity.getEmail());
    }
}

package cn.intellijboy.demo.studentcourse.application;

import cn.intellijboy.demo.studentcourse.domain.exception.NotFoundException;
import cn.intellijboy.demo.studentcourse.domain.model.Student;
import cn.intellijboy.demo.studentcourse.domain.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentApplicationService {

    private final StudentRepository studentRepository;

    public StudentApplicationService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional
    public Student create(String name, String email) {
        return studentRepository.save(new Student(null, name, email));
    }

    @Transactional
    public Student update(Long id, String name, String email) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("学生不存在: id=" + id));
        student.updateName(name);
        student.updateEmail(email);
        return studentRepository.save(student);
    }

    @Transactional
    public void delete(Long id) {
        if (studentRepository.findById(id).isEmpty()) {
            throw new NotFoundException("学生不存在: id=" + id);
        }
        studentRepository.deleteById(id);
    }
}

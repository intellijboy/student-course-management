package cn.intellijboy.demo.application.studentcourse;

import cn.intellijboy.demo.domain.studentcourse.exception.NotFoundException;
import cn.intellijboy.demo.domain.studentcourse.model.Student;
import cn.intellijboy.demo.domain.studentcourse.repository.StudentRepository;
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
        student.setName(name);
        student.setEmail(email);
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

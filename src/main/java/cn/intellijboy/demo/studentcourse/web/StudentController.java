package cn.intellijboy.demo.studentcourse.web;

import cn.intellijboy.demo.studentcourse.application.StudentApplicationService;
import cn.intellijboy.demo.studentcourse.domain.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentApplicationService studentApplicationService;

    public StudentController(StudentApplicationService studentApplicationService) {
        this.studentApplicationService = studentApplicationService;
    }

    /**
     * 查询全部学生列表。
     *
     * @return 200 与学生列表
     */
    @GetMapping
    public List<StudentDto> list() {
        return studentApplicationService.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 创建学生。
     *
     * @param request 创建请求（name、email），id 由服务端生成
     * @return 201 与新建学生
     */
    @PostMapping
    public ResponseEntity<StudentDto> create(@RequestBody StudentRequest request) {
        Student student = studentApplicationService.create(request.name(), request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(student));
    }

    /**
     * 更新指定 id 学生的姓名与邮箱。
     *
     * @param id     学生 id
     * @param request 更新请求（name、email）
     * @return 200 与更新后学生，id 不存在时返回 404
     */
    @PutMapping("/{id}")
    public StudentDto update(@PathVariable Long id, @RequestBody StudentRequest request) {
        return toDto(studentApplicationService.update(id, request.name(), request.email()));
    }

    /**
     * 删除指定 id 的学生。
     *
     * @param id 学生 id
     * @return 204，id 不存在时返回 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private StudentDto toDto(Student student) {
        return new StudentDto(student.getId(), student.getName(), student.getEmail().value());
    }
}

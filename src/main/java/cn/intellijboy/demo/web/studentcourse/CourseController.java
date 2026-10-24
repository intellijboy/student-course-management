package cn.intellijboy.demo.web.studentcourse;

import cn.intellijboy.demo.application.studentcourse.CourseApplicationService;
import cn.intellijboy.demo.domain.studentcourse.model.Course;
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
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseApplicationService courseApplicationService;

    public CourseController(CourseApplicationService courseApplicationService) {
        this.courseApplicationService = courseApplicationService;
    }

    /**
     * 查询全部课程列表。
     *
     * @return 200 与课程列表
     */
    @GetMapping
    public List<CourseDto> list() {
        return courseApplicationService.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 创建课程。
     *
     * @param request 创建请求（name、credits），id 由服务端生成
     * @return 201 与新建课程
     */
    @PostMapping
    public ResponseEntity<CourseDto> create(@RequestBody CourseRequest request) {
        Course course = courseApplicationService.create(request.name(), request.credits());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(course));
    }

    /**
     * 更新指定 id 课程的名称与学分。
     *
     * @param id      课程 id
     * @param request 更新请求（name、credits）
     * @return 200 与更新后课程，id 不存在时返回 404
     */
    @PutMapping("/{id}")
    public CourseDto update(@PathVariable Long id, @RequestBody CourseRequest request) {
        return toDto(courseApplicationService.update(id, request.name(), request.credits()));
    }

    /**
     * 删除指定 id 的课程。
     *
     * @param id 课程 id
     * @return 204，id 不存在时返回 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CourseDto toDto(Course course) {
        return new CourseDto(course.getId(), course.getName(), course.getCredits());
    }
}

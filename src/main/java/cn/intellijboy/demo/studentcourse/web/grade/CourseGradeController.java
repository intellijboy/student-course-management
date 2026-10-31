package cn.intellijboy.demo.studentcourse.web.grade;

import cn.intellijboy.demo.studentcourse.application.CourseGradeApplicationService;
import cn.intellijboy.demo.studentcourse.domain.exception.InvalidScoreException;
import cn.intellijboy.demo.studentcourse.domain.exception.NotFoundException;
import cn.intellijboy.demo.studentcourse.domain.model.CourseGrade;
import cn.intellijboy.demo.studentcourse.domain.repository.CourseGradeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grades")
public class CourseGradeController {

    private final CourseGradeApplicationService courseGradeApplicationService;
    private final CourseGradeRepository courseGradeRepository;

    public CourseGradeController(CourseGradeApplicationService courseGradeApplicationService,
                                 CourseGradeRepository courseGradeRepository) {
        this.courseGradeApplicationService = courseGradeApplicationService;
        this.courseGradeRepository = courseGradeRepository;
    }

    /**
     * 登记成绩（成绩范围 0-100，同一学生同一课程唯一）。
     *
     * @param request 登记请求（studentId、courseId、score）
     * @return 201 与新建成绩；分数越界或重复登记返回 400
     */
    @PostMapping
    public ResponseEntity<CourseGradeView> register(@RequestBody GradeRequest request) {
        CourseGrade grade = courseGradeApplicationService.register(
            request.studentId(), request.courseId(), requireScore(request.score()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toView(grade));
    }

    /**
     * 修改指定 id 成绩的分数。
     *
     * @param id      成绩 id
     * @param request 更新请求（score）
     * @return 200 与更新后成绩；分数越界返回 400，id 不存在返回 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseGradeView> update(@PathVariable Long id, @RequestBody GradeUpdateRequest request) {
        requireExists(id);
        CourseGrade grade = courseGradeApplicationService.update(id, requireScore(request.score()));
        return ResponseEntity.ok(toView(grade));
    }

    /**
     * 删除指定 id 的成绩。
     *
     * @param id 成绩 id
     * @return 204，id 不存在返回 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        requireExists(id);
        courseGradeApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void requireExists(Long id) {
        if (courseGradeRepository.findById(id).isEmpty()) {
            throw new NotFoundException("成绩不存在: id=" + id);
        }
    }

    private int requireScore(Integer score) {
        if (score == null) {
            throw new InvalidScoreException("成绩不能为空");
        }
        return score;
    }

    private CourseGradeView toView(CourseGrade grade) {
        return new CourseGradeView(grade.getId(), grade.getStudentId(), grade.getCourseId(), grade.getScore().value());
    }
}

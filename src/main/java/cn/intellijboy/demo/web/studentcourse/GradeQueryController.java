package cn.intellijboy.demo.web.studentcourse;

import cn.intellijboy.demo.application.studentcourse.CourseGradeApplicationService;
import cn.intellijboy.demo.domain.studentcourse.model.CourseGrade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GradeQueryController {

    private final CourseGradeApplicationService courseGradeApplicationService;

    public GradeQueryController(CourseGradeApplicationService courseGradeApplicationService) {
        this.courseGradeApplicationService = courseGradeApplicationService;
    }

    /**
     * 按学生查询其全部课程成绩。
     *
     * @param id 学生 id
     * @return 200 与该学生的成绩列表
     */
    @GetMapping("/api/students/{id}/grades")
    public List<CourseGradeDto> findByStudent(@PathVariable Long id) {
        return courseGradeApplicationService.findByStudent(id).stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 按课程查询全部选课学生的成绩。
     *
     * @param id 课程 id
     * @return 200 与该课程的成绩列表
     */
    @GetMapping("/api/courses/{id}/grades")
    public List<CourseGradeDto> findByCourse(@PathVariable Long id) {
        return courseGradeApplicationService.findByCourse(id).stream()
                .map(this::toDto)
                .toList();
    }

    private CourseGradeDto toDto(CourseGrade grade) {
        return new CourseGradeDto(grade.getId(), grade.getStudentId(), grade.getCourseId(), grade.getScore());
    }
}

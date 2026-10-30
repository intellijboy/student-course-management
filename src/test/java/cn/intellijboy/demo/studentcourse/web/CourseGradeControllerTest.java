package cn.intellijboy.demo.studentcourse.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseGradeControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void registerGradeReturns201WithDto() throws Exception {
        long studentId = createStudent("成绩学生", "grade@example.com");
        long courseId = createCourse("成绩课程", 3);

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":" + studentId + ",\"courseId\":" + courseId + ",\"score\":90}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.studentId").value(studentId))
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(jsonPath("$.score").value(90));
    }

    @Test
    void registerGradeWithOutOfRangeScoreReturns400() throws Exception {
        long studentId = createStudent("越界学生", "invalid@example.com");
        long courseId = createCourse("越界课程", 2);

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":" + studentId + ",\"courseId\":" + courseId + ",\"score\":101}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void registerDuplicateGradeReturns400() throws Exception {
        long studentId = createStudent("重复学生", "dup@example.com");
        long courseId = createCourse("重复课程", 4);

        registerGrade(studentId, courseId, 80);

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":" + studentId + ",\"courseId\":" + courseId + ",\"score\":85}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateGradeReturns200WhenExists() throws Exception {
        long studentId = createStudent("修改学生", "update@example.com");
        long courseId = createCourse("修改课程", 3);
        long gradeId = registerGrade(studentId, courseId, 60);

        mockMvc.perform(put("/api/grades/{id}", gradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":95}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gradeId))
                .andExpect(jsonPath("$.studentId").value(studentId))
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(jsonPath("$.score").value(95));
    }

    @Test
    void updateGradeWithOutOfRangeScoreReturns400() throws Exception {
        long studentId = createStudent("越界修改学生", "update-invalid@example.com");
        long courseId = createCourse("越界修改课程", 2);
        long gradeId = registerGrade(studentId, courseId, 70);

        mockMvc.perform(put("/api/grades/{id}", gradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":150}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateGradeReturns404WhenNotFound() throws Exception {
        mockMvc.perform(put("/api/grades/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":80}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteGradeReturns204WhenExists() throws Exception {
        long studentId = createStudent("删除学生", "delete@example.com");
        long courseId = createCourse("删除课程", 3);
        long gradeId = registerGrade(studentId, courseId, 88);

        mockMvc.perform(delete("/api/grades/{id}", gradeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteGradeReturns404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/grades/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void findGradesByStudentReturnsList() throws Exception {
        long studentId = createStudent("查询学生", "query@example.com");
        long course1 = createCourse("课程一", 3);
        long course2 = createCourse("课程二", 4);
        registerGrade(studentId, course1, 85);
        registerGrade(studentId, course2, 92);

        mockMvc.perform(get("/api/students/{id}/grades", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].studentId").value(studentId));
    }

    @Test
    void findGradesByCourseReturnsList() throws Exception {
        long courseId = createCourse("查询课程", 3);
        long student1 = createStudent("学生甲", "a@example.com");
        long student2 = createStudent("学生乙", "b@example.com");
        registerGrade(student1, courseId, 78);
        registerGrade(student2, courseId, 88);

        mockMvc.perform(get("/api/courses/{id}/grades", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].courseId").value(courseId));
    }

    private long createStudent(String name, String email) throws Exception {
        return extractId(mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
    }

    private long createCourse(String name, int credits) throws Exception {
        return extractId(mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"credits\":" + credits + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
    }

    private long registerGrade(long studentId, long courseId, int score) throws Exception {
        return extractId(mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":" + studentId + ",\"courseId\":" + courseId + ",\"score\":" + score + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
    }

    private long extractId(String body) {
        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(body);
        if (!matcher.find()) {
            throw new IllegalStateException("响应中未找到 id 字段: " + body);
        }
        return Long.parseLong(matcher.group(1));
    }
}

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
class StudentCourseControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    // ---------- Student ----------

    @Test
    void createStudentReturns201WithId() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"张三\",\"email\":\"zhangsan@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("张三"))
                .andExpect(jsonPath("$.email").value("zhangsan@example.com"));
    }

    @Test
    void listStudentsReturnsCreatedStudents() throws Exception {
        createStudent("张三", "zhangsan@example.com");
        createStudent("李四", "lisi@example.com");
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateStudentReturns200WhenExists() throws Exception {
        long id = createStudent("张三", "zhangsan@example.com");
        mockMvc.perform(put("/api/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"张三丰\",\"email\":\"zhangsanfeng@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("张三丰"))
                .andExpect(jsonPath("$.email").value("zhangsanfeng@example.com"));
    }

    @Test
    void updateStudentReturns404WhenNotFound() throws Exception {
        mockMvc.perform(put("/api/students/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"email\":\"x@example.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteStudentReturns204WhenExists() throws Exception {
        long id = createStudent("待删除", "delete@example.com");
        mockMvc.perform(delete("/api/students/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteStudentReturns404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/students/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ---------- Course ----------

    @Test
    void createCourseReturns201WithId() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"高等数学\",\"credits\":4}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("高等数学"))
                .andExpect(jsonPath("$.credits").value(4));
    }

    @Test
    void listCoursesReturnsCreatedCourses() throws Exception {
        createCourse("高等数学", 4);
        createCourse("大学英语", 2);
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateCourseReturns200WhenExists() throws Exception {
        long id = createCourse("高等数学", 4);
        mockMvc.perform(put("/api/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"线性代数\",\"credits\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("线性代数"))
                .andExpect(jsonPath("$.credits").value(3));
    }

    @Test
    void updateCourseReturns404WhenNotFound() throws Exception {
        mockMvc.perform(put("/api/courses/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"credits\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteCourseReturns204WhenExists() throws Exception {
        long id = createCourse("待删除课程", 1);
        mockMvc.perform(delete("/api/courses/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCourseReturns404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/courses/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
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

    private long extractId(String body) {
        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(body);
        if (!matcher.find()) {
            throw new IllegalStateException("响应中未找到 id 字段: " + body);
        }
        return Long.parseLong(matcher.group(1));
    }
}

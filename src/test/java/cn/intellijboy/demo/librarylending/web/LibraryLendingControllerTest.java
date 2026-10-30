package cn.intellijboy.demo.librarylending.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 图书借阅接口层测试（少量）：馆藏登记、借阅、预约、归还与查询主要端点。
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("图书借阅接口测试")
class LibraryLendingControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private void createStudent(Long id, String name) throws Exception {
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\",\"email\":\"reader" + id + "@example.com\"}"))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("登记图书返回 201")
    void registerBookReturns201() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"DDD 实战\",\"author\":\"作者\",\"copyCount\":2}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("DDD 实战"))
            .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    @DisplayName("读者借阅图书返回 201")
    void borrowReturns201() throws Exception {
        createStudent(1L, "张三");
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"DDD 实战\",\"author\":\"作者\",\"copyCount\":1}"))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":1,\"bookId\":1}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.bookCopyId").value(101));
    }

    @Test
    @DisplayName("读者不存在时借阅返回 404")
    void borrowWithUnknownReaderReturns404() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"DDD 实战\",\"author\":\"作者\",\"copyCount\":1}"))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":999,\"bookId\":1}"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("全部副本借出时预约排队，归还后预约转正")
    void reserveWhenAllBorrowedThenReturnPromotes() throws Exception {
        createStudent(1L, "张三");
        createStudent(2L, "李四");
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"DDD 实战\",\"author\":\"作者\",\"copyCount\":1}"))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":1,\"bookId\":1}"))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":2,\"bookId\":1}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("WAITING"));

        // 归还触发预约转正
        mockMvc.perform(post("/api/loans/1/return"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.promotedReaderId").value(2));
    }

    @Test
    @DisplayName("查询读者借阅列表返回 200")
    void readerLoansReturns200() throws Exception {
        createStudent(1L, "张三");
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"DDD 实战\",\"author\":\"作者\",\"copyCount\":1}"))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":1,\"bookId\":1}"))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/readers/1/loans"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].bookId").value(1));
    }
}

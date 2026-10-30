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
 * 图书借阅端到端集成验证：登记馆藏→借阅→满借拒绝→预约→归还转正→防腐层读者校验，
 * 覆盖 spec 主要场景，通过真实 REST 链路验证。
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("图书借阅端到端测试")
class LendingEndToEndTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("完整借阅流程依次执行成功：登记馆藏、借阅、满借拒绝、预约、归还转正与查询")
    void fullLendingFlow() throws Exception {
        // 1. 创建三名读者（复用学生上下文，经防腐层校验）
        mockMvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"读者一\",\"email\":\"r1@example.com\"}")).andExpect(status().isCreated());
        mockMvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"读者二\",\"email\":\"r2@example.com\"}")).andExpect(status().isCreated());
        mockMvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"读者三\",\"email\":\"r3@example.com\"}")).andExpect(status().isCreated());

        // 2. 登记馆藏（2 副本）
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"DDD 实战\",\"author\":\"作者\",\"copyCount\":2}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.availableCopies").value(2));

        // 3. 读者一借阅（第一副本）
        mockMvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":1,\"bookId\":1}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.bookCopyId").value(101));

        // 4. 读者二借阅（第二副本）→ 满借
        mockMvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":2,\"bookId\":1}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.bookCopyId").value(102));

        // 5. 满借后读者三借阅被拒绝（提示可预约）
        mockMvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":3,\"bookId\":1}"))
            .andExpect(status().isBadRequest());

        // 6. 读者三预约 → 排队成功
        mockMvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":3,\"bookId\":1}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("WAITING"));

        // 7. 读者一归还 → 触发读者三预约转正
        mockMvc.perform(post("/api/loans/1/return"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.promotedReaderId").value(3));

        // 8. 馆藏状态：1 本被借出（读者二），1 本预约占用（转正给读者三）
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableCopies").value(0))
            .andExpect(jsonPath("$.borrowedCopies").value(1))
            .andExpect(jsonPath("$.reservationQueue[0].status").value("NOTIFIED"));

        // 9. 读者一借阅列表仍含已归还记录
        mockMvc.perform(get("/api/readers/1/loans"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].bookId").value(1));
    }

    @Test
    @DisplayName("防腐层拒绝不存在读者的借阅与预约并返回 404")
    void unknownReaderRejectedByAcl() throws Exception {
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"领域驱动设计\",\"author\":\"Eric Evans\",\"copyCount\":1}"))
            .andExpect(status().isCreated());

        // 防腐层：不存在的读者借阅被拒绝（404）
        mockMvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":999,\"bookId\":1}"))
            .andExpect(status().isNotFound());

        // 防腐层：不存在的读者预约被拒绝（404）
        mockMvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON)
                .content("{\"readerId\":999,\"bookId\":1}"))
            .andExpect(status().isNotFound());
    }
}

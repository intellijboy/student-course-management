package cn.intellijboy.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("应用启动测试")
class StudentCourseManagementApplicationTests {

    @Test
    @DisplayName("应用上下文正常加载")
    void contextLoads() {
    }
}

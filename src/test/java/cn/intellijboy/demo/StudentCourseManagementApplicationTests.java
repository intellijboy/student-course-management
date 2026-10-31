/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 应用启动冒烟测试：验证 Spring 应用上下文可正常加载。
 */
@SpringBootTest
@DisplayName("应用启动测试")
class StudentCourseManagementApplicationTests {

    @Test
    @DisplayName("应用上下文正常加载")
    void contextLoads() {
    }
}

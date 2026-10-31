/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.common;

/**
 * 通用字符串工具：提供跨上下文共享的文本处理便捷方法。
 *
 * <p>本类位于 sc-common，各层（domain/application/infrastructure/interfaces）均可依赖并调用，
 * 是模块化单体中「公共契约模块」被多层复用的示例。</p>
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * 将字符串首字母转为大写，其余不变；空串或 null 原样返回。
     *
     * @param value 待处理字符串，可为 {@code null}
     * @return 首字母大写的字符串；{@code null}/{@code ""} 则原样返回
     */
    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * 判断字符串是否为空白（null、空串或仅含空格）。
     *
     * @param value 待判断字符串，可为 {@code null}
     * @return 为空白时返回 {@code true}
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
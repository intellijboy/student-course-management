/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending.book;

/**
 * 登记图书请求。
 */
public record BookRequest(String title, String author, int copyCount) {
}

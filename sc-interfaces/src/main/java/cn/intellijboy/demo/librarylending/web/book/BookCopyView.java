/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.web.book;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyStatus;

/**
 * 馆藏副本视图：承载某馆藏副本的标识与借阅状态。
 */
public record BookCopyView(Long id, BookCopyStatus status) {
}

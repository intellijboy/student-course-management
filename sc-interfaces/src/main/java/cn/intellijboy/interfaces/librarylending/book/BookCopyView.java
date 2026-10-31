/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending.book;

import cn.intellijboy.domain.librarylending.model.BookCopyStatus;

/**
 * 馆藏副本视图：承载某馆藏副本的标识与借阅状态。
 */
public record BookCopyView(Long id, BookCopyStatus status) {
}

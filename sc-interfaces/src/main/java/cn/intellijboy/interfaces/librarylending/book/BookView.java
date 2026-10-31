/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending.book;

import cn.intellijboy.common.StringUtils;
import cn.intellijboy.domain.librarylending.model.Book;

/**
 * 图书视图：承载图书基本信息（标题、作者、总/可借副本数），用于图书列表与登记响应。
 */
public record BookView(Long id, String title, String author, int totalCopies, int availableCopies) {
    public static BookView of(Book book) {
        return new BookView(book.getId().value(), StringUtils.capitalize(book.getTitle()),
            book.getAuthor(), book.totalCount(), book.availableCount());
    }
}

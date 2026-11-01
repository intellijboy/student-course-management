/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.librarylending.event;

import cn.intellijboy.domain.event.DomainEvent;
import cn.intellijboy.domain.librarylending.model.BookCopyId;
import cn.intellijboy.domain.librarylending.model.ReaderId;

/**
 * 归还领域事件：表示图书已归还。
 */
public class BookReturnedEvent implements DomainEvent {

    private final ReaderId readerId;
    private final BookCopyId bookCopyId;

    /**
     * 创建归还领域事件。
     *
     * @param readerId 归还读者标识
     * @param bookCopyId 归还副本标识
     */
    public BookReturnedEvent(ReaderId readerId, BookCopyId bookCopyId) {
        this.readerId = readerId;
        this.bookCopyId = bookCopyId;
    }

    public ReaderId getReaderId() {
        return readerId;
    }

    public BookCopyId getBookCopyId() {
        return bookCopyId;
    }
}

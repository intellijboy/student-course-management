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
 * 借出领域事件：表示某图书副本已借出给某读者，承载这一领域事实。
 */
public class BookLoanedEvent implements DomainEvent {

    private final ReaderId readerId;
    private final BookCopyId bookCopyId;

    /**
     * 创建借出领域事件。
     *
     * @param readerId 借阅读者标识
     * @param bookCopyId 借出副本标识
     */
    public BookLoanedEvent(ReaderId readerId, BookCopyId bookCopyId) {
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

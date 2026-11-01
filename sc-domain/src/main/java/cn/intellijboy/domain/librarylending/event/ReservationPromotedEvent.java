/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.librarylending.event;

import cn.intellijboy.domain.event.DomainEvent;
import cn.intellijboy.domain.librarylending.model.BookId;
import cn.intellijboy.domain.librarylending.model.ReaderId;

/**
 * 预约转正（可借通知）领域事件：表示某预约转正、对应副本已可借，用于通知读者借阅。
 */
public class ReservationPromotedEvent implements DomainEvent {

    private final ReaderId readerId;
    private final BookId bookId;

    /**
     * 创建预约转正（可借通知）领域事件。
     *
     * @param readerId 被通知的读者标识
     * @param bookId 可借图书标识
     */
    public ReservationPromotedEvent(ReaderId readerId, BookId bookId) {
        this.readerId = readerId;
        this.bookId = bookId;
    }

    public ReaderId getReaderId() {
        return readerId;
    }

    public BookId getBookId() {
        return bookId;
    }
}

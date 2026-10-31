/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.domain.event;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

/**
 * 借阅逾期领域事件：表示某副本的借阅已逾期（超过应还日期仍未归还），触发逾期处理流程。
 */
public class LoanOverdueEvent extends DomainEvent {

    private final ReaderId readerId;
    private final BookCopyId bookCopyId;

    /**
     * 创建借阅逾期领域事件。
     *
     * @param readerId 逾期借阅的读者标识
     * @param bookCopyId 逾期未还的副本标识
     */
    public LoanOverdueEvent(ReaderId readerId, BookCopyId bookCopyId) {
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

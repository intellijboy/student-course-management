package cn.intellijboy.demo.librarylending.domain.event;

import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

/**
 * 预约转正（可借通知）领域事件。
 */
public class ReservationPromotedEvent extends DomainEvent {

    private final ReaderId readerId;
    private final BookId bookId;

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

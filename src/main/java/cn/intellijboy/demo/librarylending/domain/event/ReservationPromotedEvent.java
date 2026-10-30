package cn.intellijboy.demo.librarylending.domain.event;

import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

/**
 * 预约转正（可借通知）领域事件。
 */
public class ReservationPromotedEvent extends DomainEvent {

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

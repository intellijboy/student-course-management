package cn.intellijboy.demo.librarylending.domain.event;

import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

/**
 * 预约创建领域事件。
 */
public class ReservationCreatedEvent extends DomainEvent {

    private final ReaderId readerId;
    private final BookId bookId;

    /**
     * 创建预约创建领域事件。
     *
     * @param readerId 预约读者标识
     * @param bookId 被预约图书标识
     */
    public ReservationCreatedEvent(ReaderId readerId, BookId bookId) {
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

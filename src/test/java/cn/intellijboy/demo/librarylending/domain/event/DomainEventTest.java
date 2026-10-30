package cn.intellijboy.demo.librarylending.domain.event;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 领域事件与发布端口单元测试。
 */
class DomainEventTest {

    private final ReaderId reader = new ReaderId(1L);

    @Test
    void eventsCarryDataAndTimestamp() {
        BookLoanedEvent loaned = new BookLoanedEvent(reader, new BookCopyId(10L));
        assertEquals(reader, loaned.getReaderId());
        assertEquals(new BookCopyId(10L), loaned.getBookCopyId());
        assertNotNull(loaned.getOccurredAt());

        BookReturnedEvent returned = new BookReturnedEvent(reader, new BookCopyId(10L));
        assertEquals(new BookCopyId(10L), returned.getBookCopyId());

        ReservationPromotedEvent promoted = new ReservationPromotedEvent(reader, new BookId(1L));
        assertEquals(new BookId(1L), promoted.getBookId());
        assertEquals(reader, promoted.getReaderId());
    }

    @Test
    void eventPublisherPortIsUsable() {
        List<DomainEvent> published = new ArrayList<>();
        DomainEventPublisher publisher = published::add;

        publisher.publish(new BookLoanedEvent(reader, new BookCopyId(10L)));
        publisher.publish(new LoanOverdueEvent(reader, new BookCopyId(10L)));

        assertEquals(2, published.size());
        assertTrue(published.get(0) instanceof BookLoanedEvent);
        assertTrue(published.get(1) instanceof LoanOverdueEvent);
    }
}

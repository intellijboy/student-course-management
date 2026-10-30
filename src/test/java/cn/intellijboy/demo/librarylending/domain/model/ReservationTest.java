package cn.intellijboy.demo.librarylending.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Reservation 聚合根领域单元测试：预约排队状态与转正流转。
 */
class ReservationTest {

    private final ReaderId reader = new ReaderId(1L);
    private final BookId book = new BookId(1L);
    private final LocalDateTime now = LocalDateTime.of(2026, 10, 30, 10, 0);

    @Test
    void newReservationIsWaiting() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        assertEquals(ReservationStatus.WAITING, r.getStatus());
        assertEquals(book, r.getBookId());
        assertEquals(reader, r.getReaderId());
    }

    @Test
    void promoteMovesWaitingToNotified() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        r.promote();
        assertEquals(ReservationStatus.NOTIFIED, r.getStatus());
    }

    @Test
    void fulfillMovesNotifiedToFulfilled() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        r.promote();
        r.fulfill();
        assertEquals(ReservationStatus.FULFILLED, r.getStatus());
    }

    @Test
    void cancelWaitingReservation() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        r.cancel();
        assertEquals(ReservationStatus.CANCELLED, r.getStatus());
    }

    @Test
    void promoteNonWaitingRejected() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        r.promote();
        assertThrows(IllegalStateException.class, r::promote);
    }

    @Test
    void fulfillNonNotifiedRejected() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        assertThrows(IllegalStateException.class, r::fulfill);
    }
}

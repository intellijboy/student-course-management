package cn.intellijboy.demo.librarylending.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Reservation 聚合根领域单元测试：预约排队状态与转正流转。
 */
@DisplayName("预约聚合测试")
class ReservationTest {

    private final ReaderId reader = new ReaderId(1L);
    private final BookId book = new BookId(1L);
    private final LocalDateTime now = LocalDateTime.of(2026, 10, 30, 10, 0);

    @Test
    @DisplayName("新建预约默认处于等待状态")
    void newReservationIsWaiting() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        assertEquals(ReservationStatus.WAITING, r.getStatus());
        assertEquals(book, r.getBookId());
        assertEquals(reader, r.getReaderId());
    }

    @Test
    @DisplayName("候补预约晋升后变为已通知")
    void promoteMovesWaitingToNotified() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        r.promote();
        assertEquals(ReservationStatus.NOTIFIED, r.getStatus());
    }

    @Test
    @DisplayName("已通知预约完成后变为已完成")
    void fulfillMovesNotifiedToFulfilled() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        r.promote();
        r.fulfill();
        assertEquals(ReservationStatus.FULFILLED, r.getStatus());
    }

    @Test
    @DisplayName("等待中的预约可被取消")
    void cancelWaitingReservation() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        r.cancel();
        assertEquals(ReservationStatus.CANCELLED, r.getStatus());
    }

    @Test
    @DisplayName("非等待状态的预约再次晋升被拒绝")
    void promoteNonWaitingRejected() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        r.promote();
        assertThrows(IllegalStateException.class, r::promote);
    }

    @Test
    @DisplayName("非已通知状态的预约完成被拒绝")
    void fulfillNonNotifiedRejected() {
        Reservation r = new Reservation(1L, reader, book, ReservationStatus.WAITING, now);
        assertThrows(IllegalStateException.class, r::fulfill);
    }
}

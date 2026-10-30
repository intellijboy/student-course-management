package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图书馆藏读模型：CQRS 读路径直接返回的视图对象，不含写模型状态字段。
 */
public record BookStatusView(
        BookId bookId,
        String title,
        int availableCount,
        int borrowedCount,
        List<ReservationView> reservationQueue) {

    public record ReservationView(Long id, Long readerId, ReservationStatus status, LocalDateTime createdAt) {
    }

    public static BookStatusView of(BookId bookId, String title, int availableCount, int borrowedCount,
                                    List<Reservation> reservations) {
        List<ReservationView> queue = reservations.stream()
            .map(r -> new ReservationView(r.getId(), r.getReaderId().value(), r.getStatus(), r.getCreatedAt()))
            .toList();
        return new BookStatusView(bookId, title, availableCount, borrowedCount, queue);
    }
}

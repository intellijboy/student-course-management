package cn.intellijboy.demo.librarylending.web.reservation;

import cn.intellijboy.demo.librarylending.domain.model.Reservation;

import java.time.LocalDateTime;

public record ReservationView(Long id, Long readerId, String status, LocalDateTime createdAt) {
    public static ReservationView of(Reservation r) {
        return new ReservationView(r.getId(), r.getReaderId().value(), r.getStatus().name(), r.getCreatedAt());
    }
}

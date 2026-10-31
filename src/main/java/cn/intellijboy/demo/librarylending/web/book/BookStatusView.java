package cn.intellijboy.demo.librarylending.web.book;

import cn.intellijboy.demo.librarylending.web.reservation.ReservationView;

import java.util.List;

public record BookStatusView(Long id, String title, int availableCopies, int borrowedCopies,
                             List<ReservationView> reservationQueue) {
}

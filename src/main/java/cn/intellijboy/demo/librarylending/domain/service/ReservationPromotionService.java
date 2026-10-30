package cn.intellijboy.demo.librarylending.domain.service;

import cn.intellijboy.demo.librarylending.domain.event.DomainEventPublisher;
import cn.intellijboy.demo.librarylending.domain.event.ReservationPromotedEvent;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;

import java.util.Optional;

/**
 * 预约转正领域服务：副本归还后按预约创建顺序将最早等待预约转正为可借通知，并发布事件。
 */
public class ReservationPromotionService {

    private final ReservationRepository reservationRepository;
    private final DomainEventPublisher publisher;

    public ReservationPromotionService(ReservationRepository reservationRepository, DomainEventPublisher publisher) {
        this.reservationRepository = reservationRepository;
        this.publisher = publisher;
    }

    /** 将某图书最早的等待中预约转正；无等待预约时返回 empty */
    public Optional<Reservation> promoteNextFor(BookId bookId) {
        Optional<Reservation> next = reservationRepository.findWaitingByBookOrderedByCreatedAt(bookId)
            .stream().findFirst();
        next.ifPresent(reservation -> {
            reservation.promote();
            reservationRepository.save(reservation);
            publisher.publish(new ReservationPromotedEvent(reservation.getReaderId(), bookId));
        });
        return next;
    }
}

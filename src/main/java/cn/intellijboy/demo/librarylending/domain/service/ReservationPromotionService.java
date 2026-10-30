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

    /**
     * 创建预约转正领域服务。
     *
     * @param reservationRepository 预约仓储端口，用于查询与保存预约
     * @param publisher 领域事件发布器，用于发布预约转正事件
     */
    public ReservationPromotionService(ReservationRepository reservationRepository, DomainEventPublisher publisher) {
        this.reservationRepository = reservationRepository;
        this.publisher = publisher;
    }

    /**
     * 将某图书按预约创建顺序最早的等待中预约转正为可借通知，并发布预约转正事件；无等待预约时不做任何处理。
     *
     * @param bookId 目标图书标识，不能为 {@code null}
     * @return 被转正的预约；若该图书没有等待中的预约则返回 {@link Optional#empty()}
     */
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

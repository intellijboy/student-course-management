package cn.intellijboy.demo.librarylending.infrastructure.mapper;

import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.ReservationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationJpaEntity toJpa(Reservation reservation) {
        ReservationJpaEntity entity = new ReservationJpaEntity();
        entity.setId(reservation.getId());
        entity.setReaderId(reservation.getReaderId().value());
        entity.setBookId(reservation.getBookId().value());
        entity.setStatus(reservation.getStatus());
        entity.setCreatedAt(reservation.getCreatedAt());
        return entity;
    }

    public Reservation toDomain(ReservationJpaEntity entity) {
        return new Reservation(entity.getId(), new ReaderId(entity.getReaderId()),
            new BookId(entity.getBookId()), entity.getStatus(), entity.getCreatedAt());
    }
}

package cn.intellijboy.demo.librarylending.infrastructure.repository;

import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.model.ReservationStatus;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;
import cn.intellijboy.demo.librarylending.infrastructure.mapper.ReservationMapper;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.SpringDataReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaReservationRepository implements ReservationRepository {

    private final SpringDataReservationRepository springDataRepository;
    private final ReservationMapper mapper;

    public JpaReservationRepository(SpringDataReservationRepository springDataRepository, ReservationMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(reservation)));
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Reservation> findByBookOrderedByCreatedAt(BookId bookId) {
        return springDataRepository.findByBookIdOrderByCreatedAtAsc(bookId.value()).stream()
            .map(mapper::toDomain).toList();
    }

    @Override
    public List<Reservation> findWaitingByBookOrderedByCreatedAt(BookId bookId) {
        return springDataRepository
            .findByBookIdAndStatusOrderByCreatedAtAsc(bookId.value(), ReservationStatus.WAITING).stream()
            .map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsWaitingByReaderAndBook(ReaderId readerId, BookId bookId) {
        return springDataRepository.existsByReaderIdAndBookIdAndStatus(
            readerId.value(), bookId.value(), ReservationStatus.WAITING);
    }
}

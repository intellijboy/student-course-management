package cn.intellijboy.demo.librarylending.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataReservationRepository extends JpaRepository<ReservationJpaEntity, Long> {

    List<ReservationJpaEntity> findByBookIdOrderByCreatedAtAsc(Long bookId);

    /** 等待中预约按创建时间升序 */
    List<ReservationJpaEntity> findByBookIdAndStatusOrderByCreatedAtAsc(Long bookId, cn.intellijboy.demo.librarylending.domain.model.ReservationStatus status);

    boolean existsByReaderIdAndBookIdAndStatus(Long readerId, Long bookId,
                                               cn.intellijboy.demo.librarylending.domain.model.ReservationStatus status);
}

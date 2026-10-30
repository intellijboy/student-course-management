package cn.intellijboy.demo.librarylending.domain.repository;

import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

import java.util.List;
import java.util.Optional;

/**
 * 预约仓储端口：领域层定义，由基础设施层实现。
 */
public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);

    /** 某图书的全部预约，按创建时间升序 */
    List<Reservation> findByBookOrderedByCreatedAt(BookId bookId);

    /** 某图书的等待中预约，按创建时间升序（最早优先） */
    List<Reservation> findWaitingByBookOrderedByCreatedAt(BookId bookId);

    /** 读者是否已对该图书存在等待中的预约 */
    boolean existsWaitingByReaderAndBook(ReaderId readerId, BookId bookId);
}

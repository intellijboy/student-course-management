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

    /**
     * 保存预约；若预约已存在则更新，否则新增。
     *
     * @param reservation 待保存的预约，不能为 {@code null}
     * @return 保存后的预约
     */
    Reservation save(Reservation reservation);

    /**
     * 按预约标识查找预约。
     *
     * @param id 预约标识，不能为 {@code null}
     * @return 对应的预约；不存在时返回 {@link Optional#empty()}
     */
    Optional<Reservation> findById(Long id);

    /**
     * 查找某图书的全部预约，按创建时间升序排列。
     *
     * @param bookId 图书标识，不能为 {@code null}
     * @return 该图书的预约列表（创建时间升序）；无预约时返回空列表
     */
    List<Reservation> findByBookOrderedByCreatedAt(BookId bookId);

    /**
     * 查找某图书处于等待状态的预约，按创建时间升序排列（最早优先）。
     *
     * @param bookId 图书标识，不能为 {@code null}
     * @return 该图书等待中的预约列表（创建时间升序）；无等待预约时返回空列表
     */
    List<Reservation> findWaitingByBookOrderedByCreatedAt(BookId bookId);

    /**
     * 判断读者是否已对某图书存在等待中的预约。
     *
     * @param readerId 读者标识，不能为 {@code null}
     * @param bookId 图书标识，不能为 {@code null}
     * @return 若已存在等待中的预约则返回 {@code true}，否则返回 {@code false}
     */
    boolean existsWaitingByReaderAndBook(ReaderId readerId, BookId bookId);
}

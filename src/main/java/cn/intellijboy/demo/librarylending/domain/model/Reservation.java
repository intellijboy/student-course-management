package cn.intellijboy.demo.librarylending.domain.model;

import java.time.LocalDateTime;

/**
 * 预约聚合根：按创建时间排队，维护 WAITING/NOTIFIED/FULFILLED/CANCELLED 状态流转。
 */
public class Reservation {

    private Long id;
    private final ReaderId readerId;
    private final BookId bookId;
    private ReservationStatus status;
    private final LocalDateTime createdAt;

    /**
     * 创建预约记录。
     *
     * @param id        预约标识，可持久化前为 null
     * @param readerId  预约读者标识，不能为 null
     * @param bookId    预约图书标识，不能为 null
     * @param status    初始状态
     * @param createdAt 预约创建时间，用于排队顺序
     * @throws IllegalArgumentException 读者或图书标识为 null 时抛出
     */
    public Reservation(Long id, ReaderId readerId, BookId bookId, ReservationStatus status, LocalDateTime createdAt) {
        if (readerId == null || bookId == null) {
            throw new IllegalArgumentException("读者与图书标识不能为空");
        }
        this.id = id;
        this.readerId = readerId;
        this.bookId = bookId;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * 转正通知：将等待中的预约置为已通知状态。
     *
     * @throws IllegalStateException 预约不处于等待状态时抛出
     */
    public void promote() {
        if (status != ReservationStatus.WAITING) {
            throw new IllegalStateException("仅等待中的预约可转正");
        }
        this.status = ReservationStatus.NOTIFIED;
    }

    /**
     * 预约完成（读者取书）：将已通知的预约置为已完成状态。
     *
     * @throws IllegalStateException 预约不处于已通知状态时抛出
     */
    public void fulfill() {
        if (status != ReservationStatus.NOTIFIED) {
            throw new IllegalStateException("仅已通知的预约可完成");
        }
        this.status = ReservationStatus.FULFILLED;
    }

    /**
     * 取消预约：等待中或已通知的预约可取消。
     *
     * @throws IllegalStateException 预约已完成或已取消时抛出
     */
    public void cancel() {
        if (status == ReservationStatus.FULFILLED || status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("已完成的预约不可取消");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    /**
     * 判断预约是否处于等待状态。
     *
     * @return 预约状态为等待时返回 true，否则返回 false
     */
    public boolean isWaiting() {
        return status == ReservationStatus.WAITING;
    }

    /**
     * 判断预约是否处于已通知状态。
     *
     * @return 预约状态为已通知时返回 true，否则返回 false
     */
    public boolean isNotified() {
        return status == ReservationStatus.NOTIFIED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReaderId getReaderId() {
        return readerId;
    }

    public BookId getBookId() {
        return bookId;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

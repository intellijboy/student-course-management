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

    /** 转正通知：仅等待中的预约可转正 */
    public void promote() {
        if (status != ReservationStatus.WAITING) {
            throw new IllegalStateException("仅等待中的预约可转正");
        }
        this.status = ReservationStatus.NOTIFIED;
    }

    /** 预约完成（读者取书）：仅已通知的预约可完成 */
    public void fulfill() {
        if (status != ReservationStatus.NOTIFIED) {
            throw new IllegalStateException("仅已通知的预约可完成");
        }
        this.status = ReservationStatus.FULFILLED;
    }

    /** 取消：等待或已通知的预约可取消 */
    public void cancel() {
        if (status == ReservationStatus.FULFILLED || status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("已完成的预约不可取消");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isWaiting() {
        return status == ReservationStatus.WAITING;
    }

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

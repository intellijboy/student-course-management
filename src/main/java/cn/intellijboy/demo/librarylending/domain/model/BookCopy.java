package cn.intellijboy.demo.librarylending.domain.model;

import cn.intellijboy.demo.librarylending.domain.exception.BookCopyUnavailableException;

/**
 * 馆藏副本实体：作为 {@link Book} 聚合的一部分，内部维护可借/已借/预约占用状态机。
 */
public class BookCopy {

    private final BookCopyId id;
    private BookCopyStatus status;

    public BookCopy(BookCopyId id) {
        this(id, BookCopyStatus.AVAILABLE);
    }

    public BookCopy(BookCopyId id, BookCopyStatus status) {
        this.id = id;
        this.status = status;
    }

    /** 借阅：仅可借副本可借出 */
    public void borrow() {
        requireAvailable("副本不可借出");
        this.status = BookCopyStatus.BORROWED;
    }

    /** 预约转正后占用：仅可借副本可被预约占用 */
    public void reserve() {
        requireAvailable("副本不可预约占用");
        this.status = BookCopyStatus.RESERVED;
    }

    /** 预约读者取书：仅预约占用副本可借出 */
    public void borrowReserved() {
        if (this.status != BookCopyStatus.RESERVED) {
            throw new BookCopyUnavailableException("副本未被预约占用，无法按预约借出");
        }
        this.status = BookCopyStatus.BORROWED;
    }

    /** 归还：仅已借出副本可归还 */
    public void returnCopy() {
        if (this.status != BookCopyStatus.BORROWED) {
            throw new BookCopyUnavailableException("副本未处于已借出状态，无法归还");
        }
        this.status = BookCopyStatus.AVAILABLE;
    }

    /** 归还并直接转为预约占用（归还给预约者）：仅已借出副本可转换 */
    public void returnToReserved() {
        if (this.status != BookCopyStatus.BORROWED) {
            throw new BookCopyUnavailableException("副本未处于已借出状态，无法转为预约占用");
        }
        this.status = BookCopyStatus.RESERVED;
    }

    /** 预约取消/超时释放：仅预约占用副本可释放 */
    public void releaseReservation() {
        if (this.status != BookCopyStatus.RESERVED) {
            throw new BookCopyUnavailableException("副本未处于预约占用状态");
        }
        this.status = BookCopyStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return status == BookCopyStatus.AVAILABLE;
    }

    private void requireAvailable(String message) {
        if (status != BookCopyStatus.AVAILABLE) {
            throw new BookCopyUnavailableException(message);
        }
    }

    public BookCopyId getId() {
        return id;
    }

    public BookCopyStatus getStatus() {
        return status;
    }
}

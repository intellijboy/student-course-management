package cn.intellijboy.demo.librarylending.domain.model;

import cn.intellijboy.demo.librarylending.domain.exception.BookCopyUnavailableException;

/**
 * 馆藏副本实体：作为 {@link Book} 聚合的一部分，内部维护可借/已借/预约占用状态机。
 */
public class BookCopy {

    private final BookCopyId id;
    private BookCopyStatus status;

    /**
     * 创建默认可借状态的馆藏副本。
     *
     * @param id 馆藏副本标识
     */
    public BookCopy(BookCopyId id) {
        this(id, BookCopyStatus.AVAILABLE);
    }

    /**
     * 创建指定状态的馆藏副本。
     *
     * @param id     馆藏副本标识
     * @param status 初始状态
     */
    public BookCopy(BookCopyId id, BookCopyStatus status) {
        this.id = id;
        this.status = status;
    }

    /**
     * 借出副本：将状态由可借置为已借出。
     *
     * @throws BookCopyUnavailableException 副本当前不处于可借状态时抛出
     */
    public void borrow() {
        requireAvailable("副本不可借出");
        this.status = BookCopyStatus.BORROWED;
    }

    /**
     * 预约转正后占用副本：将状态由可借置为预约占用。
     *
     * @throws BookCopyUnavailableException 副本当前不处于可借状态时抛出
     */
    public void reserve() {
        requireAvailable("副本不可预约占用");
        this.status = BookCopyStatus.RESERVED;
    }

    /**
     * 预约读者取书借出：将状态由预约占用置为已借出。
     *
     * @throws BookCopyUnavailableException 副本未处于预约占用状态时抛出
     */
    public void borrowReserved() {
        if (this.status != BookCopyStatus.RESERVED) {
            throw new BookCopyUnavailableException("副本未被预约占用，无法按预约借出");
        }
        this.status = BookCopyStatus.BORROWED;
    }

    /**
     * 归还副本：将状态由已借出置为可借。
     *
     * @throws BookCopyUnavailableException 副本未处于已借出状态时抛出
     */
    public void returnCopy() {
        if (this.status != BookCopyStatus.BORROWED) {
            throw new BookCopyUnavailableException("副本未处于已借出状态，无法归还");
        }
        this.status = BookCopyStatus.AVAILABLE;
    }

    /**
     * 归还副本并直接转为预约占用（归还给预约者）：将状态由已借出置为预约占用。
     *
     * @throws BookCopyUnavailableException 副本未处于已借出状态时抛出
     */
    public void returnToReserved() {
        if (this.status != BookCopyStatus.BORROWED) {
            throw new BookCopyUnavailableException("副本未处于已借出状态，无法转为预约占用");
        }
        this.status = BookCopyStatus.RESERVED;
    }

    /**
     * 预约取消或超时后释放副本：将状态由预约占用置为可借。
     *
     * @throws BookCopyUnavailableException 副本未处于预约占用状态时抛出
     */
    public void releaseReservation() {
        if (this.status != BookCopyStatus.RESERVED) {
            throw new BookCopyUnavailableException("副本未处于预约占用状态");
        }
        this.status = BookCopyStatus.AVAILABLE;
    }

    /**
     * 判断副本当前是否处于可借状态。
     *
     * @return 副本状态为可借时返回 true，否则返回 false
     */
    public boolean isAvailable() {
        return status == BookCopyStatus.AVAILABLE;
    }

    /**
     * 校验副本处于可借状态，否则以给定消息抛出不可借异常。
     *
     * @param message 校验失败时异常携带的提示消息
     * @throws BookCopyUnavailableException 副本当前不处于可借状态时抛出
     */
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

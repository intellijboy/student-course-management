package cn.intellijboy.demo.librarylending.domain.model;

import cn.intellijboy.demo.librarylending.domain.exception.LoanNotFoundException;

import java.time.LocalDate;

/**
 * 借阅记录聚合根：维护借出时间、应还期限、归还时间与逾期判定不变式。
 */
public class Loan {

    private Long id;
    private final ReaderId readerId;
    private final BookId bookId;
    private final BookCopyId bookCopyId;
    private final LocalDate borrowedAt;
    private final LocalDate dueDate;
    private LocalDate returnedAt;

    /**
     * 创建借阅记录，并根据借期计算应还日期。
     *
     * @param id          借阅记录标识，可持久化前为 null
     * @param readerId    借阅读者标识
     * @param bookId      所借图书标识
     * @param bookCopyId  所借馆藏副本标识
     * @param borrowedAt  借出日期
     * @param period      借期，天数必须为正
     * @throws IllegalArgumentException 借期为 null 或天数不大于 0 时抛出
     */
    public Loan(Long id, ReaderId readerId, BookId bookId, BookCopyId bookCopyId, LocalDate borrowedAt, LoanPeriod period) {
        if (period == null || period.days() <= 0) {
            throw new IllegalArgumentException("借期必须为正");
        }
        this.id = id;
        this.readerId = readerId;
        this.bookId = bookId;
        this.bookCopyId = bookCopyId;
        this.borrowedAt = borrowedAt;
        this.dueDate = borrowedAt.plusDays(period.days());
    }

    /**
     * 归还借阅：记录归还日期，已归还的记录不可重复归还。
     *
     * @param today 归还日期
     * @throws LoanNotFoundException 借阅记录已归还时抛出
     */
    public void returnBook(LocalDate today) {
        if (returnedAt != null) {
            throw new LoanNotFoundException("借阅记录已归还，不可重复归还");
        }
        this.returnedAt = today;
    }

    /**
     * 逾期判定：未归还且指定日期晚于应还日期即视为逾期。
     *
     * @param today 用于判定的日期
     * @return 未归还且已超过应还日期时返回 true，否则返回 false
     */
    public boolean isOverdue(LocalDate today) {
        return returnedAt == null && today.isAfter(dueDate);
    }

    /**
     * 计算逾期天数：未归还且逾期时返回超出应还日期的天数，否则返回 0。
     *
     * @param today 用于计算的日期
     * @return 逾期天数；未逾期或已归还时返回 0
     */
    public int overdueDays(LocalDate today) {
        if (!isOverdue(today)) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
    }

    /**
     * 判断借阅记录是否已归还。
     *
     * @return 已记录归还日期时返回 true，否则返回 false
     */
    public boolean isReturned() {
        return returnedAt != null;
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

    public BookCopyId getBookCopyId() {
        return bookCopyId;
    }

    public LocalDate getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedAt() {
        return returnedAt;
    }
}

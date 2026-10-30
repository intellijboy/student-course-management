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

    /** 归还：已归还的记录不可重复归还 */
    public void returnBook(LocalDate today) {
        if (returnedAt != null) {
            throw new LoanNotFoundException("借阅记录已归还，不可重复归还");
        }
        this.returnedAt = today;
    }

    /** 逾期判定：未归还且今天晚于应还期限 */
    public boolean isOverdue(LocalDate today) {
        return returnedAt == null && today.isAfter(dueDate);
    }

    /** 逾期天数：未归还且逾期时返回天数差，否则 0 */
    public int overdueDays(LocalDate today) {
        if (!isOverdue(today)) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
    }

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

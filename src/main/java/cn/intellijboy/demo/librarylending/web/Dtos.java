package cn.intellijboy.demo.librarylending.web;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyStatus;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 接口层 DTO：转换领域模型为对外视图。
 */
public final class Dtos {

    private Dtos() {
    }

    public record BookView(Long id, String title, String author, int totalCopies, int availableCopies) {
        public static BookView of(Book book) {
            return new BookView(book.getId().value(), book.getTitle(), book.getAuthor(),
                book.totalCount(), book.availableCount());
        }
    }

    public record BookCopyView(Long id, BookCopyStatus status) {
    }

    public record BookStatusView(Long id, String title, int availableCopies, int borrowedCopies,
                                 List<ReservationView> reservationQueue) {
    }

    public record ReservationView(Long id, Long readerId, String status, LocalDateTime createdAt) {
        public static ReservationView of(Reservation r) {
            return new ReservationView(r.getId(), r.getReaderId().value(), r.getStatus().name(), r.getCreatedAt());
        }
    }

    public record LoanView(Long id, Long readerId, Long bookId, Long bookCopyId,
                           LocalDate borrowedAt, LocalDate dueDate, LocalDate returnedAt) {
        public static LoanView of(Loan loan) {
            return new LoanView(loan.getId(), loan.getReaderId().value(), loan.getBookId().value(),
                loan.getBookCopyId().value(), loan.getBorrowedAt(), loan.getDueDate(), loan.getReturnedAt());
        }
    }

    public record OverdueView(Long loanId, Long readerId, Long bookCopyId, LocalDate dueDate,
                              java.math.BigDecimal fee) {
        public static OverdueView of(Loan loan, java.math.BigDecimal fee) {
            return new OverdueView(loan.getId(), loan.getReaderId().value(), loan.getBookCopyId().value(),
                loan.getDueDate(), fee);
        }
    }

    public record ReturnView(java.math.BigDecimal overdueFee, Long promotedReaderId) {
    }
}
